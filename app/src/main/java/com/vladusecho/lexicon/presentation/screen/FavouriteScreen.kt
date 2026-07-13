package com.vladusecho.lexicon.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vladusecho.lexicon.R
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
            actions = {
                IconButton(
                    onClick = {
                        viewModel.processCommand(FavouriteViewModel.FavouriteCommand.SearchActive)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        )
        FavouriteScreenContent(
            currentState = currentState,
            query = viewModel.query,
            onInputQuery = {
                viewModel.processCommand(FavouriteViewModel.FavouriteCommand.QueryInput(it))
            },
            isSearchActive = viewModel.isSearchActive,
            onShortDefinitionClick = onShortDefinitionClick
        )
    }
}

@Composable
fun FavouriteScreenContent(
    modifier: Modifier = Modifier,
    query: String = "",
    onInputQuery: (String) -> Unit = {},
    isSearchActive: Boolean = false,
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
            Column() {
                if (isSearchActive) {
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = query,
                        onValueChange = onInputQuery,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.tertiary,
                            focusedTextColor = MaterialTheme.colorScheme.tertiary,
                            unfocusedTextColor = MaterialTheme.colorScheme.tertiary,
                            disabledTextColor = MaterialTheme.colorScheme.tertiary,
                            errorTextColor = MaterialTheme.colorScheme.tertiary,
                            focusedContainerColor = MaterialTheme.colorScheme.secondary,
                            unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                            disabledContainerColor = MaterialTheme.colorScheme.secondary,
                            errorContainerColor = MaterialTheme.colorScheme.secondary,
                        ),
                        shape = RoundedCornerShape(8.dp),
                        placeholder = {
                            Text(
                                text = "Поиск...",
                                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                            )
                        },
                        singleLine = true,
                    )
                }
                DefinitionsListWithAlphabetTitle(
                    definitionsList = currentState.favourites,
                    onShortDefinitionClick = onShortDefinitionClick
                )
            }
        }
    }
}

