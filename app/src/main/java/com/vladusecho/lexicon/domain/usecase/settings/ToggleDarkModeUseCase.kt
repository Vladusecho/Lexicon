package com.vladusecho.lexicon.domain.usecase.settings

import com.vladusecho.lexicon.domain.repository.DefinitionRepository
import javax.inject.Inject

class ToggleDarkModeUseCase @Inject constructor(
    private val definitionRepository: DefinitionRepository
){

    suspend operator fun invoke(isDarkMode: Boolean) = definitionRepository.toggleDarkMode(isDarkMode)
}