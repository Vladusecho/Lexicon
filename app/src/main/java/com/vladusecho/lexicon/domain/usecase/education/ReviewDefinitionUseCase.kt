package com.vladusecho.lexicon.domain.usecase.education

import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.entity.ReviewQuality
import com.vladusecho.lexicon.domain.repository.DefinitionsRepository
import javax.inject.Inject

class ReviewDefinitionUseCase @Inject constructor(
    private val definitionsRepository: DefinitionsRepository,
    private val calculateSpacedRepetitionUseCase: CalculateSpacedRepetitionUseCase
) {

    suspend operator fun invoke(
        definition: Definition,
        quality: ReviewQuality,
        currentTime: Long = System.currentTimeMillis()
    ): Result<SpacedRepetitionResult> {
        val repetitionResult = calculateSpacedRepetitionUseCase(definition, quality, currentTime)

        return definitionsRepository.updateReviewProgress(
            id = definition.id,
            repetitions = repetitionResult.repetitions,
            intervalDays = repetitionResult.intervalDays,
            easeFactor = repetitionResult.easeFactor,
            nextReviewDate = repetitionResult.nextReviewDate
        ).map { repetitionResult }
    }
}
