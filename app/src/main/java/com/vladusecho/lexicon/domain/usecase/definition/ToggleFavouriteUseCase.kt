package com.vladusecho.lexicon.domain.usecase.definition

import com.vladusecho.lexicon.domain.repository.DefinitionsRepository
import javax.inject.Inject

class ToggleFavouriteUseCase @Inject constructor(
    private val definitionsRepository: DefinitionsRepository
) {

    suspend operator fun invoke(id: Int, isFavourite: Boolean) {
        definitionsRepository.toggleFavorite(id, isFavourite)
    }
}
