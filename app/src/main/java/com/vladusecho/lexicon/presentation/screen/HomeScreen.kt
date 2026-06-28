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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import com.vladusecho.lexicon.presentation.element.ShortDefinition
import com.vladusecho.lexicon.presentation.ui.theme.LexiconTheme
import com.vladusecho.lexicon.presentation.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onShortDefinitionClick: (Int) -> Unit,
    onAddDefinitionClick: () -> Unit
) {

    val state = viewModel.state.collectAsStateWithLifecycle()
    val currentState = state.value

    val definitionsCount = viewModel.definitionsCount.collectAsStateWithLifecycle()
    val definitionsCountValue = definitionsCount.value


    Column() {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Главная ($definitionsCountValue)",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color(0xff0d1e25)
            ),
            actions = {
                IconButton(
                    onClick = onAddDefinitionClick
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        )
        HomeScreenContent(
            currentState = currentState,
            onShortDefinitionClick = onShortDefinitionClick
        )
    }
}

@Composable
fun HomeScreenContent(
    currentState: HomeViewModel.HomeState,
    onShortDefinitionClick: (Int) -> Unit,
) {
    when (currentState) {
        HomeViewModel.HomeState.Error -> {

        }

        HomeViewModel.HomeState.Loading -> {
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

        is HomeViewModel.HomeState.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = currentState.definitions,
                    key = { it.id }
                ) {
                    ShortDefinition(
                        definition = it,
                        onClick = onShortDefinitionClick
                    )
                }
            }
        }
    }
}

@Composable
@Preview(
    showBackground = true
)
fun HomeScreenSuccessPreview() {
    LexiconTheme() {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            HomeScreenContent(
                currentState = HomeViewModel.HomeState.Success(
                    listOf(
                        Definition(
                            id = 1,
                            word = "Толерантность",
                            description = "характер, когда человек не обращает внимания на действия остальных людей",
                            isFavorite = false
                        ),
                        Definition(
                            id = 2,
                            word = "Толерантность",
                            description = "характер, когда человек не обращает внимания на действия остальных людей",
                            isFavorite = false
                        ),
                        Definition(
                            id = 3,
                            word = "Толерантность",
                            description = "характер, когда человек не обращает внимания на действия остальных людей",
                            isFavorite = false
                        ),
                    )
                ),
                onShortDefinitionClick = {}
            )
        }
    }
}

@Composable
@Preview(
    showBackground = true
)
fun HomeScreenLoadingPreview() {
    LexiconTheme() {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            HomeScreenContent(
                currentState = HomeViewModel.HomeState.Loading,
                onShortDefinitionClick = {}
            )
        }
    }
}