package com.avs.avsplayer.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.avs.avsplayer.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreRepositoryImpl(context: Context) : DataStoreRepository {

    private val dataStore = context.applicationContext.dataStore

    private val SHOULD_OPEN_FIRST_SCREEN = booleanPreferencesKey("should_open_first_screen")

    override val isShouldOpenFirstScreenFlow: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[SHOULD_OPEN_FIRST_SCREEN] ?: true
        }

    override suspend fun updateFirstScreenPref(shouldOpenFirst: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOULD_OPEN_FIRST_SCREEN] = shouldOpenFirst
        }
    }
}
