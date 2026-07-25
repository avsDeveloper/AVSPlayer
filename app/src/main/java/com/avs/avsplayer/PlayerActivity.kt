package com.avs.avsplayer

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.avs.avsplayer.PlaybackService.Companion.STOP_AVS_PLAYER_PLAYBACK
import com.avs.avsplayer.domain.model.MediaListItem
import com.avs.avsplayer.presentation.ProgressIndicator
import com.avs.avsplayer.presentation.player.PlayerScreen
import com.avs.avsplayer.presentation.playerinfo.PlayerInfoScreen
import com.avs.avsplayer.ui.AVSPlayerTheme
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.launch
import androidx.core.net.toUri

class PlayerActivity : ComponentActivity() {

    private val viewModel: PlayerViewModel by viewModels { PlayerViewModel.Factory }

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller by mutableStateOf<MediaController?>(null)

    private val resultReceiver = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            openMedia(result.data)
        } else {
            viewModel.dispatch(PlayerAction.Finish)
        }
    }

    private val notificationPermissionReceiver = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
    //  do nothing here, it still works, but without notifications
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.dispatch(PlayerAction.ShowBottomSheet)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.action == Intent.ACTION_VIEW && intent.data != null) {
            openMedia(intent)
        }

        setContent {
            AVSPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    PlayerRoot()
                }
            }
        }

        setFullScreen()

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.action == Intent.ACTION_VIEW && intent.data != null) {
            openMedia(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.state.value.uiState == PlayerUiState.PLAYER) {
            connectController()
        }
    }

    override fun onStop() {
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controllerFuture = null
        controller = null
        super.onStop()
    }

    @Composable
    fun PlayerRoot() {
        val state by viewModel.state.collectAsStateWithLifecycle()

        when (state.uiState) {
            PlayerUiState.INFO_SCREEN -> {
                PlayerInfoScreen(
                    onCloseClicked = {
                        viewModel.dispatch(PlayerAction.SetFirstScreenShown)
                    }
                )
            }

            PlayerUiState.PLAYER -> {
                val player = controller
                if (player != null) {
                    PlayerScreen(
                        player = player,
                        currentPosition = state.currentPosition,
                        showBottomSheet = state.showBottomSheet,
                        onHideBottomSheet = {
                            viewModel.dispatch(PlayerAction.HideBottomSheet)
                        },
                        onShowBottomSheet = {
                            viewModel.dispatch(PlayerAction.ShowBottomSheet)
                        },
                        onOpenPicker = {
                            viewModel.dispatch(PlayerAction.OpenPicker)
                        },
                        onFinish = {
                            viewModel.dispatch(PlayerAction.Finish)
                        }
                    )
                } else {
                    ProgressIndicator()
                }
            }

            else -> {
                ProgressIndicator()
            }
        }
    }

    private fun openMedia(intent: Intent?) {
        if (intent == null) return
        val uris = mutableListOf<Uri>()
        val clipData = intent.clipData
        if (clipData != null) {
            for (i in 0 until clipData.itemCount) {
                clipData.getItemAt(i)?.uri?.let { uris.add(it) }
            }
        } else {
            intent.data?.let { uris.add(it) }
        }
        val items = uris.map { buildMediaListItem(it) }
        if (items.isNotEmpty()) {
            viewModel.dispatch(PlayerAction.PrepareRunPlayer(items))
        }
    }

    private fun buildMediaListItem(uri: Uri): MediaListItem {
        var displayName: String? = null
        var mimeType: String? = null
        var size: Long? = null

        val projection = arrayOf(
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
        )
        try {
            contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    displayName = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                        .takeIf { it >= 0 }?.let { cursor.getString(it) }
                    mimeType = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)
                        .takeIf { it >= 0 }?.let { cursor.getString(it) }
                    size = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)
                        .takeIf { it >= 0 }?.let { cursor.getLong(it) }
                }
            }
        } catch (_: Exception) {}

        if (mimeType == null) {
            mimeType = try {
                contentResolver.getType(uri)
            } catch (e: Exception) {
                null
            }
        }

        return MediaListItem(
            uri = uri.toString(),
            displayName = displayName ?: uri.lastPathSegment ?: uri.toString(),
            mimeType = mimeType,
            size = size
        )
    }

    private fun setFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.attributes = window.attributes.apply {
            layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        WindowInsetsControllerCompat(window, window.decorView).apply {
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    private fun stopPlayback() {
        val stopIntent = Intent(this, PlaybackService::class.java)
        stopIntent.action = STOP_AVS_PLAYER_PLAYBACK
        startService(stopIntent)
    }

    /**
     * Without POST_NOTIFICATIONS the foreground service still runs, but Android 13 and later hide
     * its media notification, which also removes the lock-screen controls.
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            notificationPermissionReceiver.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun preparePlayer() {
        requestNotificationPermission()
        connectController { player ->
            player.setMediaItems(createMediaItems(viewModel.state.value.selectedMedia))
            player.prepare()
            player.play()
            viewModel.dispatch(PlayerAction.RunPlayer)
        }
    }

    private fun connectController(onConnected: (MediaController) -> Unit = {}) {
        controller?.let {
            onConnected(it)
            return
        }
        val future = controllerFuture ?: MediaController
            .Builder(
                this,
                SessionToken(this, ComponentName(this, PlaybackService::class.java))
            )
            .buildAsync()
            .also { controllerFuture = it }
        future.addListener(
            {
                if (controllerFuture === future) {
                    val connected = runCatching { future.get() }.getOrNull() ?: return@addListener
                    connected.addListener(object : Player.Listener {
                        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                            viewModel.dispatch(
                                PlayerAction.SetCurrentItemNum(connected.currentMediaItemIndex)
                            )
                        }
                    })
                    controller = connected
                    onConnected(connected)
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    private fun openPicker() {
        val pickMediaIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("audio/*", "video/*"))
        }
        resultReceiver.launch(pickMediaIntent)
    }

    private fun createMediaItems(items: List<MediaListItem>): List<MediaItem> =
        items.map { item ->
            val isVideo = item.mimeType?.contains("video", ignoreCase = true) == true
            val artworkRes = if (isVideo) R.drawable.video_notification else R.drawable.audio_notification
            val descriptionText = item.mimeType?.uppercase()?.replace("/", " · ")
                ?: getString(if (isVideo) R.string.video_label else R.string.audio_label)

            MediaItem.Builder()
                .setMediaId(item.uri)
                .setMimeType(item.mimeType)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(item.displayName)
                        .setDescription(descriptionText)
                        .setArtworkUri("android.resource://$packageName/$artworkRes".toUri())
                        .build()
                )
                .build()
        }
}
