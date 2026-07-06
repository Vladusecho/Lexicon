package com.vladusecho.lexicon.domain.usecase.definition

import com.vladusecho.lexicon.domain.repository.DefinitionRepository
import javax.inject.Inject

class DeleteDefinitionUseCase @Inject constructor(
    private val definitionRepository: DefinitionRepository
)
{

    suspend operator fun invoke(id: Int) {
        definitionRepository.deleteDefinition(id)
    }
}