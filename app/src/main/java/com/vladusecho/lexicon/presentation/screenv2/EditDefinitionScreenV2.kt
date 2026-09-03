package com.vladusecho.lexicon.presentation.screenv2

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vladusecho.lexicon.R
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.entity.PartOfSpeech
import com.vladusecho.lexicon.presentation.element.ErrorView
import com.vladusecho.lexicon.presentation.element.LoadingView
import com.vladusecho.lexicon.presentation.ui.theme.LexiconTheme
import com.vladusecho.lexicon.presentation.viewmodel.CreateDefinitionViewModel
import com.vladusecho.lexicon.presentation.viewmodel.EditDefinitionViewModel
import com.vladusecho.lexicon.presentation.viewmodel.EditDefinitionViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDefinitionScreenV2(
    id: Int,
    viewModel: EditDefinitionViewModel = hiltViewModel(
        creationCallback = { factory: EditDefinitionViewModelFactory ->
            factory.create(id)
        }
    ),
    onBackClick: () -> Unit,
) {

    val currentState by viewModel.state.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                EditDefinitionViewModel.EditDefinitionEvent.FinishEdit -> {
                    onBackClick()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .shadow(elevation = 3.dp, spotColor = MaterialTheme.colorScheme.tertiary),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                title = {
                    Text(
                        text = "Редактирование",
                        color = MaterialTheme.colorScheme.tertiary,
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
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.processCommand(
                                EditDefinitionViewModel.EditDefinitionCommand.CleanData
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Text(
                            text = "СБРОСИТЬ",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        EditDefinitionScreenV2Content(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding()),
            currentState = currentState,
            onWordChange = {
                viewModel.processCommand(
                    EditDefinitionViewModel.EditDefinitionCommand.UpdateWord(it)
                )
            },
            onDescriptionChange = {
                viewModel.processCommand(
                    EditDefinitionViewModel.EditDefinitionCommand.UpdateDescription(it)
                )
            },
            word = viewModel.word,
            description = viewModel.description,
            imageUri = viewModel.imageUri,
            onImageUriChange = {
                viewModel.processCommand(
                    EditDefinitionViewModel.EditDefinitionCommand.UpdateImageUri(it)
                )
            },
            onRemoveImageClick = {
                viewModel.processCommand(
                    EditDefinitionViewModel.EditDefinitionCommand.RemoveImage
                )
            },
            onSaveClick = {

                val formattedWord = viewModel.word.trim().replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }

                viewModel.processCommand(
                    EditDefinitionViewModel.EditDefinitionCommand.EditDefinition(
                        definition = Definition(
                            id = id,
                            word = formattedWord,
                            description = viewModel.description,
                            isFavorite = isFavorite,
                            partOfSpeech = viewModel.selectedPartOfSpeech
                        ),
                        imageUri = viewModel.imageUri
                    )
                )
            },
            selectedPartOfSpeech = viewModel.selectedPartOfSpeech,
            onPartOfSpeechClick = {
                viewModel.processCommand(
                    EditDefinitionViewModel.EditDefinitionCommand.PickPartOfSpeech(it)
                )
            },
            isEnabledSaveButton = viewModel.allCorrect
        )
    }
}

@Composable
fun EditDefinitionScreenV2Content(
    modifier: Modifier = Modifier,
    currentState: EditDefinitionViewModel.EditDefinitionState,
    onWordChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    word: String,
    description: String,
    imageUri: Uri?,
    onImageUriChange: (Uri) -> Unit,
    onRemoveImageClick: () -> Unit,
    onSaveClick: () -> Unit,
    selectedPartOfSpeech: PartOfSpeech,
    onPartOfSpeechClick: (PartOfSpeech) -> Unit,
    isEnabledSaveButton: Boolean
) {

    val scrollState = rememberScrollState()

    when (currentState) {
        EditDefinitionViewModel.EditDefinitionState.Error -> {
            ErrorView()
        }
        EditDefinitionViewModel.EditDefinitionState.Loading -> {
//            LoadingView()
        }
        EditDefinitionViewModel.EditDefinitionState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Spacer(Modifier.height(24.dp))
                Title(
                    text = "ОСНОВНАЯ ИНФОРМАЦИЯ",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 12
                )
                Spacer(Modifier.height(16.dp))
                TextFieldWithTitle(
                    title = "Слово*",
                    placeholder = "Например: Толерантность",
                    value = word,
                    onValueChange = onWordChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    singleLine = true
                )
                Spacer(Modifier.height(24.dp))
                TextFieldWithTitle(
                    title = "Определение*",
                    placeholder = "Опишите значение слова...",
                    value = description,
                    onValueChange = onDescriptionChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    singleLine = false
                )
                Text(
                    text = "*Обязательно для заполнения",
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(16.dp))
                Title(
                    text = "ДОПОЛНИТЕЛЬНАЯ ИНФОРМАЦИЯ",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 12
                )
                Spacer(Modifier.height(16.dp))
                BoxWithImageChoice(
                    imageUri = imageUri,
                    onImageUriChange = onImageUriChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onRemoveImageClick = onRemoveImageClick
                )
                Spacer(Modifier.height(16.dp))
                Title(
                    text = "ЧАСТЬ РЕЧИ",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 12
                )
                Spacer(Modifier.height(16.dp))
                RowWithPartOfSpeechChoice(
                    selectedPartOfSpeech = selectedPartOfSpeech,
                    onPartOfSpeechClick = onPartOfSpeechClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(24.dp))
                SaveButton(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    isEnabledSaveButton = isEnabledSaveButton
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
@Preview(
    showBackground = true
)
fun EditDefinitionScreenV2ContentPreview() {
    LexiconTheme {
        EditDefinitionScreenV2Content(
            onWordChange = {},
            onDescriptionChange = {},
            word = "",
            description = "",
            imageUri = null,
            onImageUriChange = {},
            onRemoveImageClick = {},
            onSaveClick = {},
            selectedPartOfSpeech = PartOfSpeech.NOUN,
            onPartOfSpeechClick = {},
            isEnabledSaveButton = false,
            currentState = EditDefinitionViewModel.EditDefinitionState.Success
        )
    }
}