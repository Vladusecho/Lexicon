package com.vladusecho.lexicon.domain.usecase.education

import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.repository.DefinitionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDueDefinitionsUseCase @Inject constructor(
    private val definitionsRepository: DefinitionsRepository
) {

    operator fun invoke(currentTime: Long = System.currentTimeMillis()): Flow<List<Definition>> {
        return definitionsRepository.getDueDefinitions(currentTime)
    }
}
