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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Vector Icon Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Telegram Icon
                    val telegramIcon = remember {
                        ImageVector.Builder(
                            name = "Telegram",
                            defaultWidth = 24.dp,
                            defaultHeight = 24.dp,
                            viewportWidth = 24f,
                            viewportHeight = 24f
                        ).apply {
                            path(fill = SolidColor(Color.Black)) {
                                moveTo(20.67f, 3.47f)
                                lineTo(2.33f, 10.53f)
                                curveTo(1.4f, 10.9f, 1.4f, 11.45f, 2.15f, 11.69f)
                                lineTo(6.87f, 13.16f)
                                lineTo(17.8f, 6.27f)
                                curveTo(18.32f, 5.95f, 18.79f, 6.13f, 18.39f, 6.48f)
                                lineTo(9.54f, 14.48f)
                                lineTo(9.26f, 18.66f)
                                curveTo(9.67f, 18.66f, 9.85f, 18.47f, 10.08f, 18.25f)
                                lineTo(12.06f, 16.32f)
                                lineTo(16.19f, 19.37f)
                                curveTo(16.95f, 19.79f, 17.5f, 19.57f, 17.69f, 18.67f)
                                lineTo(21.43f, 4.29f)
                                curveTo(21.7f, 3.25f, 20.89f, 2.87f, 20.67f, 3.47f)
                                close()
                            }
                        }.build()
                    }

                    IconButton(
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Silicon_official")))
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = telegramIcon,
                            contentDescription = "Telegram",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // GitHub Icon
                    val githubIcon = remember {
                        ImageVector.Builder(
                            name = "GitHub",
                            defaultWidth = 24.dp,
                            defaultHeight = 24.dp,
                            viewportWidth = 24f,
                            viewportHeight = 24f
                        ).apply {
                            path(fill = SolidColor(Color.Black)) {
                                moveTo(12.0f, 2.0f)
                                curveTo(6.477f, 2.0f, 2.0f, 6.477f, 2.0f, 12.0f)
                                curveTo(2.0f, 16.418f, 4.865f, 20.166f, 8.839f, 21.488f)
                                curveTo(9.339f, 21.58f, 9.52f, 21.27f, 9.52f, 21.012f)
                                curveTo(9.52f, 20.783f, 9.51f, 19.982f, 9.505f, 19.11f)
                                curveTo(6.723f, 19.715f, 6.136f, 17.962f, 6.136f, 17.962f)
                                curveTo(5.681f, 16.807f, 5.025f, 16.5f, 5.025f, 16.5f)
                                curveTo(4.118f, 15.881f, 5.093f, 15.894f, 5.093f, 15.894f)
                                curveTo(6.095f, 15.964f, 6.622f, 16.924f, 6.622f, 16.924f)
                                curveTo(7.513f, 18.452f, 8.956f, 18.01f, 9.54f, 17.755f)
                                curveTo(9.63f, 17.091f, 9.9f, 16.65f, 10.198f, 16.398f)
                                curveTo(7.98f, 16.146f, 5.648f, 15.289f, 5.648f, 11.47f)
                                curveTo(5.648f, 10.383f, 6.035f, 9.493f, 6.671f, 8.79f)
                                curveTo(6.568f, 8.538f, 6.23f, 7.525f, 6.768f, 6.137f)
                                curveTo(6.768f, 6.137f, 7.602f, 5.87f, 9.51f, 7.16f)
                                curveTo(10.3f, 6.94f, 11.15f, 6.83f, 12.0f, 6.83f)
                                curveTo(12.85f, 6.83f, 13.7f, 6.94f, 14.49f, 7.16f)
                                curveTo(16.398f, 5.87f, 17.232f, 6.137f, 17.232f, 6.137f)
                                curveTo(17.77f, 7.525f, 17.432f, 8.538f, 17.33f, 8.79f)
                                curveTo(17.966f, 9.493f, 18.353f, 10.383f, 18.353f, 11.47f)
                                curveTo(18.353f, 15.297f, 16.02f, 16.146f, 13.79f, 16.398f)
                                curveTo(14.16f, 16.712f, 14.5f, 17.336f, 14.5f, 18.28f)
                                curveTo(14.5f, 19.632f, 14.488f, 20.725f, 14.488f, 21.012f)
                                curveTo(14.488f, 21.27f, 14.668f, 21.58f, 15.168f, 21.488f)
                                curveTo(19.135f, 20.166f, 22.0f, 16.418f, 22.0f, 12.0f)
                                curveTo(22.0f, 6.477f, 17.523f, 2.0f, 12.0f, 2.0f)
                                close()
                            }
                        }.build()
                    }

                    IconButton(
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/silicon-developer")))
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = githubIcon,
                            contentDescription = "GitHub",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
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
