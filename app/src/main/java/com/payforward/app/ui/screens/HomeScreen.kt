package com.payforward.app.ui.screens

import android.Manifest
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.payforward.app.service.ListenerForegroundService
import com.payforward.app.ui.theme.PayForwardColors
import com.payforward.app.ui.viewmodels.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val isActive by viewModel.isActive.collectAsState()
    val scannedToday by viewModel.scannedToday.collectAsState()
    val forwardedToday by viewModel.forwardedToday.collectAsState()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    // Permission states
    var smsPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS)
                    == PackageManager.PERMISSION_GRANTED
        )
    }
    var notificationListenerEnabled by remember {
        mutableStateOf(isNotificationListenerEnabled(context))
    }
    var showPermissionDialog by remember { mutableStateOf(false) }

    // Permission launcher
    val smsPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        smsPermissionGranted = results[Manifest.permission.RECEIVE_SMS] == true
    }

    // Pulsing animation when active
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseAnim.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Denied permission banners
    val deniedPermissions = mutableListOf<String>()
    if (isActive && !smsPermissionGranted) deniedPermissions.add("SMS Access")
    if (isActive && !notificationListenerEnabled) deniedPermissions.add("Notification Access")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Permission Denial Banners ────────────────
        deniedPermissions.forEach { perm ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PayForwardColors.WarningAmber.copy(alpha = 0.12f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = null,
                        tint = PayForwardColors.WarningAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "App can't run properly: $perm denied",
                        style = MaterialTheme.typography.bodySmall,
                        color = PayForwardColors.WarningAmber,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Master Toggle ────────────────────────────
        val toggleColor = if (isActive) PayForwardColors.NeonGreen else MaterialTheme.colorScheme.onSurfaceVariant
        val activeMod = if (isActive) Modifier.scale(pulseScale) else Modifier

        Box(
            modifier = Modifier
                .size(140.dp)
                .then(activeMod)
                .shadow(
                    elevation = if (isActive) 24.dp else 0.dp,
                    shape = CircleShape,
                    ambientColor = toggleColor.copy(alpha = 0.4f),
                    spotColor = toggleColor.copy(alpha = 0.4f)
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            toggleColor.copy(alpha = 0.15f),
                            toggleColor.copy(alpha = 0.05f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (!isActive) {
                        // Check permissions before activating
                        smsPermissionGranted = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.RECEIVE_SMS
                        ) == PackageManager.PERMISSION_GRANTED
                        notificationListenerEnabled = isNotificationListenerEnabled(context)

                        if (!smsPermissionGranted) {
                            smsPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.RECEIVE_SMS,
                                    Manifest.permission.READ_SMS,
                                    Manifest.permission.SEND_SMS
                                )
                            )
                        }
                        if (!notificationListenerEnabled) {
                            showPermissionDialog = true
                        }
                        // Activate and start foreground service
                        viewModel.toggleService()
                        ListenerForegroundService.start(context)
                    } else {
                        viewModel.toggleService()
                        ListenerForegroundService.stop(context)
                    }
                },
                modifier = Modifier.size(100.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PowerSettingsNew,
                    contentDescription = "Toggle Service",
                    modifier = Modifier.size(56.dp),
                    tint = toggleColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isActive) "SERVICE ACTIVE" else "SERVICE INACTIVE",
            style = MaterialTheme.typography.labelLarge,
            color = toggleColor,
            letterSpacing = 3.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = if (isActive) "Monitoring financial messages"
            else "Tap power button to start",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── Stats Cards ──────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Visibility,
                label = "Scanned Today",
                value = "$scannedToday",
                accentColor = MaterialTheme.colorScheme.primary
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Send,
                label = "Forwarded",
                value = "$forwardedToday",
                accentColor = PayForwardColors.NeonGreen
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Simulate Payment Button ──────────────────
        OutlinedButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.simulatePayment(context)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            )
        ) {
            Icon(
                Icons.Outlined.Science,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                "Simulate Payment",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Notification listener permission dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Notification Access Required") },
            text = {
                Text("PayForward needs notification access to monitor payment app alerts. You'll be taken to system settings to enable it.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    val intent = android.content.Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    context.startActivity(intent)
                }) {
                    Text("Open Settings", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Later", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    accentColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun isNotificationListenerEnabled(context: android.content.Context): Boolean {
    val pkgName = context.packageName
    val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
    return flat != null && flat.contains(pkgName)
}
