package com.vladusecho.lexicon.presentation.viewmodel

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.usecase.GetDefinitionByIdUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel(
    assistedFactory = DetailsViewModel.Factory::class
)
class DetailsViewModel @AssistedInject constructor(
    private val getDefinitionByIdUseCase: GetDefinitionByIdUseCase,
    @Assisted("id") private val id: Int
) : ViewModel() {

    private val _state = MutableStateFlow<DetailsState>(DetailsState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = DetailsState.Loading
            try {
                val definition = getDefinitionByIdUseCase(id)
                _state.value = DetailsState.Success(definition)
            } catch (e: Exception) {
                _state.value = DetailsState.Error
            }
        }
    }

    sealed interface DetailsState {
        data class Success(val definition: Definition) : DetailsState
        object Loading : DetailsState
        object Error : DetailsState
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("id") id: Int
        ) : DetailsViewModel
    }
}