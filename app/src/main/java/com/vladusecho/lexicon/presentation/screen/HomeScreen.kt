package com.vladusecho.lexicon.presentation.screen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vladusecho.lexicon.R
import com.vladusecho.lexicon.domain.entity.PartOfSpeech
import com.vladusecho.lexicon.presentation.element.LoadingView
import com.vladusecho.lexicon.presentation.element.ShortDefinition
import com.vladusecho.lexicon.presentation.viewmodel.FilterChips
import com.vladusecho.lexicon.presentation.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onShortDefinitionClick: (Int) -> Unit,
    onAddDefinitionClick: () -> Unit
) {
    val currentState by viewModel.state.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val selectedPartOfSpeech by viewModel.selectedPartOfSpeech.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier
                    .shadow(elevation = 3.dp, spotColor = MaterialTheme.colorScheme.tertiary),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    IconButton(onClick = onAddDefinitionClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = stringResource(id = R.string.create_title),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        HomeScreenContent(
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
            currentState = currentState,
            onShortDefinitionClick = onShortDefinitionClick,
            value = query,
            selectedFilter = selectedFilter,
            selectedPartOfSpeech = selectedPartOfSpeech,
            onCommand = viewModel::processCommand
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    currentState: HomeViewModel.HomeState,
    onShortDefinitionClick: (Int) -> Unit,
    value: String,
    selectedFilter: FilterChips,
    selectedPartOfSpeech: PartOfSpeech?,
    onCommand: (HomeViewModel.HomeCommand) -> Unit
) {
    LazyColumn(modifier = modifier) {
        item {
            Spacer(Modifier.height(24.dp))
            LexiconSearchBar(
                value = value,
                onValueChange = { onCommand(HomeViewModel.HomeCommand.QueryInput(it)) }
            )
        }
        stickyHeader {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            0.35f to MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                            0.65f to MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                            1f to Color.Transparent
                        )
                    )
            ) {
                Spacer(Modifier.height(16.dp))
                FilterList(
                    modifier = Modifier.fillMaxWidth(),
                    selectedFilter = selectedFilter,
                    onFilterClick = { onCommand(HomeViewModel.HomeCommand.FilterClick(it)) },
                    selectedPartOfSpeech = selectedPartOfSpeech,
                    onPartOfSpeechClick = { onCommand(HomeViewModel.HomeCommand.PartOfSpeechClick(it)) }
                )
                Spacer(Modifier.height(16.dp))
            }
        }
        when (currentState) {
            is HomeViewModel.HomeState.Empty -> {
                item {
                    val (titleRes, descRes) = when (currentState.errorType) {
                        HomeViewModel.ErrorType.NO_WORDS ->
                            R.string.home_empty_title to R.string.home_empty_description
                        HomeViewModel.ErrorType.NO_SEARCH_RESULTS ->
                            R.string.home_search_empty_title to R.string.home_search_empty_description
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = titleRes),
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(id = descRes),
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            HomeViewModel.HomeState.Loading -> {
                item {
                    Spacer(Modifier.height(32.dp))
                    LoadingView()
                }
            }

            is HomeViewModel.HomeState.Success -> {
                val definitions = currentState.definitions

                items(
                    count = definitions.size,
                    key = { index -> definitions[index].id }
                ) { index ->
                    val item = definitions[index]

                    if (currentState.showAlphabetHeaders) {
                        val currentLetter = item.word.firstOrNull()?.uppercaseChar() ?: '?'
                        val previousLetter = if (index > 0) {
                            definitions[index - 1].word.firstOrNull()?.uppercaseChar()
                        } else {
                            null
                        }

                        val showHeader = previousLetter != currentLetter

                        Column {
                            if (showHeader) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    HorizontalDivider(modifier = Modifier.weight(1f))
                                    Text(
                                        text = currentLetter.toString(),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        textAlign = TextAlign.Center
                                    )
                                    HorizontalDivider(modifier = Modifier.weight(1f))
                                }
                            }
                            ShortDefinition(
                                definition = item,
                                onClick = onShortDefinitionClick,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                onFavouriteClick = {
                                    onCommand(
                                        HomeViewModel.HomeCommand.ToggleFavourite(
                                            item.id,
                                            !item.isFavorite
                                        )
                                    )
                                }
                            )
                        }
                    } else {
                        ShortDefinition(
                            definition = item,
                            onClick = onShortDefinitionClick,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            onFavouriteClick = {
                                onCommand(
                                    HomeViewModel.HomeCommand.ToggleFavourite(
                                        item.id,
                                        !item.isFavorite
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LexiconSearchBar(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f), CircleShape),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            unfocusedContainerColor = MaterialTheme.colorScheme.onBackground,
            focusedContainerColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor = MaterialTheme.colorScheme.onBackground,
            errorContainerColor = MaterialTheme.colorScheme.onBackground,
        ),
        shape = CircleShape,
        placeholder = {
            Text(
                text = stringResource(id = R.string.search_placeholder),
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
            )
        },
        singleLine = true,
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_eraser),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    )
}

@Composable
fun FilterList(
    modifier: Modifier = Modifier,
    selectedFilter: FilterChips,
    selectedPartOfSpeech: PartOfSpeech?,
    onPartOfSpeechClick: (PartOfSpeech) -> Unit,
    onFilterClick: (FilterChips) -> Unit
) {
    val listState = rememberLazyListState()
    var isMenuExpanded by remember { mutableStateOf(false) }

    val chips = remember(selectedFilter) {
        listOf(selectedFilter) + FilterChips.entries.filter { it != selectedFilter }
    }

    LaunchedEffect(selectedFilter) {
        listState.animateScrollToItem(0)
    }

    Box(modifier = modifier) {
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                items = chips,
                key = { it.name }
            ) { chip ->
                val buttonLabel = if (chip == FilterChips.PART_OF_SPEECH && selectedPartOfSpeech != null) {
                    selectedPartOfSpeech.label
                } else {
                    stringResource(id = chip.labelResId)
                }

                FilterButton(
                    name = buttonLabel,
                    iconId = chip.iconId,
                    isSelected = selectedFilter == chip,
                    onClick = {
                        if (chip == FilterChips.PART_OF_SPEECH) {
                            isMenuExpanded = true
                        }
                        onFilterClick(chip)
                    },
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.onBackground),
                shape = RoundedCornerShape(16.dp)
            ) {
                PartOfSpeech.entries.forEachIndexed { index, part ->
                    Column {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = part.label,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            onClick = {
                                isMenuExpanded = false
                                onPartOfSpeechClick(part)
                                onFilterClick(FilterChips.PART_OF_SPEECH)
                            },
                            modifier = Modifier.clip(RoundedCornerShape(16.dp))
                        )
                        if (index != PartOfSpeech.entries.size - 1) {
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterButton(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    name: String,
    iconId: Int? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .border(
                1.dp,
                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconId != null) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.tertiary
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = name,
            color = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.tertiary,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
