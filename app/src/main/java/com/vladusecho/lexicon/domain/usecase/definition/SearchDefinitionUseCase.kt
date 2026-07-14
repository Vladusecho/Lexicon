package com.vladusecho.lexicon.domain.usecase.definition

import com.vladusecho.lexicon.domain.repository.DefinitionsRepository
import javax.inject.Inject

class SearchDefinitionUseCase @Inject constructor(
    private val definitionsRepository: DefinitionsRepository
) {

    operator fun invoke(query: String, searchFavourite: Boolean = false) =
        definitionsRepository.search(query, searchFavourite)
}