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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.vladusecho.lexicon.presentation.ui.theme.LexiconTheme
import com.vladusecho.lexicon.presentation.viewmodel.DetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    id: Int,
    viewModel: DetailsViewModel = hiltViewModel(
        creationCallback = { factory: DetailsViewModel.Factory ->
            factory.create(id)
        }
    ),
    onBackClick: () -> Unit,
    onEditClick: (
        id: Int,
    ) -> Unit
) {

    val state = viewModel.state.collectAsStateWithLifecycle()
    val currentState = state.value

    Column() {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Определение",
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
                        onEditClick(id)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        )
        DetailsScreenContent(
            modifier = Modifier.padding(top = 32.dp),
            currentState = currentState
        )
    }
}

@Composable
fun DetailsScreenContent(
    modifier: Modifier = Modifier,
    currentState: DetailsViewModel.DetailsState
) {
    when (currentState) {
        DetailsViewModel.DetailsState.Error -> {

        }

        DetailsViewModel.DetailsState.Loading -> {
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

        is DetailsViewModel.DetailsState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xfff1f1f1))
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentState.definition.word,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xfff1f1f1))
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentState.definition.description,
                        color = Color.Black,
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
@Preview(
    showBackground = true
)
fun DetailsScreenSuccessPreview() {
    LexiconTheme() {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            DetailsScreenContent(
                currentState = DetailsViewModel.DetailsState.Success(
                    Definition(
                        id = 1,
                        word = "Толерантность",
                        description = "характер, когда человек не обращает внимания на действия остальных людей"
                    )
                )
            )
        }
    }
}

@Composable
@Preview(
    showBackground = true
)
fun DetailsScreenLoadingPreview() {
    LexiconTheme() {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            DetailsScreenContent(
                currentState = DetailsViewModel.DetailsState.Loading
            )
        }
    }
}