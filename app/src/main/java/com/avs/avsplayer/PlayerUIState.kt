package com.avs.avsplayer

sealed class PlayerUIState {

    // show progress bar until we check if the app is opened first time,
    // set automatically only when the app is just started
    object JustCreated: PlayerUIState()

    // the app is opened first time, so PlayerInfoView screen needs to be shown
    object InfoScreen: PlayerUIState()

    // everything ready, open media picker
    object OpenPicker: PlayerUIState()

    // Media files selected, show Progress bar until files are opened
    object PrepareRunPlayer: PlayerUIState()

    // Media files ready, show and run player
    object RunPlayer: PlayerUIState()
}