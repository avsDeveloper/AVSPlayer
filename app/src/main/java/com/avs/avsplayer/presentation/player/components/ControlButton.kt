package com.avs.avsplayer.presentation.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avs.avsplayer.R
import com.avs.avsplayer.ui.AVSPlayerTheme
import com.avs.avsplayer.ui.Cream
import com.avs.avsplayer.ui.PlayerBlack

@Composable
fun ControlButton(
    text: String,
    iconRes: Int,
    filled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    val contentColor = if (filled) PlayerBlack else Color.White.copy(alpha = .75f)

    Row(
        modifier = modifier
            .height(56.dp)
            .clip(shape)
            .background(if (filled) Cream else Color.Transparent)
            .border(
                1.dp,
                if (filled) Color.Transparent else Color.White.copy(alpha = .14f),
                shape
            )
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (filled) FontWeight.SemiBold else FontWeight.Medium,
            color = contentColor
        )
    }
}

@Preview(name = "Control buttons", showBackground = true, backgroundColor = 0xFF131316)
@Composable
private fun ControlButtonPreview() {
    AVSPlayerTheme {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            ControlButton(
                text = "Open files",
                iconRes = R.drawable.ic_search,
                filled = true,
                onClick = {},
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            ControlButton(
                text = "Exit",
                iconRes = R.drawable.ic_close,
                filled = false,
                onClick = {},
                modifier = Modifier.weight(1f)
            )
        }
    }
}
