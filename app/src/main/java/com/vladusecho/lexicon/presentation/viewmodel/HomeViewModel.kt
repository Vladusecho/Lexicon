package com.vladusecho.lexicon.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.usecase.definition.GetDefinitionsUseCase
import com.vladusecho.lexicon.presentation.screenv2.FilterChips
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getDefinitionsUseCase: GetDefinitionsUseCase,
) : ViewModel() {

    var query by mutableStateOf("")
        private set
    var isSearchActive by mutableStateOf(false)
        private set

    var selectedFilter by mutableStateOf(FilterChips.ALL)
        private set

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    // StateFlow with the current state of the search
    val state = combine(
        snapshotFlow { query } // Convert query to a flow
            .debounce(500) // Debounce the flow to avoid making too many requests
            .distinctUntilChanged(), // Only emit a new value if it's different from the previous one
        snapshotFlow { selectedFilter }, // Convert selectedFilter to a flow
        getDefinitionsUseCase() // Get the definitions from the use case
    ) { query, selectedFilter, definitions ->
        Triple(
            query,
            selectedFilter,
            definitions
        )
    } // Combine the 3 flows into a triple
        .map { (query, selectedFilter, definitions) -> // Switch to a new flow based on the selected filter
            when (selectedFilter) {
                FilterChips.ALL -> {
                    definitions
                }

                FilterChips.FAVORITE -> {
                    definitions.filter { definition -> definition.isFavorite }
                }

                FilterChips.RECENT -> {
                    definitions.sortedByDescending { definition -> definition.id }.take(3)
                }
            }.filter { definition -> definition.word.startsWith(query.trim(), ignoreCase = true) }
        }
        .map { HomeState.Success(it) as HomeState } // Convert the flow to a state
        .catch { emit(HomeState.Error) } // Catch any errors and emit an error state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeState.Loading
        )

    val definitionsCount = state
        .map {
            when (it) {
                is HomeState.Success -> it.definitions.size
                else -> 0
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun processCommand(command: HomeCommand) {
        when (command) {
            is HomeCommand.QueryInput -> {
                query = command.query
            }

            HomeCommand.SearchActive -> {
                isSearchActive = !isSearchActive
                if (!isSearchActive) query = ""
            }

            is HomeCommand.FilterClick -> {
                selectedFilter = command.filter
            }
        }
    }

    sealed interface HomeState {
        data class Success(val definitions: List<Definition>) : HomeState
        object Loading : HomeState
        object Error : HomeState
    }

    sealed interface HomeCommand {
        data class QueryInput(val query: String) : HomeCommand
        data object SearchActive : HomeCommand

        data class FilterClick(
            val filter: FilterChips
        ) : HomeCommand
    }
}