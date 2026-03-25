package com.payforward.app.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.payforward.app.ui.screens.*

enum class Screen(val title: String) {
    HOME("PayForward"),
    KEYWORDS("Keywords"),
    LOGS("Activity Log"),
    SETTINGS("Settings"),
    ABOUT("About")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun PayForwardApp(
    onExportCsv: () -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentScreen.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    // Export CSV button only on Logs screen
                    if (currentScreen == Screen.LOGS) {
                        IconButton(onClick = onExportCsv) {
                            Icon(
                                Icons.Outlined.FileDownload,
                                contentDescription = "Export CSV",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // 3-dot kebab menu
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Home") },
                            onClick = {
                                currentScreen = Screen.HOME
                                menuExpanded = false
                            },
                            leadingIcon = {
                                Icon(Icons.Outlined.Home, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Keywords") },
                            onClick = {
                                currentScreen = Screen.KEYWORDS
                                menuExpanded = false
                            },
                            leadingIcon = {
                                Icon(Icons.Outlined.Key, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Logs") },
                            onClick = {
                                currentScreen = Screen.LOGS
                                menuExpanded = false
                            },
                            leadingIcon = {
                                Icon(Icons.Outlined.History, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                currentScreen = Screen.SETTINGS
                                menuExpanded = false
                            },
                            leadingIcon = {
                                Icon(Icons.Outlined.Settings, contentDescription = null)
                            }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text("About") },
                            onClick = {
                                currentScreen = Screen.ABOUT
                                menuExpanded = false
                            },
                            leadingIcon = {
                                Icon(Icons.Outlined.Info, contentDescription = null)
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        AnimatedContent(
            targetState = currentScreen,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            transitionSpec = {
                fadeIn() + slideInHorizontally { it / 4 } with
                fadeOut() + slideOutHorizontally { -it / 4 }
            },
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                Screen.HOME -> HomeScreen()
                Screen.KEYWORDS -> KeywordsScreen()
                Screen.LOGS -> LogsScreen()
                Screen.SETTINGS -> SettingsScreen()
                Screen.ABOUT -> AboutScreen()
            }
        }
    }
}
