package com.avs.avsplayer.presentation.player

import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.SimpleBasePlayer
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.avs.avsplayer.presentation.player.components.AudioOnlyPlaceholder
import com.avs.avsplayer.presentation.player.components.PlayerControlBottomSheet
import com.avs.avsplayer.presentation.player.components.PlayerTimeline
import com.avs.avsplayer.presentation.player.components.PlayerTopBar
import com.avs.avsplayer.presentation.player.components.PlayerTransport
import com.avs.avsplayer.ui.AVSPlayerTheme
import com.avs.avsplayer.ui.PlayerBlack
import kotlinx.coroutines.delay

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    player: Player,
    showBottomSheet: Boolean,
    onHideBottomSheet: () -> Unit,
    onShowBottomSheet: () -> Unit,
    onOpenPicker: () -> Unit,
    onFinish: () -> Unit,
    currentPosition: Int
) {
    val context = LocalContext.current
    var controlsVisible by remember { mutableStateOf(true) }
    var hasVideo by remember { mutableStateOf(player.hasVideoOutput()) }
    var playing by remember { mutableStateOf(player.isPlaying) }
    var title by remember { mutableStateOf(player.mediaMetadata.title?.toString().orEmpty()) }
    var subtitle by remember { mutableStateOf(player.mediaMetadata.description?.toString().orEmpty()) }
    var positionMs by remember { mutableStateOf(0L) }
    var durationMs by remember { mutableStateOf(0L) }

    val playerView = remember {
        PlayerView(context).apply {
            useController = false
            artworkDisplayMode = PlayerView.ARTWORK_DISPLAY_MODE_OFF
        }
    }

    DisposableEffect(player) {
        playerView.player = player
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playing = isPlaying
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                title = mediaMetadata.title?.toString().orEmpty()
                subtitle = mediaMetadata.description?.toString().orEmpty()
            }

            override fun onTracksChanged(tracks: Tracks) {
                hasVideo = player.hasVideoOutput()
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                hasVideo = player.hasVideoOutput()
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            playerView.player = null
        }
    }

    LaunchedEffect(controlsVisible) {
        while (controlsVisible) {
            positionMs = player.currentPosition
            durationMs = player.duration.coerceAtLeast(0)
            delay(500)
        }
    }

    LaunchedEffect(controlsVisible, playing, showBottomSheet) {
        if (controlsVisible && playing && !showBottomSheet) {
            delay(4000)
            controlsVisible = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PlayerBlack)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { playerView }
        )

        if (!hasVideo) {
            AudioOnlyPlaceholder(playing = playing, modifier = Modifier.fillMaxSize())
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        controlsVisible = !controlsVisible
                    }
                }
        )

        if (controlsVisible) {
            PlayerTopBar(
                title = title,
                subtitle = subtitle,
                playing = playing,
                onOpenPlaylist = onShowBottomSheet,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(PlayerBlack.copy(alpha = .75f), Color.Transparent)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, PlayerBlack.copy(alpha = .85f))
                        )
                    )
                    .windowInsetsPadding(WindowInsets.displayCutout)
                    .padding(top = 24.dp, bottom = 24.dp)
            ) {
                PlayerTransport(
                    playing = playing,
                    onPrevious = { player.seekToPrevious() },
                    onReplay = { player.seekBack() },
                    onPlayPause = { if (player.isPlaying) player.pause() else player.play() },
                    onForward = { player.seekForward() },
                    onNext = { player.seekToNext() },
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                PlayerTimeline(
                    positionMs = positionMs,
                    durationMs = durationMs,
                    onSeek = { ms ->
                        player.seekTo(ms)
                        positionMs = ms
                    }
                )
            }
        }

        if (!hasVideo && showBottomSheet) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PlayerBlack.copy(alpha = .6f))
            )
        }

        if (showBottomSheet) {
            PlayerControlBottomSheet(
                onDismiss = onHideBottomSheet,
                onOpenPicker = onOpenPicker,
                onFinish = onFinish,
                player = player,
                currentPosition = currentPosition
            )
        }
    }
}

private fun Player.hasVideoOutput(): Boolean =
    videoSize.width > 0 || currentTracks.isTypeSelected(C.TRACK_TYPE_VIDEO)

@androidx.annotation.OptIn(UnstableApi::class)
@Preview(name = "Player screen", widthDp = 372, heightDp = 692, showBackground = true)
@Composable
private fun PlayerScreenPreview() {
    AVSPlayerTheme {
        PlayerScreen(
            player = remember { previewPlayer() },
            showBottomSheet = false,
            onHideBottomSheet = {},
            onShowBottomSheet = {},
            onOpenPicker = {},
            onFinish = {},
            currentPosition = 0
        )
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
private fun previewPlayer(): Player = object : SimpleBasePlayer(Looper.getMainLooper()) {
    private val mediaItem = MediaItem.Builder()
        .setMediaId("preview")
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle("Midnight City")
                .setDescription("MP3 · 320KBPS")
                .build()
        )
        .build()

    override fun getState(): State =
        State.Builder()
            .setAvailableCommands(Player.Commands.Builder().addAllCommands().build())
            .setPlaylist(
                listOf(
                    MediaItemData.Builder(mediaItem.mediaId)
                        .setMediaItem(mediaItem)
                        .setDurationUs(180_000_000L)
                        .build()
                )
            )
            .setPlayWhenReady(true, Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST)
            .setPlaybackState(Player.STATE_READY)
            .setContentPositionMs(45_000L)
            .build()
}
