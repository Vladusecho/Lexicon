package com.vladusecho.lexicon.domain.usecase.definition

import com.vladusecho.lexicon.domain.repository.DefinitionRepository
import com.vladusecho.lexicon.domain.repository.FavouritesRepository
import javax.inject.Inject

class GetFavouritesUseCase @Inject constructor(
    private val favouritesRepository: FavouritesRepository
){

    operator fun invoke() = favouritesRepository.getFavorites()
}