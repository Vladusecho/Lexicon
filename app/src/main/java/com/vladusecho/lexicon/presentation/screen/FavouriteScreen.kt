package com.vladusecho.lexicon.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vladusecho.lexicon.presentation.element.ShortDefinition
import com.vladusecho.lexicon.presentation.viewmodel.FavouriteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteScreen(
    viewModel: FavouriteViewModel = hiltViewModel(),
    onShortDefinitionClick: (Int) -> Unit,
) {

    val state = viewModel.state.collectAsStateWithLifecycle()
    val currentState = state.value

    val favouritesCount = viewModel.favouritesCount.collectAsStateWithLifecycle(initialValue = 0)
    val favouritesCountValue = favouritesCount.value

    Column() {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Избранное ($favouritesCountValue)",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
        )
        FavouriteScreenContent(
            currentState = currentState,
            onShortDefinitionClick = onShortDefinitionClick
        )
    }
}

@Composable
fun FavouriteScreenContent(
    modifier: Modifier = Modifier,
    currentState: FavouriteViewModel.FavouriteState,
    onShortDefinitionClick: (Int) -> Unit,
) {

    when (currentState) {
        FavouriteViewModel.FavouriteState.Error -> {

        }
        FavouriteViewModel.FavouriteState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xff0d1e25)
                )
            }
        }
        is FavouriteViewModel.FavouriteState.Success -> {
            DefinitionsListWithAlphabetTitle(
                definitionsList = currentState.favourites,
                onShortDefinitionClick = onShortDefinitionClick
            )
        }
    }
}

