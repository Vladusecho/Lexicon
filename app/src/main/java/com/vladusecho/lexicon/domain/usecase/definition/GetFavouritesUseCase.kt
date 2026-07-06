package com.vladusecho.lexicon.domain.usecase.definition

import com.vladusecho.lexicon.domain.repository.DefinitionRepository
import javax.inject.Inject

class GetFavouritesUseCase @Inject constructor(
    private val definitionRepository: DefinitionRepository
){

    operator fun invoke() = definitionRepository.getFavorites()
}