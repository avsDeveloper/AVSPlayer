package com.avs.avsplayer.presentation.playerinfo

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.avs.avsplayer.R
import com.avs.avsplayer.PlayerViewModel
import com.avs.avsplayer.presentation.player.components.InfoText
import com.avs.avsplayer.presentation.playerinfo.components.ActionButton
import com.avs.avsplayer.ui.AVSPlayerTheme

@Composable
fun PlayerInfoScreen(viewModel: PlayerViewModel? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.info_title),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )

        InfoText(text = stringResource(R.string.info_text))
        InfoText(text = stringResource(R.string.info_text2))
        InfoText(text = stringResource(R.string.info_text3))
        InfoText(
            text = stringResource(R.string.info_text4),
            modifier = Modifier.weight(1f)
        )
        InfoText(
            text = stringResource(R.string.info_text5),
            style = MaterialTheme.typography.bodyMedium
        )

        ActionButton(
            text = "Close and start player",
            onClick = {
                viewModel?.setFirstScreenShown(false)
                viewModel?.setOpenPicker()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        )
    }
}

@Preview (name = "Light mode", showSystemUi = true, showBackground = true)
@Preview (name = "Dark mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showSystemUi = true, showBackground = true)
@Composable
private fun PlayerInfoViewPreview() {

    AVSPlayerTheme {
        PlayerInfoScreen()
    }

}
