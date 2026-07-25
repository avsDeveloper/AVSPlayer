package com.avs.avsplayer.presentation.player.components

import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avs.avsplayer.R
import com.avs.avsplayer.ui.AVSPlayerTheme
import com.avs.avsplayer.ui.AccentAmber

@Composable
fun AVSListItemView(
    title: String,
    description: String,
    itemPos: Int,
    currentPosition: Int,
    onClick: () -> Unit
) {
    val active = itemPos == currentPosition
    val shape = RoundedCornerShape(18.dp)
    val isVideo = description.contains("video", ignoreCase = true)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(shape)
            .background(if (active) AccentAmber.copy(alpha = .1f) else Color.Transparent)
            .border(
                1.dp,
                if (active) AccentAmber.copy(alpha = .26f) else Color.Transparent,
                shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(
                    if (active) AccentAmber.copy(alpha = .16f)
                    else Color.White.copy(alpha = .06f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    if (isVideo) R.drawable.ic_video_clip else R.drawable.ic_music_note
                ),
                contentDescription = null,
                tint = if (active) AccentAmber else Color.White.copy(alpha = .65f),
                modifier = Modifier.size(22.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                color = if (active) Color.White else Color.White.copy(alpha = .85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.itemMarquee(active)
            )
            Text(
                text = description,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = if (active) .42f else .36f),
                maxLines = 1,
                modifier = Modifier
                    .padding(top = 3.dp)
                    .itemMarquee(active)
            )
        }

        if (active) {
            EqBars(
                playing = true,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

private fun Modifier.itemMarquee(active: Boolean) =
    basicMarquee(
        animationMode = if (active) MarqueeAnimationMode.Immediately
        else MarqueeAnimationMode.WhileFocused,
        repeatDelayMillis = 0,
    )

@Preview(name = "Playlist items", showBackground = true, backgroundColor = 0xFF131316)
@Composable
private fun AVSListItemViewPreview() {
    AVSPlayerTheme {
        Column {
            AVSListItemView(
                title = "Now Playing Track",
                description = "AUDIO · MP3",
                itemPos = 0,
                currentPosition = 0,
                onClick = {}
            )
            AVSListItemView(
                title = "Some Other Video File",
                description = "VIDEO · MP4",
                itemPos = 1,
                currentPosition = 0,
                onClick = {}
            )
        }
    }
}
