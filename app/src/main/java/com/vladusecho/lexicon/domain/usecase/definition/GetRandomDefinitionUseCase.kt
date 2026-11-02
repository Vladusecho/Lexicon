package com.vladusecho.lexicon.domain.usecase.definition

import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.repository.DefinitionsRepository
import javax.inject.Inject

class GetRandomDefinitionUseCase @Inject constructor(
    private val definitionsRepository: DefinitionsRepository
) {
    suspend operator fun invoke(
        excludedId: Int = -1,
        category: String? = null,
        onlyFavorites: Boolean = false
    ): Result<Definition?> {
        return definitionsRepository.getRandomDefinitionFiltered(
            category = category,
            onlyFavorites = onlyFavorites,
            excludedId = excludedId
        )
    }
}