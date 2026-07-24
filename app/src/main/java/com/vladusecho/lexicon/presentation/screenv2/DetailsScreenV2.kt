package com.vladusecho.lexicon.presentation.screenv2

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.vladusecho.lexicon.domain.entity.PartOfSpeech
import com.vladusecho.lexicon.presentation.element.LoadingView
import com.vladusecho.lexicon.presentation.ui.theme.LexiconTheme
import com.vladusecho.lexicon.presentation.viewmodel.DetailsViewModel
import com.vladusecho.lexicon.presentation.viewmodel.DetailsViewModelFactory

@Composable
fun DetailsScreenV2(
    id: Int,
    viewModel: DetailsViewModel = hiltViewModel(
        creationCallback = { factory: DetailsViewModelFactory ->
            factory.create(id)
        }
    ),
    onBackClick: () -> Unit,
    onEditClick: (id: Int) -> Unit
) {

    LaunchedEffect(key1 = Unit) {
        viewModel.event.collect { event ->
            when (event) {
                DetailsViewModel.DetailsEvent.DeleteDefinition -> {
                    onBackClick()
                }
            }
        }
    }

    val currentState by viewModel.state.collectAsStateWithLifecycle()

    val isFavorite by viewModel.isFavourite.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            DetailsScreenV2TopAppBar(
                isFavorite = isFavorite,
                onBackClick = onBackClick,
                onEditClick = onEditClick,
                onDeleteClick = {
                    viewModel.processCommand(
                        DetailsViewModel.DetailsCommand.DeleteDefinition
                    )
                },
                onFavouriteClick = {
                    viewModel.processCommand(
                        DetailsViewModel.DetailsCommand.ToggleFavourite
                    )
                },
                id = id
            )
        }
    ) { paddingValues ->
        DetailsScreenV2Content(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding()),
            currentState = currentState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreenV2TopAppBar(
    id: Int,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavouriteClick: () -> Unit,
    onEditClick: (id: Int) -> Unit,
    onDeleteClick: () -> Unit
) {
    TopAppBar(
        modifier = Modifier
            .shadow(elevation = 3.dp),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        ),
        title = {
            Text(
                text = "Определение",
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    onEditClick(id)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = null,
                    tint = Color.Black
                )
            }
            IconButton(
                onClick = onFavouriteClick
            ) {
                Icon(
                    painter = painterResource(id = if (!isFavorite) R.drawable.ic_favorite else R.drawable.ic_not_favourite),
                    contentDescription = null,
                    tint = Color.Black
                )
            }
            IconButton(
                onClick = onDeleteClick
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_trash),
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        }
    )
}

@Composable
fun DetailsScreenV2Content(
    modifier: Modifier = Modifier,
    currentState: DetailsViewModel.DetailsState,
) {

    val scrollState = rememberScrollState()

    when (currentState) {
        DetailsViewModel.DetailsState.Error -> {

        }
        DetailsViewModel.DetailsState.Loading -> {
            LoadingView()
        }
        is DetailsViewModel.DetailsState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                WordWithPartOfSpeech(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    word = currentState.definition.word,
                    partOfSpeech = currentState.definition.partOfSpeech
                )
                Spacer(modifier = Modifier.height(32.dp))
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
            .border(1.dp, Color(0xffC5C5D4), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Определение",
            color = Color(0xff24389C),
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = definition.description,
            color = Color(0xff454652),
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
        )
        if (definition.imgUri != null) {
            Spacer(modifier = Modifier.height(16.dp))
            AsyncImage(
                model = definition.imgUri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1 / 1f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xffC5C5D4), RoundedCornerShape(16.dp)),
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
    Column(
        modifier = modifier
    ) {
        Text(
            text = word,
            color = Color(0xff24389C),
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        PartOfSpeech(
            partOfSpeech = partOfSpeech
        )
    }
}

@Composable
fun PartOfSpeech(
    modifier: Modifier = Modifier,
    partOfSpeech: PartOfSpeech
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0xff85f6e5))
            .padding(vertical = 4.dp, horizontal = 16.dp)
    ) {
        Text(
            text = partOfSpeech.label,
            color = Color(0xff007166),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
@Preview(
    showBackground = true
)
fun DetailsScreenV2SuccessPreview() {
    LexiconTheme {
        DetailsScreenV2Content(
            currentState = DetailsViewModel.DetailsState.Success(
                Definition(
                    id = 1,
                    word = "Толерантность",
                    description = "характер, когда человек не обращает внимания на действия остальных людей или животных",
                    isFavorite = false,
                    imgUri = "null",
                    partOfSpeech = PartOfSpeech.NOUN
                )
            )
        )
    }
}

@Composable
@Preview(
    showBackground = true
)
fun DetailsScreenV2LoadingPreview() {
    LexiconTheme {
        DetailsScreenV2Content(
            currentState = DetailsViewModel.DetailsState.Loading
        )
    }
}

@Composable
@Preview(
    showBackground = true
)
fun DetailsScreenV2TopAppBarPreview() {
    LexiconTheme {
        DetailsScreenV2TopAppBar(
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {},
            id = 1,
            isFavorite = false,
            onFavouriteClick = {}
        )
    }
}
