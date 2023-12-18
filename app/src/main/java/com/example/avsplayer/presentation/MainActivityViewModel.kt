package com.example.avsplayer.presentation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.avsplayer.data.DataStoreRepository
import com.example.avsplayer.data.MediaListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val repository: DataStoreRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.InfoScreen)
    val uiState = _uiState

    private val _currentItemNum = MutableStateFlow(0)
    val currentItemNum = _currentItemNum.asStateFlow()

    private val _isBottomSheetShown = MutableStateFlow(false)
    val isBottomSheetShown = _isBottomSheetShown.asStateFlow()

    private val _isFinished = MutableStateFlow(false)
    val isFinished = _isFinished.asStateFlow()

    private val _mediaListItemList = MutableStateFlow<MutableList<MediaListItem>>(mutableListOf())
    val mediaListItemList = _mediaListItemList.asStateFlow()

    init {
        viewModelScope.launch {
            repository.isShouldOpenFirstScreenFlow.collect { shouldOpen ->
                _uiState.value = if (shouldOpen) UIState.InfoScreen else UIState.Initiated
            }
        }
    }

    fun setFirstScreenShown(isShown: Boolean) {
        viewModelScope.launch {
            repository.updateFirstScreenPref(isShown)
        }
    }

    fun setInfoScreen() {
        _uiState.value = UIState.InfoScreen
    }

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

    fun setCurrentItemNum(itemNum: Int) {
        _currentItemNum.value = itemNum
    }

    fun addMediaListItem(item: MediaListItem) {
        _mediaListItemList.value.add(item)
    }

    fun clearMediaListItem() {
        _mediaListItemList.value.clear()
    }

    fun addMediaListItems(items: List<MediaListItem>) {
        _mediaListItemList.value.addAll(items)
    }

}

sealed class UIState {

    object InfoScreen: UIState()

    // Initial state
    object Initiated: UIState()

    // Media file selected, show Progress bar
    object Selected: UIState()

    // Media file ready, show Player
    object Ready: UIState()
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