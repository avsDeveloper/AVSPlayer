package com.example.avsplayer.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.avsplayer.R
import com.example.avsplayer.presentation.MainActivityViewModel

@Composable
fun AVSPlayerInfoView(
    viewModel: MainActivityViewModel? = null
) {
    ConstraintLayout(
        modifier = Modifier
            .background(colorResource(id = R.color.colorBlack))
            .fillMaxSize(),
    ) {
        Column {

            Text(
                text = stringResource(R.string.info_title),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                color = colorResource(id = R.color.colorWhite),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = stringResource(R.string.info_text),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp),
                color = colorResource(id = R.color.colorWhite),
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = stringResource(R.string.info_only_once_text),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                color = colorResource(id = R.color.colorWhite),
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.bodyLarge
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp, end = 4.dp),
                onClick = {
                    viewModel?.setFirstScreenShown(false)
                    viewModel?.setInitialized()
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

@Preview (showSystemUi = true)
@Composable
fun AVSPlayerInfoViewPreview() {
    AVSPlayerInfoView()
}