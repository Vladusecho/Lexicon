package com.vladusecho.lexicon.domain.usecase.education

import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.entity.PartOfSpeech
import com.vladusecho.lexicon.domain.entity.ReviewQuality
import java.time.Instant
import java.time.ZoneOffset
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

    private val utcZone = ZoneOffset.UTC

    @Test
    fun `when AGAIN selected, resets repetitions to 0 and sets due date to tomorrow midnight`() {
        val learnedWord = freshDefinition.copy(repetitions = 3, intervalDays = 10, easeFactor = 2.5f)
        // Wednesday Oct 21, 2026 at 22:30:00 UTC
        val fixedTime = Instant.parse("2026-10-21T22:30:00Z").toEpochMilli()

        val result = useCase(learnedWord, ReviewQuality.AGAIN, currentTime = fixedTime, zoneId = utcZone)

        assertEquals(0, result.repetitions)
        assertEquals(1, result.intervalDays)
        assertTrue(result.easeFactor < 2.5f)

        // Must be Thursday Oct 22, 2026 at 00:00:00 UTC
        val expectedMidnight = Instant.parse("2026-10-22T00:00:00Z").toEpochMilli()
        assertEquals(expectedMidnight, result.nextReviewDate)
    }

    @Test
    fun `when GOOD selected for first time, sets repetitions 1 and interval 1 day at midnight`() {
        val fixedTime = Instant.parse("2026-10-21T10:15:00Z").toEpochMilli()
        val result = useCase(freshDefinition, ReviewQuality.GOOD, currentTime = fixedTime, zoneId = utcZone)

        assertEquals(1, result.repetitions)
        assertEquals(1, result.intervalDays)

        val expectedMidnight = Instant.parse("2026-10-22T00:00:00Z").toEpochMilli()
        assertEquals(expectedMidnight, result.nextReviewDate)
    }

    @Test
    fun `when GOOD selected for second time, sets repetitions 2 and interval 3 days at midnight`() {
        val rep1Word = freshDefinition.copy(repetitions = 1, intervalDays = 1, easeFactor = 2.5f)
        val fixedTime = Instant.parse("2026-10-21T10:15:00Z").toEpochMilli()

        val result = useCase(rep1Word, ReviewQuality.GOOD, currentTime = fixedTime, zoneId = utcZone)

        assertEquals(2, result.repetitions)
        assertEquals(3, result.intervalDays)

        // 3 days later: Oct 24, 2026 at 00:00:00 UTC
        val expectedMidnight = Instant.parse("2026-10-24T00:00:00Z").toEpochMilli()
        assertEquals(expectedMidnight, result.nextReviewDate)
    }

    @Test
    fun `when EASY selected for first time, sets interval to 3 days at midnight`() {
        val fixedTime = Instant.parse("2026-10-21T10:15:00Z").toEpochMilli()
        val result = useCase(freshDefinition, ReviewQuality.EASY, currentTime = fixedTime, zoneId = utcZone)

        assertEquals(1, result.repetitions)
        assertEquals(3, result.intervalDays)
        assertTrue(result.easeFactor > 2.5f)

        val expectedMidnight = Instant.parse("2026-10-24T00:00:00Z").toEpochMilli()
        assertEquals(expectedMidnight, result.nextReviewDate)
    }

    @Test
    fun `ease factor does not drop below 1_3`() {
        var def = freshDefinition.copy(easeFactor = 1.3f)
        repeat(5) {
            val result = useCase(def, ReviewQuality.AGAIN, zoneId = utcZone)
            def = def.copy(easeFactor = result.easeFactor)
        }

        assertEquals(1.3f, def.easeFactor, 0.001f)
    }

    @Test
    fun `previewIntervalDays returns predictions for all 4 qualities`() {
        val previews = useCase.previewIntervalDays(freshDefinition, zoneId = utcZone)

        assertEquals(4, previews.size)
        assertTrue(previews.containsKey(ReviewQuality.AGAIN))
        assertTrue(previews.containsKey(ReviewQuality.HARD))
        assertTrue(previews.containsKey(ReviewQuality.GOOD))
        assertTrue(previews.containsKey(ReviewQuality.EASY))
    }
}
