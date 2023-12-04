package com.example.avsplayer.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(): ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.Initiated)
    val uiState = _uiState.asStateFlow()

    fun setReady() {
        _uiState.value = UIState.Ready
    }

    fun setSelected() {
        _uiState.value = UIState.Selected
    }

}

sealed class UIState {
    object Selected: UIState()
    object Initiated: UIState()
    object Ready: UIState()
}