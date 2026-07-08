package com.vladusecho.lexicon.presentation.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladusecho.lexicon.data.local.FileManagerHelper
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.usecase.definition.CheckIsFavouriteUseCase
import com.vladusecho.lexicon.domain.usecase.definition.EditDefinitionUseCase
import com.vladusecho.lexicon.domain.usecase.definition.GetDefinitionByIdUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import androidx.core.net.toUri

@HiltViewModel(
    assistedFactory = EditDefinitionViewModelFactory::class
)
class EditDefinitionViewModel @AssistedInject constructor(
    private val getDefinitionByIdUseCase: GetDefinitionByIdUseCase,
    private val editDefinitionUseCase: EditDefinitionUseCase,
    private val checkIsFavouriteUseCase: CheckIsFavouriteUseCase,
    private val fileManagerHelper: FileManagerHelper,
    @Assisted("id") private val id: Int
) : ViewModel() {

    var imageUri by mutableStateOf<Uri?>(null)
        private set

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
            imageUri = definition.imgUri?.toUri()
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
        when (command) {
            is EditDefinitionCommand.EditDefinition -> {
                viewModelScope.launch {
                    val finalImageUri = command.imageUri?.let {
                        fileManagerHelper.saveImageToInternalStorage(it)
                    }
                    val finalDefinition = command.definition.copy(imgUri = finalImageUri)
                    editDefinitionUseCase(finalDefinition)
                    _event.emit(EditDefinitionEvent.FinishEdit)
                }
            }

            is EditDefinitionCommand.UpdateDescription -> {
                _description = command.description
            }

            is EditDefinitionCommand.UpdateWord -> {
                _word = command.word
            }

            is EditDefinitionCommand.UpdateImageUri -> {
                imageUri = command.uri
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
            val definition: Definition,
            val imageUri: Uri? = null
        ) : EditDefinitionCommand

        data class UpdateWord(
            val word: String
        ) : EditDefinitionCommand

        data class UpdateDescription(
            val description: String
        ) : EditDefinitionCommand

        data class UpdateImageUri(
            val uri: Uri
        ) : EditDefinitionCommand
    }

    sealed interface EditDefinitionEvent {
        data object FinishEdit : EditDefinitionEvent
    }
}

@AssistedFactory
interface EditDefinitionViewModelFactory {
    fun create(
        @Assisted("id") id: Int
    ): EditDefinitionViewModel
}