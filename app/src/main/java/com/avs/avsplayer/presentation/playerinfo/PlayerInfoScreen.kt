package com.avs.avsplayer.presentation.playerinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.avs.avsplayer.R
import com.avs.avsplayer.presentation.player.components.InfoText
import com.avs.avsplayer.presentation.playerinfo.components.ActionButton
import com.avs.avsplayer.ui.AVSPlayerTheme

@Composable
fun PlayerInfoScreen(
    onCloseClicked: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val privacyPolicyUrl = stringResource(R.string.privacy_policy_url)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.displayCutout)
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

        TextButton(
            onClick = { uriHandler.openUri(privacyPolicyUrl) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.privacy_policy))
        }

        ActionButton(
            text = stringResource(R.string.info_button),
            onClick = onCloseClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        )
    }
}

@Preview(name = "Player info", widthDp = 372, heightDp = 692, showBackground = true)
@Composable
private fun PlayerInfoScreenPreview() {
    AVSPlayerTheme {
        PlayerInfoScreen(onCloseClicked = {})
    }
}
