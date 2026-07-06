package com.vladusecho.lexicon.domain.usecase.settings

import com.vladusecho.lexicon.domain.repository.DefinitionRepository
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val definitionRepository: DefinitionRepository
) {

    operator fun invoke() = definitionRepository.getSettings()
}