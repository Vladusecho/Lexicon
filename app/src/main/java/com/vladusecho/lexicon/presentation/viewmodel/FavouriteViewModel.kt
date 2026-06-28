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
class FavouriteViewModel @Inject constructor(
    private val getFavoritesUseCase: GetDefinitionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<FavouriteState>(FavouriteState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getFavoritesUseCase().collect {
                _state.value = FavouriteState.Success(it)
            }
        }
    }

    sealed interface FavouriteState {
        data class Success(val definitions: List<Definition>) : FavouriteState
        object Loading : FavouriteState
        object Error : FavouriteState
    }
}