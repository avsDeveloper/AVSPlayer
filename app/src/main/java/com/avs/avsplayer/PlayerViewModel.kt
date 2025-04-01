package com.avs.avsplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.avs.avsplayer.data.repositories.DataStoreRepository
import com.avs.avsplayer.data.MediaListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val repository: DataStoreRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.JustCreated)
    val uiState = _uiState

    private val _isShowFirstScreen = MutableStateFlow(true)
    val isShowFirstScreen = _isShowFirstScreen.asStateFlow()

    private val _currentItemNum = MutableStateFlow(0)
    val currentItemNum = _currentItemNum.asStateFlow()

    private val _isBottomSheetShown = MutableStateFlow(false)
    val isBottomSheetShown = _isBottomSheetShown.asStateFlow()

    private val _isFinished = MutableStateFlow(false)
    val isFinished = _isFinished.asStateFlow()

    private val _mediaListItemList = MutableStateFlow<MutableList<MediaListItem>>(mutableListOf())
    val mediaListItemList = _mediaListItemList.asStateFlow()

    init  {
        viewModelScope.launch {
            repository.isShouldOpenFirstScreenFlow.collect { shouldOpen ->
                viewModelScope.launch {
                    _isShowFirstScreen.value = shouldOpen
                }
            }
        }
    }

    fun setFirstScreenShown(isShown: Boolean) {
        viewModelScope.launch {
            repository.updateFirstScreenPref(isShown)
        }
    }
    fun setShowInfoScreen() {
        _uiState.value = UIState.InfoScreen
    }
    fun setOpenPicker() {
        _uiState.value = UIState.OpenPicker
    }
    fun setRunPlayer() {
        _uiState.value = UIState.RunPlayer
    }

    fun setPrepareRunPlayer() {
        _uiState.value = UIState.PrepareRunPlayer
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

    fun setCurrentItemNum(itemNum: Int) {
        _currentItemNum.value = itemNum
    }

    fun addMediaListItem(item: MediaListItem) {
        _mediaListItemList.value.add(item)
    }

    fun clearMediaListItem() {
        _mediaListItemList.value.clear()
    }

}

sealed class UIState {

    // show progress bar until we check if the app is opened first time,
    // set automatically only when the app is just started
    object JustCreated: UIState()

    // the app is opened first time, so PlayerInfoView screen needs to be shown
    object InfoScreen: UIState()

    // everything ready, open media picker
    object OpenPicker: UIState()

    // Media files selected, show Progress bar until files are opened
    object PrepareRunPlayer: UIState()

    // Media files ready, show and run player
    object RunPlayer: UIState()
}


class MainActivityViewModelFactory(private val repository: DataStoreRepository)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainActivityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}