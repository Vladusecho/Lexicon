package com.vladusecho.lexicon.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vladusecho.lexicon.R
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.presentation.ui.theme.LexiconTheme
import com.vladusecho.lexicon.presentation.viewmodel.CreateDefinitionViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDefinitionScreen(
    viewModel: CreateDefinitionViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {

    val state = viewModel.state.collectAsStateWithLifecycle()
    val currentState = state.value

    LaunchedEffect(key1 = Unit) {
        viewModel.event.collect { event ->
            when (event) {
                CreateDefinitionViewModel.CreateDefinitionEvent.FinishCreate -> {
                    onBackClick()
                }
            }
        }
    }

    Column(

    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Добавление",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            actions = {
                IconButton(
                    onClick = {

                        val formattedWord = viewModel.word.trim().replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase() else it.toString()
                        }

                        viewModel.processCommand(
                            CreateDefinitionViewModel.CreateDefinitionCommand.CreateDefinition(
                                definition = Definition(
                                    id = Random.nextInt(),
                                    word = formattedWord,
                                    description = viewModel.description,
                                    isFavorite = false
                                )
                            )
                        )
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        )
        CreateDefinitionScreenContent(
            currentState = currentState,
            word = viewModel.word,
            description = viewModel.description,
            onWordChange = {
                viewModel.processCommand(
                    CreateDefinitionViewModel.CreateDefinitionCommand.UpdateWord(it)
                )
            },
            onDescriptionChange = {
                viewModel.processCommand(
                    CreateDefinitionViewModel.CreateDefinitionCommand.UpdateDescription(it)
                )
            }
        )
    }
}

@Composable
fun CreateDefinitionScreenContent(
    modifier: Modifier = Modifier,
    word: String,
    description: String,
    onWordChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    currentState: CreateDefinitionViewModel.CreateDefinitionState
) {
    when (currentState) {
        CreateDefinitionViewModel.CreateDefinitionState.Error -> {

        }

        CreateDefinitionViewModel.CreateDefinitionState.Success -> {
            val focusManager = LocalFocusManager.current

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
                    .padding(top = 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondary),
                    contentAlignment = Alignment.Center
                ) {
                    TextField(
                        value = word,
                        onValueChange = onWordChange,
                        placeholder = {
                            Text(
                                text = "Введите слово",
                                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Normal,
                                fontSize = 24.sp
                            )
                        },
                        singleLine = true,
                        maxLines = 1,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.tertiary,
                        ),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondary),
                    contentAlignment = Alignment.Center
                ) {
                    TextField(
                        value = description,
                        onValueChange = onDescriptionChange,
                        placeholder = {
                            Text(
                                text = "Введите описание",
                                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Normal,
                                fontSize = 20.sp
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.tertiary,
                        ),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Normal,
                            fontSize = 20.sp
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                    )
                }
            }
        }

        CreateDefinitionViewModel.CreateDefinitionState.Loading -> {

        }
    }
}

@Composable
@Preview(
    showBackground = true
)
fun CreateDefinitionScreenSuccessPreview() {
    LexiconTheme() {
        CreateDefinitionScreenContent(
            currentState = CreateDefinitionViewModel.CreateDefinitionState.Success,
            word = "Толерантность",
            description = "характер, когда человек не обращает внимания на действия остальных людей",
            onWordChange = {},
            onDescriptionChange = {}
        )
    }
}