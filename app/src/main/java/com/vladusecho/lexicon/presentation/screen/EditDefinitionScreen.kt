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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vladusecho.lexicon.R
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.presentation.viewmodel.EditDefinitionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDefinitionScreen(
    id: Int,
    viewModel: EditDefinitionViewModel = hiltViewModel(
        creationCallback = { factory: EditDefinitionViewModel.Factory ->
            factory.create(id)
        }
    ),
    onBackClick: () -> Unit,
) {

    val state = viewModel.state.collectAsStateWithLifecycle()
    val currentState = state.value

    LaunchedEffect(key1 = Unit) {
        viewModel.event.collect { event ->
            when (event) {
                EditDefinitionViewModel.EditDefinitionEvent.FinishEdit -> {
                    onBackClick()
                }
            }
        }
    }

    Column() {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Редактирование",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color(0xff0d1e25)
            ),
            navigationIcon = {
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        viewModel.processCommand(
                            EditDefinitionViewModel.EditDefinitionCommand.EditDefinition(
                                definition = Definition(
                                    id = id,
                                    word = viewModel.word,
                                    description = viewModel.description
                                )
                            )
                        )
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        )
        EditDefinitionScreenContent(
            modifier = Modifier.padding(top = 32.dp),
            currentState = currentState,
            word = viewModel.word,
            description = viewModel.description,
            onWordChange = {
                viewModel.processCommand(
                    EditDefinitionViewModel.EditDefinitionCommand.UpdateWord(it)
                )
            },
            onDescriptionChange = {
                viewModel.processCommand(
                    EditDefinitionViewModel.EditDefinitionCommand.UpdateDescription(it)
                )
            }
        )
    }
}

@Composable
fun EditDefinitionScreenContent(
    modifier: Modifier = Modifier,
    currentState: EditDefinitionViewModel.EditDefinitionState,
    word: String,
    description: String,
    onWordChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
) {

    when (currentState) {
        EditDefinitionViewModel.EditDefinitionState.Error -> {

        }

        EditDefinitionViewModel.EditDefinitionState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xff0d1e25)
                )
            }
        }

        is EditDefinitionViewModel.EditDefinitionState.Success -> {
            val focusManager = LocalFocusManager.current

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xfff1f1f1)),
                    contentAlignment = Alignment.Center
                ) {
                    TextField(
                        value = word,
                        onValueChange = onWordChange,
                        placeholder = {
                            Text(
                                text = "Введите слово",
                                color = Color.Gray,
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
                            cursorColor = Color.Black,
                        ),
                        textStyle = TextStyle(
                            color = Color.Black,
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
                        .background(Color(0xfff1f1f1)),
                    contentAlignment = Alignment.Center
                ) {
                    TextField(
                        value = description,
                        onValueChange = onDescriptionChange,
                        placeholder = {
                            Text(
                                text = "Введите описание",
                                color = Color.Gray,
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
                            cursorColor = Color.Black,
                        ),
                        textStyle = TextStyle(
                            color = Color.Black,
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
    }
}