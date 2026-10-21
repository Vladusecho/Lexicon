package com.vladusecho.lexicon.presentation.screen

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vladusecho.lexicon.R
import com.vladusecho.lexicon.presentation.element.ErrorView
import com.vladusecho.lexicon.presentation.element.LoadingView
import com.vladusecho.lexicon.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val currentState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            viewModel.processCommand(SettingsViewModel.SettingsCommand.ExportData(it.toString()))
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            viewModel.processCommand(SettingsViewModel.SettingsCommand.ImportData(it.toString()))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is SettingsViewModel.SettingsEvent.ExportSuccess -> {
                    Toast.makeText(context, context.getString(R.string.export_success), Toast.LENGTH_SHORT).show()
                }

                is SettingsViewModel.SettingsEvent.ExportError -> {
                    Toast.makeText(context, context.getString(R.string.export_error, event.message), Toast.LENGTH_SHORT).show()
                }

                is SettingsViewModel.SettingsEvent.ImportError -> {
                    Toast.makeText(context, context.getString(R.string.import_error, event.message), Toast.LENGTH_SHORT).show()
                }

                is SettingsViewModel.SettingsEvent.ImportSuccess -> {
                    Toast.makeText(context, context.getString(R.string.import_success), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = { SettingsTopAppBar() }
    ) { paddingValues ->
        SettingsScreenContent(
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
            currentState = currentState,
            onThemeChange = { isDark ->
                viewModel.processCommand(SettingsViewModel.SettingsCommand.ToggleDarkMode(isDark))
            },
            exportLauncher = exportLauncher,
            importLauncher = importLauncher
        )
    }
}

@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    currentState: SettingsViewModel.SettingsState,
    exportLauncher: ManagedActivityResultLauncher<String, Uri?>,
    importLauncher: ManagedActivityResultLauncher<Array<String>, Uri?>,
    onThemeChange: (Boolean) -> Unit
) {
    when (currentState) {
        SettingsViewModel.SettingsState.Error -> {
            ErrorView(modifier = modifier)
        }

        SettingsViewModel.SettingsState.Loading -> {
            LoadingView()
        }

        is SettingsViewModel.SettingsState.Success -> {
            val verticalScrollState = rememberScrollState()

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScrollState)
            ) {
                SettingsHeader(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
                )
                SwitchThemeItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    isDarkTheme = currentState.settings.isDarkMode,
                    onThemeChange = onThemeChange
                )
                Spacer(modifier = Modifier.height(16.dp))
                ExportImportCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onExportClick = {
                        exportLauncher.launch("lexicon_backup_${System.currentTimeMillis()}.json")
                    },
                    onImportClick = {
                        importLauncher.launch(arrayOf("application/json"))
                    }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SettingsHeader(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.settings_title),
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.primary
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
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_brush),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = R.string.settings_theme_title),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.settings_dark_mode),
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
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
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onThemeChange(false) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Светлая",
                    fontWeight = FontWeight.Bold,
                    color = if (!isDarkTheme) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.tertiary,
                    fontSize = 14.sp
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onThemeChange(true) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Тёмная",
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.tertiary,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ExportImportCard(
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
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_export),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = R.string.settings_export),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.settings_export_description),
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onExportClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = stringResource(id = R.string.settings_export),
                color = MaterialTheme.colorScheme.background,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f))
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_import),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = R.string.settings_import),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.settings_import_description),
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onImportClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = stringResource(id = R.string.settings_import),
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
