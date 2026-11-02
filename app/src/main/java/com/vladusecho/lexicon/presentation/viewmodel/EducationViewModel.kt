package com.vladusecho.lexicon.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.entity.ReviewQuality
import com.vladusecho.lexicon.domain.usecase.definition.GetAllCategoriesUseCase
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
    private val getRandomDefinitionUseCase: GetRandomDefinitionUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<EducationState>(EducationState.Loading())
    val state = _state.asStateFlow()

    private val dueQueue = mutableListOf<Definition>()
    private var isFreePractice = false
    private var reviewedCountInSession = 0
    private var lastRandomId = -1

    private var currentCategory: String? = null
    private var isOnlyFavorites: Boolean = false
    private var availableCategories: List<String> = emptyList()

    init {
        viewModelScope.launch {
            getAllCategoriesUseCase().collect { categories ->
                availableCategories = categories
                updateCurrentStateFilters()
            }
        }
        startSession()
    }

    fun startSession() {
        viewModelScope.launch {
            _state.value = EducationState.Loading(
                categories = availableCategories,
                selectedCategory = currentCategory,
                onlyFavorites = isOnlyFavorites
            )
            isFreePractice = false
            val allWords = getDefinitionsUseCase().first()
            if (allWords.isEmpty()) {
                _state.value = EducationState.Error(
                    errorType = ErrorType.NO_WORDS,
                    categories = availableCategories,
                    selectedCategory = currentCategory,
                    onlyFavorites = isOnlyFavorites
                )
                return@launch
            }

            val matchingWords = allWords.filter { word ->
                (currentCategory == null || word.category.equals(currentCategory, ignoreCase = true)) &&
                        (!isOnlyFavorites || word.isFavorite)
            }
            if (matchingWords.isEmpty()) {
                _state.value = EducationState.Error(
                    errorType = ErrorType.EMPTY_CATEGORY,
                    categories = availableCategories,
                    selectedCategory = currentCategory,
                    onlyFavorites = isOnlyFavorites
                )
                return@launch
            }

            val dueWords = getDueDefinitionsUseCase(
                currentTime = System.currentTimeMillis(),
                category = currentCategory,
                onlyFavorites = isOnlyFavorites
            ).first()

            dueQueue.clear()
            dueQueue.addAll(dueWords)

            if (dueQueue.isNotEmpty()) {
                showNextDueWord()
            } else {
                _state.value = EducationState.AllDone(
                    reviewedCount = reviewedCountInSession,
                    categories = availableCategories,
                    selectedCategory = currentCategory,
                    onlyFavorites = isOnlyFavorites
                )
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
                isFreePracticeMode = false,
                categories = availableCategories,
                selectedCategory = currentCategory,
                onlyFavorites = isOnlyFavorites
            )
        } else {
            _state.value = EducationState.AllDone(
                reviewedCount = reviewedCountInSession,
                categories = availableCategories,
                selectedCategory = currentCategory,
                onlyFavorites = isOnlyFavorites
            )
        }
    }

    fun startFreePractice() {
        isFreePractice = true
        loadNextRandomWord()
    }

    private fun loadNextRandomWord() {
        viewModelScope.launch {
            _state.value = EducationState.Loading(
                categories = availableCategories,
                selectedCategory = currentCategory,
                onlyFavorites = isOnlyFavorites
            )
            val definition = getRandomDefinitionUseCase(
                excludedId = lastRandomId,
                category = currentCategory,
                onlyFavorites = isOnlyFavorites
            ).getOrNull() ?: getRandomDefinitionUseCase(
                excludedId = -1,
                category = currentCategory,
                onlyFavorites = isOnlyFavorites
            ).getOrNull()

            if (definition != null) {
                lastRandomId = definition.id
                val intervals = calculateSpacedRepetitionUseCase.previewIntervalDays(definition)
                _state.value = EducationState.Success(
                    definition = definition,
                    isFlipped = false,
                    intervalPreviews = intervals,
                    remainingCount = 0,
                    isFreePracticeMode = true,
                    categories = availableCategories,
                    selectedCategory = currentCategory,
                    onlyFavorites = isOnlyFavorites
                )
            } else {
                _state.value = EducationState.Error(
                    errorType = if (currentCategory != null || isOnlyFavorites) ErrorType.EMPTY_CATEGORY else ErrorType.NO_WORDS,
                    categories = availableCategories,
                    selectedCategory = currentCategory,
                    onlyFavorites = isOnlyFavorites
                )
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

            is EducationCommand.SelectCategory -> {
                if (currentCategory != command.category) {
                    currentCategory = command.category
                    startSession()
                }
            }

            is EducationCommand.ToggleOnlyFavorites -> {
                isOnlyFavorites = !isOnlyFavorites
                startSession()
            }
        }
    }

    private fun updateCurrentStateFilters() {
        val current = _state.value
        _state.value = when (current) {
            is EducationState.Success -> current.copy(
                categories = availableCategories,
                selectedCategory = currentCategory,
                onlyFavorites = isOnlyFavorites
            )
            is EducationState.AllDone -> current.copy(
                categories = availableCategories,
                selectedCategory = currentCategory,
                onlyFavorites = isOnlyFavorites
            )
            is EducationState.Error -> current.copy(
                categories = availableCategories,
                selectedCategory = currentCategory,
                onlyFavorites = isOnlyFavorites
            )
            is EducationState.Loading -> current.copy(
                categories = availableCategories,
                selectedCategory = currentCategory,
                onlyFavorites = isOnlyFavorites
            )
        }
    }

    sealed interface EducationState {
        val categories: List<String>
        val selectedCategory: String?
        val onlyFavorites: Boolean

        data class Success(
            val definition: Definition,
            val isFlipped: Boolean = false,
            val intervalPreviews: Map<ReviewQuality, Int> = emptyMap(),
            val remainingCount: Int = 0,
            val isFreePracticeMode: Boolean = false,
            override val categories: List<String> = emptyList(),
            override val selectedCategory: String? = null,
            override val onlyFavorites: Boolean = false
        ) : EducationState

        data class AllDone(
            val reviewedCount: Int,
            override val categories: List<String> = emptyList(),
            override val selectedCategory: String? = null,
            override val onlyFavorites: Boolean = false
        ) : EducationState

        data class Error(
            val errorType: ErrorType,
            override val categories: List<String> = emptyList(),
            override val selectedCategory: String? = null,
            override val onlyFavorites: Boolean = false
        ) : EducationState

        data class Loading(
            override val categories: List<String> = emptyList(),
            override val selectedCategory: String? = null,
            override val onlyFavorites: Boolean = false
        ) : EducationState
    }

    enum class ErrorType {
        NO_WORDS,
        EMPTY_CATEGORY,
        UNKNOWN
    }

    sealed interface EducationCommand {
        data object FlipCard : EducationCommand
        data class SubmitReview(val quality: ReviewQuality) : EducationCommand
        data object StartFreePractice : EducationCommand
        data object RestartDueSession : EducationCommand
        data class SelectCategory(val category: String?) : EducationCommand
        data object ToggleOnlyFavorites : EducationCommand
    }
}
