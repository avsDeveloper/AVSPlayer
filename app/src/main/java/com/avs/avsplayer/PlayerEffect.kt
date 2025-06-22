package com.avs.avsplayer

sealed interface PlayerEffect {
    object OpenPicker : PlayerEffect
    object StopPlayback : PlayerEffect
    object Finish : PlayerEffect
    object PreparePlayer : PlayerEffect
}