package com.vladusecho.lexicon.domain.repository

import com.vladusecho.lexicon.domain.entity.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun getSettings(): Flow<Settings>

    suspend fun toggleDarkMode(isDarkMode: Boolean)
}