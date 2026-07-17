package com.vladusecho.lexicon.presentation.screenv2

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vladusecho.lexicon.R
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.presentation.element.ShortDefinitionV2
import com.vladusecho.lexicon.presentation.ui.theme.LexiconTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(

) {
    Column() {
        TopAppBar(
            title = {
                Text(
                    text = "Lexicon",
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xff24389C)
                )
            },
            modifier = Modifier
                .shadow(elevation = 3.dp)
        )
        Spacer(Modifier.height(24.dp))
        LexiconSearchBar(
            value = "",
            onValueChange = {}
        )
        Spacer(Modifier.height(16.dp))
        FilterList(
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        DefinitionsList()
    }
}

@Composable
fun DefinitionsList(
    modifier: Modifier = Modifier
) {

    val definitions = listOf(
        Definition(
            id = 1,
            word = "Толерантность",
            description = "характер, когда человек не обращает внимания на действия остальных людей или животных",
            isFavorite = false
        ),
        Definition(
            id = 2,
            word = "Толерантность",
            description = "характер, когда человек не обращает внимания на действия остальных людей или животных",
            isFavorite = false
        ),
    )

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(items = definitions) {
            ShortDefinitionV2(
                definition = it
            )
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
            unfocusedContainerColor = Color(0xffF3F4F5)
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
        }
    )
}

@Composable
fun FilterList(
    modifier: Modifier = Modifier
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(items = FilterChips.entries.toTypedArray()) {
            FilterButton(
                name = it.label,
                iconId = it.iconId,
                isSelected = it == FilterChips.ALL
            )
        }
    }
}

@Composable
fun FilterButton(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    name: String,
    iconId: Int
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(if(isSelected) Color(0xff3F51B5) else Color(0xffE7E8E9))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = if(isSelected) Color.White else Color.Black
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = name,
            color = if(isSelected) Color.White else Color.Black
        )
    }
}

@Composable
@Preview(
    showBackground = true
)
fun HomeScreenContentSuccessPreview() {
    LexiconTheme {
        HomeScreen()
    }
}

@Composable
@Preview
fun FilterButtonNotSelectedPreview() {
    LexiconTheme {
        FilterButton(name = "Все", iconId = R.drawable.ic_home, isSelected = false)
    }
}

@Composable
@Preview
fun FilterButtonSelectedPreview() {
    LexiconTheme {
        FilterButton(name = "Все", iconId = R.drawable.ic_home, isSelected = true)
    }
}

enum class FilterChips(val label: String, val iconId: Int) {
    ALL(
        label = "Все",
        iconId = R.drawable.ic_home
    ),
    FAVORITE(
        label = "Избранное",
        iconId = R.drawable.ic_favorite
    )
}