package com.vladusecho.lexicon.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.vladusecho.lexicon.R
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.entity.PartOfSpeech
import com.vladusecho.lexicon.presentation.element.ErrorView
import com.vladusecho.lexicon.presentation.element.LoadingView
import com.vladusecho.lexicon.presentation.viewmodel.DetailsViewModel

@Composable
fun DetailsScreen(
    id: Int,
    viewModel: DetailsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onEditClick: (id: Int) -> Unit
) {
    val context = LocalContext.current
    val currentState by viewModel.state.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                DetailsViewModel.DetailsEvent.DeleteDefinition -> {
                    onBackClick()
                }

                is DetailsViewModel.DetailsEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val isFavorite = (currentState as? DetailsViewModel.DetailsUiState.Success)?.definition?.isFavorite ?: false

    if (showDeleteDialog && currentState is DetailsViewModel.DetailsUiState.Success) {
        val word = (currentState as DetailsViewModel.DetailsUiState.Success).definition.word
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = stringResource(id = R.string.dialog_delete_title),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.dialog_delete_message, word)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.processCommand(DetailsViewModel.DetailsCommand.DeleteDefinition)
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.action_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(id = R.string.action_cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            DetailsTopAppBar(
                isFavorite = isFavorite,
                onBackClick = onBackClick,
                onEditClick = { onEditClick(id) },
                onDeleteClick = { showDeleteDialog = true },
                onFavouriteClick = {
                    viewModel.processCommand(DetailsViewModel.DetailsCommand.ToggleFavourite)
                }
            )
        }
    ) { paddingValues ->
        DetailsContent(
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
            currentState = currentState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsTopAppBar(
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavouriteClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    TopAppBar(
        modifier = Modifier
            .shadow(elevation = 3.dp, spotColor = MaterialTheme.colorScheme.tertiary),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        title = {
            Text(
                text = stringResource(id = R.string.details_title),
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            IconButton(onClick = onEditClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = stringResource(id = R.string.edit_title),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onFavouriteClick) {
                Icon(
                    painter = if (isFavorite) {
                        painterResource(id = R.drawable.ic_favorite_filled)
                    } else {
                        painterResource(id = R.drawable.ic_favorite)
                    },
                    contentDescription = null,
                    tint = if (isFavorite) Color(0xFFFFB300) else MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_trash),
                    contentDescription = stringResource(id = R.string.action_delete),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Composable
fun DetailsContent(
    modifier: Modifier = Modifier,
    currentState: DetailsViewModel.DetailsUiState
) {
    val scrollState = rememberScrollState()

    when (currentState) {
        DetailsViewModel.DetailsUiState.Error -> {
            ErrorView(modifier = modifier)
        }

        DetailsViewModel.DetailsUiState.Loading -> {
            LoadingView()
        }

        is DetailsViewModel.DetailsUiState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                WordWithPartOfSpeech(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    word = currentState.definition.word,
                    partOfSpeech = currentState.definition.partOfSpeech
                )
                Spacer(modifier = Modifier.height(24.dp))
                DefinitionWithImage(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    definition = currentState.definition
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun DefinitionWithImage(
    modifier: Modifier = Modifier,
    definition: Definition
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.field_description_label),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = definition.description,
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp
        )
        if (definition.imgUri != null) {
            Spacer(modifier = Modifier.height(16.dp))
            AsyncImage(
                model = definition.imgUri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun WordWithPartOfSpeech(
    modifier: Modifier = Modifier,
    word: String,
    partOfSpeech: PartOfSpeech
) {
    Column(modifier = modifier) {
        Text(
            text = word,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onBackground)
                .padding(vertical = 4.dp, horizontal = 16.dp)
        ) {
            Text(
                text = partOfSpeech.label,
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
