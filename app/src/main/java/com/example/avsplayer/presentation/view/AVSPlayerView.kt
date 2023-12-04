package com.example.avsplayer.presentation.view

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.media3.session.MediaController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.media3.ui.PlayerView

@Composable
fun AVSPlayerView(
    player: MediaController?
) {
    val context = LocalContext.current

    ConstraintLayout {
        val (title, videoPlayer) = createRefs()

        DisposableEffect(
            AndroidView(
                modifier = Modifier
                    .constrainAs(videoPlayer) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                factory = {

                    PlayerView(context).apply {
                        setPlayer(player)
                        layoutParams =
                            FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams
                                    .MATCH_PARENT,
                                ViewGroup.LayoutParams
                                    .MATCH_PARENT
                            )
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