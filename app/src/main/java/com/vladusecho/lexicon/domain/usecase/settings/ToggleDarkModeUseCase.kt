package com.vladusecho.lexicon.domain.usecase.settings

import com.vladusecho.lexicon.domain.repository.DefinitionRepository
import com.vladusecho.lexicon.domain.repository.SettingsRepository
import javax.inject.Inject

class ToggleDarkModeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
){

    suspend operator fun invoke(isDarkMode: Boolean) = settingsRepository.toggleDarkMode(isDarkMode)
}