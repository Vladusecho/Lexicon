package com.vladusecho.lexicon.presentation.screenv2

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vladusecho.lexicon.R
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.presentation.element.LoadingView
import com.vladusecho.lexicon.presentation.element.ShortDefinitionV2
import com.vladusecho.lexicon.presentation.ui.theme.LexiconTheme
import com.vladusecho.lexicon.presentation.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreenV2(
    viewModel: HomeViewModel = hiltViewModel(),
    onShortDefinitionClick: (Int) -> Unit,
    onAddDefinitionClick: () -> Unit
) {

    val currentState by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Lexicon",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xff24389C)
                    )
                },
                modifier = Modifier
                    .shadow(elevation = 3.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                actions = {
                    IconButton(
                        onClick = onAddDefinitionClick
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = null,
                            tint = Color(0xff24389C)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            HomeScreenV2Content(
                currentState = currentState,
                onShortDefinitionClick = onShortDefinitionClick,
                value = viewModel.query,
                onValueChange = {
                    viewModel.processCommand(
                        HomeViewModel.HomeCommand.QueryInput(it)
                    )
                },
                selectedFilter = viewModel.selectedFilter,
                onFilterClick = {
                    viewModel.processCommand(
                        HomeViewModel.HomeCommand.FilterClick(it)
                    )
                },
                onFavouriteClick = { id, isFavourite ->
                    viewModel.processCommand(
                        HomeViewModel.HomeCommand.ToggleFavourite(id, isFavourite)
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenV2Content(
    currentState: HomeViewModel.HomeState,
    onShortDefinitionClick: (Int) -> Unit,
    value: String,
    onValueChange: (String) -> Unit,
    selectedFilter: FilterChips,
    onFilterClick: (FilterChips) -> Unit,
    onFavouriteClick: (Int, Boolean) -> Unit
) {
    LazyColumn {
        item {
            Spacer(Modifier.height(24.dp))
            LexiconSearchBar(
                value = value,
                onValueChange = onValueChange
            )
        }
        stickyHeader {
            Spacer(Modifier.height(16.dp))
            FilterList(
                modifier = Modifier.fillMaxWidth(),
                selectedFilter = selectedFilter,
                onFilterClick = onFilterClick
            )
        }
        item {
            Spacer(Modifier.height(16.dp))
        }

        when (currentState) {
            HomeViewModel.HomeState.Error -> {
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

                    if (currentState.showAlphabetHeaders) {
                        val item = definitions[index]

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
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    HorizontalDivider(
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = currentLetter.toString(),
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xff24389C),
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            ShortDefinitionV2(
                                definition = item,
                                onClick = onShortDefinitionClick,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                onFavouriteClick = {
                                    onFavouriteClick(item.id, !item.isFavorite)
                                }
                            )
                        }
                    } else {
                        ShortDefinitionV2(
                            definition = definitions[index],
                            onClick = onShortDefinitionClick,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            onFavouriteClick = {
                                onFavouriteClick(
                                    definitions[index].id,
                                    !definitions[index].isFavorite
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
            .shadow(elevation = 1.dp, shape = CircleShape),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            unfocusedContainerColor = Color(0xffF3F4F5),
            focusedContainerColor = Color(0xffF3F4F5),
            disabledContainerColor = Color(0xffF3F4F5),
            errorContainerColor = Color(0xffF3F4F5),
        ),
        shape = CircleShape,
        placeholder = {
            Text(text = "Поиск...")
        },
        singleLine = true,
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = null
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onValueChange("")
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_eraser),
                        contentDescription = null
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
    onFilterClick: (FilterChips) -> Unit
) {

    val listState = rememberLazyListState()

    val chips = remember(selectedFilter) {
        listOf(selectedFilter) + FilterChips.entries.filter { it != selectedFilter }
    }

    LaunchedEffect(selectedFilter) {
        listState.animateScrollToItem(0)
    }

    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(
            items = chips,
            key = { it.name }
        ) {
            FilterButton(
                name = it.label,
                iconId = it.iconId,
                isSelected = selectedFilter == it,
                onClick = { onFilterClick(it) },
                modifier = Modifier.animateItem()
            )
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
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                onClick()
            }
            .background(if (isSelected) Color(0xff3F51B5) else Color(0xffedeeef))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconId != null) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (isSelected) Color.White else Color.Black
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = name,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Composable
@Preview(
    showBackground = true
)
fun HomeScreenContentSuccessPreview() {
    LexiconTheme {
        HomeScreenV2Content(
            currentState = HomeViewModel.HomeState.Success(
                listOf(
                    Definition(
                        id = 1,
                        word = "Толерантность",
                        description = "характер, когда человек не обращает внимания на действия остальных людей или животных",
                        isFavorite = false,
                        partOfSpeech = com.vladusecho.lexicon.domain.entity.PartOfSpeech.NOUN
                    ),
                    Definition(
                        id = 2,
                        word = "Волерантность",
                        description = "характер, когда человек не обращает внимания на действия остальных людей или животных",
                        isFavorite = false,
                        partOfSpeech = com.vladusecho.lexicon.domain.entity.PartOfSpeech.NOUN
                    ),
                    Definition(
                        id = 3,
                        word = "Толерантность",
                        description = "характер, когда человек не обращает внимания на действия остальных людей или животных",
                        isFavorite = false,
                        partOfSpeech = com.vladusecho.lexicon.domain.entity.PartOfSpeech.NOUN
                    ),
                    Definition(
                        id = 4,
                        word = "Толерантность",
                        description = "характер, когда человек не обращает внимания на действия остальных людей или животных",
                        isFavorite = false,
                        partOfSpeech = com.vladusecho.lexicon.domain.entity.PartOfSpeech.NOUN
                    ),
                    Definition(
                        id = 6,
                        word = "Толерантность",
                        description = "характер, когда человек не обращает внимания на действия остальных людей или животных",
                        isFavorite = false,
                        partOfSpeech = com.vladusecho.lexicon.domain.entity.PartOfSpeech.NOUN
                    ),
                    Definition(
                        id = 7,
                        word = "Толерантность",
                        description = "характер, когда человек не обращает внимания на действия остальных людей или животных",
                        isFavorite = false,
                        partOfSpeech = com.vladusecho.lexicon.domain.entity.PartOfSpeech.NOUN
                    )
                ),
                showAlphabetHeaders = true
            ),
            onShortDefinitionClick = {},
            value = "",
            onValueChange = {},
            selectedFilter = FilterChips.ALL,
            onFilterClick = {},
            onFavouriteClick = { _, _ -> }
        )
    }
}

@Composable
@Preview(
    showBackground = true
)
fun HomeScreenContentLoadingPreview() {
    LexiconTheme {
        HomeScreenV2Content(
            currentState = HomeViewModel.HomeState.Loading,
            onShortDefinitionClick = {},
            value = "",
            onValueChange = {},
            selectedFilter = FilterChips.ALL,
            onFilterClick = {},
            onFavouriteClick = { _, _ -> }
        )
    }
}

@Composable
@Preview
fun FilterButtonNotSelectedPreview() {
    LexiconTheme {
        FilterButton(name = "Все", iconId = R.drawable.ic_items, isSelected = false, onClick = {})
    }
}

@Composable
@Preview
fun FilterButtonSelectedPreview() {
    LexiconTheme {
        FilterButton(name = "Все", iconId = R.drawable.ic_items, isSelected = true, onClick = {})
    }
}

enum class FilterChips(val label: String, val iconId: Int) {
    ALL(
        label = "Все",
        iconId = R.drawable.ic_items
    ),
    FAVORITE(
        label = "Избранное",
        iconId = R.drawable.ic_favorite
    ),
    RECENT(
        label = "Недавние",
        iconId = R.drawable.ic_clock
    ),
    VERB(
        label = "Глагол.",
        iconId = R.drawable.ic_cubes
    ),
    NOUN(
        label = "Сущ.",
        iconId = R.drawable.ic_cubes
    ),
    ADVERB(
        label = "Нареч.",
        iconId = R.drawable.ic_cubes
    ),
    ADJECTIVE(
        label = "Прил.",
        iconId = R.drawable.ic_cubes
    )
}