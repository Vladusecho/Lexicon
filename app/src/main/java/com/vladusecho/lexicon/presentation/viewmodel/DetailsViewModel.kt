package com.vladusecho.lexicon.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.usecase.definition.DeleteDefinitionUseCase
import com.vladusecho.lexicon.domain.usecase.definition.GetDefinitionByIdUseCase
import com.vladusecho.lexicon.domain.usecase.definition.ToggleFavouriteUseCase
import com.vladusecho.lexicon.presentation.navigation.NavScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getDefinitionByIdUseCase: GetDefinitionByIdUseCase,
    private val deleteDefinitionUseCase: DeleteDefinitionUseCase,
    private val toggleFavouriteUseCase: ToggleFavouriteUseCase
) : ViewModel() {

    private val id: Int = savedStateHandle.toRoute<NavScreen.Details>().id

    val state: StateFlow<DetailsUiState> = getDefinitionByIdUseCase(id)
        .map { definition ->
            DetailsUiState.Success(definition) as DetailsUiState
        }
        .catch { emit(DetailsUiState.Error) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DetailsUiState.Loading
        )

    private val _event = MutableSharedFlow<DetailsEvent>()
    val event = _event.asSharedFlow()

    fun processCommand(command: DetailsCommand) {
        viewModelScope.launch {
            when (command) {
                is DetailsCommand.DeleteDefinition -> {
                    deleteDefinitionUseCase(id)
                        .onSuccess { _event.emit(DetailsEvent.DeleteDefinition) }
                        .onFailure { _event.emit(DetailsEvent.ShowError(it.message ?: "Unknown error")) }
                }

                is DetailsCommand.ToggleFavourite -> {
                    val currentState = state.value
                    if (currentState is DetailsUiState.Success) {
                        toggleFavouriteUseCase(id, !currentState.definition.isFavorite)
                    }
                }
            }
        }
    }

    sealed interface DetailsUiState {
        data class Success(val definition: Definition) : DetailsUiState
        data object Loading : DetailsUiState
        data object Error : DetailsUiState
    }

    sealed interface DetailsCommand {
        data object DeleteDefinition : DetailsCommand
        data object ToggleFavourite : DetailsCommand
    }

    sealed interface DetailsEvent {
        data object DeleteDefinition : DetailsEvent
        data class ShowError(val message: String) : DetailsEvent
    }
}
