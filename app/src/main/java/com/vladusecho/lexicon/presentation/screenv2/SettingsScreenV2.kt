package com.vladusecho.lexicon.presentation.screenv2

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vladusecho.lexicon.R
import com.vladusecho.lexicon.domain.entity.Settings
import com.vladusecho.lexicon.presentation.ui.theme.LexiconTheme
import com.vladusecho.lexicon.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreenV2(
    viewModel: SettingsViewModel = hiltViewModel()
) {

    val currentState by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SettingsTopAppBar()
        }
    ) { paddingValues ->
        SettingsScreenV2Content(
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
            currentState = currentState,
            onThemeChange = {
                viewModel.processCommand(
                    SettingsViewModel.SettingsCommand.ToggleDarkMode(it)
                )
            }
        )
    }
}

@Composable
fun SettingsScreenV2Content(
    modifier: Modifier = Modifier,
    currentState: SettingsViewModel.SettingsState = SettingsViewModel.SettingsState.Loading,
    onThemeChange: (Boolean) -> Unit
) {

    when (currentState) {
        SettingsViewModel.SettingsState.Error -> {

        }

        SettingsViewModel.SettingsState.Loading -> {

        }

        is SettingsViewModel.SettingsState.Success -> {
            val verticalScrollState = rememberScrollState()

            Column(
                modifier = modifier
                    .verticalScroll(verticalScrollState)
            ) {
                SettingsMainTitle(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 32.dp
                    )
                )
                SwitchThemeItem(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                    ),
                    isDarkTheme = currentState.settings.isDarkMode,
                    onThemeChange = onThemeChange
                )
            }
        }
    }
}

@Composable
fun SettingsMainTitle(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "Настройки",
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Настройте внешний вид приложения и управляйте данными вашего словаря",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.tertiary,
        )
    }
}

@Composable
fun SwitchThemeItem(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xffC5C5D4), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_brush),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Тема оформления",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Выберите режим, который лучше всего подходит для ваших глаз.",
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = Modifier.height(16.dp))
        ThemeSwitcher(
            isDarkTheme = isDarkTheme,
            onThemeChange = onThemeChange
        )
    }
}

@Composable
fun ThemeSwitcher(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.onBackground)
            .height(48.dp),
    ) {

        val maxWidth = maxWidth
        val tabWidth = maxWidth / 2

        val indicatorOffset by animateDpAsState(
            targetValue = if (isDarkTheme) tabWidth else 0.dp,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            label = "indicator"
        )

        Box(
            modifier = Modifier
                .padding(4.dp)
                .width(tabWidth - 8.dp)
                .fillMaxHeight()
                .offset(x = indicatorOffset)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onThemeChange(false) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Светлая",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onThemeChange(true) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Темная",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopAppBar() {
    TopAppBar(
        title = {
            Text(
                text = "Lexicon",
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


@Composable
@Preview(
    showBackground = true
)
fun SettingsScreenSuccessPreview() {
    LexiconTheme() {
        SettingsScreenV2Content(
            currentState = SettingsViewModel.SettingsState.Success(
                settings = Settings(
                    isDarkMode = false
                ),
            ),
            onThemeChange = {}
        )
    }
}