package com.vladusecho.lexicon.presentation.navigation

import kotlinx.serialization.Serializable

sealed class NavScreen {

    @Serializable
    data object Home : NavScreen()

    @Serializable
    data object HomeGraph : NavScreen()

    @Serializable
    data class Details(
        val id: Int
    ) : NavScreen()

    @Serializable
    data object Settings : NavScreen()

    @Serializable
    data object CreateDefinition : NavScreen()

    @Serializable
    data class EditDefinition(
        val id: Int
    ) : NavScreen()

    @Serializable
    data object Education : NavScreen()

    @Serializable
    data object EducationGraph : NavScreen()
}
