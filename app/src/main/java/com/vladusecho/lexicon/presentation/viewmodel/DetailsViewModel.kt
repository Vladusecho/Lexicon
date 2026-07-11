package com.vladusecho.lexicon.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.usecase.definition.CheckIsFavouriteUseCase
import com.vladusecho.lexicon.domain.usecase.definition.DeleteDefinitionUseCase
import com.vladusecho.lexicon.domain.usecase.definition.GetDefinitionByIdUseCase
import com.vladusecho.lexicon.domain.usecase.definition.ToggleFavouriteUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(
    assistedFactory = DetailsViewModelFactory::class
)
class DetailsViewModel @AssistedInject constructor(
    private val getDefinitionByIdUseCase: GetDefinitionByIdUseCase,
    private val deleteDefinitionUseCase: DeleteDefinitionUseCase,
    private val checkIsFavouriteUseCase: CheckIsFavouriteUseCase,
    private val toggleFavouriteUseCase: ToggleFavouriteUseCase,
    @Assisted("id") private val id: Int
) : ViewModel() {

    val state: StateFlow<DetailsState> = getDefinitionByIdUseCase(id)
        .map { definition ->
            DetailsState.Success(definition) as DetailsState
        }
        .catch { emit(DetailsState.Error) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DetailsState.Loading
        )

    private val _event = MutableSharedFlow<DetailsEvent>()
    val event = _event.asSharedFlow()

    val isFavorite: StateFlow<Boolean> = checkIsFavouriteUseCase(id)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun processCommand(command: DetailsCommand) {
        viewModelScope.launch {
            when (command) {
                is DetailsCommand.DeleteDefinition -> {
                    deleteDefinitionUseCase(id)
                    _event.emit(DetailsEvent.DeleteDefinition)
                }

                is DetailsCommand.ToggleFavourite -> {
                    toggleFavouriteUseCase(id)
                }
            }
        }
    }

    sealed interface DetailsState {
        data class Success(val definition: Definition) : DetailsState
        object Loading : DetailsState
        object Error : DetailsState
    }

    sealed interface DetailsCommand {
        data object DeleteDefinition : DetailsCommand

        data class ToggleFavourite(val id: Int) : DetailsCommand
    }

    sealed interface DetailsEvent {
        data object DeleteDefinition : DetailsEvent
    }
}

@AssistedFactory
interface DetailsViewModelFactory {
    fun create(
        @Assisted("id") id: Int
    ): DetailsViewModel
}