package com.example.avsplayer.data

import android.net.Uri
import androidx.media3.common.MediaItem

data class MediaListItem(
    val uri: Uri,
    val displayName: String?,
    val mimeType: String?,
    val size: Long?
)