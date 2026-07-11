package com.vladusecho.lexicon.domain.usecase.definition

import com.vladusecho.lexicon.domain.repository.DefinitionRepository
import javax.inject.Inject

class SearchDefinitionUseCase @Inject constructor(
    private val repository: DefinitionRepository
){

    operator fun invoke(query: String) = repository.search(query)
}