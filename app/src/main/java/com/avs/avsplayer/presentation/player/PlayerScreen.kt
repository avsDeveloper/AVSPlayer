package com.avs.avsplayer.presentation.player

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.media3.session.MediaController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.avs.avsplayer.PlayerAction
import com.avs.avsplayer.R
import com.avs.avsplayer.PlayerViewModel
import com.avs.avsplayer.presentation.player.components.DraggableFAB
import com.avs.avsplayer.presentation.player.components.PlayerControlBottomSheet

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun PlayerUiScreen(
    player: MediaController?,
    showBottomSheet: Boolean,
    viewModel: PlayerViewModel
) {
    val context = LocalContext.current
    val playerView = remember { PlayerView(context) }
    var viewMeasured by remember { mutableStateOf(false) }

    LaunchedEffect(player) {
        playerView.setPlayer(player)
        playerView.defaultArtwork = context.getDrawable(R.drawable.video_off_outline)
        playerView.artworkDisplayMode = PlayerView.ARTWORK_DISPLAY_MODE_FIT
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        playerView.videoSurfaceView?.setOnLongClickListener {
            viewModel.dispatch(PlayerAction.ShowBottomSheet)
//            viewModel.showBottomSheet()
            true
        }

        player?.addListener(object : Player.Listener {
            override fun onVideoSizeChanged(size: VideoSize) {
                super.onVideoSizeChanged(size)
                val videoWidth = size.width
                val videoHeight = size.height
                Log.d("VideoSize", "Width: $videoWidth, Height: $videoHeight")
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_READY && !viewMeasured) {
                    playerView.invalidate()
                    viewMeasured = true
                }
            }
        })
    }

    DisposableEffect(player) {
        onDispose {
            player?.release()
        }
    }

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        if (showBottomSheet) {
            PlayerControlBottomSheet(
                onDismiss = {
                    viewModel.dispatch(PlayerAction.HideBottomSheet)
//                    viewModel.hideBottomSheet()
                            },
                viewModel = viewModel,
                player = player
            )
        }

        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            factory = { playerView }
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            DraggableFAB(
                onClick = {
                    viewModel.dispatch(PlayerAction.ShowBottomSheet)
//                    viewModel.showBottomSheet()
                          },
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
                    .padding(bottom = 56.dp, end = 56.dp)
            )
        }
    }
}