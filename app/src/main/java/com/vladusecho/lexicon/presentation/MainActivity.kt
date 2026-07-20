package com.vladusecho.lexicon.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.vladusecho.lexicon.presentation.navigation.AppNavGraph
import com.vladusecho.lexicon.presentation.navigation.NavItem
import com.vladusecho.lexicon.presentation.navigation.NavigationState
import com.vladusecho.lexicon.presentation.navigation.rememberNavigationState
import com.vladusecho.lexicon.presentation.ui.theme.LexiconTheme
import com.vladusecho.lexicon.presentation.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize SplashScreen
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            val isDarkMode by settingsViewModel.isDarkMode.collectAsStateWithLifecycle()

            // Handle the splash screen transition.
            splashScreen.setKeepOnScreenCondition {
                settingsViewModel.isSettingsLoaded.value
            }

            LexiconTheme(
                darkTheme = isDarkMode
            ) {
                val navState = rememberNavigationState()
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(
                            navState = navState
                        )
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = paddingValues.calculateBottomPadding()),
                    ) {
                        AppNavGraph(
                            navState = navState
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navState: NavigationState
) {
    val backStackEntry by navState.navHostController.currentBackStackEntryAsState()

    NavigationBar(
        containerColor = Color(0xffedeeef),
        modifier = Modifier
            .shadow(elevation = 20.dp)
    ) {
        val items = listOf(
            NavItem.Home,
            NavItem.Education,
            NavItem.Settings,
        )
        items.forEach { item ->
            val isSelected =
                backStackEntry?.destination?.hierarchy?.any {
                    it.hasRoute(item.screen::class)
                } ?: false

            NavigationBarItem(
                selected = isSelected,
                onClick = { navState.navigateTo(item.screen) },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconId),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xff85f6e5),
                ),
                label = {
                    Text(
                        text = item.label,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            )
        }
    }
}

@Composable
@Preview
fun BottomNavigationBarPreview() {
    LexiconTheme {
        BottomNavigationBar(
            navState = rememberNavigationState()
        )
    }
}
