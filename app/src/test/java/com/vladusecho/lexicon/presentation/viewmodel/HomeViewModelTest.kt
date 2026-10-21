package com.vladusecho.lexicon.presentation.viewmodel

import app.cash.turbine.test
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.entity.PartOfSpeech
import com.vladusecho.lexicon.domain.usecase.definition.GetDefinitionsUseCase
import com.vladusecho.lexicon.domain.usecase.definition.ToggleFavouriteUseCase
import com.vladusecho.lexicon.util.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getDefinitionsUseCase: GetDefinitionsUseCase = mockk()
    private val toggleFavouriteUseCase: ToggleFavouriteUseCase = mockk(relaxed = true)

    private val testDefinitions = listOf(
        Definition(
            id = 1,
            word = "Альтруизм",
            description = "Бескорыстная забота о благе других",
            isFavorite = true,
            partOfSpeech = PartOfSpeech.NOUN,
            createdAt = 1000L
        ),
        Definition(
            id = 2,
            word = "Эмпатия",
            description = "Способность сопереживать",
            isFavorite = false,
            partOfSpeech = PartOfSpeech.NOUN,
            createdAt = 2000L
        ),
        Definition(
            id = 3,
            word = "Лаконичный",
            description = "Краткий и четкий",
            isFavorite = false,
            partOfSpeech = PartOfSpeech.ADJECTIVE,
            createdAt = 3000L
        )
    )

    @Test
    fun `when definitions are empty, state is Empty NO_WORDS`() = runTest {
        val flow = MutableStateFlow<List<Definition>>(emptyList())
        every { getDefinitionsUseCase() } returns flow

        val viewModel = HomeViewModel(getDefinitionsUseCase, toggleFavouriteUseCase)

        viewModel.state.test {
            val item = awaitItem()
            assertTrue(item is HomeViewModel.HomeState.Empty)
            assertEquals(HomeViewModel.ErrorType.NO_WORDS, (item as HomeViewModel.HomeState.Empty).errorType)
        }
    }

    @Test
    fun `when definitions loaded, state is Success with all words`() = runTest {
        val flow = MutableStateFlow(testDefinitions)
        every { getDefinitionsUseCase() } returns flow

        val viewModel = HomeViewModel(getDefinitionsUseCase, toggleFavouriteUseCase)

        viewModel.state.test {
            val item = awaitItem()
            assertTrue(item is HomeViewModel.HomeState.Success)
            val success = item as HomeViewModel.HomeState.Success
            assertEquals(3, success.definitions.size)
        }
    }

    @Test
    fun `when query entered, filters definitions by query`() = runTest {
        val flow = MutableStateFlow(testDefinitions)
        every { getDefinitionsUseCase() } returns flow

        val viewModel = HomeViewModel(getDefinitionsUseCase, toggleFavouriteUseCase)

        viewModel.state.test {
            awaitItem() // initial success

            viewModel.processCommand(HomeViewModel.HomeCommand.QueryInput("Альтр"))
            val filtered = awaitItem() as HomeViewModel.HomeState.Success
            assertEquals(1, filtered.definitions.size)
            assertEquals("Альтруизм", filtered.definitions.first().word)
        }
    }

    @Test
    fun `when search query has no matches, state is Empty NO_SEARCH_RESULTS`() = runTest {
        val flow = MutableStateFlow(testDefinitions)
        every { getDefinitionsUseCase() } returns flow

        val viewModel = HomeViewModel(getDefinitionsUseCase, toggleFavouriteUseCase)

        viewModel.state.test {
            awaitItem()

            viewModel.processCommand(HomeViewModel.HomeCommand.QueryInput("НесуществующееСлово"))
            val emptyState = awaitItem()
            assertTrue(emptyState is HomeViewModel.HomeState.Empty)
            assertEquals(HomeViewModel.ErrorType.NO_SEARCH_RESULTS, (emptyState as HomeViewModel.HomeState.Empty).errorType)
        }
    }

    @Test
    fun `when favorite filter selected, shows only favorite definitions`() = runTest {
        val flow = MutableStateFlow(testDefinitions)
        every { getDefinitionsUseCase() } returns flow

        val viewModel = HomeViewModel(getDefinitionsUseCase, toggleFavouriteUseCase)

        viewModel.state.test {
            awaitItem()

            viewModel.processCommand(HomeViewModel.HomeCommand.FilterClick(FilterChips.FAVORITE))
            val favs = awaitItem() as HomeViewModel.HomeState.Success
            assertEquals(1, favs.definitions.size)
            assertTrue(favs.definitions.first().isFavorite)
        }
    }

    @Test
    fun `when toggle favourite command processed, calls usecase`() = runTest {
        val flow = MutableStateFlow(testDefinitions)
        every { getDefinitionsUseCase() } returns flow

        val viewModel = HomeViewModel(getDefinitionsUseCase, toggleFavouriteUseCase)

        viewModel.processCommand(HomeViewModel.HomeCommand.ToggleFavourite(id = 1, isFavourite = false))

        coVerify { toggleFavouriteUseCase(1, false) }
    }
}
