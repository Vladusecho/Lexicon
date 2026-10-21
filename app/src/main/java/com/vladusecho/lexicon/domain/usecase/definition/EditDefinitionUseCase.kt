package com.vladusecho.lexicon.domain.usecase.definition

import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.repository.DefinitionsRepository
import javax.inject.Inject

class EditDefinitionUseCase @Inject constructor(
    private val definitionsRepository: DefinitionsRepository
) {
    suspend operator fun invoke(
        definition: Definition,
        newImageUri: String? = null,
        isImageRemoved: Boolean = false
    ): Result<Unit> =
        definitionsRepository.updateDefinition(definition, newImageUri, isImageRemoved)
}
