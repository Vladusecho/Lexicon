package com.vladusecho.lexicon.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.usecase.CheckIsFavouriteUseCase
import com.vladusecho.lexicon.domain.usecase.EditDefinitionUseCase
import com.vladusecho.lexicon.domain.usecase.GetDefinitionByIdUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

@HiltViewModel(
    assistedFactory = EditDefinitionViewModel.Factory::class
)
class EditDefinitionViewModel @AssistedInject constructor(
    private val getDefinitionByIdUseCase: GetDefinitionByIdUseCase,
    private val editDefinitionUseCase: EditDefinitionUseCase,
    private val checkIsFavouriteUseCase: CheckIsFavouriteUseCase,
    @Assisted("id") private val id: Int
) : ViewModel() {

    private var _word by mutableStateOf("")
    val word: String
        get() = _word
    private var _description by mutableStateOf("")
    val description: String
        get() = _description

    val isFavorite = checkIsFavouriteUseCase(id)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    val state = getDefinitionByIdUseCase(id)
        .take(1)
        .map { definition ->
            _word = definition.word
            _description = definition.description
            EditDefinitionState.Success as EditDefinitionState
        }
        .catch { emit(EditDefinitionState.Error) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = EditDefinitionState.Loading
        )

    private val _event = MutableSharedFlow<EditDefinitionEvent>()
    val event = _event.asSharedFlow()

    fun processCommand(command: EditDefinitionCommand) {
        viewModelScope.launch {
            when (command) {
                is EditDefinitionCommand.EditDefinition -> {
                    editDefinitionUseCase(command.definition)
                    _event.emit(EditDefinitionEvent.FinishEdit)
                }

                is EditDefinitionCommand.UpdateDescription -> {
                    _description = command.description
                }

                is EditDefinitionCommand.UpdateWord -> {
                    _word = command.word
                }
            }
        }
    }

    sealed interface EditDefinitionState {
        data object Success : EditDefinitionState
        data object Loading : EditDefinitionState
        data object Error : EditDefinitionState
    }

    sealed interface EditDefinitionCommand {
        data class EditDefinition(
            val definition: Definition
        ) : EditDefinitionCommand

        data class UpdateWord(
            val word: String
        ) : EditDefinitionCommand

        data class UpdateDescription(
            val description: String
        ) : EditDefinitionCommand
    }

    sealed interface EditDefinitionEvent {
        data object FinishEdit : EditDefinitionEvent
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("id") id: Int
        ): EditDefinitionViewModel
    }
}