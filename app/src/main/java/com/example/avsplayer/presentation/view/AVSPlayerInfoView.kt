package com.example.avsplayer.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
            .fillMaxSize(),
    ) {
        Column {

            Text(
                text = stringResource(R.string.info_title),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = stringResource(R.string.info_text),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = stringResource(R.string.info_text2),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = stringResource(R.string.info_text3),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = stringResource(R.string.info_text4),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(1f),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.bodyLarge
            )


            Text(
                text = stringResource(R.string.info_text5),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.bodyMedium
            )



            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 8.dp, bottom = 64.dp, end = 4.dp),
                onClick = {
                    viewModel?.setFirstScreenShown(false)
                    viewModel?.setInitialized()
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    containerColor = MaterialTheme.colorScheme.primary
                )

            ) {

                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = "Close and start player",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview (showSystemUi = true, showBackground = true)
@Composable
fun AVSPlayerInfoViewPreview() {
    AVSPlayerInfoView()
}