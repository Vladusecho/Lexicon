package com.vladusecho.lexicon.presentation.screenv2

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.vladusecho.lexicon.R
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.entity.PartOfSpeech
import com.vladusecho.lexicon.presentation.ui.theme.LexiconTheme
import com.vladusecho.lexicon.presentation.viewmodel.CreateDefinitionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDefinitionScreenV2(
    viewModel: CreateDefinitionViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {

    val currentState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                CreateDefinitionViewModel.CreateDefinitionEvent.FinishCreate -> {
                    onBackClick()
                }
            }
        }
    }

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
                        onClick = {
                            viewModel.processCommand(
                                CreateDefinitionViewModel.CreateDefinitionCommand.CleanData
                            )
                        },
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
        CreateDefinitionScreenV2Content(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = paddingValues.calculateTopPadding()),
            currentState = currentState,
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
            word = viewModel.word,
            description = viewModel.description,
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
            onSaveClick = {
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
                            partOfSpeech = viewModel.selectedPartOfSpeech
                        ),
                        imageUri = viewModel.imageUri
                    )
                )
            },
            selectedPartOfSpeech = viewModel.selectedPartOfSpeech,
            onPartOfSpeechClick = {
                viewModel.processCommand(
                    CreateDefinitionViewModel.CreateDefinitionCommand.PickPartOfSpeech(it)
                )
            },
            isEnabledSaveButton = viewModel.allCorrect
        )
    }
}

@Composable
fun CreateDefinitionScreenV2Content(
    modifier: Modifier = Modifier,
    currentState: CreateDefinitionViewModel.CreateDefinitionState,
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
        CreateDefinitionViewModel.CreateDefinitionState.Error -> {

        }

        CreateDefinitionViewModel.CreateDefinitionState.Loading -> {

        }

        CreateDefinitionViewModel.CreateDefinitionState.Success -> {
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
                    color = Color(0xff454652).copy(alpha = 0.5f),
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
fun SaveButton(
    modifier: Modifier = Modifier,
    isEnabledSaveButton: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xff24389C)
        ),
        enabled = isEnabledSaveButton
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
    selectedPartOfSpeech: PartOfSpeech,
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
    imageUri: Uri?,
    onImageUriChange: (Uri) -> Unit,
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

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                launcher.launch("image/*")
            }
            .background(Color(0xffe7e8e9))
            .border(1.dp, Color(0xffC5C5D4), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1 / 1f),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1 / 1f)
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
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
fun CreateDefinitionScreenV2SuccessPreview() {
    LexiconTheme {
        CreateDefinitionScreenV2Content(
            currentState = CreateDefinitionViewModel.CreateDefinitionState.Success,
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
            isEnabledSaveButton = false
        )
    }
}