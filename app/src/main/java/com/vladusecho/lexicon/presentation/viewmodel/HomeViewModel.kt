package com.vladusecho.lexicon.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.usecase.GetDefinitionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDefinitionsUseCase: GetDefinitionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state = _state.asStateFlow()

    fun processCommand(command: HomeCommand) {
        when (command) {
            is HomeCommand.OpenDefinition -> {

            }
        }
    }


    init {
        viewModelScope.launch {
            _state.value = HomeState.Loading
            try {
                val definitions = getDefinitionsUseCase()
                _state.value = HomeState.Success(definitions)
            } catch (e: Exception) {
                _state.value = HomeState.Error
            }
        }
    }

    sealed interface HomeState {
        data class Success(val definitions: List<Definition>) : HomeState
        object Loading : HomeState
        object Error : HomeState
    }

    sealed interface HomeCommand {
        data class OpenDefinition(val id: Int) : HomeCommand
    }
}