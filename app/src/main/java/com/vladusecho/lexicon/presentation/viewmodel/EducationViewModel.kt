package com.vladusecho.lexicon.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EducationViewModel @Inject constructor(

) : ViewModel() {

    var isDefinitionShown by mutableStateOf(false)
        private set

    fun processCommand(command: EducationCommand) {
        when (command) {
            is EducationCommand.ShowDefinition -> {
                isDefinitionShown = command.isDefinitionShown
            }
        }
    }

    sealed interface EducationCommand {
        data class ShowDefinition(
            val isDefinitionShown: Boolean
        ) : EducationCommand
    }
}
