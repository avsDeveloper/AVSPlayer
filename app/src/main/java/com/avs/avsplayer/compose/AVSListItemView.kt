package com.avs.avsplayer.compose

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avs.avsplayer.viewmodels.MainActivityViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AVSListItemView(
    viewModel: MainActivityViewModel? = null,
    title: String,
    description: String,
    itemPos: Int,
    onClickCall: () -> Unit
) {

    val currentPosition = viewModel?.currentItemNum?.collectAsStateWithLifecycle()

    val surfaceModifier = if (itemPos == currentPosition?.value) {
        Modifier
            .fillMaxWidth()
            .clickable {
                onClickCall()
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
                onClickCall()
            }
            .height(64.dp)
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
    }

    val textColor = if (itemPos == currentPosition?.value) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = surfaceModifier.padding(start = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AVSMediaItemImage(
            if (description.toString().contains("video", true)) MediaType.VIDEO
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
                    .basicMarquee(
                        animationMode = if (itemPos == currentPosition?.value) MarqueeAnimationMode.Immediately
                        else MarqueeAnimationMode.WhileFocused,
                        delayMillis = 0,
                    ),
                text = title,
                maxLines = 1,
                style = MaterialTheme.typography.labelLarge,
                color = textColor,
            )

            Text(
                text = description,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee(
                        animationMode = if (itemPos == currentPosition?.value) MarqueeAnimationMode.Immediately
                        else MarqueeAnimationMode.WhileFocused,
                        delayMillis = 0,
                    ),
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
    }
}


