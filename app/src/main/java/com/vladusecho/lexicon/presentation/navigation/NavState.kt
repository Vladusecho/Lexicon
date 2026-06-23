package com.vladusecho.lexicon.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class NavigationState(
    val navHostController: NavHostController
) {

    fun navigateTo(route: Any) {
        navHostController.navigate(route) {
            // only one copy of screen we can use
            popUpTo(navHostController.graph.findStartDestination().id) {
                saveState = true
            }
            // only one same screen at the top
            launchSingleTop = true
            // save screen state after another screen
            restoreState = true
        }
    }
}

@Composable
fun rememberNavigationState(
    navHostController: NavHostController = rememberNavController()
): NavigationState {
    return NavigationState(navHostController)
}