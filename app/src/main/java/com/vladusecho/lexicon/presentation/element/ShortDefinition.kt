package com.vladusecho.lexicon.presentation.element

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vladusecho.lexicon.R
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.entity.PartOfSpeech
import com.vladusecho.lexicon.presentation.ui.theme.LexiconTheme

@Composable
fun ShortDefinition(
    modifier: Modifier = Modifier,
    definition: Definition,
    onClick: (Int) -> Unit,
    onFavouriteClick: (Int) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                onClick(definition.id)
            }
            .border(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            Text(
                text = definition.word,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(
                onClick = { onFavouriteClick(definition.id) }
            ) {
                Icon(
                    painter = if (definition.isFavorite) {
                        painterResource(id = R.drawable.ic_favorite_filled)
                    } else {
                        painterResource(id = R.drawable.ic_favorite)
                    },
                    contentDescription = null,
                    tint = if (definition.isFavorite) {
                        Color(0xFFFFB300)
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = definition.description,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ShortDefinitionPreview() {
    LexiconTheme {
        ShortDefinition(
            definition = Definition(
                id = 1,
                word = "Толерантность",
                description = "терпимость к иному мировоззрению, образу жизни, поведению и обычаям.",
                isFavorite = true,
                imgUri = null,
                partOfSpeech = PartOfSpeech.NOUN
            ),
            onClick = {},
            onFavouriteClick = {}
        )
    }
}
