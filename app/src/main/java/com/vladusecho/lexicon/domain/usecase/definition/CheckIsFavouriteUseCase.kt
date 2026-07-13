package com.vladusecho.lexicon.domain.usecase.definition

import com.vladusecho.lexicon.domain.repository.DefinitionRepository
import com.vladusecho.lexicon.domain.repository.FavouritesRepository
import javax.inject.Inject

class CheckIsFavouriteUseCase @Inject constructor(
    private val favouritesRepository: FavouritesRepository
)
{
    operator fun invoke(id: Int) = favouritesRepository.checkIsFavorite(id)
}