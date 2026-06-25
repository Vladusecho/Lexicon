package com.vladusecho.lexicon.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.vladusecho.lexicon.presentation.screen.CreateDefinitionScreen
import com.vladusecho.lexicon.presentation.screen.DetailsScreen
import com.vladusecho.lexicon.presentation.screen.EditDefinitionScreen
import com.vladusecho.lexicon.presentation.screen.FavouriteScreen
import com.vladusecho.lexicon.presentation.screen.HomeScreen
import com.vladusecho.lexicon.presentation.screen.SettingsScreen

@Composable
fun AppNavGraph(
    navState: NavigationState,
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
        navigation<NavScreen.HomeGraph>(
            startDestination = NavScreen.Home
        ) {
            composable<NavScreen.Home> {
                HomeScreen(
                    onShortDefinitionClick = {
                        navState.navHostController.navigate(NavScreen.Details(it))
                    },
                    onAddDefinitionClick = {
                        navState.navHostController.navigate(NavScreen.CreateDefinition)
                    }
                )
            }
            composable<NavScreen.Details> { backStackEntry ->
                val args = backStackEntry.toRoute<NavScreen.Details>()
                DetailsScreen(
                    id = args.id,
                    onBackClick = {
                        navState.navHostController.navigateUp()
                    },
                    onEditClick = {
                        navState.navHostController.navigate(NavScreen.EditDefinition(args.id))
                    }
                )
            }
            composable<NavScreen.CreateDefinition> {
                CreateDefinitionScreen(
                    onBackClick = {
                        navState.navHostController.navigateUp()
                    }
                )
            }
            composable<NavScreen.EditDefinition> { backStackEntry ->
                val args = backStackEntry.toRoute<NavScreen.EditDefinition>()
                EditDefinitionScreen(
                    id = args.id,
                    onBackClick = {
                        navState.navHostController.navigateUp()
                    }
                )
            }
        }
        composable<NavScreen.Favorites> {
            FavouriteScreen()
        }
        composable<NavScreen.Settings> {
            SettingsScreen()
        }
    }
}