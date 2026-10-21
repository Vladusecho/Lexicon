package com.vladusecho.lexicon.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.entity.PartOfSpeech
import com.vladusecho.lexicon.domain.usecase.definition.CreateDefinitionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateDefinitionViewModel @Inject constructor(
    private val createDefinitionUseCase: CreateDefinitionUseCase
) : ViewModel() {

    data class CreateUiState(
        val word: String = "",
        val description: String = "",
        val imageUri: Uri? = null,
        val selectedPartOfSpeech: PartOfSpeech = PartOfSpeech.NOUN,
        val isLoading: Boolean = false
    ) {
        val isValid: Boolean
            get() = word.isNotBlank() && description.isNotBlank()
    }

    private val _uiState = MutableStateFlow(CreateUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<CreateDefinitionEvent>()
    val event = _event.asSharedFlow()

    fun processCommand(command: CreateDefinitionCommand) {
        when (command) {
            is CreateDefinitionCommand.UpdateWord -> {
                _uiState.update { it.copy(word = command.word) }
            }

            is CreateDefinitionCommand.UpdateDescription -> {
                _uiState.update { it.copy(description = command.description) }
            }

            is CreateDefinitionCommand.UpdateImageUri -> {
                _uiState.update { it.copy(imageUri = command.uri) }
            }

            CreateDefinitionCommand.RemoveImage -> {
                _uiState.update { it.copy(imageUri = null) }
            }

            is CreateDefinitionCommand.PickPartOfSpeech -> {
                _uiState.update { it.copy(selectedPartOfSpeech = command.partOfSpeech) }
            }

            CreateDefinitionCommand.CleanData -> {
                _uiState.value = CreateUiState()
            }

            CreateDefinitionCommand.Save -> {
                val state = _uiState.value
                if (!state.isValid) return

                val formattedWord = state.word.trim().replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }

                val definition = Definition(
                    word = formattedWord,
                    description = state.description.trim(),
                    isFavorite = false,
                    partOfSpeech = state.selectedPartOfSpeech
                )

                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true) }
                    createDefinitionUseCase(definition, state.imageUri?.toString())
                        .onSuccess {
                            _uiState.update { it.copy(isLoading = false) }
                            _event.emit(CreateDefinitionEvent.FinishCreate)
                        }
                        .onFailure { error ->
                            _uiState.update { it.copy(isLoading = false) }
                            _event.emit(CreateDefinitionEvent.ShowError(error.message ?: "Unknown error"))
                        }
                }
            }
        }
    }

    sealed interface CreateDefinitionEvent {
        data object FinishCreate : CreateDefinitionEvent
        data class ShowError(val message: String) : CreateDefinitionEvent
    }

    sealed interface CreateDefinitionCommand {
        data class UpdateWord(val word: String) : CreateDefinitionCommand
        data class UpdateDescription(val description: String) : CreateDefinitionCommand
        data class UpdateImageUri(val uri: Uri?) : CreateDefinitionCommand
        data object RemoveImage : CreateDefinitionCommand
        data class PickPartOfSpeech(val partOfSpeech: PartOfSpeech) : CreateDefinitionCommand
        data object CleanData : CreateDefinitionCommand
        data object Save : CreateDefinitionCommand
    }
}
