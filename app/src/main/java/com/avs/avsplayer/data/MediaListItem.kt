package com.avs.avsplayer.data

import android.net.Uri

data class MediaListItem(
    val uri: Uri,
    val displayName: String?,
    val mimeType: String?,
    val size: Long?
)