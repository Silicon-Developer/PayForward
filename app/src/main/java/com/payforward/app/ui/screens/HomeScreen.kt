package com.payforward.app.ui.screens

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.payforward.app.ui.theme.PayForwardColors
import com.payforward.app.ui.viewmodels.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val isActive by viewModel.isServiceActive.collectAsState()
    val scannedToday by viewModel.scannedToday.collectAsState()
    val forwardedToday by viewModel.forwardedToday.collectAsState()

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // Permission launchers
    val smsPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PayForwardColors.DeepBlack)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // ── App Title ──────────────────────────────────
        Text(
            text = "PayForward",
            style = MaterialTheme.typography.displayLarge,
            color = PayForwardColors.TextPrimary
        )

        Text(
            text = "Financial Message Intelligence",
            style = MaterialTheme.typography.bodyMedium,
            color = PayForwardColors.TextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // ── Master Toggle ──────────────────────────────
        MasterToggle(
            isActive = isActive,
            onToggle = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.toggleService()
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isActive) "Service Active" else "Service Inactive",
            style = MaterialTheme.typography.titleMedium,
            color = if (isActive) PayForwardColors.NeonGreen else PayForwardColors.TextSecondary,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = if (isActive) "Monitoring incoming messages" else "Tap to start listening",
            style = MaterialTheme.typography.bodySmall,
            color = PayForwardColors.TextTertiary,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        // ── Stats Cards ────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatsCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Scanner,
                label = "Scanned Today",
                value = scannedToday.toString(),
                accentColor = PayForwardColors.NeonBlue
            )
            StatsCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Send,
                label = "Forwarded",
                value = forwardedToday.toString(),
                accentColor = PayForwardColors.NeonGreen
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Permission Cards ───────────────────────────
        Text(
            text = "PERMISSIONS",
            style = MaterialTheme.typography.labelMedium,
            color = PayForwardColors.TextTertiary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        )

        PermissionCard(
            title = "SMS Access",
            description = "Required to read incoming SMS messages containing transaction details. Messages are processed entirely on your device.",
            icon = Icons.Outlined.Sms,
            onClick = {
                smsPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_SMS
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        PermissionCard(
            title = "Notification Access",
            description = "Required to capture transaction notifications from payment apps like GPay, PhonePe, and Paytm.",
            icon = Icons.Outlined.Notifications,
            onClick = {
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                context.startActivity(intent)
            }
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Spacer(modifier = Modifier.height(10.dp))
            PermissionCard(
                title = "Show Notifications",
                description = "Required to display status notifications about the forwarding service.",
                icon = Icons.Outlined.NotificationsActive,
                onClick = {
                    smsPermissionLauncher.launch(
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ─── Master Toggle Component ──────────────────────────────────────────────
@Composable
fun MasterToggle(isActive: Boolean, onToggle: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val bgColor by animateColorAsState(
        targetValue = if (isActive) PayForwardColors.NeonGreen.copy(alpha = 0.12f)
        else PayForwardColors.CardDark,
        animationSpec = tween(500),
        label = "bgColor"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isActive) PayForwardColors.NeonGreen
        else PayForwardColors.DarkBorder,
        animationSpec = tween(500),
        label = "borderColor"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isActive) PayForwardColors.NeonGreen
        else PayForwardColors.TextSecondary,
        animationSpec = tween(500),
        label = "iconColor"
    )

    Box(contentAlignment = Alignment.Center) {
        // Pulsing glow ring (only when active)
        if (isActive) {
            Box(
                modifier = Modifier
                    .size((140 * pulseScale).dp)
                    .clip(CircleShape)
                    .background(PayForwardColors.NeonGreen.copy(alpha = pulseAlpha))
            )
        }

        // Main toggle button
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .background(bgColor)
                .border(2.dp, borderColor, CircleShape)
                .clickable { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isActive) Icons.Filled.PowerSettingsNew
                else Icons.Outlined.PowerSettingsNew,
                contentDescription = "Toggle Service",
                modifier = Modifier.size(52.dp),
                tint = iconColor
            )
        }
    }
}

// ─── Stats Card Component ─────────────────────────────────────────────────
@Composable
fun StatsCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    accentColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PayForwardColors.CardDark),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            PayForwardColors.DarkBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
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
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                color = PayForwardColors.TextPrimary,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = PayForwardColors.TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─── Permission Card Component ────────────────────────────────────────────
@Composable
fun PermissionCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PayForwardColors.CardDark),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            PayForwardColors.DarkBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PayForwardColors.NeonBlue.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PayForwardColors.NeonBlue,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = PayForwardColors.TextPrimary
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = PayForwardColors.TextSecondary,
                    modifier = Modifier.padding(top = 3.dp),
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Grant",
                tint = PayForwardColors.TextTertiary
            )
        }
    }
}

private val EaseInOutCubic = CubicBezierEasing(0.645f, 0.045f, 0.355f, 1f)
