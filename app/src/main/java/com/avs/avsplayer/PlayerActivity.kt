package com.avs.avsplayer

import android.content.ComponentName
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.avs.avsplayer.PlaybackService.Companion.STOP_AVS_PLAYER_PLAYBACK
import com.avs.avsplayer.presentation.ProgressIndicator
import com.avs.avsplayer.domain.model.MediaListItem
import com.avs.avsplayer.presentation.player.PlayerUiScreen
import com.avs.avsplayer.presentation.playerinfo.PlayerInfoScreen
import com.avs.avsplayer.ui.AVSPlayerTheme
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayerActivity : ComponentActivity(), MediaController.Listener {

    private val viewModel: PlayerViewModel by viewModels()

    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private lateinit var resultReceiver: ActivityResultLauncher<Intent>
    lateinit var player: Player

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.dispatch(PlayerAction.ShowBottomSheet)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle media shared via ACTION_VIEW
        if (intent.action == Intent.ACTION_VIEW && intent.data != null) {
            viewModel.dispatch(PlayerAction.PrepareRunPlayer)
            generateMediaList(intent)
        }

        setContent {
            AVSPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    PlayerScreen()
                }
            }
        }

        // Remove toolbar, etc. to show player in full screen mode
        setFullScreen()

        // Show bottom sheet dialog instead of closing the app
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        // Set "Selected" UI state if media files picked
        resultReceiver = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            when (it.resultCode) {
                RESULT_OK -> {
                    viewModel.dispatch(PlayerAction.PrepareRunPlayer)
                    generateMediaList(it.data)
                }
                else -> {
                    viewModel.dispatch(PlayerAction.Finish)
                }
            }
        }

        // Observe effects for one-off events (picker, finish, etc.)
        lifecycleScope.launch {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is PlayerEffect.OpenPicker -> openPicker()
                    is PlayerEffect.StopPlayback -> stopPlayback()
                    is PlayerEffect.Finish -> finish()
                    is PlayerEffect.PreparePlayer -> preparePlayer()
                }
            }
        }
    }

    private fun generateMediaList(intent: Intent?) {
        intent?.let {
            val uris = mutableListOf<Uri>()
            if (it.clipData != null) {
                for (i in 0 until it.clipData!!.itemCount) {
                    it.clipData?.getItemAt(i)?.uri?.let { uri -> uris.add(uri) }
                }
            } else {
                it.data?.let { uri -> uris.add(uri) }
            }
            uris.forEach { uri -> generateMediaListItem(uri) }
        }
    }

    private fun generateMediaListItem(uri: Uri) {
        val projection = arrayOf(
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
        )

        try {
            contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                    val mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE))
                    val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))

                    viewModel.dispatch( PlayerAction.AddMediaListItem(
                        MediaListItem(
                            uri = uri,
                            displayName = displayName,
                            mimeType = mimeType,
                            size = size
                        )
                    ))
                }
            }
        } catch (e: Exception) {
            Log.e("PlayerActivity", "Error generating media list item", e)
        }
    }

//    @Suppress("DEPRECATION")
private fun setFullScreen() {
    actionBar?.hide()
    WindowCompat.setDecorFitsSystemWindows(window, false)
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    } else {
        window.insetsController?.apply {
            hide(WindowInsets.Type.statusBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        window.decorView.setOnApplyWindowInsetsListener { view, windowInsets ->
            if (windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars())
                || windowInsets.isVisible(WindowInsetsCompat.Type.statusBars())
            ) {
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
        val state by viewModel.state.collectAsStateWithLifecycle()

        when (state.uiState) {

            // First launch, show some how-to-use text
            PlayerUiState.INFO_SCREEN -> {
                PlayerInfoScreen(viewModel)
            }

            // everything ready, open media picker
            PlayerUiState.PICKER -> {
                stopPlayback()
                openPicker()
            }

            PlayerUiState.EMPTY_PLAYER -> {
                ProgressIndicator()
            }

            PlayerUiState.PLAYER -> {
                if (state.selectedMedia.isNotEmpty()) {
                    PlayerUiScreen(
                        player = controllerFuture.get(),
                        showBottomSheet = state.showBottomSheet,
                        viewModel
                    )
                }
            }

            PlayerUiState.NONE -> {
                ProgressIndicator()
            }
        }
    }

    private fun preparePlayer() {
        val sessionToken = SessionToken(
            this,
            ComponentName(this, PlaybackService::class.java)
        )
        controllerFuture = MediaController
            .Builder(this, sessionToken)
            .buildAsync()
        controllerFuture.addListener(
            {
                val items = createMediaItems(viewModel.state.value.selectedMedia)
                player = controllerFuture.get()
                player.setMediaItems(items)
                player.prepare()
                player.play()
                player.addListener(object : Player.Listener {
                    override fun onTracksChanged(tracks: Tracks) {
                        super.onTracksChanged(tracks)
                        viewModel.dispatch(PlayerAction.SetCurrentItemNum(player.currentMediaItemIndex))
                    }
                })
                viewModel.dispatch(PlayerAction.RunPlayer)
            },
            MoreExecutors.directExecutor()
        )
    }

    //  open standard Android file browser to pick audio / video files
    private fun openPicker() {
        val pickMediaIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("audio/*", "video/*"))
        }
        resultReceiver.launch(pickMediaIntent)
    }

    private fun createMediaItems(uriList: List<MediaListItem>): List<MediaItem> {
        val retriever = MediaMetadataRetriever()
        val mediaItemList = mutableListOf<MediaItem>()

        uriList.forEach { item ->
            retriever.setDataSource(this, item.uri)

            val mediaItem = MediaItem.Builder()
            val isVideo = item.mimeType?.contains("video", ignoreCase = true)
            val artworkUri = if (isVideo == true) {
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
                    MediaMetadata.Builder()
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