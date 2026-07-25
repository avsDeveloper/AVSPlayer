package com.avs.avsplayer.presentation.player.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.avs.avsplayer.ui.AVSPlayerTheme

@Composable
fun InfoText(
    text: String,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge
) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Left,
        style = style
    )
}

@Preview(name = "Info text", showBackground = true)
@Composable
private fun InfoTextPreview() {
    AVSPlayerTheme {
        InfoText(text = "This is some sample informational text shown to the user.")
    }
}