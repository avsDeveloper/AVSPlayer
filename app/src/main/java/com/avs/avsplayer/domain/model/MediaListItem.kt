package com.avs.avsplayer.domain.model

data class MediaListItem(
    val uri: String,
    val displayName: String?,
    val mimeType: String?,
    val size: Long?
)