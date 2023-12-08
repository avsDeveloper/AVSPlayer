package com.example.avsplayer.presentation.view

import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.media3.session.MediaController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.example.avsplayer.presentation.MainActivityViewModel

@androidx.annotation.OptIn(UnstableApi::class) @OptIn(ExperimentalFoundationApi::class)
@Composable
fun AVSPlayerView(
    player: MediaController?,
    showBottomSheet: Boolean,
    viewModel: MainActivityViewModel
) {
    val context = LocalContext.current

    ConstraintLayout (
        modifier = Modifier.combinedClickable(
            onClick = {},
            onLongClick = {
                viewModel.showBottomSheet()
            }
        )
    ) {
        val (title, videoPlayer) = createRefs()

        if (showBottomSheet) {
            AVSPlayerBottomSheetView(
                onDismiss = { viewModel.hideBottomSheet() },
                viewModel = viewModel
            )
        }

        DisposableEffect(
            AndroidView(
                modifier = Modifier
                    .constrainAs(videoPlayer) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            viewModel.showBottomSheet()
                        },
                        onDoubleClick = {
                            viewModel.showBottomSheet()
                        }
                    ),
                factory = {
                    PlayerView(
                        context
                    ).apply {
                        setPlayer(player)
                        layoutParams =
                            FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams
                                    .MATCH_PARENT,
                                ViewGroup.LayoutParams
                                    .MATCH_PARENT
                            )
                        videoSurfaceView?.setOnLongClickListener {
                            viewModel.showBottomSheet()
                            true
                        }
                    }
                }
            )
        ) {
            onDispose {
                player?.release()
            }
        }
    }

}