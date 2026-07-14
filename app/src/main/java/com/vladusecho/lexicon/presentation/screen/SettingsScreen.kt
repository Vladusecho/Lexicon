package com.vladusecho.lexicon.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vladusecho.lexicon.domain.entity.Settings
import com.vladusecho.lexicon.presentation.element.ErrorView
import com.vladusecho.lexicon.presentation.element.LoadingView
import com.vladusecho.lexicon.presentation.ui.theme.LexiconTheme
import com.vladusecho.lexicon.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {

    val currentState by viewModel.state.collectAsStateWithLifecycle()

    Column {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Настройки",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
        )
        SettingsScreenContent(
            currentState = currentState,
            onDarkModeClick = {
                viewModel.processCommand(SettingsViewModel.SettingsCommand.ToggleDarkMode(it))
            }
        )
    }
}

@Composable
fun SettingsScreenContent(
    currentState: SettingsViewModel.SettingsState,
    onDarkModeClick: (Boolean) -> Unit
) {
    when (currentState) {
        SettingsViewModel.SettingsState.Error -> {
            ErrorView()
        }

        SettingsViewModel.SettingsState.Loading -> {
//            LoadingView()
        }

        is SettingsViewModel.SettingsState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 8.dp)
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Тёмная тема",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Switch(
                        checked = currentState.settings.isDarkMode,
                        onCheckedChange = {
                            onDarkModeClick(it)
                        },
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = "Created by Vladusecho <3",
                        color = Color.Gray,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp
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
fun SettingsScreenSuccessPreview() {
    LexiconTheme {
        SettingsScreenContent(
            currentState = SettingsViewModel.SettingsState.Success(
                settings = Settings(
                    isDarkMode = true
                ),
            ),
            onDarkModeClick = {}
        )
    }
}

@Composable
@Preview(
    showBackground = true
)
fun SettingsScreenLoadingPreview() {
    LexiconTheme {
        SettingsScreenContent(
            currentState = SettingsViewModel.SettingsState.Loading,
            onDarkModeClick = {}
        )
    }
}

@Composable
@Preview(
    showBackground = true
)
fun SettingsScreenErrorPreview() {
    LexiconTheme {
        SettingsScreenContent(
            currentState = SettingsViewModel.SettingsState.Error,
            onDarkModeClick = {}
        )
    }
}

