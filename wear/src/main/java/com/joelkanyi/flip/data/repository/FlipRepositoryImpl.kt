package com.joelkanyi.flip.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.joelkanyi.flip.data.model.CoinSide
import com.joelkanyi.flip.data.model.FlipStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "flip_stats")

class FlipRepositoryImpl(
    private val context: Context
) : FlipRepository {

    private object PreferencesKeys {
        val HEADS_COUNT = intPreferencesKey("heads_count")
        val TAILS_COUNT = intPreferencesKey("tails_count")
        val LAST_RESULT = stringPreferencesKey("last_result")
    }

    override val stats: Flow<FlipStats> = context.dataStore.data.map { preferences ->
        FlipStats(
            headsCount = preferences[PreferencesKeys.HEADS_COUNT] ?: 0,
            tailsCount = preferences[PreferencesKeys.TAILS_COUNT] ?: 0
        )
    }

    override val lastResult: Flow<CoinSide?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LAST_RESULT]?.let { CoinSide.valueOf(it) }
    }

    override suspend fun recordFlip(result: CoinSide) {
        context.dataStore.edit { preferences ->
            when (result) {
                CoinSide.HEADS -> {
                    val currentCount = preferences[PreferencesKeys.HEADS_COUNT] ?: 0
                    preferences[PreferencesKeys.HEADS_COUNT] = currentCount + 1
                }
                CoinSide.TAILS -> {
                    val currentCount = preferences[PreferencesKeys.TAILS_COUNT] ?: 0
                    preferences[PreferencesKeys.TAILS_COUNT] = currentCount + 1
                }
            }
            preferences[PreferencesKeys.LAST_RESULT] = result.name
        }
    }

    override suspend fun resetStats() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HEADS_COUNT] = 0
            preferences[PreferencesKeys.TAILS_COUNT] = 0
            preferences.remove(PreferencesKeys.LAST_RESULT)
        }
    }
}
