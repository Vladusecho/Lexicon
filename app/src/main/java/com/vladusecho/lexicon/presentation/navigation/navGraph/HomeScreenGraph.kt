package com.vladusecho.lexicon.presentation.navigation.navGraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.vladusecho.lexicon.presentation.navigation.NavScreen
import com.vladusecho.lexicon.presentation.navigation.NavigationState
import com.vladusecho.lexicon.presentation.screen.CreateDefinitionScreen
import com.vladusecho.lexicon.presentation.screen.DetailsScreen
import com.vladusecho.lexicon.presentation.screen.EditDefinitionScreen
import com.vladusecho.lexicon.presentation.screen.HomeScreen
import com.vladusecho.lexicon.presentation.screenv2.HomeScreenV2

fun NavGraphBuilder.homeScreenGraph(
    navState: NavigationState
) {
    navigation<NavScreen.HomeGraph>(
        startDestination = NavScreen.Home
    ) {
        composable<NavScreen.Home> {
            HomeScreenV2(
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
                },
                onDeleteClick = {
                    navState.navHostController.navigateUp()
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
}