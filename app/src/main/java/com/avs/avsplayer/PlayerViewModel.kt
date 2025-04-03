package com.avs.avsplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avs.avsplayer.data.MediaListItem
import com.avs.avsplayer.data.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: DataStoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlayerUIState>(PlayerUIState.JustCreated)
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
        _uiState.value = PlayerUIState.InfoScreen
    }
    fun setOpenPicker() {
        _uiState.value = PlayerUIState.OpenPicker
    }
    fun setRunPlayer() {
        _uiState.value = PlayerUIState.RunPlayer
    }

    fun setPrepareRunPlayer() {
        _uiState.value = PlayerUIState.PrepareRunPlayer
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

