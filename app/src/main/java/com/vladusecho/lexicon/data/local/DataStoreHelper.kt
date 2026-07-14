package com.vladusecho.lexicon.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vladusecho.lexicon.domain.entity.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreHelper @Inject constructor(
    @field:ApplicationContext val context: Context
) {

    val isDarkMode = booleanPreferencesKey(IS_DARK_MODE)

    fun getSettings(): Flow<Settings> {
        return context.dataStore.data.map {
            Settings(
                isDarkMode = it[isDarkMode] ?: false
            )
        }
    }

    companion object {

        const val IS_DARK_MODE = "is_dark_mode"
    }
}