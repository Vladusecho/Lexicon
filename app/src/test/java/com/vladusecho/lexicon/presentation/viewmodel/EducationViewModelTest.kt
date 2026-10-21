package com.vladusecho.lexicon.presentation.viewmodel

import app.cash.turbine.test
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.entity.PartOfSpeech
import com.vladusecho.lexicon.domain.usecase.definition.GetDefinitionsUseCase
import com.vladusecho.lexicon.domain.usecase.definition.GetRandomDefinitionUseCase
import com.vladusecho.lexicon.util.MainDispatcherRule
import io.mockk.coEvery
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

    private val getRandomDefinitionUseCase: GetRandomDefinitionUseCase = mockk()
    private val getDefinitionsUseCase: GetDefinitionsUseCase = mockk()

    private val testDefinition = Definition(
        id = 1,
        word = "Толерантность",
        description = "Терпимость к иному мировоззрению",
        isFavorite = false,
        partOfSpeech = PartOfSpeech.NOUN
    )

    @Test
    fun `when definitions are empty, emits Error NO_WORDS`() = runTest {
        val flow = MutableStateFlow<List<Definition>>(emptyList())
        every { getDefinitionsUseCase() } returns flow

        val viewModel = EducationViewModel(getRandomDefinitionUseCase, getDefinitionsUseCase)

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state is EducationViewModel.EducationState.Error)
            assertEquals(EducationViewModel.ErrorType.NO_WORDS, (state as EducationViewModel.EducationState.Error).errorType)
        }
    }

    @Test
    fun `when definitions exist, loads word with isFlipped false`() = runTest {
        val flow = MutableStateFlow(listOf(testDefinition))
        every { getDefinitionsUseCase() } returns flow
        coEvery { getRandomDefinitionUseCase(any()) } returns Result.success(testDefinition)

        val viewModel = EducationViewModel(getRandomDefinitionUseCase, getDefinitionsUseCase)

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state is EducationViewModel.EducationState.Success)
            val success = state as EducationViewModel.EducationState.Success
            assertEquals("Толерантность", success.definition.word)
            assertFalse(success.isFlipped)
        }
    }

    @Test
    fun `FlipCard toggles isFlipped state`() = runTest {
        val flow = MutableStateFlow(listOf(testDefinition))
        every { getDefinitionsUseCase() } returns flow
        coEvery { getRandomDefinitionUseCase(any()) } returns Result.success(testDefinition)

        val viewModel = EducationViewModel(getRandomDefinitionUseCase, getDefinitionsUseCase)

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
}
