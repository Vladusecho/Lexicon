package com.vladusecho.lexicon.presentation.screenv2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.presentation.element.LoadingView
import com.vladusecho.lexicon.presentation.ui.theme.LexiconTheme
import com.vladusecho.lexicon.presentation.viewmodel.EducationViewModel

@Composable
fun EducationScreen(
    viewModel: EducationViewModel = hiltViewModel()
) {

    val state = viewModel.state.collectAsState()
    val currentState = state.value

    Scaffold(
        topBar = {
            EducationTopAppBar()
        }
    ) { paddingValues ->
        EducationScreenContent(
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
            currentState = currentState,
            isDefinitionShown = viewModel.isDefinitionShown,
            isImgShown = viewModel.isImgShown,
            onDefinitionClick = {
                viewModel.processCommand(
                    EducationViewModel.EducationCommand.ShowDefinition(!viewModel.isDefinitionShown)
                )
            },
            onImgClick = {
                viewModel.processCommand(
                    EducationViewModel.EducationCommand.ShowImg(!viewModel.isImgShown)
                )
            },
            onContinueClick = {
                viewModel.processCommand(
                    EducationViewModel.EducationCommand.ShowNextWord
                )
            }
        )
    }
}

@Composable
fun EducationScreenContent(
    modifier: Modifier = Modifier,
    currentState: EducationViewModel.EducationState,
    isDefinitionShown: Boolean,
    isImgShown: Boolean,
    onDefinitionClick: () -> Unit,
    onImgClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        MainTitle(
            modifier = Modifier.padding(
                horizontal = 16.dp,
            )
        )
        Spacer(modifier = Modifier.height(32.dp))
        when (currentState) {
            EducationViewModel.EducationState.Loading -> {
                LoadingView()
            }
            is EducationViewModel.EducationState.Error -> {}
            is EducationViewModel.EducationState.Success -> {
                ShowDefinitionCard(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                    ),
                    definition = currentState.definition,
                    isDefinitionShown = isDefinitionShown,
                    isImgShown = isImgShown,
                    onDefinitionClick = onDefinitionClick,
                    onImgClick = onImgClick
                )
                Spacer(modifier = Modifier.height(32.dp))
                OptionButtons(
                    modifier = Modifier.padding(
                        horizontal = 16.dp
                    ),
                    onContinueClick = onContinueClick
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
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
    onContinueClick: () -> Unit
) {
    Row(
        modifier = modifier
    ) {
        Button(
            onClick = onContinueClick,
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
    isImgShown: Boolean,
    onDefinitionClick: () -> Unit,
    onImgClick: () -> Unit
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
        Spacer(modifier = Modifier.height(16.dp))
        if (isImgShown) {
            AsyncImage(
                model = definition.imgUri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .aspectRatio(1 / 1f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        onImgClick()
                    }
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                        RoundedCornerShape(16.dp)
                    ),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        onImgClick()
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
                        text = "Изображение скрыто",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Коснитесь здесь, чтобы отобразить изображение слова",
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
            isImgShown = true,
            onDefinitionClick = {},
            onImgClick = {},
            onContinueClick = {},
            currentState = EducationViewModel.EducationState.Success(
                definition = Definition(
                    id = 1,
                    word = "Толерантность",
                    description = "характер, когда человек не обращает внимания на действия остальных людей или животных",
                    isFavorite = false,
                    partOfSpeech = com.vladusecho.lexicon.domain.entity.PartOfSpeech.NOUN
                )
            )
        )
    }
}