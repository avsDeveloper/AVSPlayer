package com.example.avsplayer.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivityViewModel: ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.Initiated)
    val uiState = _uiState.asStateFlow()

    private val _isBottomSheetShown = MutableStateFlow<Boolean>(false)
    val isBottomSheetShown = _isBottomSheetShown.asStateFlow()

    private val _isFinished = MutableStateFlow<Boolean>(false)
    val isFinished = _isFinished.asStateFlow()

    fun setInitialized() {
        _uiState.value = UIState.Initiated
    }
    fun setReady() {
        _uiState.value = UIState.Ready
    }

    fun setSelected() {
        _uiState.value = UIState.Selected
    }

    fun showBottomSheet() {
        _isBottomSheetShown.value = true
    }

    fun hideBottomSheet() {
        _isBottomSheetShown.value = false
    }

    fun setFinished() {
        _isFinished.value = true
    }

}

sealed class UIState {

    // Initial state
    object Initiated: UIState()

    // Media file selected, show Progress bar
    object Selected: UIState()

    // Media file ready, show Player
    object Ready: UIState()
}