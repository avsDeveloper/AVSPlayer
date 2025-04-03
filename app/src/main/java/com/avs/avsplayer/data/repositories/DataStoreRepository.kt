package com.avs.avsplayer.data.repositories

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    val isShouldOpenFirstScreenFlow: Flow<Boolean>
    suspend fun fetchShouldOpenFirstScreenPref(preferences: Preferences): Boolean
    suspend fun updateFirstScreenPref(shouldOpenFirst: Boolean)
}