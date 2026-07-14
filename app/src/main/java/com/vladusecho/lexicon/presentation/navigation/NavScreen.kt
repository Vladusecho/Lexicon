package com.vladusecho.lexicon.presentation.navigation

import kotlinx.serialization.Serializable

sealed class NavScreen {

    @Serializable
    object Home : NavScreen()

    @Serializable
    object HomeGraph : NavScreen()

    @Serializable
    data class Details(
        val id: Int
    ) : NavScreen()

    @Serializable

    object Favorites : NavScreen()

    @Serializable

    object Settings : NavScreen()

    @Serializable
    object CreateDefinition : NavScreen()

    @Serializable
    data class EditDefinition(
        val id: Int
    ) : NavScreen()

    @Serializable
    object FavouritesGraph
}