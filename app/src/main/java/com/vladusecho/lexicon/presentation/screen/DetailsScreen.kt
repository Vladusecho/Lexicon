package com.vladusecho.lexicon.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.vladusecho.lexicon.R
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.presentation.ui.theme.LexiconTheme
import com.vladusecho.lexicon.presentation.viewmodel.DetailsViewModel
import com.vladusecho.lexicon.presentation.viewmodel.DetailsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    id: Int,
    viewModel: DetailsViewModel = hiltViewModel(
        creationCallback = { factory: DetailsViewModelFactory ->
            factory.create(id)
        }
    ),
    onBackClick: () -> Unit,
    onEditClick: (
        id: Int,
    ) -> Unit,
    onDeleteClick: () -> Unit
) {

    val state = viewModel.state.collectAsStateWithLifecycle()
    val currentState = state.value

    val isFavoriteState = viewModel.isFavorite.collectAsStateWithLifecycle()
    val isFavorite = isFavoriteState.value

    var displayActions by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        viewModel.event.collect { event ->
            when (event) {
                DetailsViewModel.DetailsEvent.DeleteDefinition -> {
                    onDeleteClick()
                }
            }
        }
    }

    Column() {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Определение",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            navigationIcon = {
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        displayActions = !displayActions
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_more),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
                DropdownMenu(
                    expanded = displayActions,
                    onDismissRequest = { displayActions = false },
                    border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.tertiary),
                    shape = RoundedCornerShape(8.dp),
                    containerColor = MaterialTheme.colorScheme.secondary,
                    content = {
                        DropdownMenuItem(
                            onClick = {
                                onEditClick(id)
                            },
                            text = {
                                Text(
                                    text = "Редактировать",
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_edit),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary
                                )

                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                viewModel.processCommand(
                                    DetailsViewModel.DetailsCommand.ToggleFavourite(id)
                                )
                            },
                            text = {
                                Text(
                                    text = if (isFavorite) {
                                        "Из избранного"
                                    } else {
                                        "В избранное"
                                    },
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            },
                            leadingIcon = {
                                val icon = if (!isFavorite) {
                                    R.drawable.ic_favorite
                                } else {
                                    R.drawable.ic_not_favourite
                                }
                                Icon(
                                    painter = painterResource(icon),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                viewModel.processCommand(
                                    DetailsViewModel.DetailsCommand.DeleteDefinition
                                )
                            },
                            text = {
                                Text(
                                    text = "Удалить",
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_trash),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        )
                    }
                )
            }
        )
        DetailsScreenContent(
            currentState = currentState,
        )
    }
}

@Composable
fun DetailsScreenContent(
    modifier: Modifier = Modifier,
    currentState: DetailsViewModel.DetailsState
) {
    when (currentState) {
        DetailsViewModel.DetailsState.Error -> {

        }

        DetailsViewModel.DetailsState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xff0d1e25)
                )
            }
        }

        is DetailsViewModel.DetailsState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
                    .padding(top = 32.dp)
            ) {
                if (currentState.definition.imgUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondary)
                            .aspectRatio(1 / 1f),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = currentState.definition.imgUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentState.definition.word,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentState.definition.description,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp
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
fun DetailsScreenSuccessPreview() {
    LexiconTheme() {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            DetailsScreenContent(
                currentState = DetailsViewModel.DetailsState.Success(
                    Definition(
                        id = 1,
                        word = "Толерантность",
                        description = "характер, когда человек не обращает внимания на действия остальных людей",
                        isFavorite = false
                    )
                )
            )
        }
    }
}

@Composable
@Preview(
    showBackground = true
)
fun DetailsScreenLoadingPreview() {
    LexiconTheme() {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            DetailsScreenContent(
                currentState = DetailsViewModel.DetailsState.Loading
            )
        }
    }
}