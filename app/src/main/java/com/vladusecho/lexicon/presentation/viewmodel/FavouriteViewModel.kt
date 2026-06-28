package com.vladusecho.lexicon.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.usecase.GetFavouritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    getFavoritesUseCase: GetFavouritesUseCase
) : ViewModel() {

    val state = getFavoritesUseCase()
        .map { FavouriteState.Success(it) as FavouriteState }
        .catch { emit(FavouriteState.Error) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FavouriteState.Loading
        )

    val favouritesCount = state.map {
        when (it) {
            is FavouriteState.Success -> it.favourites.size
            else -> 0
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    sealed interface FavouriteState {
        data class Success(
            val favourites: List<Definition>,
        ) : FavouriteState
        object Loading : FavouriteState
        object Error : FavouriteState
    }
}