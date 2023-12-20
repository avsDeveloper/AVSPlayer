package com.example.avsplayer.presentation

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.avsplayer.R
import com.example.avsplayer.data.DataStoreRepository
import com.example.avsplayer.data.MediaListItem
import com.example.avsplayer.presentation.PlaybackService.Companion.STOP_AVS_PLAYER_PLAYBACK
import com.example.avsplayer.presentation.theme.AVSPlayerTheme
import com.example.avsplayer.presentation.view.AVSPlayerInfoView
import com.example.avsplayer.presentation.view.AVSPlayerView
import com.example.avsplayer.presentation.view.AVSProgressIndicatorView
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.launch


private val Context.dataStore by preferencesDataStore(
    name = "AVS_datastore"
)

class MainActivity : ComponentActivity(), MediaController.Listener {

    private val repository: DataStoreRepository by lazy {
        DataStoreRepository(dataStore)
    }

    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory(repository)
    }

    private lateinit var controllerFuture : ListenableFuture<MediaController>
    private lateinit var resultReceiver : ActivityResultLauncher<Intent>
    lateinit var player: Player

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.showBottomSheet()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // finish activity and session if true
        lifecycleScope.launch {
            viewModel.isFinished.collect { isFinished ->
                if (isFinished)  {
                    stopPlayback()
                    finish()
                }
            }
        }

        setContent {
            AVSPlayerTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    PlayerScreen()
                }
            }
        }

        // remove toolbar, etc. to show player in full screen mode
        setFullScreen()

        // show bottom sheet dialog instead of closing the app
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        // set "Selected" UI state if media files picked
        resultReceiver = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {

            when(it.resultCode) {
                Activity.RESULT_OK -> {
                    viewModel.setSelected()
                    viewModel.clearMediaListItem()

                    if (it?.data?.clipData != null) {
                        for (i in 0 until it.data?.clipData?.itemCount!!) {
                            it.data?.clipData?.getItemAt(i)?.uri?.let {
                                generateMediaListData(it)
                            }
                        }
                    } else { // only one item selected
                        it.data?.data?.let {
                            generateMediaListData(it)
                        }
                    }
                }
                else -> {
                    viewModel.setFinished()
                }
            }

        }
    }

    private fun generateMediaListData(uri: Uri) {

        val projection = arrayOf(
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
        )

        this.contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val displayName =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                val mimeType =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE))
                val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))

                viewModel.addMediaListItem(
                    MediaListItem(
                        uri = uri,
                        displayName = displayName,
                        mimeType = mimeType,
                        size = size
                    )
                )
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun setFullScreen() {
        actionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

            window.decorView.setOnApplyWindowInsetsListener { view, windowInsets ->
                if (windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars())
                    || windowInsets.isVisible(WindowInsetsCompat.Type.statusBars())) {
                        window.insetsController?.hide(WindowInsetsCompat.Type.systemBars())
                }
                view.onApplyWindowInsets(windowInsets)
            }
        }
    }

    private fun stopPlayback() {
        val stopIntent = Intent(this@MainActivity, PlaybackService::class.java)
        stopIntent.action = STOP_AVS_PLAYER_PLAYBACK
        startService(stopIntent)
    }

    @Composable
    fun PlayerScreen() {
        val uiState = viewModel.uiState.collectAsStateWithLifecycle()
        val isBottomSheetShown = viewModel.isBottomSheetShown.collectAsStateWithLifecycle()

        when (uiState.value) {

            // show initial screen or when the user opens it
            UIState.InfoScreen -> {
                AVSPlayerInfoView(viewModel)
            }

            // showUIState player
            UIState.Ready -> {
                AVSPlayerView(
                    controllerFuture.get(),
                    isBottomSheetShown.value,
                    viewModel
                )
            }

            // create media session and show progress bar
            UIState.Selected -> {

                // show indicator
                AVSProgressIndicatorView()

                val sessionToken = SessionToken(
                    this,
                    ComponentName(this, PlaybackService::class.java)
                )
                controllerFuture = MediaController
                    .Builder(this, sessionToken)
                    .buildAsync()
                controllerFuture.addListener(
                    {
                        val items = createMediaItems(viewModel.mediaListItemList.value)
                        player = controllerFuture.get()
                        player.setMediaItems(items)
                        player.prepare()
                        player.play()
                        player.addListener(object : Player.Listener {
                            override fun onTracksChanged(tracks: Tracks) {
                                super.onTracksChanged(tracks)
                                viewModel.setCurrentItemNum(player.currentMediaItemIndex)
                            }
                        })
                        viewModel.setReady()
                    },
                    MoreExecutors.directExecutor()
                )
            }

            // launch picker here =)
            else -> {
                stopPlayback()
                openPicker()
            }
        }
    }

    //  open standard Android file browser to pick audio / video file
    private fun openPicker() {
        val pickMediaIntent = Intent()
            .apply {
                action = Intent.ACTION_GET_CONTENT
                type = "*/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("audio/*", "video/*"))
            }
        resultReceiver.launch(pickMediaIntent)
    }

    private fun createMediaItems(uriList: List<MediaListItem>) : List<MediaItem> {

        val retriever = MediaMetadataRetriever()
        val mediaItemList = mutableListOf<MediaItem>()

        uriList.forEach {item ->
            retriever.setDataSource(this,  item.uri)

            val mediaItem = MediaItem
                .Builder()

                val isVideo = item.mimeType?.contains("video", ignoreCase = true)

                val artworkUri = if  (isVideo == true) {
                    Uri.parse("android.resource://$packageName/${R.drawable.icon_video_list_trans}")
                } else {
                    Uri.parse("android.resource://$packageName/${R.drawable.icon_audio_list_transp}")
                }

                val descriptionText = if (isVideo == true) {
                    getString(R.string.video_file, item.mimeType)
                } else {
                    getString(R.string.audio_file, item.mimeType)
                }

                mediaItem
                    .setMediaId(item.uri.toString())
                    .setMediaMetadata(
                        MediaMetadata
                            .Builder()
                            .setTitle(item.displayName)
                            .setDescription(descriptionText)
                            .setArtworkUri(artworkUri)
                            .build()
                    )
                    .setMimeType(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE))

            mediaItemList.add(mediaItem.build())
        }
        retriever.release()
        return mediaItemList
    }

}
