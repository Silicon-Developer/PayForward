package com.payforward.app.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.payforward.app.ui.screens.*

enum class Screen(val title: String) {
    HOME("Dashboard"),
    KEYWORDS("Keyword Engine"),
    LOGS("Live Activity"),
    SETTINGS("System Config"),
    SECURITY_HUB("Security Hub"),
    ABOUT("About")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun PayForwardApp(
    onExportCsv: () -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var menuExpanded by remember { mutableStateOf(false) }

    // Close menu on back press if open
    BackHandler(enabled = menuExpanded) {
        menuExpanded = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main Content underneath
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = currentScreen.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    actions = {
                        if (currentScreen == Screen.LOGS) {
                            IconButton(onClick = onExportCsv) {
                                Icon(
                                    Icons.Outlined.FileDownload,
                                    contentDescription = "Export CSV",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // The Great Menu Trigger
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                Icons.Filled.Sort,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
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
                    fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 8 } with
                    fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { -it / 8 }
                },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    Screen.HOME -> HomeScreen()
                    Screen.KEYWORDS -> KeywordsScreen()
                    Screen.LOGS -> LogsScreen()
                    Screen.SETTINGS -> SettingsScreen()
                    Screen.SECURITY_HUB -> SecurityHubScreen()
                    Screen.ABOUT -> AboutScreen()
                }
            }
        }

        // Custom Right-Side Overlay Drawer (The Great Menu)
        AnimatedVisibility(
            visible = menuExpanded,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300))
        ) {
            // Darken background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { menuExpanded = false }
            )
        }

        AnimatedVisibility(
            visible = menuExpanded,
            enter = slideInHorizontally(tween(400), initialOffsetX = { it }),
            exit = slideOutHorizontally(tween(400), targetOffsetX = { it }),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            GreatMenuDrawer(
                currentScreen = currentScreen,
                onNavigate = {
                    currentScreen = it
                    menuExpanded = false
                },
                onClose = { menuExpanded = false }
            )
        }
    }
}

@Composable
fun GreatMenuDrawer(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.85f),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp),
        shadowElevation = 16.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Premium Gradient Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    )
                    .padding(vertical = 32.dp, horizontal = 24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PAYFORWARD",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            letterSpacing = 2.sp
                        )
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                    Text(
                        text = "PRO v1.2",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Menu Items
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp, horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MenuDrawerItem(
                    title = Screen.HOME.title,
                    icon = Icons.Outlined.Dashboard,
                    isSelected = currentScreen == Screen.HOME,
                    onClick = { onNavigate(Screen.HOME) }
                )
                MenuDrawerItem(
                    title = Screen.KEYWORDS.title,
                    icon = Icons.Outlined.VpnKey,
                    isSelected = currentScreen == Screen.KEYWORDS,
                    onClick = { onNavigate(Screen.KEYWORDS) }
                )
                MenuDrawerItem(
                    title = Screen.LOGS.title,
                    icon = Icons.Outlined.Timeline,
                    isSelected = currentScreen == Screen.LOGS,
                    onClick = { onNavigate(Screen.LOGS) }
                )
                MenuDrawerItem(
                    title = Screen.SECURITY_HUB.title,
                    icon = Icons.Outlined.Security,
                    isSelected = currentScreen == Screen.SECURITY_HUB,
                    onClick = { onNavigate(Screen.SECURITY_HUB) }
                )
                MenuDrawerItem(
                    title = Screen.SETTINGS.title,
                    icon = Icons.Outlined.SettingsApplications,
                    isSelected = currentScreen == Screen.SETTINGS,
                    onClick = { onNavigate(Screen.SETTINGS) }
                )
                MenuDrawerItem(
                    title = Screen.ABOUT.title,
                    icon = Icons.Outlined.Info,
                    isSelected = currentScreen == Screen.ABOUT,
                    onClick = { onNavigate(Screen.ABOUT) }
                )
            }

            // Developer Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "DEVELOPED BY SILICON DEVELOPER",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Text(
                    text = "[Telegram: t.me/Silicon_official]",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Silicon_official")))
                        }
                        .padding(vertical = 4.dp)
                )
                
                Text(
                    text = "[GitHub: github.com/silicon-developer]",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/silicon-developer")))
                        }
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun MenuDrawerItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
