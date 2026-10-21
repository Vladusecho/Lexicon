package com.vladusecho.lexicon.presentation.viewmodel

import app.cash.turbine.test
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.entity.PartOfSpeech
import com.vladusecho.lexicon.domain.entity.ReviewQuality
import com.vladusecho.lexicon.domain.usecase.definition.GetDefinitionsUseCase
import com.vladusecho.lexicon.domain.usecase.definition.GetRandomDefinitionUseCase
import com.vladusecho.lexicon.domain.usecase.education.CalculateSpacedRepetitionUseCase
import com.vladusecho.lexicon.domain.usecase.education.GetDueDefinitionsUseCase
import com.vladusecho.lexicon.domain.usecase.education.ReviewDefinitionUseCase
import com.vladusecho.lexicon.domain.usecase.education.SpacedRepetitionResult
import com.vladusecho.lexicon.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EducationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getDueDefinitionsUseCase: GetDueDefinitionsUseCase = mockk()
    private val getDefinitionsUseCase: GetDefinitionsUseCase = mockk()
    private val reviewDefinitionUseCase: ReviewDefinitionUseCase = mockk()
    private val calculateSpacedRepetitionUseCase: CalculateSpacedRepetitionUseCase = mockk()
    private val getRandomDefinitionUseCase: GetRandomDefinitionUseCase = mockk()

    private val testDefinition = Definition(
        id = 1,
        word = "Толерантность",
        description = "Терпимость к иному мировоззрению",
        isFavorite = false,
        partOfSpeech = PartOfSpeech.NOUN,
        repetitions = 0,
        intervalDays = 0,
        easeFactor = 2.5f,
        nextReviewDate = 0L
    )

    private fun createViewModel(): EducationViewModel {
        return EducationViewModel(
            getDueDefinitionsUseCase = getDueDefinitionsUseCase,
            getDefinitionsUseCase = getDefinitionsUseCase,
            reviewDefinitionUseCase = reviewDefinitionUseCase,
            calculateSpacedRepetitionUseCase = calculateSpacedRepetitionUseCase,
            getRandomDefinitionUseCase = getRandomDefinitionUseCase
        )
    }

    @Test
    fun `when definitions are empty, emits Error NO_WORDS`() = runTest {
        every { getDefinitionsUseCase() } returns MutableStateFlow(emptyList())

        val viewModel = createViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state is EducationViewModel.EducationState.Error)
            assertEquals(EducationViewModel.ErrorType.NO_WORDS, (state as EducationViewModel.EducationState.Error).errorType)
        }
    }

    @Test
    fun `when due definitions exist, loads first due word with isFlipped false`() = runTest {
        every { getDefinitionsUseCase() } returns MutableStateFlow(listOf(testDefinition))
        every { getDueDefinitionsUseCase(any()) } returns MutableStateFlow(listOf(testDefinition))
        every { calculateSpacedRepetitionUseCase.previewIntervalDays(testDefinition, any()) } returns mapOf(
            ReviewQuality.AGAIN to 1,
            ReviewQuality.HARD to 1,
            ReviewQuality.GOOD to 3,
            ReviewQuality.EASY to 6
        )

        val viewModel = createViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state is EducationViewModel.EducationState.Success)
            val success = state as EducationViewModel.EducationState.Success
            assertEquals("Толерантность", success.definition.word)
            assertFalse(success.isFlipped)
            assertEquals(4, success.intervalPreviews.size)
        }
    }

    @Test
    fun `when no due words for today, shows AllDone state`() = runTest {
        every { getDefinitionsUseCase() } returns MutableStateFlow(listOf(testDefinition))
        every { getDueDefinitionsUseCase(any()) } returns MutableStateFlow(emptyList())

        val viewModel = createViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state is EducationViewModel.EducationState.AllDone)
        }
    }

    @Test
    fun `FlipCard toggles isFlipped state`() = runTest {
        every { getDefinitionsUseCase() } returns MutableStateFlow(listOf(testDefinition))
        every { getDueDefinitionsUseCase(any()) } returns MutableStateFlow(listOf(testDefinition))
        every { calculateSpacedRepetitionUseCase.previewIntervalDays(testDefinition, any()) } returns emptyMap()

        val viewModel = createViewModel()

        viewModel.state.test {
            val initial = awaitItem() as EducationViewModel.EducationState.Success
            assertFalse(initial.isFlipped)

            viewModel.processCommand(EducationViewModel.EducationCommand.FlipCard)
            val flipped = awaitItem() as EducationViewModel.EducationState.Success
            assertTrue(flipped.isFlipped)

            viewModel.processCommand(EducationViewModel.EducationCommand.FlipCard)
            val unFlipped = awaitItem() as EducationViewModel.EducationState.Success
            assertFalse(unFlipped.isFlipped)
        }
    }

    @Test
    fun `SubmitReview calls reviewDefinitionUseCase and moves to next word or AllDone`() = runTest {
        every { getDefinitionsUseCase() } returns MutableStateFlow(listOf(testDefinition))
        every { getDueDefinitionsUseCase(any()) } returns MutableStateFlow(listOf(testDefinition))
        every { calculateSpacedRepetitionUseCase.previewIntervalDays(testDefinition, any()) } returns emptyMap()
        coEvery { reviewDefinitionUseCase(testDefinition, ReviewQuality.GOOD, any()) } returns Result.success(
            SpacedRepetitionResult(repetitions = 1, intervalDays = 1, easeFactor = 2.5f, nextReviewDate = 2000L)
        )

        val viewModel = createViewModel()

        viewModel.state.test {
            awaitItem() // Initial Success state

            viewModel.processCommand(EducationViewModel.EducationCommand.SubmitReview(ReviewQuality.GOOD))

            val nextState = awaitItem()
            assertTrue(nextState is EducationViewModel.EducationState.AllDone)

            coVerify(exactly = 1) { reviewDefinitionUseCase(testDefinition, ReviewQuality.GOOD, any()) }
        }
    }
}
