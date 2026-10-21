package com.vladusecho.lexicon.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.usecase.definition.GetDefinitionsUseCase
import com.vladusecho.lexicon.domain.usecase.definition.GetRandomDefinitionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
                    } else if (_state.value !is EducationState.Success) {
                        loadNextWord()
                    }
                }
        }
    }

    private fun loadNextWord() {
        viewModelScope.launch {
            _state.value = EducationState.Loading
            getRandomDefinitionUseCase(lastDefinitionId).fold(
                onSuccess = { definition ->
                    if (definition != null) {
                        lastDefinitionId = definition.id
                        _state.value = EducationState.Success(
                            definition = definition,
                            isFlipped = false
                        )
                    } else {
                        getRandomDefinitionUseCase(-1).fold(
                            onSuccess = { fallbackDefinition ->
                                if (fallbackDefinition != null) {
                                    lastDefinitionId = fallbackDefinition.id
                                    _state.value = EducationState.Success(
                                        definition = fallbackDefinition,
                                        isFlipped = false
                                    )
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
            is EducationCommand.FlipCard -> {
                val current = _state.value
                if (current is EducationState.Success) {
                    _state.value = current.copy(isFlipped = !current.isFlipped)
                }
            }

            is EducationCommand.ShowNextWord -> {
                loadNextWord()
            }
        }
    }

    sealed interface EducationState {
        data class Success(
            val definition: Definition,
            val isFlipped: Boolean = false
        ) : EducationState

        data class Error(val errorType: ErrorType) : EducationState
        data object Loading : EducationState
    }

    enum class ErrorType {
        NO_WORDS,
        UNKNOWN
    }

    sealed interface EducationCommand {
        data object FlipCard : EducationCommand
        data object ShowNextWord : EducationCommand
    }
}
