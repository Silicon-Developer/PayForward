package com.payforward.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.payforward.app.data.ForwardStatus
import com.payforward.app.data.MessageLog
import com.payforward.app.service.ListenerForegroundService
import com.payforward.app.ui.theme.PayForwardColors
import com.payforward.app.ui.viewmodels.HomeViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val isActive by viewModel.isActive.collectAsState()
    val scannedToday by viewModel.scannedToday.collectAsState()
    val forwardedToday by viewModel.forwardedToday.collectAsState()
    val recentLogs by viewModel.recentLogs.collectAsState()
    val activeKeywords by viewModel.activeKeywords.collectAsState()
    
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

    val smsPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        smsPermissionGranted = results[Manifest.permission.RECEIVE_SMS] == true
    }

    val deniedPermissions = mutableListOf<String>()
    if (isActive && !smsPermissionGranted) deniedPermissions.add("SMS Access")
    if (isActive && !notificationListenerEnabled) deniedPermissions.add("Notification Access")

    // The entire screen is scrollable
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Warning banners
        deniedPermissions.forEach { perm ->
            WarningBanner("App can't run properly: $perm denied")
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Advanced Glowing Stats Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Polyline,
                label = "Scanned Today",
                value = NumberFormat.getNumberInstance(Locale.US).format(scannedToday),
                color = PayForwardColors.BrandSecondary
            )
            ProStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.RocketLaunch,
                label = "Forwarded",
                value = NumberFormat.getNumberInstance(Locale.US).format(forwardedToday),
                color = PayForwardColors.EnergyGreen
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Central Anti-Gravity Core Button
        AntiGravityCoreButton(
            isActive = isActive,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                if (!isActive) {
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
                    viewModel.toggleService()
                    ListenerForegroundService.start(context)
                } else {
                    viewModel.toggleService()
                    ListenerForegroundService.stop(context)
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Dynamic Connecting Data Connector if active
        if (isActive) {
            DataStreamConnector()
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            Spacer(modifier = Modifier.height(28.dp))
        }

        // Live Activity Log Section
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isActive) PayForwardColors.EnergyGreen else PayForwardColors.TextTertiaryLight)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "RECENT ACTIVITY LOG (Live Feed)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (recentLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "System online. Awaiting data streams...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                recentLogs.forEach { log ->
                    LiveLogEntry(log)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Keyword Listen Bar (Ticker)
        val keywordsText = if (activeKeywords.isNotEmpty()) {
            activeKeywords.joinToString(" • ") { it.word }
        } else {
            "No active keywords"
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Radar,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Listening For: [$keywordsText]",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.basicMarquee()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Simulate Test Trigger
        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.simulatePayment(context)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            )
        ) {
            Icon(Icons.Outlined.Biotech, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
            Text("Test Event Trigger", style = MaterialTheme.typography.labelLarge)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Dialogs...
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Notification Access Required") },
            text = { Text("PayForward Pro needs notification access to monitor payment app alerts. You'll be taken to system settings to enable it.") },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    val intent = android.content.Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    context.startActivity(intent)
                }) { Text("Open Settings", color = MaterialTheme.colorScheme.primary) }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) { Text("Later", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        )
    }
}

// ── Custom Pro UI Components ───────────────────────────────────────────

@Composable
fun WarningBanner(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PayForwardColors.StatusWarning.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Warning, contentDescription = null, tint = PayForwardColors.StatusWarning, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(message, style = MaterialTheme.typography.bodySmall, color = PayForwardColors.StatusWarning, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ProStatCard(modifier: Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun AntiGravityCoreButton(isActive: Boolean, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "core")
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    val coreColor = if (isActive) PayForwardColors.EnergyGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    
    Box(
        modifier = Modifier
            .size(160.dp)
            .scale(if (isActive) pulseScale else 1f)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Outer glowing ring
        if (isActive) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(coreColor.copy(alpha = 0.4f), Color.Transparent)
                    ),
                    radius = size.width / 2
                )
            }
            
            // Rotating particle dashed ring
            Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                drawCircle(
                    color = coreColor.copy(alpha = 0.6f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 4.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 20f), rotation)
                    )
                )
            }
        }

        // Inner solid core
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = if (isActive) listOf(coreColor, PayForwardColors.BrandSecondary)
                        else listOf(coreColor, coreColor.copy(alpha = 0.5f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isActive) Icons.Filled.Stream else Icons.Filled.PowerSettingsNew,
                contentDescription = "Core",
                tint = if (isActive) PayForwardColors.DeepBlack else MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(48.dp)
            )
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = if (isActive) "CORE ENGAGED" else "CORE STANDBY",
        style = MaterialTheme.typography.labelLarge,
        color = coreColor,
        letterSpacing = 4.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun DataStreamConnector() {
    val infiniteTransition = rememberInfiniteTransition(label = "stream")
    val streamOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(animation = tween(500, easing = LinearEasing)),
        label = "streamOffset"
    )

    Canvas(modifier = Modifier.width(2.dp).height(20.dp)) {
        drawLine(
            color = PayForwardColors.EnergyGreen.copy(alpha = 0.6f),
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, size.height),
            strokeWidth = 4.dp.toPx(),
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), streamOffset)
        )
    }
}

@Composable
fun LiveLogEntry(log: MessageLog) {
    val dateFormatter = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    
    val statusColor = when (log.status) {
        ForwardStatus.SUCCESS -> PayForwardColors.StatusSuccess
        ForwardStatus.FAILED -> PayForwardColors.StatusError
        ForwardStatus.PENDING -> PayForwardColors.StatusInfo
    }
    
    val statusText = when (log.status) {
        ForwardStatus.SUCCESS -> "Forwarded"
        ForwardStatus.FAILED -> "Skipped/Failed"
        ForwardStatus.PENDING -> "Processing"
    }
    
    val statusIcon = when (log.status) {
        ForwardStatus.SUCCESS -> "✓"
        ForwardStatus.FAILED -> "✕"
        ForwardStatus.PENDING -> "⏳"
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Source icon box
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "►",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                // Header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = log.sender,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = dateFormatter.format(Date(log.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Body truncate
                Text(
                    text = log.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Meta row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "[$statusIcon $statusText]",
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                    if (log.matchedKeyword.isNotBlank()) {
                        Text(
                            text = "  — Layer: ${log.matchedKeyword}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun isNotificationListenerEnabled(context: android.content.Context): Boolean {
    val pkgName = context.packageName
    val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
    return flat != null && flat.contains(pkgName)
}
