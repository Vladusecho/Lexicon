package com.vladusecho.lexicon.presentation.viewmodel

import app.cash.turbine.test
import com.vladusecho.lexicon.domain.entity.PartOfSpeech
import com.vladusecho.lexicon.domain.usecase.definition.CreateDefinitionUseCase
import com.vladusecho.lexicon.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateDefinitionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val createDefinitionUseCase: CreateDefinitionUseCase = mockk()

    @Test
    fun `initial state is empty and invalid`() {
        val viewModel = CreateDefinitionViewModel(createDefinitionUseCase)
        val state = viewModel.uiState.value

        assertEquals("", state.word)
        assertEquals("", state.description)
        assertFalse(state.isValid)
    }

    @Test
    fun `when word and description are filled, state becomes valid`() {
        val viewModel = CreateDefinitionViewModel(createDefinitionUseCase)

        viewModel.processCommand(CreateDefinitionViewModel.CreateDefinitionCommand.UpdateWord("Слово"))
        viewModel.processCommand(CreateDefinitionViewModel.CreateDefinitionCommand.UpdateDescription("Определение слова"))

        val state = viewModel.uiState.value
        assertEquals("Слово", state.word)
        assertEquals("Определение слова", state.description)
        assertTrue(state.isValid)
    }

    @Test
    fun `when CleanData processed, state resets to defaults`() {
        val viewModel = CreateDefinitionViewModel(createDefinitionUseCase)

        viewModel.processCommand(CreateDefinitionViewModel.CreateDefinitionCommand.UpdateWord("Слово"))
        viewModel.processCommand(CreateDefinitionViewModel.CreateDefinitionCommand.UpdateDescription("Определение"))
        viewModel.processCommand(CreateDefinitionViewModel.CreateDefinitionCommand.CleanData)

        val state = viewModel.uiState.value
        assertEquals("", state.word)
        assertEquals("", state.description)
        assertFalse(state.isValid)
    }

    @Test
    fun `when Save executed with valid input, formats word and emits FinishCreate`() = runTest {
        coEvery { createDefinitionUseCase(any(), any()) } returns Result.success(Unit)

        val viewModel = CreateDefinitionViewModel(createDefinitionUseCase)
        viewModel.processCommand(CreateDefinitionViewModel.CreateDefinitionCommand.UpdateWord("  инновация  "))
        viewModel.processCommand(CreateDefinitionViewModel.CreateDefinitionCommand.UpdateDescription("Нововведение"))
        viewModel.processCommand(CreateDefinitionViewModel.CreateDefinitionCommand.PickPartOfSpeech(PartOfSpeech.NOUN))

        viewModel.event.test {
            viewModel.processCommand(CreateDefinitionViewModel.CreateDefinitionCommand.Save)

            val event = awaitItem()
            assertTrue(event is CreateDefinitionViewModel.CreateDefinitionEvent.FinishCreate)
        }

        coVerify {
            createDefinitionUseCase(
                match { it.word == "Инновация" && it.description == "Нововведение" && it.partOfSpeech == PartOfSpeech.NOUN },
                null
            )
        }
    }

    @Test
    fun `when Save fails, emits ShowError`() = runTest {
        coEvery { createDefinitionUseCase(any(), any()) } returns Result.failure(RuntimeException("DB error"))

        val viewModel = CreateDefinitionViewModel(createDefinitionUseCase)
        viewModel.processCommand(CreateDefinitionViewModel.CreateDefinitionCommand.UpdateWord("Тест"))
        viewModel.processCommand(CreateDefinitionViewModel.CreateDefinitionCommand.UpdateDescription("Описание"))

        viewModel.event.test {
            viewModel.processCommand(CreateDefinitionViewModel.CreateDefinitionCommand.Save)

            val event = awaitItem()
            assertTrue(event is CreateDefinitionViewModel.CreateDefinitionEvent.ShowError)
            assertEquals("DB error", (event as CreateDefinitionViewModel.CreateDefinitionEvent.ShowError).message)
        }
    }
}
