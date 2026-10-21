package com.vladusecho.lexicon.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
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
import com.vladusecho.lexicon.domain.entity.Definition
import com.vladusecho.lexicon.domain.entity.ReviewQuality
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
            onCardFlip = {
                viewModel.processCommand(EducationViewModel.EducationCommand.FlipCard)
            },
            onReviewSubmit = { quality ->
                viewModel.processCommand(EducationViewModel.EducationCommand.SubmitReview(quality))
            },
            onStartFreePractice = {
                viewModel.processCommand(EducationViewModel.EducationCommand.StartFreePractice)
            },
            onRestartDue = {
                viewModel.processCommand(EducationViewModel.EducationCommand.RestartDueSession)
            }
        )
    }
}

@Composable
fun EducationScreenContent(
    modifier: Modifier = Modifier,
    currentState: EducationViewModel.EducationState,
    onCardFlip: () -> Unit,
    onReviewSubmit: (ReviewQuality) -> Unit,
    onStartFreePractice: () -> Unit,
    onRestartDue: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

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
                                .padding(horizontal = 24.dp, vertical = 48.dp),
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

            is EducationViewModel.EducationState.AllDone -> {
                AllDoneCard(
                    onStartFreePractice = onStartFreePractice,
                    onRestartDue = onRestartDue
                )
            }

            is EducationViewModel.EducationState.Success -> {
                EducationHeader(
                    remainingCount = currentState.remainingCount,
                    isFreePractice = currentState.isFreePracticeMode,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Flashcard(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    definition = currentState.definition,
                    isFlipped = currentState.isFlipped,
                    onFlip = onCardFlip
                )
                Spacer(modifier = Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = currentState.isFlipped,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    ReviewButtonsRow(
                        intervalPreviews = currentState.intervalPreviews,
                        onReviewSubmit = onReviewSubmit,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                AnimatedVisibility(
                    visible = !currentState.isFlipped,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Button(
                        onClick = onCardFlip,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Показать ответ",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.background,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ReviewButtonsRow(
    intervalPreviews: Map<ReviewQuality, Int>,
    onReviewSubmit: (ReviewQuality) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReviewButton(
            quality = ReviewQuality.AGAIN,
            days = intervalPreviews[ReviewQuality.AGAIN] ?: 1,
            color = Color(0xFFE53935),
            onClick = { onReviewSubmit(ReviewQuality.AGAIN) },
            modifier = Modifier.weight(1f)
        )
        ReviewButton(
            quality = ReviewQuality.HARD,
            days = intervalPreviews[ReviewQuality.HARD] ?: 1,
            color = Color(0xFFFB8C00),
            onClick = { onReviewSubmit(ReviewQuality.HARD) },
            modifier = Modifier.weight(1f)
        )
        ReviewButton(
            quality = ReviewQuality.GOOD,
            days = intervalPreviews[ReviewQuality.GOOD] ?: 3,
            color = Color(0xFF43A047),
            onClick = { onReviewSubmit(ReviewQuality.GOOD) },
            modifier = Modifier.weight(1f)
        )
        ReviewButton(
            quality = ReviewQuality.EASY,
            days = intervalPreviews[ReviewQuality.EASY] ?: 6,
            color = Color(0xFF1E88E5),
            onClick = { onReviewSubmit(ReviewQuality.EASY) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ReviewButton(
    quality: ReviewQuality,
    days: Int,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.5.dp, color.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = quality.label,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = formatDaysInterval(days),
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.65f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatDaysInterval(days: Int): String {
    return when {
        days <= 1 -> "<1 дн"
        days in 2..4 -> "$days дн"
        days in 5..20 -> "$days дн"
        days in 21..30 -> "$days дн"
        else -> "${days / 30} мес"
    }
}

@Composable
fun AllDoneCard(
    onStartFreePractice: () -> Unit,
    onRestartDue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0xFF43A047).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = null,
                tint = Color(0xFF43A047),
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(id = R.string.education_all_done_title),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.education_all_done_desc),
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(36.dp))
        Button(
            onClick = onStartFreePractice,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.education_start_free_practice),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.background,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onRestartDue,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))
        ) {
            Text(
                text = "Проверить снова",
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun Flashcard(
    modifier: Modifier = Modifier,
    definition: Definition,
    isFlipped: Boolean,
    onFlip: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "flashcardFlip"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 14f * density
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onFlip
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        if (rotation <= 90f) {
            FlashcardFront(definition = definition)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        rotationY = 180f
                    }
            ) {
                FlashcardBack(definition = definition)
            }
        }
    }
}

@Composable
fun FlashcardFront(
    definition: Definition
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 340.dp)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onBackground)
                    .padding(vertical = 4.dp, horizontal = 14.dp)
            ) {
                Text(
                    text = definition.partOfSpeech.label,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 24.dp)
        ) {
            Text(
                text = definition.word,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 34.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_flip),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.45f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(id = R.string.education_flip_hint_front),
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun FlashcardBack(
    definition: Definition
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 340.dp)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = definition.word,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onBackground)
                        .padding(vertical = 3.dp, horizontal = 12.dp)
                ) {
                    Text(
                        text = definition.partOfSpeech.label,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.education_definition_label),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = definition.description,
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )

            if (definition.imgUri != null) {
                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    model = definition.imgUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.4f)
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                            RoundedCornerShape(14.dp)
                        ),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_flip),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.45f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(id = R.string.education_flip_hint_back),
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun EducationHeader(
    remainingCount: Int,
    isFreePractice: Boolean,
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
            text = if (isFreePractice) {
                stringResource(id = R.string.education_start_free_practice)
            } else {
                stringResource(id = R.string.education_remaining, remainingCount)
            },
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = if (isFreePractice) MaterialTheme.colorScheme.primary else Color(0xFF43A047),
            textAlign = TextAlign.Center
        )
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
