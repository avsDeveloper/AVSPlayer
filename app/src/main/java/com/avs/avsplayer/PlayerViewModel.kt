package com.avs.avsplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avs.avsplayer.domain.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: DataStoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<PlayerEffect>()
    val effect: SharedFlow<PlayerEffect> = _effect.asSharedFlow()

    init {
        dispatch(PlayerAction.CheckFirstScreen)
    }

    fun dispatch(action: PlayerAction) {
        when (action) {
            is PlayerAction.CheckFirstScreen -> {
                viewModelScope.launch {
                    repository.isShouldOpenFirstScreenFlow.collect { shouldOpen ->
                            _state.update { it.copy(
                                showInfoScreen = shouldOpen,
                                uiState = if (shouldOpen) PlayerUiState.INFO_SCREEN else PlayerUiState.PICKER
                            ) }
                    }
                }
            }
            is PlayerAction.SetFirstScreenShown -> {
                viewModelScope.launch {
                    repository.updateFirstScreenPref(shouldOpenFirst = action.isShown.not())
                }
                _state.update { it.copy(
                    showInfoScreen = false
                ) }
            }
            is PlayerAction.ShowInfoScreen -> {
                if (state.value.showInfoScreen) {
                    _state.update { it.copy(
                        showInfoScreen = false,
                        uiState = PlayerUiState.INFO_SCREEN
                    ) }
                }
            }
            is PlayerAction.OpenPicker -> {
                _state.update { it.copy(
                    uiState = PlayerUiState.PICKER
                ) }
            }
            is PlayerAction.PrepareRunPlayer -> {
                _state.update { it.copy(
                    selectedMedia = emptyList(),
                    uiState = PlayerUiState.EMPTY_PLAYER
                ) }
                viewModelScope.launch {
                    _effect.emit(PlayerEffect.PreparePlayer)
                }
            }
            is PlayerAction.RunPlayer -> {
                _state.update { it.copy(
                    uiState = PlayerUiState.PLAYER
                ) }
            }
            is PlayerAction.ShowBottomSheet -> {
                _state.update { it.copy(
                    showBottomSheet = true
                ) }
            }
            is PlayerAction.HideBottomSheet -> {
                _state.update { it.copy(
                    showBottomSheet = false
                ) }
            }
            is PlayerAction.Finish -> {
                _state.update { it.copy(isFinished = true) }
                viewModelScope.launch {
                    _effect.emit(PlayerEffect.StopPlayback)
                    _effect.emit(PlayerEffect.Finish)
                }
            }
            is PlayerAction.SetCurrentItemNum -> {
                _state.update { it.copy(
                    currentPosition = action.itemNum
                ) }
            }
            is PlayerAction.AddMediaListItem -> {
                _state.update { it.copy(
                    selectedMedia = it.selectedMedia + action.item
                ) }
            }
            is PlayerAction.ClearMediaList -> {
                _state.update { it.copy(
                    selectedMedia = emptyList()
                ) }
            }
        }
    }
}














