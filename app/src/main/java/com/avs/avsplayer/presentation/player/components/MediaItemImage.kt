package com.avs.avsplayer.presentation.player.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.avs.avsplayer.R

@Composable
fun MediaItemImage(
    mediaType: MediaType,
    modifier: Modifier = Modifier,
    size: Int = 36,
    contentDescription: String? = null
) {
    val iconRes = when (mediaType) {
        MediaType.AUDIO -> R.drawable.icon_audio_list_transp
        MediaType.VIDEO -> R.drawable.icon_video_list_transp
    }

    Image(
        painter = painterResource(id = iconRes),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
    )
}

enum class MediaType {
    VIDEO, AUDIO
}