package com.avs.avsplayer

import com.avs.avsplayer.domain.model.MediaListItem
import com.avs.avsplayer.domain.repository.DataStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModelTest {

    private class FakeRepository(shouldOpenFirst: Boolean) : DataStoreRepository {
        val pref = MutableStateFlow(shouldOpenFirst)
        override val isShouldOpenFirstScreenFlow: Flow<Boolean> = pref
        override suspend fun updateFirstScreenPref(shouldOpenFirst: Boolean) {
            pref.value = shouldOpenFirst
        }
    }

    private val testItems = listOf(
        MediaListItem(
            uri = "content://media/external/file/1",
            displayName = "song.mp3",
            mimeType = "audio/mpeg",
            size = 1024L
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun TestScope.collectEffects(viewModel: PlayerViewModel): List<PlayerEffect> {
        val effects = mutableListOf<PlayerEffect>()
        val job = launch { viewModel.effect.collect { effects.add(it) } }
        advanceUntilIdle()
        job.cancel()
        return effects
    }

    @Test
    fun `first launch shows info screen`() = runTest {
        val viewModel = PlayerViewModel(FakeRepository(shouldOpenFirst = true))
        advanceUntilIdle()

        assertEquals(PlayerUiState.INFO_SCREEN, viewModel.state.value.uiState)
    }

    @Test
    fun `info screen dismiss persists preference and opens picker`() = runTest {
        val repository = FakeRepository(shouldOpenFirst = true)
        val viewModel = PlayerViewModel(repository)
        advanceUntilIdle()

        viewModel.dispatch(PlayerAction.SetFirstScreenShown)
        advanceUntilIdle()

        assertFalse(repository.pref.value)
        assertEquals(PlayerUiState.PICKER, viewModel.state.value.uiState)
        assertTrue(collectEffects(viewModel).contains(PlayerEffect.OpenPicker))
    }

    @Test
    fun `subsequent launch opens picker`() = runTest {
        val viewModel = PlayerViewModel(FakeRepository(shouldOpenFirst = false))
        advanceUntilIdle()

        assertEquals(PlayerUiState.PICKER, viewModel.state.value.uiState)
        assertTrue(collectEffects(viewModel).contains(PlayerEffect.OpenPicker))
    }

    @Test
    fun `action view flow prepares and runs player`() = runTest {
        val viewModel = PlayerViewModel(FakeRepository(shouldOpenFirst = false))
        viewModel.dispatch(PlayerAction.PrepareRunPlayer(testItems))

        assertEquals(PlayerUiState.EMPTY_PLAYER, viewModel.state.value.uiState)
        assertEquals(testItems, viewModel.state.value.selectedMedia)

        viewModel.dispatch(PlayerAction.RunPlayer)
        assertEquals(PlayerUiState.PLAYER, viewModel.state.value.uiState)
    }

    @Test
    fun `prepare player effect is delivered to a late collector`() = runTest {
        val viewModel = PlayerViewModel(FakeRepository(shouldOpenFirst = false))
        viewModel.dispatch(PlayerAction.PrepareRunPlayer(testItems))

        assertTrue(collectEffects(viewModel).contains(PlayerEffect.PreparePlayer))
    }

    @Test
    fun `check first screen does not clobber an active session`() = runTest {
        val viewModel = PlayerViewModel(FakeRepository(shouldOpenFirst = true))
        viewModel.dispatch(PlayerAction.PrepareRunPlayer(testItems))
        advanceUntilIdle()

        assertEquals(PlayerUiState.EMPTY_PLAYER, viewModel.state.value.uiState)

        viewModel.dispatch(PlayerAction.RunPlayer)
        viewModel.dispatch(PlayerAction.CheckFirstScreen)
        advanceUntilIdle()

        assertEquals(PlayerUiState.PLAYER, viewModel.state.value.uiState)
    }

    @Test
    fun `finish emits stop playback then finish`() = runTest {
        val viewModel = PlayerViewModel(FakeRepository(shouldOpenFirst = false))
        viewModel.dispatch(PlayerAction.PrepareRunPlayer(testItems))
        viewModel.dispatch(PlayerAction.Finish)

        val effects = collectEffects(viewModel)
        val stopIndex = effects.indexOf(PlayerEffect.StopPlayback)
        val finishIndex = effects.indexOf(PlayerEffect.Finish)
        assertTrue(stopIndex in 0 until finishIndex)
    }
}
