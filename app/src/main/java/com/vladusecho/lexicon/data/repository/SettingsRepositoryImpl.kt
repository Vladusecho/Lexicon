package com.vladusecho.lexicon.data.repository

import androidx.datastore.preferences.core.edit
import com.vladusecho.lexicon.data.local.DataStoreHelper
import com.vladusecho.lexicon.data.local.dataStore
import com.vladusecho.lexicon.domain.entity.Settings
import com.vladusecho.lexicon.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStoreHelper: DataStoreHelper
) : SettingsRepository {

    override fun getSettings(): Flow<Settings> {
        return dataStoreHelper.getSettings()
    }

    override suspend fun toggleDarkMode(isDarkMode: Boolean) {
        dataStoreHelper.context.dataStore.edit {
            it[dataStoreHelper.isDarkMode] = isDarkMode
        }
    }
}