package com.vladusecho.lexicon.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.vladusecho.lexicon.presentation.navigation.navGraph.homeScreenGraph
import com.vladusecho.lexicon.presentation.screenv2.EducationScreen
import com.vladusecho.lexicon.presentation.screenv2.SettingsScreenV2

@Composable
fun AppNavGraph(
    navState: NavigationState
) {
    NavHost(
        navController = navState.navHostController,
        startDestination = NavScreen.HomeGraph,
        enterTransition = {
            fadeIn(animationSpec = tween(durationMillis = 0))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(durationMillis = 0))
        }
    ) {
        homeScreenGraph(navState)
        composable<NavScreen.Settings> {
            SettingsScreenV2()
        }
        navigation<NavScreen.EducationGraph>(
            startDestination = NavScreen.Education,
        ) {
            composable<NavScreen.Education> {
                EducationScreen()
            }
        }
    }
}