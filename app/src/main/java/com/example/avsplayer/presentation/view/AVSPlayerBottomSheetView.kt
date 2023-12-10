package com.example.avsplayer.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.avsplayer.R
import com.example.avsplayer.presentation.MainActivityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AVSPlayerBottomSheetView(
    onDismiss: () -> Unit,
    viewModel: MainActivityViewModel
) {
    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        shape = RoundedCornerShape(10.dp),
        dragHandle = null
    ) {

        Column (
            modifier = Modifier
                .padding(bottom = 32.dp)
        ) {

            Image (
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(16.dp)
                    .clickable {
                        onDismiss()
                    },
                painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                contentDescription = null
            )


            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .clickable {
                    onDismiss() // not sure it should be done like this ))
                    viewModel.setInitialized()
                }
                .border(
                    1.dp,
                    Color.Black,
                    RoundedCornerShape(10.dp)
                )
            ) {
                Image (
                    painter = painterResource(id = R.drawable.icon_search),
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp)
                )
                Text (
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp),
                    text = "Stop and choose another file",
                )
            }


            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .clickable {
                    onDismiss() // not sure it should be done like this ))
                    viewModel.setFinished()
                }
                .border(
                    1.dp,
                    Color.Black,
                    RoundedCornerShape(10.dp)
                )
            ) {
                Image (
                    painter = painterResource(id = R.drawable.icon_stop_and_close),
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp)
                )
                Text (
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp),
                    text = "Stop and close player"
                )
            }

        }
    }
}