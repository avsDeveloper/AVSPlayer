package com.avs.avsplayer

import com.avs.avsplayer.domain.model.MediaListItem

sealed interface PlayerAction {
    object CheckFirstScreen : PlayerAction
    object SetFirstScreenShown : PlayerAction
    object OpenPicker : PlayerAction
    data class PrepareRunPlayer(val items: List<MediaListItem>) : PlayerAction
    object RunPlayer : PlayerAction
    object ShowBottomSheet : PlayerAction
    object HideBottomSheet : PlayerAction
    object Finish : PlayerAction
    data class SetCurrentItemNum(val itemNum: Int) : PlayerAction
}
