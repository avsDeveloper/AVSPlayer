package com.avs.avsplayer

import com.avs.avsplayer.domain.model.MediaListItem

data class PlayerState(
    val uiState: PlayerUiState = PlayerUiState.NONE,
    val showBottomSheet: Boolean = false,
    val showInfoScreen: Boolean = false,
    val showProgress: Boolean = false,

    val selectedMedia: List<MediaListItem> = emptyList(),
    val isFinished: Boolean = false,
    val currentPosition: Int = 0
)

enum class PlayerUiState {
    INFO_SCREEN,
    PICKER,
    PLAYER,
    EMPTY_PLAYER,
    NONE
}