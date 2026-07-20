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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vladusecho.lexicon.R
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.presentation.element.ErrorView
import com.vladusecho.lexicon.presentation.element.LoadingView
import com.vladusecho.lexicon.presentation.ui.theme.LexiconTheme
import com.vladusecho.lexicon.presentation.viewmodel.FavouriteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteScreen(
    viewModel: FavouriteViewModel = hiltViewModel(),
    onShortDefinitionClick: (Int) -> Unit,
) {

    val currentState by viewModel.state.collectAsStateWithLifecycle()
    val favouritesCountValue by viewModel.favouritesCount.collectAsStateWithLifecycle()

    Column {
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
    query: String = "",
    onInputQuery: (String) -> Unit = {},
    isSearchActive: Boolean = false,
    currentState: FavouriteViewModel.FavouriteState,
    onShortDefinitionClick: (Int) -> Unit,
) {

    when (currentState) {
        FavouriteViewModel.FavouriteState.Error -> {
            ErrorView()
        }

        FavouriteViewModel.FavouriteState.Loading -> {
            LoadingView()
        }

        is FavouriteViewModel.FavouriteState.Success -> {
            Column {
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

@Composable
@Preview(
    showBackground = true
)
fun FavouriteScreenSuccessPreview() {
    LexiconTheme {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            FavouriteScreenContent(
                currentState = FavouriteViewModel.FavouriteState.Success(
                    listOf(
                        Definition(
                            id = 1,
                            word = "Толерантность",
                            description = "характер, когда человек не обращает внимания на действия остальных людей",
                            isFavorite = false,
                            partOfSpeech = com.vladusecho.lexicon.domain.entity.PartOfSpeech.NOUN
                        ),
                        Definition(
                            id = 2,
                            word = "Толерантность",
                            description = "характер, когда человек не обращает внимания на действия остальных людей",
                            isFavorite = false,
                            partOfSpeech = com.vladusecho.lexicon.domain.entity.PartOfSpeech.NOUN
                        ),
                        Definition(
                            id = 3,
                            word = "Толерантность",
                            description = "характер, когда человек не обращает внимания на действия остальных людей",
                            isFavorite = false,
                            partOfSpeech = com.vladusecho.lexicon.domain.entity.PartOfSpeech.NOUN
                        ),
                    )
                ),
                onShortDefinitionClick = {},
                isSearchActive = true
            )
        }
    }
}

@Composable
@Preview(
    showBackground = true
)
fun FavouriteScreenLoadingPreview() {
    LexiconTheme {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            FavouriteScreenContent(
                currentState = FavouriteViewModel.FavouriteState.Loading,
                onShortDefinitionClick = {}
            )
        }
    }
}

@Composable
@Preview(
    showBackground = true
)
fun FavouriteScreenErrorPreview() {
    LexiconTheme {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            FavouriteScreenContent(
                currentState = FavouriteViewModel.FavouriteState.Error,
                onShortDefinitionClick = {}
            )
        }
    }
}
