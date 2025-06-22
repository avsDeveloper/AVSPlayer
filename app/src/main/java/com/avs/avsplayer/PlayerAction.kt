package com.avs.avsplayer

import com.avs.avsplayer.domain.model.MediaListItem

sealed interface PlayerAction {
    object CheckFirstScreen : PlayerAction
    data class SetFirstScreenShown(val isShown: Boolean) : PlayerAction
    object ShowInfoScreen : PlayerAction
    object OpenPicker : PlayerAction
    object PrepareRunPlayer : PlayerAction
    object RunPlayer : PlayerAction
    object ShowBottomSheet : PlayerAction
    object HideBottomSheet : PlayerAction
    object Finish : PlayerAction
    data class SetCurrentItemNum(val itemNum: Int) : PlayerAction
    data class AddMediaListItem(val item: MediaListItem) : PlayerAction
    object ClearMediaList : PlayerAction
}