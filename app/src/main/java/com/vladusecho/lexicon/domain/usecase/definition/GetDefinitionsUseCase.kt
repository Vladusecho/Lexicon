package com.vladusecho.lexicon.domain.usecase.definition

import com.vladusecho.lexicon.domain.repository.DefinitionsRepository
import javax.inject.Inject

class GetDefinitionsUseCase @Inject constructor(
    private val definitionsRepository: DefinitionsRepository
) {

    operator fun invoke() = definitionsRepository.getDefinitions()
}