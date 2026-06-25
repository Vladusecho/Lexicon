package com.vladusecho.lexicon.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.usecase.CreateDefinitionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateDefinitionViewModel @Inject constructor(
    private val createDefinitionUseCase: CreateDefinitionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CreateDefinitionState>(CreateDefinitionState.Success)
    val state = _state.asStateFlow()

    private var _word by mutableStateOf("")
    val word: String
        get() = _word
    private var _description by mutableStateOf("")
    val description: String
        get() = _description

    fun processCommand(command: CreateDefinitionCommand) {
        viewModelScope.launch {
            when (command) {
                is CreateDefinitionCommand.CreateDefinition -> {
                    createDefinitionUseCase(command.definition)
                    _state.value = CreateDefinitionState.Finish
                }

                is CreateDefinitionCommand.UpdateDescription -> {
                    _description = command.description
                }

                is CreateDefinitionCommand.UpdateWord -> {
                    _word = command.word
                }
            }
        }
    }

    sealed interface CreateDefinitionState {
        object Success : CreateDefinitionState

        object Finish : CreateDefinitionState
        object Loading : CreateDefinitionState
        object Error : CreateDefinitionState
    }

    sealed interface CreateDefinitionCommand {
        data class CreateDefinition(
            val definition: Definition
        ) : CreateDefinitionCommand

        data class UpdateWord(
            val word: String
        ) : CreateDefinitionCommand

        data class UpdateDescription(
            val description: String
        ) : CreateDefinitionCommand
    }
}