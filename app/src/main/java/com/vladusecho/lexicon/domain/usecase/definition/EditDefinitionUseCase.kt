package com.vladusecho.lexicon.domain.usecase.definition

import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.repository.DefinitionRepository
import javax.inject.Inject

class EditDefinitionUseCase @Inject constructor(
    private val definitionRepository: DefinitionRepository
)
{
    suspend operator fun invoke(definition: Definition) = definitionRepository.updateDefinition(definition)
}