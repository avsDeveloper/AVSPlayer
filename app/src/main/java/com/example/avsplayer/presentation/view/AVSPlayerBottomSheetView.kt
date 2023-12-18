package com.example.avsplayer.presentation.view

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import com.example.avsplayer.R
import com.example.avsplayer.presentation.MainActivityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AVSPlayerBottomSheetView(
    onDismiss: () -> Unit,
    viewModel: MainActivityViewModel,
    player: MediaController?
) {

    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    var sheetWidth by remember {
        mutableStateOf (96.dp)
    }

    val configuration = LocalConfiguration.current
    sheetWidth = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            configuration.screenWidthDp.dp
        } else {
            configuration.screenHeightDp.dp
        }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ModalBottomSheet(
            modifier = Modifier
                .layoutId("bottomsheet")
                .width(sheetWidth),
            onDismissRequest = { onDismiss() },
            sheetState = modalBottomSheetState,
            shape = RoundedCornerShape(10.dp),
            dragHandle = null,
            containerColor = Color.Black
        ) {

            Column(
                modifier = Modifier
                    .padding(bottom = 32.dp)
            ) {

                Image(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(16.dp)
                        .clickable {
                            onDismiss()
                        },
                    painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = null
                )

                LazyColumn(
                    modifier = Modifier
                        .heightIn(0.dp, 196.dp)
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

                Row (modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(colorResource(id = R.color.colorBlack))
                ) {

                    Button (
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 8.dp, bottom = 8.dp, end = 4.dp),
                        onClick = {
                            onDismiss() // not sure it should be done like this ))
                            viewModel.setInitialized()
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = colorResource(id = R.color.colorPrimaryDark),
                            containerColor = colorResource(id = R.color.colorPrimaryDark)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {

                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp),
                            tint = Color.White
                        )
                        Text(
                            text = "Select files",
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }

                    Button (
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 8.dp, bottom = 8.dp, start = 4.dp),
                        onClick = {
                            onDismiss() // not sure it should be done like this ))
                            viewModel.setFinished()
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = colorResource(id = R.color.colorPrimaryDark),
                            containerColor = colorResource(id = R.color.colorPrimaryDark)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp),
                            tint = Color.White
                        )
                        Text(
                            modifier = Modifier.padding(end = 8.dp),
                            text = "Close player",
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }


}