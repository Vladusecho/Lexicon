package com.vladusecho.lexicon.presentation.screenv2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vladusecho.lexicon.R
import com.vladusecho.lexicon.domain.entity.PartOfSpeech
import com.vladusecho.lexicon.presentation.ui.theme.LexiconTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDefinitionScreenV2(
    onBackClick: () -> Unit
) {

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .shadow(elevation = 3.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                title = {
                    Text(
                        text = "Добавление",
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
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Text(
                            text = "СБРОСИТЬ",
                            color = Color(0xff24389C),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
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
                .scrollable(scrollState, orientation = Orientation.Horizontal)
        ) {
            Spacer(Modifier.height(24.dp))
            Title(
                text = "ОСНОВНАЯ ИНФОРМАЦИЯ",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 12
            )
            Spacer(Modifier.height(16.dp))
            TextFieldWithTitle(
                title = "Слово",
                placeholder = "Например: Толерантность",
                value = "",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true
            )
            Spacer(Modifier.height(24.dp))
            TextFieldWithTitle(
                title = "Определение",
                placeholder = "Опишите значение слова...",
                value = "",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = false
            )
            Spacer(Modifier.height(24.dp))
            Title(
                text = "ДОПОЛНИТЕЛЬНАЯ ИНФОРМАЦИЯ",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 12
            )
            Spacer(Modifier.height(16.dp))
            BoxWithImageChoice(
                imageUri = "",
                onImageClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(16.dp))
            Title(
                text = "ЧАСТЬ РЕЧИ",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 12
            )
            Spacer(Modifier.height(16.dp))
            RowWithPartOfSpeechChoice(
                selectedPartOfSpeech = null,
                onPartOfSpeechClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(24.dp))
            SaveButton(
                onClick = {},
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun SaveButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xff24389C)
        )
    ) {
        Text(
            text = "СОХРАНИТЬ",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RowWithPartOfSpeechChoice(
    modifier: Modifier = Modifier,
    selectedPartOfSpeech: PartOfSpeech?,
    onPartOfSpeechClick: (PartOfSpeech) -> Unit
) {

    val partsOfSpeech = PartOfSpeech.entries

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        partsOfSpeech.forEach {
            FilterButton(
                name = it.label,
                isSelected = it == selectedPartOfSpeech,
                onClick = { onPartOfSpeechClick(it) }
            )
        }
    }
}

@Composable
fun BoxWithImageChoice(
    modifier: Modifier = Modifier,
    imageUri: String,
    onImageClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                onImageClick()
            }
            .background(Color(0xffe7e8e9))
            .border(1.dp, Color(0xffC5C5D4), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (imageUri.isNotEmpty()) {
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1 / 1f),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_image),
                    contentDescription = null,
                    tint = Color(0xff454652),
                    modifier = Modifier.size(42.dp)
                )
                Text(
                    text = "Добавить изображение",
                    color = Color(0xff454652),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Визуальные ассоциации\nпомогают лучше запоминать",
                    color = Color(0xff454652),
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun TextFieldWithTitle(
    modifier: Modifier = Modifier,
    title: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .border(1.dp, Color(0xffC5C5D4), RoundedCornerShape(16.dp))
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,

                ),
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xff454652).copy(alpha = 0.5f)
                )
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = singleLine
        )
    }
}

@Composable
fun Title(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: Int
) {
    Text(
        modifier = modifier,
        text = text,
        color = Color(0xff24389C),
        fontWeight = FontWeight.SemiBold,
        fontSize = fontSize.sp
    )
}

@Composable
@Preview(
    showBackground = true
)
fun CreateDefinitionScreenV2Preview() {
    LexiconTheme {
        CreateDefinitionScreenV2(
            onBackClick = {}
        )
    }
}