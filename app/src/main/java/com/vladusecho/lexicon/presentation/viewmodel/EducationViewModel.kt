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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
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

    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)
    private var lastDefinitionId: Int = -1

    init {
        refreshTrigger.tryEmit(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val state = combine(
        refreshTrigger,
        getDefinitionsUseCase().map { it.isEmpty() }.distinctUntilChanged()
    ) { _, isEmpty ->
        isEmpty
    }
        .onEach {
            isDefinitionShown = false
            isImgShown = false
        }
        .flatMapLatest { isEmpty ->
            flow {
                if (isEmpty) {
                    emit(EducationState.Error(ErrorType.NO_WORDS))
                } else {
                    emit(EducationState.Loading)
                    delay(500)
                    getRandomDefinitionUseCase(lastDefinitionId).fold(
                        onSuccess = { definition ->
                            if (definition != null) {
                                lastDefinitionId = definition.id
                                emit(EducationState.Success(definition))
                            } else {
                                getRandomDefinitionUseCase(-1).fold(
                                    onSuccess = { fallbackDefinition ->
                                        if (fallbackDefinition != null) {
                                            lastDefinitionId = fallbackDefinition.id
                                            emit(EducationState.Success(fallbackDefinition))
                                        } else {
                                            emit(EducationState.Error(ErrorType.NO_WORDS))
                                        }
                                    },
                                    onFailure = {
                                        emit(EducationState.Error(ErrorType.UNKNOWN))
                                    }
                                )
                            }
                        },
                        onFailure = {
                            emit(EducationState.Error(ErrorType.UNKNOWN))
                        }
                    )
                }
            }
        }.catch { emit(EducationState.Error(ErrorType.UNKNOWN)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = EducationState.Loading
        )

    fun processCommand(command: EducationCommand) {
        when (command) {
            is EducationCommand.ShowDefinition -> {
                isDefinitionShown = command.isDefinitionShown
            }

            is EducationCommand.ShowImg -> {
                isImgShown = command.isImgShown
            }

            EducationCommand.ShowNextWord -> {
                refreshTrigger.tryEmit(Unit)
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
