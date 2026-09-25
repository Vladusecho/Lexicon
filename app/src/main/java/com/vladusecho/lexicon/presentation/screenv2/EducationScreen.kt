package com.vladusecho.lexicon.presentation.screenv2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.presentation.ui.theme.LexiconTheme
import com.vladusecho.lexicon.presentation.viewmodel.EducationViewModel

@Composable
fun EducationScreen(
    viewModel: EducationViewModel = hiltViewModel()
) {

    Scaffold(
        topBar = {
            EducationTopAppBar()
        }
    ) { paddingValues ->
        EducationScreenContent(
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
            isDefinitionShown = viewModel.isDefinitionShown,
            onDefinitionClick = {
                viewModel.processCommand(
                    EducationViewModel.EducationCommand.ShowDefinition(!viewModel.isDefinitionShown)
                )
            }
        )
    }
}

@Composable
fun EducationScreenContent(
    modifier: Modifier = Modifier,
    isDefinitionShown: Boolean,
    onDefinitionClick: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        MainTitle(
            modifier = Modifier.padding(
                horizontal = 16.dp,
            )
        )
        Spacer(modifier = Modifier.height(32.dp))
        ShowDefinitionCard(
            modifier = Modifier.padding(
                horizontal = 16.dp,
            ),
            definition = Definition(
                id = 1,
                word = "Толерантность",
                description = "характер, когда человек не обращает внимания на действия остальных людей или животных",
                isFavorite = false,
                partOfSpeech = com.vladusecho.lexicon.domain.entity.PartOfSpeech.NOUN
            ),
            isDefinitionShown = isDefinitionShown,
            onDefinitionClick = onDefinitionClick
        )
        Spacer(modifier = Modifier.height(32.dp))
        OptionButtons(
            modifier = Modifier.padding(
                horizontal = 16.dp
            )
        )
    }
}

@Composable
fun MainTitle(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(
            text = "Повторение слов",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Все слова • Общий словарь",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun OptionButtons(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
    ) {
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Далее",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ShowDefinitionCard(
    modifier: Modifier = Modifier,
    definition: Definition,
    isDefinitionShown: Boolean,
    onDefinitionClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                RoundedCornerShape(16.dp)
            )
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onBackground)
                .padding(vertical = 4.dp, horizontal = 16.dp)
        ) {
            Text(
                text = definition.partOfSpeech.label,
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = definition.word,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.primary
        )
        if (isDefinitionShown) {
            Spacer(modifier = Modifier.height(42.dp))
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        onDefinitionClick()
                    }

            ) {
                Text(
                    text = "Определение",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = definition.description,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        onDefinitionClick()
                    }
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.onBackground),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Определение скрыто",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Коснитесь здесь, чтобы отобразить значение слова",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationTopAppBar() {
    TopAppBar(
        title = {
            Text(
                text = "Lexicon",
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier
            .shadow(elevation = 3.dp, spotColor = MaterialTheme.colorScheme.tertiary),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
    )
}

@Composable
@Preview(
    showBackground = true
)
fun EducationScreenPreview() {
    LexiconTheme {
        EducationScreenContent(
            isDefinitionShown = true,
            onDefinitionClick = {}
        )
    }
}