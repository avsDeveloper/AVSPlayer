package com.avs.avsplayer

import com.avs.avsplayer.domain.model.MediaListItem

data class PlayerState(
    val uiState: PlayerUiState = PlayerUiState.SHOW_PROGRESS_BAR,
    val selectedMedia: List<MediaListItem> = emptyList(),
    val isFinished: Boolean = false,
    val currentPosition: Int = 0,
    val isShowFirstScreen: Boolean = false
)

enum class PlayerUiState {
    SHOW_INFO_SCREEN,
    SHOW_PROGRESS_BAR,
    SHOW_BOTTOM_SHEET,
    SHOW_PICKER,
    RUN_PLAYER
}