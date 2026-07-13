package com.vladusecho.lexicon.domain.usecase.definition

import com.vladusecho.lexicon.domain.repository.DefinitionRepository
import com.vladusecho.lexicon.domain.repository.DefinitionsRepository
import javax.inject.Inject

class GetDefinitionByIdUseCase @Inject constructor(
    private val definitionsRepository: DefinitionsRepository
) {
    operator fun invoke(id: Int) = definitionsRepository.getDefinition(id)
}