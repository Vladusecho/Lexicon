package com.vladusecho.lexicon.presentation.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
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

    Column {
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
                                    id = 0,
                                    word = formattedWord,
                                    description = viewModel.description,
                                    isFavorite = false,
                                ),
                                imageUri = viewModel.imageUri
                            )
                        )
                    },
                    enabled = viewModel.allCorrect
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = null,
                        tint = if (viewModel.allCorrect) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.tertiary.copy(
                            alpha = 0.3f
                        )
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
            },
            imageUri = viewModel.imageUri,
            onImageUriChange = {
                viewModel.processCommand(
                    CreateDefinitionViewModel.CreateDefinitionCommand.UpdateImageUri(it)
                )
            },
            onRemoveImageClick = {
                viewModel.processCommand(
                    CreateDefinitionViewModel.CreateDefinitionCommand.RemoveImage
                )
            },
        )
    }
}

@Composable
fun CreateDefinitionScreenContent(
    modifier: Modifier = Modifier,
    word: String,
    description: String,
    imageUri: Uri?,
    onImageUriChange: (Uri) -> Unit,
    onWordChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    currentState: CreateDefinitionViewModel.CreateDefinitionState,
    onRemoveImageClick: () -> Unit
) {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            if (it != null) {
                onImageUriChange(it)
            }
        }
    )

    when (currentState) {
        CreateDefinitionViewModel.CreateDefinitionState.Error -> {

        }

        CreateDefinitionViewModel.CreateDefinitionState.Success -> {
            val focusManager = LocalFocusManager.current

            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 32.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondary)
                            .aspectRatio(1 / 1f)
                            .clickable {
                                launcher.launch("image/*")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.TopEnd
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable{
                                            onRemoveImageClick()
                                        }
                                        .background(Color.Red)
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_trash),
                                        contentDescription = null,
                                        tint = Color.White,
                                    )
                                }
                            }
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_add_image),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
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
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
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
            onDescriptionChange = {},
            imageUri = null,
            onImageUriChange = {},
            onRemoveImageClick = {},
        )
    }
}