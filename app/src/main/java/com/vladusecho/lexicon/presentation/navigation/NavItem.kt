package com.vladusecho.lexicon.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.vladusecho.lexicon.R

sealed class NavItem(
    @StringRes val labelResId: Int,
    @DrawableRes val iconId: Int,
    val screen: Any,
) {

    data object Home : NavItem(
        labelResId = R.string.nav_home,
        iconId = R.drawable.ic_library,
        screen = NavScreen.HomeGraph,
    )

    data object Education : NavItem(
        labelResId = R.string.nav_education,
        iconId = R.drawable.ic_education,
        screen = NavScreen.EducationGraph,
    )

    data object Settings : NavItem(
        labelResId = R.string.nav_settings,
        iconId = R.drawable.ic_settings,
        screen = NavScreen.Settings,
    )
}
