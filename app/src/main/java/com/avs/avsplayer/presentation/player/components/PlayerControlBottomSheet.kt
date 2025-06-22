package com.avs.avsplayer.presentation.player.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
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
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sheetWidth = LocalConfiguration.current.let { config ->
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            config.screenWidthDp.dp
        } else {
            config.screenHeightDp.dp
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = modalBottomSheetState,
        dragHandle = { /* Custom drag handle */ },
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = Modifier.width(sheetWidth)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Media List",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }

            // Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 196.dp)
                    .padding(horizontal = 8.dp)
            ) {
                player?.mediaItemCount?.let { count ->
                    items(count) { index ->
                        AVSListItemView(
                            viewModel = viewModel,
                            title = player.getMediaItemAt(index).mediaMetadata.title.toString(),
                            description = player.getMediaItemAt(index).mediaMetadata.description.toString(),
                            itemPos = index
                        ) {
                            if (index != player.currentMediaItemIndex) player.seekTo(index, 0)
                        }
                    }
                }
            }

            // Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ControlButton(
                    text = "Select",
                    icon = Icons.Default.Search,
                    onClick = {
                        onDismiss()
                        viewModel?.setOpenPicker()
                    },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                ControlButton(
                    text = "Close",
                    icon = Icons.Default.Close,
                    onClick = {
                        onDismiss()
                        viewModel?.setFinished()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
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

