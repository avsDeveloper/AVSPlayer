package com.avs.avsplayer.presentation

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import com.avs.avsplayer.PlayerViewModel
import com.avs.avsplayer.ui.AVSPlayerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerControlBottomSheet(
    onDismiss: () -> Unit,
    viewModel: PlayerViewModel? = null,
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
        contentAlignment = Alignment.BottomCenter
    ) {


        ModalBottomSheet(
            modifier = Modifier
                .layoutId("bottomsheet")
                .width(sheetWidth)
                .align(Alignment.Center),
            onDismissRequest = { onDismiss() },
            sheetState = modalBottomSheetState,
            shape = RoundedCornerShape(10.dp),
            dragHandle = null
        ) {

            Column(
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {

                Image(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(16.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable {
                            onDismiss()
                        },
                    painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )

                LazyColumn(
                    modifier = Modifier
                        .heightIn(0.dp, 196.dp)
                        .padding(start = 4.dp, end = 4.dp)
                ) {
                    player?.mediaItemCount?.let {
                        for (i in 0 until player.mediaItemCount) {
                            item {
                                AVSListItemView(
                                    viewModel = viewModel,
                                    title = player.getMediaItemAt(i).mediaMetadata.title.toString(),
                                    description = player.getMediaItemAt(i).mediaMetadata.description.toString(),
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
                ) {

                    val buttonModifier =  Modifier
                        .weight(1f)
                        .padding(start = 4.dp, top = 8.dp, bottom = 8.dp, end = 4.dp)

                    ControlButton(
                        text = "Select",
                        icon = Icons.Default.Search,
                        modifier = buttonModifier,
                        onClick = {
                            onDismiss()
                            viewModel?.setOpenPicker()
                        }
                    )

                    ControlButton(
                        text = "Close",
                        icon = Icons.Default.Close,
                        modifier = buttonModifier,
                        onClick = {
                            onDismiss()
                            viewModel?.setFinished()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ControlButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier.padding(start = 4.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = text,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.width(16.dp))
    }
}


@Preview(
    name = "big",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showSystemUi = true,
    showBackground = true,
    device = "id:pixel_xl"
)
@Composable
private fun PlayerBottomSheetViewPreview() {
    AVSPlayerTheme {
        PlayerControlBottomSheet(
            onDismiss = {},
            viewModel = null,
            player = null
        )
    }
}

