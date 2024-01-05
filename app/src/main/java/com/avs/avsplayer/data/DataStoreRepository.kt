package com.avs.avsplayer.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreRepository (private val dataStore: DataStore<Preferences>) {

    private val SHOULD_OPEN_FIRST_SCREEN = booleanPreferencesKey("should_open_first_screen")

    val isShouldOpenFirstScreenFlow : Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            fetchShouldOpenFirstScreenPref(preferences)
        }

    suspend fun fetchShouldOpenFirstScreenPref(preferences: Preferences) = preferences[SHOULD_OPEN_FIRST_SCREEN] ?: true

    suspend fun updateFirstScreenPref(shouldOpenFirst: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOULD_OPEN_FIRST_SCREEN] = shouldOpenFirst
        }
    }
}