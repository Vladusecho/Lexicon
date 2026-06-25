package com.vladusecho.lexicon.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.usecase.EditDefinitionUseCase
import com.vladusecho.lexicon.domain.usecase.GetDefinitionByIdUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

@HiltViewModel(
    assistedFactory = EditDefinitionViewModel.Factory::class
)
class EditDefinitionViewModel @AssistedInject constructor(
    private val getDefinitionByIdUseCase: GetDefinitionByIdUseCase,
    private val editDefinitionUseCase: EditDefinitionUseCase,
    @Assisted("id") private val id: Int
) : ViewModel() {

    private val _state = MutableStateFlow<EditDefinitionState>(EditDefinitionState.Loading)
    val state = _state.asStateFlow()

    private var _word by mutableStateOf("")
    val word: String
        get() = _word
    private var _description by mutableStateOf("")
    val description: String
        get() = _description

    init {
        viewModelScope.launch {
            getDefinitionByIdUseCase(id)
                .take(1)
                .catch { _state.value = EditDefinitionState.Error }
                .collect { definition ->
                    _word = definition.word
                    _description = definition.description
                    _state.value = EditDefinitionState.Success
                }
        }
    }


    fun processCommand(command: EditDefinitionCommand) {
        viewModelScope.launch {
            when (command) {
                is EditDefinitionCommand.EditDefinition -> {
                    editDefinitionUseCase(command.definition)
                    _state.value = EditDefinitionState.Finish
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

        data object Finish : EditDefinitionState
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

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("id") id: Int
        ): EditDefinitionViewModel
    }
}