package com.vladusecho.lexicon.domain.usecase.definition

import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.repository.DefinitionsRepository
import javax.inject.Inject

class CreateDefinitionUseCase @Inject constructor(
    private val definitionsRepository: DefinitionsRepository
) {

    suspend operator fun invoke(definition: Definition) {
        definitionsRepository.createDefinition(definition)
    }
}