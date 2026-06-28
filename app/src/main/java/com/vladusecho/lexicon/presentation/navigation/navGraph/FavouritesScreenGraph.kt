package com.vladusecho.lexicon.presentation.navigation.navGraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.vladusecho.lexicon.presentation.navigation.NavScreen
import com.vladusecho.lexicon.presentation.navigation.NavigationState
import com.vladusecho.lexicon.presentation.screen.DetailsScreen
import com.vladusecho.lexicon.presentation.screen.EditDefinitionScreen
import com.vladusecho.lexicon.presentation.screen.FavouriteScreen

fun NavGraphBuilder.favouritesScreenGraph(
    navState: NavigationState
) {
    navigation<NavScreen.FavouritesGraph>(
        startDestination = NavScreen.Favorites
    ) {
        composable<NavScreen.Favorites> {
            FavouriteScreen(
                onShortDefinitionClick = {
                    navState.navHostController.navigate(NavScreen.Details(it))
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