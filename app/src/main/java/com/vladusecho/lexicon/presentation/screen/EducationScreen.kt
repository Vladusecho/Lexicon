package com.vladusecho.lexicon.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.vladusecho.lexicon.R
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.presentation.element.ErrorView
import com.vladusecho.lexicon.presentation.element.LoadingView
import com.vladusecho.lexicon.presentation.viewmodel.EducationViewModel

@Composable
fun EducationScreen(
    viewModel: EducationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { EducationTopAppBar() }
    ) { paddingValues ->
        EducationScreenContent(
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
            currentState = state,
            onDefinitionClick = {
                viewModel.processCommand(EducationViewModel.EducationCommand.ToggleDefinition)
            },
            onImgClick = {
                viewModel.processCommand(EducationViewModel.EducationCommand.ToggleImg)
            },
            onContinueClick = {
                viewModel.processCommand(EducationViewModel.EducationCommand.ShowNextWord)
            }
        )
    }
}

@Composable
fun EducationScreenContent(
    modifier: Modifier = Modifier,
    currentState: EducationViewModel.EducationState,
    onDefinitionClick: () -> Unit,
    onImgClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        EducationHeader(modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(24.dp))

        when (currentState) {
            EducationViewModel.EducationState.Loading -> {
                LoadingView()
            }

            is EducationViewModel.EducationState.Error -> {
                when (currentState.errorType) {
                    EducationViewModel.ErrorType.NO_WORDS -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp, vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(id = R.string.education_empty_title),
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(id = R.string.education_empty_description),
                                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    EducationViewModel.ErrorType.UNKNOWN -> {
                        ErrorView()
                    }
                }
            }

            is EducationViewModel.EducationState.Success -> {
                ShowDefinitionCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    definition = currentState.definition,
                    isDefinitionShown = currentState.isDefinitionShown,
                    isImgShown = currentState.isImgShown,
                    onDefinitionClick = onDefinitionClick,
                    onImgClick = onImgClick
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onContinueClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = stringResource(id = R.string.education_next),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.background,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun EducationHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.education_title),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(id = R.string.education_subtitle),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
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
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
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

        Spacer(modifier = Modifier.height(24.dp))
        if (isDefinitionShown) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.onBackground)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDefinitionClick
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.education_definition_label),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = definition.description,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                    lineHeight = 22.sp
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.onBackground)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDefinitionClick
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.education_definition_hidden),
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (definition.imgUri != null) {
            Spacer(modifier = Modifier.height(16.dp))
            if (isImgShown) {
                AsyncImage(
                    model = definition.imgUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onImgClick)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.onBackground)
                        .clickable(onClick = onImgClick)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.education_show_image),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
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
                text = stringResource(id = R.string.app_name),
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
