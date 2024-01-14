package com.avs.avsplayer.compose

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.media3.session.MediaController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.ARTWORK_DISPLAY_MODE_FIT
import com.avs.avsplayer.R
import com.avs.avsplayer.viewmodels.MainActivityViewModel
import kotlin.math.roundToInt

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun AVSPlayerScreen(
    player: MediaController?,
    showBottomSheet: Boolean,
    viewModel: MainActivityViewModel
) {
    val context = LocalContext.current

    val playerView = remember { PlayerView(context) }

    ConstraintLayout (
        modifier = Modifier

    ) {

        if (showBottomSheet) {
            AVSPlayerBottomSheet(
                onDismiss = { viewModel.hideBottomSheet() },
                viewModel = viewModel,
                player = player
            )
        }

        Column {

            val modifier =  Modifier.fillMaxWidth(1f)

            DisposableEffect(player) {
                    playerView.setPlayer(player)
                    playerView.defaultArtwork = context.getDrawable(R.drawable.video_off_outline)
                    playerView.artworkDisplayMode = ARTWORK_DISPLAY_MODE_FIT
                    playerView.videoSurfaceView?.setOnLongClickListener {
                        // Handle long click
                        viewModel.showBottomSheet()
                        true
                    }

                    onDispose {
                        player?.release()
                    }
                }

            AndroidView(
                modifier = modifier.padding(bottom = 16.dp),
                factory = { playerView }
            )
        }


        Box(
            modifier = Modifier
            .fillMaxSize()
        ) {

            var offsetX by remember { mutableStateOf(0f) }
            var offsetY by remember { mutableStateOf(0f) }

            val screenDensity = LocalConfiguration.current.densityDpi / 160f

            val minOffset = (LocalConfiguration.current.screenHeightDp.coerceAtMost(
                LocalConfiguration.current.screenWidthDp
            ) - 130) * screenDensity * -1

            val maxOffset = 130 * screenDensity

            FloatingActionButton(
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
                    .padding(bottom = 56.dp, end = 56.dp)
                    .offset {
                        IntOffset(
                            offsetX.roundToInt(),
                            offsetY.roundToInt()
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX = (offsetX + dragAmount.x).coerceIn(minOffset, maxOffset)
                            offsetY = (offsetY + dragAmount.y).coerceIn(minOffset, maxOffset)
                        }
                    },
                onClick = { viewModel.showBottomSheet() },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Menu,
                    stringResource(R.string.floating_action_button_content_description))

            }
        }
    }

}