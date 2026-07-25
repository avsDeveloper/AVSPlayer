package com.avs.avsplayer.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    val isShouldOpenFirstScreenFlow: Flow<Boolean>
    suspend fun updateFirstScreenPref(shouldOpenFirst: Boolean)
}