package com.vladusecho.lexicon.domain.usecase.education

import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.entity.ReviewQuality
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import kotlin.math.max

data class SpacedRepetitionResult(
    val repetitions: Int,
    val intervalDays: Int,
    val easeFactor: Float,
    val nextReviewDate: Long
)

class CalculateSpacedRepetitionUseCase @Inject constructor() {

    operator fun invoke(
        definition: Definition,
        quality: ReviewQuality,
        currentTime: Long = System.currentTimeMillis(),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): SpacedRepetitionResult {
        val q = quality.grade
        val currentEf = definition.easeFactor
        val currentReps = definition.repetitions
        val currentInterval = definition.intervalDays

        // SM-2 formula: EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
        val newEf = max(
            1.3f,
            currentEf + (0.1f - (5 - q) * (0.08f + (5 - q) * 0.02f))
        )

        val newReps: Int
        val newInterval: Int

        when (quality) {
            ReviewQuality.AGAIN -> {
                newReps = 0
                newInterval = 1
            }

            ReviewQuality.HARD -> {
                newReps = max(1, currentReps)
                newInterval = max(1, (currentInterval * 1.2f).toInt())
            }

            ReviewQuality.GOOD -> {
                newReps = currentReps + 1
                newInterval = when (currentReps) {
                    0 -> 1
                    1 -> 3
                    else -> max(1, (currentInterval * newEf).toInt())
                }
            }

            ReviewQuality.EASY -> {
                newReps = currentReps + 1
                newInterval = when (currentReps) {
                    0 -> 3
                    1 -> 6
                    else -> max(1, (currentInterval * newEf * 1.3f).toInt())
                }
            }
        }

        val nextReviewDate = calculateStartOfDayInFuture(
            days = newInterval,
            fromTime = currentTime,
            zoneId = zoneId
        )

        return SpacedRepetitionResult(
            repetitions = newReps,
            intervalDays = newInterval,
            easeFactor = newEf,
            nextReviewDate = nextReviewDate
        )
    }

    fun calculateStartOfDayInFuture(
        days: Int,
        fromTime: Long,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Long {
        val today = Instant.ofEpochMilli(fromTime).atZone(zoneId).toLocalDate()
        val targetDate = today.plusDays(days.toLong())
        return targetDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
    }

    fun previewIntervalDays(
        definition: Definition,
        currentTime: Long = System.currentTimeMillis(),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Map<ReviewQuality, Int> {
        return ReviewQuality.entries.associateWith { quality ->
            invoke(definition, quality, currentTime, zoneId).intervalDays
        }
    }
}
