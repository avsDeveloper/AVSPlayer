package com.example.avsplayer.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.avsplayer.R

@Composable
fun AVSMediaItemImage(
    mediaType: MediaType
) {
    Image(
        painterResource(id = if (mediaType == MediaType.AUDIO) R.drawable.icon_audio_list_transp else R.drawable.icon_video_list_transp),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
    )
}

enum class MediaType {
    VIDEO, AUDIO
}