package com.vladusecho.lexicon.presentation.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.vladusecho.lexicon.R
import com.vladusecho.lexicon.domain.entity.PartOfSpeech
import com.vladusecho.lexicon.presentation.viewmodel.CreateDefinitionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDefinitionScreen(
    viewModel: CreateDefinitionViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                CreateDefinitionViewModel.CreateDefinitionEvent.FinishCreate -> {
                    onBackClick()
                }

                is CreateDefinitionViewModel.CreateDefinitionEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
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
                        text = stringResource(id = R.string.create_title),
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
                            viewModel.processCommand(CreateDefinitionViewModel.CreateDefinitionCommand.CleanData)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text(
                            text = stringResource(id = R.string.action_reset),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = paddingValues.calculateTopPadding())
                .verticalScroll(scrollState)
        ) {
            Spacer(Modifier.height(24.dp))
            SectionTitle(
                text = "ОСНОВНАЯ ИНФОРМАЦИЯ",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(16.dp))
            DefinitionInputField(
                title = stringResource(id = R.string.field_word_label) + "*",
                placeholder = stringResource(id = R.string.field_word_placeholder),
                value = uiState.word,
                onValueChange = {
                    viewModel.processCommand(CreateDefinitionViewModel.CreateDefinitionCommand.UpdateWord(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true
            )
            Spacer(Modifier.height(20.dp))
            DefinitionInputField(
                title = stringResource(id = R.string.field_description_label) + "*",
                placeholder = stringResource(id = R.string.field_description_placeholder),
                value = uiState.description,
                onValueChange = {
                    viewModel.processCommand(CreateDefinitionViewModel.CreateDefinitionCommand.UpdateDescription(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = false
            )
            Spacer(Modifier.height(24.dp))
            SectionTitle(
                text = "ИЗОБРАЖЕНИЕ",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(16.dp))
            ImagePickerBox(
                imageUri = uiState.imageUri,
                onImagePicked = {
                    viewModel.processCommand(CreateDefinitionViewModel.CreateDefinitionCommand.UpdateImageUri(it))
                },
                onRemoveImage = {
                    viewModel.processCommand(CreateDefinitionViewModel.CreateDefinitionCommand.RemoveImage)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(24.dp))
            SectionTitle(
                text = "ЧАСТЬ РЕЧИ",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(16.dp))
            PartOfSpeechFlowRow(
                selected = uiState.selectedPartOfSpeech,
                onSelect = {
                    viewModel.processCommand(CreateDefinitionViewModel.CreateDefinitionCommand.PickPartOfSpeech(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = {
                    viewModel.processCommand(CreateDefinitionViewModel.CreateDefinitionCommand.Save)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = uiState.isValid && !uiState.isLoading
            ) {
                Text(
                    text = stringResource(id = R.string.action_save),
                    color = MaterialTheme.colorScheme.background,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 1.sp
    )
}

@Composable
fun DefinitionInputField(
    modifier: Modifier = Modifier,
    title: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                unfocusedContainerColor = MaterialTheme.colorScheme.onBackground,
                focusedContainerColor = MaterialTheme.colorScheme.onBackground,
            ),
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f)
                )
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = singleLine
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PartOfSpeechFlowRow(
    modifier: Modifier = Modifier,
    selected: PartOfSpeech,
    onSelect: (PartOfSpeech) -> Unit
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PartOfSpeech.entries.forEach { part ->
            val isSelected = part == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onSelect(part) }
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = part.label,
                    color = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.tertiary,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ImagePickerBox(
    modifier: Modifier = Modifier,
    imageUri: Uri?,
    onImagePicked: (Uri) -> Unit,
    onRemoveImage: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> if (uri != null) onImagePicked(uri) }
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { launcher.launch("image/*") }
            .background(MaterialTheme.colorScheme.onBackground)
            .border(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = onRemoveImage,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Red.copy(alpha = 0.85f))
                        .size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_trash),
                        contentDescription = stringResource(id = R.string.action_remove_image),
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_image),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.action_add_image),
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
