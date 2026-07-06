package com.vladusecho.lexicon.domain.usecase.definition

import com.vladusecho.lexicon.domain.repository.DefinitionRepository
import javax.inject.Inject

class ToggleFavouriteUseCase @Inject constructor(
    private val definitionRepository: DefinitionRepository
) {

    suspend operator fun invoke(id: Int) {
        definitionRepository.toggleFavorite(id)
    }
}