package com.vladusecho.lexicon.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladusecho.lexicon.domain.entity.Settings
import com.vladusecho.lexicon.domain.usecase.settings.GetSettingsUseCase
import com.vladusecho.lexicon.domain.usecase.settings.ToggleDarkModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val toggleDarkModeUseCase: ToggleDarkModeUseCase
) : ViewModel() {

    val settings = getSettingsUseCase()

    val state = settings
        .map { SettingsState.Success(it) as SettingsState }
        .catch { emit(SettingsState.Error) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsState.Loading
        )

    val isDarkMode = settings
        .map { it.isDarkMode }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun processCommand(command: SettingsCommand) {
        when (command) {
            is SettingsCommand.ToggleDarkMode -> {
                viewModelScope.launch {
                    toggleDarkModeUseCase(command.isDarkMode)
                }
            }
        }
    }

    sealed interface SettingsState {
        data class Success(
            val settings: Settings
        ) : SettingsState

        object Loading : SettingsState
        object Error : SettingsState
    }

    sealed interface SettingsCommand {
        data class ToggleDarkMode(val isDarkMode: Boolean) : SettingsCommand
    }
}