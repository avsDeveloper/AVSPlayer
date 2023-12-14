package com.example.avsplayer.presentation.view

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.media3.session.MediaController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.ARTWORK_DISPLAY_MODE_FIT
import com.example.avsplayer.R
import com.example.avsplayer.presentation.MainActivityViewModel
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun AVSPlayerView(
    player: MediaController?,
    showBottomSheet: Boolean,
    viewModel: MainActivityViewModel
) {
    val context = LocalContext.current
    val screenOrientation = LocalConfiguration.current.orientation

    ConstraintLayout (
        modifier = Modifier
            .background(Color.Black)
    ) {

        if (showBottomSheet) {
            AVSPlayerBottomSheetView(
                onDismiss = { viewModel.hideBottomSheet() },
                viewModel = viewModel
            )
        }

        Column () {

            val modifier = if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) Modifier
                .fillMaxWidth(1f)
                .aspectRatio(16f / 9f)
            else Modifier.fillMaxWidth(1f)

            DisposableEffect(
                    AndroidView(
                        modifier = modifier
                            .padding(bottom = 16.dp),
                        factory = {
                            PlayerView(
                                context,
                            ).apply {
                                setPlayer(player)
                                defaultArtwork = context.getDrawable(R.drawable.video_off_outline)
                                artworkDisplayMode = ARTWORK_DISPLAY_MODE_FIT
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

            // Media data views
            if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
                Column  {
                    LazyColumn(
                        modifier = Modifier
                    ) {

                        player?.mediaItemCount?.let {
                            for (i in 0 until player.mediaItemCount) {
                                item {
                                    AVSListItemView(
                                        viewModel = viewModel,
                                        title = player.getMediaItemAt(i).mediaMetadata.title.toString(),
                                        description = player.getMediaItemAt(i).mediaMetadata.description.toString(),
                                        uri = player.getMediaItemAt(i).mediaMetadata.artworkUri,
                                        itemPos = i
                                    ) {
                                        if (i != player.currentMediaItemIndex) player.seekTo(i, 0)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        Box(
            modifier = Modifier
            .fillMaxSize()
        ) {

            var offsetX by remember { mutableStateOf(0f) }
            var offsetY by remember { mutableStateOf(0f) }

            val screenDensity = LocalConfiguration.current.densityDpi / 160f

            val minOffset = LocalConfiguration.current.screenHeightDp.coerceAtMost(
                LocalConfiguration.current.screenWidthDp
            ) * screenDensity * -1

            val maxOffset = 90 * screenDensity

            FloatingActionButton(
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
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
                containerColor = colorResource(id = R.color.colorPrimaryDark),
                contentColor = Color.White,
                shape = CircleShape,
            ) {
                Icon(Icons.Filled.Close, "Floating action button.")

            }
        }
    }

}