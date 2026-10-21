package com.vladusecho.lexicon.presentation.screen

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vladusecho.lexicon.R
import com.vladusecho.lexicon.presentation.element.LoadingView
import com.vladusecho.lexicon.presentation.viewmodel.EditDefinitionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDefinitionScreen(
    id: Int,
    viewModel: EditDefinitionViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                EditDefinitionViewModel.EditDefinitionEvent.FinishEdit -> {
                    onBackClick()
                }

                is EditDefinitionViewModel.EditDefinitionEvent.ShowError -> {
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
                        text = stringResource(id = R.string.edit_title),
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
                }
            )
        }
    ) { paddingValues ->
        if (!uiState.isInitialLoaded) {
            LoadingView()
        } else {
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
                        viewModel.processCommand(EditDefinitionViewModel.EditDefinitionCommand.UpdateWord(it))
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
                        viewModel.processCommand(EditDefinitionViewModel.EditDefinitionCommand.UpdateDescription(it))
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
                        viewModel.processCommand(EditDefinitionViewModel.EditDefinitionCommand.UpdateImageUri(it))
                    },
                    onRemoveImage = {
                        viewModel.processCommand(EditDefinitionViewModel.EditDefinitionCommand.RemoveImage)
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
                        viewModel.processCommand(EditDefinitionViewModel.EditDefinitionCommand.PickPartOfSpeech(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = {
                        viewModel.processCommand(EditDefinitionViewModel.EditDefinitionCommand.Save)
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
}
