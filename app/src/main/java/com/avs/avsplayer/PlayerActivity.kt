package com.avs.avsplayer

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
import com.avs.avsplayer.PlaybackService.Companion.STOP_AVS_PLAYER_PLAYBACK
import com.avs.avsplayer.presentation.AVSPlayerInfoScreen
import com.avs.avsplayer.presentation.AVSPlayerScreen
import com.avs.avsplayer.presentation.AVSProgressIndicator
import com.avs.avsplayer.data.MediaListItem
import com.avs.avsplayer.data.repositories.DataStoreRepository
import com.avs.avsplayer.ui.AVSPlayerTheme
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(
    name = "AVS_datastore"
)

class PlayerActivity : ComponentActivity(), MediaController.Listener {

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

        // open without any steps if media shared
        if (intent.action == Intent.ACTION_VIEW && intent.data != null) {
            viewModel.setPrepareRunPlayer()
            viewModel.clearMediaListItem()
            generateMediaList(intent)
        }

        if (intent.action == Intent.ACTION_MAIN) {
            lifecycleScope.launch {
                viewModel.isShowFirstScreen.collect { shouldShow ->
                    if (shouldShow) {
                        viewModel.setShowInfoScreen()
                    }
                    else {
                        viewModel.setOpenPicker()
                    }
                }
            }
        }

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
                RESULT_OK -> {
                    viewModel.setPrepareRunPlayer()
                    viewModel.clearMediaListItem()

                    generateMediaList(it.data)
                }
                else -> {
                    viewModel.setFinished()
                }
            }

        }
    }

    private fun generateMediaList(intent: Intent?) {
        if (intent?.clipData != null) {
            for (i in 0 until intent.clipData?.itemCount!!) {
                intent.clipData?.getItemAt(i)?.uri?.let {
                    generateMediaListItem(it)
                }
            }
        } else { // only one item selected
            intent?.data?.let {
                generateMediaListItem(it)
            }
        }
    }

    private fun generateMediaListItem(uri: Uri) {

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
        val stopIntent = Intent(this@PlayerActivity, PlaybackService::class.java)
        stopIntent.action = STOP_AVS_PLAYER_PLAYBACK
        startService(stopIntent)
    }

    @Composable
    fun PlayerScreen() {
        val uiState = viewModel.uiState.collectAsStateWithLifecycle()
        val isBottomSheetShown = viewModel.isBottomSheetShown.collectAsStateWithLifecycle()

        when (uiState.value) {

            UIState.InfoScreen -> {
                AVSPlayerInfoScreen(viewModel)
            }

            // everything ready, open media picker
            UIState.OpenPicker -> {
                stopPlayback()
                openPicker()
            }

            // create media session and show progress bar
            UIState.PrepareRunPlayer -> {
                AVSProgressIndicator()

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
                        viewModel.setRunPlayer()
                    },
                    MoreExecutors.directExecutor()
                )
            }

            // show and run player
            UIState.RunPlayer -> {
                AVSPlayerScreen(
                    player = controllerFuture.get(),
                    showBottomSheet = isBottomSheetShown.value,
                    viewModel
                )
            }

            // JustCreated state, initial state, show progress bar
            else -> {
                AVSProgressIndicator()
            }
        }
    }

    //  open standard Android file browser to pick audio / video files
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
                Uri.parse("android.resource://$packageName/${R.drawable.video_notification}")
            } else {
                Uri.parse("android.resource://$packageName/${R.drawable.audio_notification}")
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

