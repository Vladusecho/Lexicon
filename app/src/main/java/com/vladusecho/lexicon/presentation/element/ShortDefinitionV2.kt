package com.vladusecho.lexicon.presentation.element

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.presentation.ui.theme.LexiconTheme

@Composable
fun ShortDefinitionV2(
    modifier: Modifier = Modifier,
    definition: Definition
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xffC5C5D4), RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = definition.word,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xff24389C)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = definition.description,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontSize = 16.sp,
            color = Color(0xff454652)
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    backgroundColor = 0x000
)
fun ShortDefinitionV2Preview() {
    LexiconTheme{
        ShortDefinitionV2(
            definition = Definition(
                id = 1,
                word = "Толерантность",
                description = "характер, когда человек не обращает внимания на действия остальных людей или животных",
                isFavorite = false
            )
        )
    }
}