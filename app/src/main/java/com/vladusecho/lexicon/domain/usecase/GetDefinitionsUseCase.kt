package com.vladusecho.lexicon.domain.usecase

import com.vladusecho.lexicon.domain.repository.DefinitionRepository
import javax.inject.Inject

class GetDefinitionsUseCase @Inject constructor(
    private val definitionRepository: DefinitionRepository
) {

    operator fun invoke() = definitionRepository.getDefinitions()
}