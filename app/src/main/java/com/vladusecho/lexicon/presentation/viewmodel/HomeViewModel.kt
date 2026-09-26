package com.vladusecho.lexicon.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.entity.PartOfSpeech
import com.vladusecho.lexicon.domain.usecase.definition.GetDefinitionsUseCase
import com.vladusecho.lexicon.domain.usecase.definition.ToggleFavouriteUseCase
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getDefinitionsUseCase: GetDefinitionsUseCase,
    val toggleFavouriteUseCase: ToggleFavouriteUseCase
) : ViewModel() {

    var query by mutableStateOf("")
        private set

    var selectedFilter by mutableStateOf(FilterChips.ALL)
        private set


    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val state = combine(
        snapshotFlow { query }
            .debounce { currentQuery ->
                if (currentQuery.isEmpty()) 0L else 500L
            }
            .distinctUntilChanged(),
        snapshotFlow { selectedFilter },
        getDefinitionsUseCase()
    ) { query, selectedFilter, definitions ->
        Triple(
            query,
            selectedFilter,
            definitions
        )
    }
        .map { (query, selectedFilter, definitions) ->

            if (definitions.isEmpty()) {
                return@map HomeState.Error(ErrorType.NO_WORDS)
            }

            val showAlphabetHeaders =
                selectedFilter != FilterChips.RECENT

            val filteredList = when (selectedFilter) {
                FilterChips.ALL -> {
                    definitions
                }

                FilterChips.FAVORITE -> {
                    definitions.filter { definition -> definition.isFavorite }
                }

                FilterChips.RECENT -> {
                    definitions.sortedByDescending { definition -> definition.id }.take(3)
                }

                FilterChips.VERB -> {
                    definitions.filter { definition -> definition.partOfSpeech == PartOfSpeech.VERB }
                }
                FilterChips.NOUN -> {
                    definitions.filter { definition -> definition.partOfSpeech == PartOfSpeech.NOUN }
                }
                FilterChips.ADVERB -> {
                    definitions.filter { definition -> definition.partOfSpeech == PartOfSpeech.ADVERB }
                }
                FilterChips.ADJECTIVE -> {
                    definitions.filter { definition -> definition.partOfSpeech == PartOfSpeech.ADJECTIVE }
                }

                FilterChips.PARTICIPLE -> {
                    definitions.filter { definition -> definition.partOfSpeech == PartOfSpeech.PARTICIPLE }
                }
                FilterChips.ADVERBIAL_PARTICIPLE -> {
                    definitions.filter { definition -> definition.partOfSpeech == PartOfSpeech.ADVERBIAL_PARTICIPLE }
                }
            }.filter { definition -> definition.word.startsWith(query.trim(), ignoreCase = true) }
            HomeState.Success(
                filteredList,
                showAlphabetHeaders
            ) as HomeState
        }
        .catch { emit(HomeState.Error(ErrorType.UNKNOWN)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeState.Loading
        )

    fun processCommand(command: HomeCommand) {
        when (command) {
            is HomeCommand.QueryInput -> {
                query = command.query
            }

            is HomeCommand.FilterClick -> {
                selectedFilter = command.filter
            }

            is HomeCommand.ToggleFavourite -> {
                viewModelScope.launch {
                    toggleFavouriteUseCase(command.id, isFavourite = command.isFavourite)
                }
            }
        }
    }

    sealed interface HomeState {
        data class Success(
            val definitions: List<Definition>,
            val showAlphabetHeaders: Boolean
        ) : HomeState

        object Loading : HomeState
        data class Error(
            val errorType: ErrorType
        ) : HomeState
    }

    sealed interface HomeCommand {
        data class QueryInput(val query: String) : HomeCommand

        data class ToggleFavourite(
            val id: Int,
            val isFavourite: Boolean
        ) : HomeCommand

        data class FilterClick(
            val filter: FilterChips
        ) : HomeCommand
    }

    enum class ErrorType {
        NO_WORDS,
        UNKNOWN
    }
}