package com.vladusecho.lexicon.presentation.screenv2

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import kotlin.let

@Composable
fun SettingsScreenV2(
    viewModel: SettingsViewModel = hiltViewModel()
) {

    val currentState by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            viewModel.processCommand(
                SettingsViewModel.SettingsCommand.ExportData(it.toString())
            )
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            viewModel.processCommand(
                SettingsViewModel.SettingsCommand.ImportData(it.toString())
            )
        }
    }


    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                is SettingsViewModel.SettingsEvent.ExportSuccess -> {
                    Toast.makeText(context, "Данные успешно экспортированы", Toast.LENGTH_SHORT).show()
                }
                is SettingsViewModel.SettingsEvent.ExportError -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }

                is SettingsViewModel.SettingsEvent.ImportError -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
                SettingsViewModel.SettingsEvent.ImportSuccess -> {
                    Toast.makeText(context, "Данные успешно импортированы", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

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
            },
            exportLauncher = exportLauncher,
            importLauncher = importLauncher
        )
    }
}

@Composable
fun SettingsScreenV2Content(
    modifier: Modifier = Modifier,
    currentState: SettingsViewModel.SettingsState = SettingsViewModel.SettingsState.Loading,
    exportLauncher: ManagedActivityResultLauncher<String, Uri?>,
    importLauncher: ManagedActivityResultLauncher<Array<String>, Uri?>,
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
                ExportImportDefinitions(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 16.dp
                    ),
                    onExportClick = {
                        exportLauncher.launch("lexicon_backup_${System.currentTimeMillis()}.json")
                    },
                    onImportClick = {
                        importLauncher.launch(arrayOf("application/json"))
                    }
                )
                Spacer(
                    modifier = Modifier.height(32.dp)
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
            .border(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
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
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 14.sp
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

@Composable
fun ExportImportDefinitions(
    modifier: Modifier = Modifier,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_export),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Экспорт (JSON)",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Выгрузите все свои определения на телефон, чтобы сохранить их для дальнейших действий в формате JSON",
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onExportClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Экспортировать",
                color = MaterialTheme.colorScheme.background,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_import),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Импорт (JSON)",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Загрузите все свои определения из JSON файла в приложение, чтобы просматривать их в удобном формате",
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onImportClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Импортировать",
                color = MaterialTheme.colorScheme.background,
                fontWeight = FontWeight.Bold
            )
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
            onThemeChange = {},
            exportLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.CreateDocument("application/json")
            ) {},
            importLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) {}
        )
    }
}