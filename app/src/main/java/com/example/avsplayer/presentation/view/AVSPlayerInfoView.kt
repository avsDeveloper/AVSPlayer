package com.example.avsplayer.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.avsplayer.R
import com.example.avsplayer.presentation.MainActivityViewModel

@Composable
fun AVSPlayerInfoView(
    viewModel: MainActivityViewModel
) {
    ConstraintLayout(
        modifier = Modifier
            .background(Color.Black),
    ) {
        Column {
            Text(text = "aaarwb")
            Button(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp, bottom = 8.dp, end = 4.dp),
                onClick = {
                    // not sure it should be done like this ))
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
        }
    }

}