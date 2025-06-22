package com.avs.avsplayer.presentation.player.components

import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment

@Composable
fun AVSListItemView(
    title: String,
    description: String,
    itemPos: Int,
    currentPosition: Int,
    onClick: () -> Unit
) {
    val surfaceModifier = if (itemPos == currentPosition) {
        Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .height(64.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            )
    } else {
        Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .height(64.dp)
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
    }

    val textColor = if (itemPos == currentPosition) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = surfaceModifier.padding(start = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MediaItemImage(
            if (description.contains("video", true)) MediaType.VIDEO
            else MediaType.AUDIO
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .itemMarquee(itemPos, currentPosition),
                text = title,
                maxLines = 1,
                style = MaterialTheme.typography.labelLarge,
                color = textColor,
            )

            Text(
                text = description,
                maxLines = 1,
                modifier = Modifier.itemMarquee(itemPos, currentPosition),
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
    }
}

private fun Modifier.itemMarquee(itemPos: Int, currentPosition: Int) =
    basicMarquee(
        animationMode = if (itemPos == currentPosition)
            MarqueeAnimationMode.Immediately
        else MarqueeAnimationMode.WhileFocused,
        repeatDelayMillis = 0,
    )


