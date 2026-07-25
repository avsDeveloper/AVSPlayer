package com.avs.avsplayer

import com.avs.avsplayer.domain.model.MediaListItem

data class PlayerState(
    val uiState: PlayerUiState = PlayerUiState.NONE,
    val showBottomSheet: Boolean = false,
    val selectedMedia: List<MediaListItem> = emptyList(),
    val currentPosition: Int = 0
)

enum class PlayerUiState {
    INFO_SCREEN,
    PICKER,
    PLAYER,
    EMPTY_PLAYER,
    NONE
}