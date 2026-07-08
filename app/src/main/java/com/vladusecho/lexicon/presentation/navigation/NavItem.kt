package com.vladusecho.lexicon.presentation.navigation

import com.vladusecho.lexicon.R

sealed class NavItem(
    val iconId: Int,
    val screen: Any,
) {

    object Home : NavItem(
        iconId = R.drawable.ic_home,
        screen = NavScreen.HomeGraph,
    )

    object Favorites : NavItem(
        iconId = R.drawable.ic_favorite,
        screen = NavScreen.FavouritesGraph,
    )

    object Settings : NavItem(
        iconId = R.drawable.ic_settings,
        screen = NavScreen.Settings,
    )
}