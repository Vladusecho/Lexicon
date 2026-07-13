package com.vladusecho.lexicon.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.usecase.definition.GetFavouritesUseCase
import com.vladusecho.lexicon.domain.usecase.definition.SearchDefinitionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    getFavoritesUseCase: GetFavouritesUseCase,
    private val searchDefinitionUseCase: SearchDefinitionUseCase
) : ViewModel() {

    var query by mutableStateOf("")
        private set

    var isSearchActive by mutableStateOf(false)
        private set


    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val state = snapshotFlow { query }
        .debounce(500)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                getFavoritesUseCase()
            } else {
                searchDefinitionUseCase(query, true)
            }
        }
        .map { FavouriteState.Success(it) as FavouriteState}
        .catch { emit(FavouriteState.Error) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FavouriteState.Loading
        )

    val favouritesCount = state.map {
        when (it) {
            is FavouriteState.Success -> it.favourites.size
            else -> 0
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun processCommand(command: FavouriteCommand) {
        when (command) {
            is FavouriteCommand.QueryInput -> {
                query = command.query
            }

            FavouriteCommand.SearchActive -> {
                isSearchActive = !isSearchActive
                if (!isSearchActive) query = ""
            }
        }
    }

    sealed interface FavouriteState {
        data class Success(
            val favourites: List<Definition>,
        ) : FavouriteState

        object Loading : FavouriteState
        object Error : FavouriteState
    }

    sealed interface FavouriteCommand {
        data class QueryInput(val query: String) : FavouriteCommand

        data object SearchActive : FavouriteCommand
    }
}