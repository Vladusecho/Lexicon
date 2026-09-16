package com.vladusecho.lexicon.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladusecho.lexicon.domain.entity.Settings
import com.vladusecho.lexicon.domain.usecase.definition.ExportDefinitionUseCase
import com.vladusecho.lexicon.domain.usecase.settings.GetSettingsUseCase
import com.vladusecho.lexicon.domain.usecase.settings.ToggleDarkModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    private val toggleDarkModeUseCase: ToggleDarkModeUseCase,
    private val exportDefinitionUseCase: ExportDefinitionUseCase
) : ViewModel() {

    // Flow with the current settings
    val settings = getSettingsUseCase()

    // StateFlow with the current settings
    val state = settings
        .map { SettingsState.Success(it) as SettingsState }
        .catch { emit(SettingsState.Error) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsState.Loading
        )

    val isSettingsLoaded = mutableStateOf(true)

    // StateFlow with the current dark mode setting
    val isDarkMode = settings
        .map { it.isDarkMode }
        .onEach { isSettingsLoaded.value = false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    private val _event = MutableSharedFlow<SettingsEvent>()
    val event = _event.asSharedFlow()


    fun processCommand(command: SettingsCommand) {
        when (command) {
            is SettingsCommand.ToggleDarkMode -> {
                viewModelScope.launch {
                    toggleDarkModeUseCase(command.isDarkMode)
                }
            }

            is SettingsCommand.ExportData -> {
                viewModelScope.launch {
                    exportDefinitionUseCase(command.uriString)
                        .onSuccess { _event.emit(SettingsEvent.ExportSuccess) }
                        .onFailure { _event.emit(SettingsEvent.ExportError(it.message ?: "Unknown error")) }
                }
            }
        }
    }

    sealed interface SettingsEvent {
        data object ExportSuccess : SettingsEvent
        data class ExportError(val message: String) : SettingsEvent
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

        data class ExportData(val uriString: String) : SettingsCommand
    }
}