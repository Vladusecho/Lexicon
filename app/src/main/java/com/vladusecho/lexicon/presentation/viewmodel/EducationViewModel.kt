package com.vladusecho.lexicon.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.entity.ReviewQuality
import com.vladusecho.lexicon.domain.usecase.definition.GetDefinitionsUseCase
import com.vladusecho.lexicon.domain.usecase.definition.GetRandomDefinitionUseCase
import com.vladusecho.lexicon.domain.usecase.education.CalculateSpacedRepetitionUseCase
import com.vladusecho.lexicon.domain.usecase.education.GetDueDefinitionsUseCase
import com.vladusecho.lexicon.domain.usecase.education.ReviewDefinitionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EducationViewModel @Inject constructor(
    private val getDueDefinitionsUseCase: GetDueDefinitionsUseCase,
    private val getDefinitionsUseCase: GetDefinitionsUseCase,
    private val reviewDefinitionUseCase: ReviewDefinitionUseCase,
    private val calculateSpacedRepetitionUseCase: CalculateSpacedRepetitionUseCase,
    private val getRandomDefinitionUseCase: GetRandomDefinitionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<EducationState>(EducationState.Loading)
    val state = _state.asStateFlow()

    private val dueQueue = mutableListOf<Definition>()
    private var isFreePractice = false
    private var reviewedCountInSession = 0
    private var lastRandomId = -1

    init {
        startSession()
    }

    fun startSession() {
        viewModelScope.launch {
            _state.value = EducationState.Loading
            isFreePractice = false
            val allWords = getDefinitionsUseCase().first()
            if (allWords.isEmpty()) {
                _state.value = EducationState.Error(ErrorType.NO_WORDS)
                return@launch
            }

            val dueWords = getDueDefinitionsUseCase(System.currentTimeMillis()).first()
            dueQueue.clear()
            dueQueue.addAll(dueWords)

            if (dueQueue.isNotEmpty()) {
                showNextDueWord()
            } else {
                _state.value = EducationState.AllDone(reviewedCount = reviewedCountInSession)
            }
        }
    }

    private fun showNextDueWord() {
        if (dueQueue.isNotEmpty()) {
            val nextDefinition = dueQueue.removeAt(0)
            val intervals = calculateSpacedRepetitionUseCase.previewIntervalDays(nextDefinition)
            _state.value = EducationState.Success(
                definition = nextDefinition,
                isFlipped = false,
                intervalPreviews = intervals,
                remainingCount = dueQueue.size + 1,
                isFreePracticeMode = false
            )
        } else {
            _state.value = EducationState.AllDone(reviewedCount = reviewedCountInSession)
        }
    }

    fun startFreePractice() {
        isFreePractice = true
        loadNextRandomWord()
    }

    private fun loadNextRandomWord() {
        viewModelScope.launch {
            _state.value = EducationState.Loading
            val definition = getRandomDefinitionUseCase(lastRandomId).getOrNull()
                ?: getRandomDefinitionUseCase(-1).getOrNull()

            if (definition != null) {
                lastRandomId = definition.id
                val intervals = calculateSpacedRepetitionUseCase.previewIntervalDays(definition)
                _state.value = EducationState.Success(
                    definition = definition,
                    isFlipped = false,
                    intervalPreviews = intervals,
                    remainingCount = 0,
                    isFreePracticeMode = true
                )
            } else {
                _state.value = EducationState.Error(ErrorType.NO_WORDS)
            }
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

            is EducationCommand.SubmitReview -> {
                val current = _state.value
                if (current is EducationState.Success) {
                    viewModelScope.launch {
                        reviewDefinitionUseCase(current.definition, command.quality)
                        reviewedCountInSession++

                        if (command.quality == ReviewQuality.AGAIN && !current.isFreePracticeMode) {
                            dueQueue.add(current.definition)
                        }

                        if (current.isFreePracticeMode) {
                            loadNextRandomWord()
                        } else {
                            showNextDueWord()
                        }
                    }
                }
            }

            is EducationCommand.StartFreePractice -> {
                startFreePractice()
            }

            is EducationCommand.RestartDueSession -> {
                startSession()
            }
        }
    }

    sealed interface EducationState {
        data class Success(
            val definition: Definition,
            val isFlipped: Boolean = false,
            val intervalPreviews: Map<ReviewQuality, Int> = emptyMap(),
            val remainingCount: Int = 0,
            val isFreePracticeMode: Boolean = false
        ) : EducationState

        data class AllDone(
            val reviewedCount: Int
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
        data class SubmitReview(val quality: ReviewQuality) : EducationCommand
        data object StartFreePractice : EducationCommand
        data object RestartDueSession : EducationCommand
    }
}
