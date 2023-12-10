package com.example.avsplayer.presentation.view

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.media3.session.MediaController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.ARTWORK_DISPLAY_MODE_FIT
import com.example.avsplayer.R
import com.example.avsplayer.presentation.MainActivityViewModel

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
            DisposableEffect(
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth(1f),
                    factory = {
                        PlayerView(
                            context
                        ).apply {
                            setPlayer(player)
                            defaultArtwork = context.getDrawable(R.drawable.baseline_music_note_24)
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
                Column (
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())

                ) {
                    Text(
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp, bottom = 8.dp, start = 8.dp, end = 8.dp),
                            style = MaterialTheme.typography.titleLarge,
                            text = player?.mediaMetadata?.title.toString(),
                            textAlign = TextAlign.Start
                        )
                }
            }
        }
    }
}