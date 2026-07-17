package com.vladusecho.lexicon.presentation.navigation

import com.vladusecho.lexicon.R

sealed class NavItem(
    val label: String,
    val iconId: Int,
    val screen: Any,
) {

    object Home : NavItem(
        label = "Словарь",
        iconId = R.drawable.ic_library,
        screen = NavScreen.HomeGraph,
    )

    object Education : NavItem(
        label = "Обучение",
        iconId = R.drawable.ic_education,
        screen = NavScreen.FavouritesGraph,
    )

    object Settings : NavItem(
        label = "Настройки",
        iconId = R.drawable.ic_settings,
        screen = NavScreen.Settings,
    )
}