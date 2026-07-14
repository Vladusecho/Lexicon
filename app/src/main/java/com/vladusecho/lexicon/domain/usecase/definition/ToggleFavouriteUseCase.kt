package com.vladusecho.lexicon.domain.usecase.definition

import com.vladusecho.lexicon.domain.repository.FavouritesRepository
import javax.inject.Inject

class ToggleFavouriteUseCase @Inject constructor(
    private val favouritesRepository: FavouritesRepository
) {

    suspend operator fun invoke(id: Int, isFavourite: Boolean) {
        favouritesRepository.toggleFavorite(id, isFavourite)
    }
}