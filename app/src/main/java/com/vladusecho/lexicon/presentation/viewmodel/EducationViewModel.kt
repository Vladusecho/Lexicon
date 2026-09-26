package com.vladusecho.lexicon.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.usecase.definition.GetDefinitionsUseCase
import com.vladusecho.lexicon.domain.usecase.definition.GetRandomDefinitionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EducationViewModel @Inject constructor(
    private val getRandomDefinitionUseCase: GetRandomDefinitionUseCase,
    private val getDefinitionsUseCase: GetDefinitionsUseCase
) : ViewModel() {

    var isDefinitionShown by mutableStateOf(false)
        private set

    var isImgShown by mutableStateOf(false)
        private set

    private val _state = MutableStateFlow<EducationState>(EducationState.Loading)
    val state = _state.asStateFlow()

    private var lastDefinitionId: Int = -1

    init {
        viewModelScope.launch {
            getDefinitionsUseCase()
                .map { it.isEmpty() }
                .distinctUntilChanged()
                .collect { isEmpty ->
                    if (isEmpty) {
                        _state.value = EducationState.Error(ErrorType.NO_WORDS)
                    } else {
                        if (_state.value !is EducationState.Success) {
                            loadNextWord()
                        }
                    }
                }
        }
    }

    private fun loadNextWord() {
        viewModelScope.launch {
            isDefinitionShown = false
            isImgShown = false
            _state.value = EducationState.Loading
            getRandomDefinitionUseCase(lastDefinitionId)
            delay(500)
            getRandomDefinitionUseCase(lastDefinitionId).fold(
                onSuccess = { definition ->
                    if (definition != null) {
                        lastDefinitionId = definition.id
                        _state.value = EducationState.Success(definition)
                    } else {
                        getRandomDefinitionUseCase(-1).fold(
                            onSuccess = { fallbackDefinition ->
                                if (fallbackDefinition != null) {
                                    lastDefinitionId = fallbackDefinition.id
                                    _state.value = EducationState.Success(fallbackDefinition)
                                } else {
                                    _state.value = EducationState.Error(ErrorType.NO_WORDS)
                                }
                            },
                            onFailure = {
                                _state.value = EducationState.Error(ErrorType.UNKNOWN)
                            }
                        )
                    }
                },
                onFailure = {
                    _state.value = EducationState.Error(ErrorType.UNKNOWN)
                }
            )
        }
    }


    fun processCommand(command: EducationCommand) {
        when (command) {
            is EducationCommand.ShowDefinition -> {
                isDefinitionShown = command.isDefinitionShown
            }

            is EducationCommand.ShowImg -> {
                isImgShown = command.isImgShown
            }

            EducationCommand.ShowNextWord -> {
                loadNextWord()
            }
        }
    }

    sealed interface EducationState {
        object Loading : EducationState
        data class Success(
            val definition: Definition
        ) : EducationState

        data class Error(
            val errorType: ErrorType
        ) : EducationState
    }

    sealed interface EducationCommand {
        data class ShowDefinition(
            val isDefinitionShown: Boolean
        ) : EducationCommand

        data class ShowImg(
            val isImgShown: Boolean
        ) : EducationCommand

        data object ShowNextWord : EducationCommand
    }

    enum class ErrorType {
        NO_WORDS,
        UNKNOWN
    }
}
