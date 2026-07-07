package com.vladusecho.lexicon.presentation.element

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.presentation.ui.theme.LexiconTheme

@Composable
fun ShortDefinition(
    modifier: Modifier = Modifier,
    definition: Definition,
    onClick: (Int) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onClick(
                    definition.id
                )
            }
            .background(MaterialTheme.colorScheme.secondary)
            .padding(horizontal = 8.dp, vertical = 16.dp)
    ) {
        Text(
            text = definition.word + " - ",
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = definition.description,
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
@Preview(
    showBackground = true
)
fun ShortDefinitionLightPreview() {
    LexiconTheme() {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ShortDefinition(
                definition = Definition(
                    id = 0,
                    word = "Толерантность",
                    description = "характер, когда человек не обращает внимания на действия остальных людей",
                    isFavorite = false
                ),
                modifier = Modifier.padding(16.dp),
                onClick = {}
            )
        }
    }
}

@Composable
@Preview(
    showBackground = true
)
fun ShortDefinitionDarkPreview() {
    LexiconTheme(
        darkTheme = true
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ShortDefinition(
                definition = Definition(
                    id = 0,
                    word = "Толерантность",
                    description = "характер, когда человек не обращает внимания на действия остальных людей",
                    isFavorite = false
                ),
                modifier = Modifier.padding(16.dp),
                onClick = {}
            )
        }
    }
}