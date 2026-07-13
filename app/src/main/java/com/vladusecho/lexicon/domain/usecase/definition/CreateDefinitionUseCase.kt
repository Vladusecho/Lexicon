package com.vladusecho.lexicon.domain.usecase.definition

import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.repository.DefinitionRepository
import com.vladusecho.lexicon.domain.repository.DefinitionsRepository
import com.vladusecho.lexicon.domain.repository.FavouritesRepository
import javax.inject.Inject

class CreateDefinitionUseCase @Inject constructor(
    private val definitionsRepository: DefinitionsRepository
){

    suspend operator fun invoke(definition: Definition) {
        definitionsRepository.createDefinition(definition)
    }
}