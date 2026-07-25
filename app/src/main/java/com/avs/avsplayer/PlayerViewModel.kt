package com.avs.avsplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.avs.avsplayer.data.repository.DataStoreRepositoryImpl
import com.avs.avsplayer.domain.repository.DataStoreRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val repository: DataStoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    private val _effect = Channel<PlayerEffect>(Channel.UNLIMITED)
    val effect: Flow<PlayerEffect> = _effect.receiveAsFlow()

    init {
        dispatch(PlayerAction.CheckFirstScreen)
    }

    fun dispatch(action: PlayerAction) {
        when (action) {
            is PlayerAction.CheckFirstScreen -> {
                viewModelScope.launch {
                    val shouldOpen = repository.isShouldOpenFirstScreenFlow.first()
                    if (state.value.uiState == PlayerUiState.NONE) {
                        if (shouldOpen) {
                            _state.update { it.copy(uiState = PlayerUiState.INFO_SCREEN) }
                        } else {
                            dispatch(PlayerAction.OpenPicker)
                        }
                    }
                }
            }
            is PlayerAction.SetFirstScreenShown -> {
                viewModelScope.launch {
                    repository.updateFirstScreenPref(shouldOpenFirst = false)
                }
                dispatch(PlayerAction.OpenPicker)
            }
            is PlayerAction.OpenPicker -> {
                _state.update { it.copy(
                    uiState = PlayerUiState.PICKER,
                    showBottomSheet = false
                ) }
                _effect.trySend(PlayerEffect.StopPlayback)
                _effect.trySend(PlayerEffect.OpenPicker)
            }
            is PlayerAction.PrepareRunPlayer -> {
                _state.update { it.copy(
                    selectedMedia = action.items,
                    uiState = PlayerUiState.EMPTY_PLAYER
                ) }
                _effect.trySend(PlayerEffect.PreparePlayer)
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
                _effect.trySend(PlayerEffect.StopPlayback)
                _effect.trySend(PlayerEffect.Finish)
            }
            is PlayerAction.SetCurrentItemNum -> {
                _state.update { it.copy(
                    currentPosition = action.itemNum
                ) }
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                PlayerViewModel(DataStoreRepositoryImpl(checkNotNull(this[APPLICATION_KEY])))
            }
        }
    }
}
