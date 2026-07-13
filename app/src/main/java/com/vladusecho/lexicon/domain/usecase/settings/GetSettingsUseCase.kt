package com.vladusecho.lexicon.domain.usecase.settings

import com.vladusecho.lexicon.domain.repository.DefinitionRepository
import com.vladusecho.lexicon.domain.repository.DefinitionsRepository
import com.vladusecho.lexicon.domain.repository.SettingsRepository
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    operator fun invoke() = settingsRepository.getSettings()
}