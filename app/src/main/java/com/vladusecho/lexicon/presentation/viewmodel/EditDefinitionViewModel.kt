package com.vladusecho.lexicon.presentation.viewmodel

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.entity.PartOfSpeech
import com.vladusecho.lexicon.domain.usecase.definition.EditDefinitionUseCase
import com.vladusecho.lexicon.domain.usecase.definition.GetDefinitionByIdUseCase
import com.vladusecho.lexicon.presentation.navigation.NavScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditDefinitionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getDefinitionByIdUseCase: GetDefinitionByIdUseCase,
    private val editDefinitionUseCase: EditDefinitionUseCase
) : ViewModel() {

    private val id: Int = savedStateHandle.toRoute<NavScreen.EditDefinition>().id

    data class EditUiState(
        val word: String = "",
        val description: String = "",
        val imageUri: Uri? = null,
        val isNewImagePicked: Boolean = false,
        val isImageRemoved: Boolean = false,
        val selectedPartOfSpeech: PartOfSpeech = PartOfSpeech.NOUN,
        val isFavorite: Boolean = false,
        val isLoading: Boolean = false,
        val isInitialLoaded: Boolean = false
    ) {
        val isValid: Boolean
            get() = word.isNotBlank() && description.isNotBlank()
    }

    private val _uiState = MutableStateFlow(EditUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<EditDefinitionEvent>()
    val event = _event.asSharedFlow()

    init {
        viewModelScope.launch {
            getDefinitionByIdUseCase(id).take(1).collect { definition ->
                _uiState.update {
                    it.copy(
                        word = definition.word,
                        description = definition.description,
                        imageUri = definition.imgUri?.toUri(),
                        selectedPartOfSpeech = definition.partOfSpeech,
                        isFavorite = definition.isFavorite,
                        isInitialLoaded = true
                    )
                }
            }
        }
    }

    fun processCommand(command: EditDefinitionCommand) {
        when (command) {
            is EditDefinitionCommand.UpdateWord -> {
                _uiState.update { it.copy(word = command.word) }
            }

            is EditDefinitionCommand.UpdateDescription -> {
                _uiState.update { it.copy(description = command.description) }
            }

            is EditDefinitionCommand.UpdateImageUri -> {
                _uiState.update {
                    it.copy(
                        imageUri = command.uri,
                        isNewImagePicked = true,
                        isImageRemoved = false
                    )
                }
            }

            EditDefinitionCommand.RemoveImage -> {
                _uiState.update {
                    it.copy(
                        imageUri = null,
                        isNewImagePicked = false,
                        isImageRemoved = true
                    )
                }
            }

            is EditDefinitionCommand.PickPartOfSpeech -> {
                _uiState.update { it.copy(selectedPartOfSpeech = command.partOfSpeech) }
            }

            EditDefinitionCommand.Save -> {
                val state = _uiState.value
                if (!state.isValid) return

                val formattedWord = state.word.trim().replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }

                val updatedDefinition = Definition(
                    id = id,
                    word = formattedWord,
                    description = state.description.trim(),
                    isFavorite = state.isFavorite,
                    partOfSpeech = state.selectedPartOfSpeech
                )

                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true) }
                    editDefinitionUseCase(
                        definition = updatedDefinition,
                        newImageUri = if (state.isNewImagePicked) state.imageUri?.toString() else null,
                        isImageRemoved = state.isImageRemoved
                    )
                        .onSuccess {
                            _uiState.update { it.copy(isLoading = false) }
                            _event.emit(EditDefinitionEvent.FinishEdit)
                        }
                        .onFailure { error ->
                            _uiState.update { it.copy(isLoading = false) }
                            _event.emit(EditDefinitionEvent.ShowError(error.message ?: "Unknown error"))
                        }
                }
            }
        }
    }

    sealed interface EditDefinitionEvent {
        data object FinishEdit : EditDefinitionEvent
        data class ShowError(val message: String) : EditDefinitionEvent
    }

    sealed interface EditDefinitionCommand {
        data class UpdateWord(val word: String) : EditDefinitionCommand
        data class UpdateDescription(val description: String) : EditDefinitionCommand
        data class UpdateImageUri(val uri: Uri?) : EditDefinitionCommand
        data object RemoveImage : EditDefinitionCommand
        data class PickPartOfSpeech(val partOfSpeech: PartOfSpeech) : EditDefinitionCommand
        data object Save : EditDefinitionCommand
    }
}
