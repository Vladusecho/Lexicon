package com.vladusecho.lexicon.domain.usecase.definition

import com.vladusecho.lexicon.domain.repository.DefinitionRepository
import javax.inject.Inject

class CheckIsFavouriteUseCase @Inject constructor(
    private val definitionRepository: DefinitionRepository
)
{
    operator fun invoke(id: Int) = definitionRepository.checkIsFavorite(id)
}