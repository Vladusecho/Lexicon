package com.vladusecho.lexicon.presentation.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladusecho.lexicon.data.local.FileManagerHelper
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.usecase.definition.CreateDefinitionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateDefinitionViewModel @Inject constructor(
    private val createDefinitionUseCase: CreateDefinitionUseCase,
    private val fileManagerHelper: FileManagerHelper
) : ViewModel() {

    private val _state = MutableStateFlow<CreateDefinitionState>(CreateDefinitionState.Success)
    val state = _state.asStateFlow()

    var imageUri by mutableStateOf<Uri?>(null)
        private set

    private var _word by mutableStateOf("")
    val word: String
        get() = _word
    private var _description by mutableStateOf("")
    val description: String
        get() = _description

    val allCorrect get() = word.isNotBlank() && description.isNotBlank()
    private val _event = MutableSharedFlow<CreateDefinitionEvent>()
    val event = _event.asSharedFlow()

    fun processCommand(command: CreateDefinitionCommand) {
        when (command) {
            is CreateDefinitionCommand.CreateDefinition -> {

                viewModelScope.launch {
                    val finalImagePath = command.imageUri?.let {
                        fileManagerHelper.saveImageToInternalStorage(it)
                    }
                    val finalDefinition = command.definition.copy(imgUri = finalImagePath)
                    createDefinitionUseCase(finalDefinition)
                    _event.emit(CreateDefinitionEvent.FinishCreate)
                }
            }

            is CreateDefinitionCommand.UpdateDescription -> {
                _description = command.description
            }

            is CreateDefinitionCommand.UpdateWord -> {
                _word = command.word
            }

            is CreateDefinitionCommand.UpdateImageUri -> {
                imageUri = command.uri
            }

            CreateDefinitionCommand.RemoveImage -> {
                imageUri = null
            }
        }
    }

    sealed interface CreateDefinitionState {
        object Success : CreateDefinitionState
        object Loading : CreateDefinitionState
        object Error : CreateDefinitionState
    }

    sealed interface CreateDefinitionCommand {
        data class CreateDefinition(
            val definition: Definition,
            val imageUri: Uri? = null
        ) : CreateDefinitionCommand

        data class UpdateWord(
            val word: String
        ) : CreateDefinitionCommand

        data class UpdateDescription(
            val description: String
        ) : CreateDefinitionCommand

        data class UpdateImageUri(
            val uri: Uri
        ) : CreateDefinitionCommand

        data object RemoveImage : CreateDefinitionCommand
    }

    sealed interface CreateDefinitionEvent {
        data object FinishCreate : CreateDefinitionEvent
    }
}