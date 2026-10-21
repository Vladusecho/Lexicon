package com.vladusecho.lexicon.domain.usecase.education

import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.entity.PartOfSpeech
import com.vladusecho.lexicon.domain.entity.ReviewQuality
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculateSpacedRepetitionUseCaseTest {

    private val useCase = CalculateSpacedRepetitionUseCase()

    private val freshDefinition = Definition(
        id = 1,
        word = "Эрудиция",
        description = "Широкие познания",
        isFavorite = false,
        partOfSpeech = PartOfSpeech.NOUN,
        repetitions = 0,
        intervalDays = 0,
        easeFactor = 2.5f,
        nextReviewDate = 0L
    )

    @Test
    fun `when AGAIN selected, resets repetitions to 0 and interval to 1 day`() {
        val learnedWord = freshDefinition.copy(repetitions = 3, intervalDays = 10, easeFactor = 2.5f)
        val result = useCase(learnedWord, ReviewQuality.AGAIN, currentTime = 1000L)

        assertEquals(0, result.repetitions)
        assertEquals(1, result.intervalDays)
        assertTrue(result.easeFactor < 2.5f)
        assertEquals(1000L + 24 * 60 * 60 * 1000L, result.nextReviewDate)
    }

    @Test
    fun `when GOOD selected for first time, sets repetitions 1 and interval 1`() {
        val result = useCase(freshDefinition, ReviewQuality.GOOD, currentTime = 1000L)

        assertEquals(1, result.repetitions)
        assertEquals(1, result.intervalDays)
    }

    @Test
    fun `when GOOD selected for second time, sets repetitions 2 and interval 3`() {
        val rep1Word = freshDefinition.copy(repetitions = 1, intervalDays = 1, easeFactor = 2.5f)
        val result = useCase(rep1Word, ReviewQuality.GOOD, currentTime = 1000L)

        assertEquals(2, result.repetitions)
        assertEquals(3, result.intervalDays)
    }

    @Test
    fun `when EASY selected for first time, sets interval to 3 days and increases EF`() {
        val result = useCase(freshDefinition, ReviewQuality.EASY, currentTime = 1000L)

        assertEquals(1, result.repetitions)
        assertEquals(3, result.intervalDays)
        assertTrue(result.easeFactor > 2.5f)
    }

    @Test
    fun `ease factor does not drop below 1_3`() {
        var def = freshDefinition.copy(easeFactor = 1.3f)
        repeat(5) {
            val result = useCase(def, ReviewQuality.AGAIN)
            def = def.copy(easeFactor = result.easeFactor)
        }

        assertEquals(1.3f, def.easeFactor, 0.001f)
    }

    @Test
    fun `previewIntervalDays returns predictions for all 4 qualities`() {
        val previews = useCase.previewIntervalDays(freshDefinition)

        assertEquals(4, previews.size)
        assertTrue(previews.containsKey(ReviewQuality.AGAIN))
        assertTrue(previews.containsKey(ReviewQuality.HARD))
        assertTrue(previews.containsKey(ReviewQuality.GOOD))
        assertTrue(previews.containsKey(ReviewQuality.EASY))
    }
}
