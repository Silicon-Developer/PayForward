package com.payforward.app.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.payforward.app.ui.screens.HomeScreen
import com.payforward.app.ui.screens.KeywordsScreen
import com.payforward.app.ui.screens.LogsScreen
import com.payforward.app.ui.screens.SettingsScreen
import com.payforward.app.ui.theme.PayForwardColors

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    data object Keywords : Screen("keywords", "Keywords", Icons.Filled.Key, Icons.Outlined.Key)
    data object Logs : Screen("logs", "Logs", Icons.Filled.List, Icons.Outlined.List)
    data object Settings : Screen("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

val screens = listOf(Screen.Home, Screen.Keywords, Screen.Logs, Screen.Settings)

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        containerColor = PayForwardColors.DeepBlack,
        bottomBar = {
            NavigationBar(
                containerColor = PayForwardColors.DarkSurface,
                contentColor = PayForwardColors.TextPrimary,
                tonalElevation = 0.dp
            ) {
                screens.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PayForwardColors.NeonBlue,
                            selectedTextColor = PayForwardColors.NeonBlue,
                            unselectedIconColor = PayForwardColors.TextSecondary,
                            unselectedTextColor = PayForwardColors.TextSecondary,
                            indicatorColor = PayForwardColors.NeonBlue.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(initialAlpha = 0.3f) + slideInHorizontally { it / 4 } },
            exitTransition = { fadeOut(targetAlpha = 0.3f) + slideOutHorizontally { -it / 4 } },
            popEnterTransition = { fadeIn(initialAlpha = 0.3f) + slideInHorizontally { -it / 4 } },
            popExitTransition = { fadeOut(targetAlpha = 0.3f) + slideOutHorizontally { it / 4 } }
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Keywords.route) { KeywordsScreen() }
            composable(Screen.Logs.route) { LogsScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}
