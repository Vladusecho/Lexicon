package com.vladusecho.lexicon.presentation.viewmodel

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladusecho.lexicon.R
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.entity.PartOfSpeech
import com.vladusecho.lexicon.domain.usecase.definition.GetDefinitionsUseCase
import com.vladusecho.lexicon.domain.usecase.definition.ToggleFavouriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class FilterChips(
    @StringRes val labelResId: Int,
    @DrawableRes val iconId: Int
) {
    ALL(
        labelResId = R.string.filter_all,
        iconId = R.drawable.ic_items
    ),
    FAVORITE(
        labelResId = R.string.filter_favorite,
        iconId = R.drawable.ic_favorite
    ),
    RECENT(
        labelResId = R.string.filter_recent,
        iconId = R.drawable.ic_clock
    ),
    PART_OF_SPEECH(
        labelResId = R.string.filter_part_of_speech,
        iconId = R.drawable.ic_cubes
    )
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    getDefinitionsUseCase: GetDefinitionsUseCase,
    private val toggleFavouriteUseCase: ToggleFavouriteUseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedFilter = MutableStateFlow(FilterChips.ALL)
    val selectedFilter: StateFlow<FilterChips> = _selectedFilter.asStateFlow()

    private val _selectedPartOfSpeech = MutableStateFlow<PartOfSpeech?>(null)
    val selectedPartOfSpeech: StateFlow<PartOfSpeech?> = _selectedPartOfSpeech.asStateFlow()

    val state: StateFlow<HomeState> = combine(
        _query,
        _selectedPartOfSpeech,
        _selectedFilter,
        getDefinitionsUseCase()
    ) { query, selectedPartOfSpeech, selectedFilter, definitions ->
        if (definitions.isEmpty()) {
            return@combine HomeState.Empty(ErrorType.NO_WORDS)
        }

        val filteredByChip = when (selectedFilter) {
            FilterChips.ALL -> definitions
            FilterChips.FAVORITE -> definitions.filter { it.isFavorite }
            FilterChips.RECENT -> definitions.sortedByDescending { it.createdAt.takeIf { t -> t > 0 } ?: it.id.toLong() }
            FilterChips.PART_OF_SPEECH -> {
                if (selectedPartOfSpeech != null) {
                    definitions.filter { it.partOfSpeech == selectedPartOfSpeech }
                } else {
                    definitions
                }
            }
        }

        val trimmedQuery = query.trim()
        val finalList = if (trimmedQuery.isEmpty()) {
            filteredByChip
        } else {
            filteredByChip.filter { definition ->
                definition.word.contains(trimmedQuery, ignoreCase = true) ||
                    definition.description.contains(trimmedQuery, ignoreCase = true)
            }
        }

        if (finalList.isEmpty()) {
            HomeState.Empty(ErrorType.NO_SEARCH_RESULTS)
        } else {
            HomeState.Success(
                definitions = finalList,
                showAlphabetHeaders = selectedFilter != FilterChips.RECENT && trimmedQuery.isEmpty()
            )
        }
    }
        .catch { emit(HomeState.Empty(ErrorType.NO_WORDS)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeState.Loading
        )

    fun processCommand(command: HomeCommand) {
        when (command) {
            is HomeCommand.QueryInput -> {
                _query.value = command.query
            }

            is HomeCommand.FilterClick -> {
                _selectedFilter.value = command.filter
                if (command.filter != FilterChips.PART_OF_SPEECH) {
                    _selectedPartOfSpeech.value = null
                }
            }

            is HomeCommand.ToggleFavourite -> {
                viewModelScope.launch {
                    toggleFavouriteUseCase(command.id, isFavourite = command.isFavourite)
                }
            }

            is HomeCommand.PartOfSpeechClick -> {
                _selectedPartOfSpeech.value = command.partOfSpeech
            }
        }
    }

    sealed interface HomeState {
        data class Success(
            val definitions: List<Definition>,
            val showAlphabetHeaders: Boolean
        ) : HomeState

        data class Empty(
            val errorType: ErrorType
        ) : HomeState

        data object Loading : HomeState
    }

    enum class ErrorType {
        NO_WORDS,
        NO_SEARCH_RESULTS
    }

    sealed interface HomeCommand {
        data class QueryInput(val query: String) : HomeCommand
        data class FilterClick(val filter: FilterChips) : HomeCommand
        data class ToggleFavourite(val id: Int, val isFavourite: Boolean) : HomeCommand
        data class PartOfSpeechClick(val partOfSpeech: PartOfSpeech?) : HomeCommand
    }
}
