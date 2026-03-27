package com.payforward.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.payforward.app.service.ForwardingMethod
import com.payforward.app.ui.theme.PayForwardColors
import com.payforward.app.ui.viewmodels.SettingsViewModel
import com.payforward.app.ui.viewmodels.TestResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityHubScreen(viewModel: SettingsViewModel = viewModel()) {
    val forwardingMethod by viewModel.forwardingMethod.collectAsState()
    val webhookUrl by viewModel.webhookUrl.collectAsState()
    val telegramToken by viewModel.telegramToken.collectAsState()
    val telegramChatId by viewModel.telegramChatId.collectAsState()
    val smsNumber by viewModel.smsNumber.collectAsState()
    val testResult by viewModel.testResult.collectAsState()
    val isTesting by viewModel.isTesting.collectAsState()
    
    val isAntiSpoofingEnabled by viewModel.isAntiSpoofingEnabled.collectAsState()
    val trustedSenderIds by viewModel.trustedSenderIds.collectAsState()

    val haptic = LocalHapticFeedback.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val testErrorMessage by viewModel.testErrorMessage.collectAsState()

    // Toast on test result change
    LaunchedEffect(testResult) {
        when (testResult) {
            com.payforward.app.ui.viewmodels.TestResult.SUCCESS -> {
                android.widget.Toast.makeText(context, "Connection Successful ✓", android.widget.Toast.LENGTH_SHORT).show()
            }
            com.payforward.app.ui.viewmodels.TestResult.FAILURE -> {
                val msg = testErrorMessage ?: "Unknown error"
                android.widget.Toast.makeText(context, "Connection Failed: $msg", android.widget.Toast.LENGTH_LONG).show()
            }
            null -> { /* no-op */ }
        }
    }

    // Spoof Checker State
    var spoofSenderId by remember { mutableStateOf("") }
    var spoofResult by remember { mutableStateOf<String?>(null) }
    var spoofIsSafe by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // ── Security Notice Header ──────────────────────
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = PayForwardColors.BrandSecondary.copy(alpha = 0.15f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Security,
                    contentDescription = null,
                    tint = PayForwardColors.BrandSecondary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Security & Transmission Hub",
                        style = MaterialTheme.typography.titleMedium,
                        color = PayForwardColors.BrandSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "All API keys are AES-256 encrypted locally.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Forwarding Method ──────────────────────────
        Text(
            text = "PRIMARY FORWARDING METHOD",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        ForwardingMethodCard(
            title = "Webhook Endpoint",
            description = "Send JSON payload via HTTP POST",
            icon = Icons.Outlined.Webhook,
            selected = forwardingMethod == ForwardingMethod.WEBHOOK,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.setForwardingMethod(ForwardingMethod.WEBHOOK)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ForwardingMethodCard(
            title = "Telegram Bot API",
            description = "Forward instantly to Telegram Chat",
            icon = Icons.Outlined.Send,
            selected = forwardingMethod == ForwardingMethod.TELEGRAM,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.setForwardingMethod(ForwardingMethod.TELEGRAM)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ForwardingMethodCard(
            title = "SMS Forwarder",
            description = "Forward as SMS (Carrier charges apply)",
            icon = Icons.Outlined.Textsms,
            selected = forwardingMethod == ForwardingMethod.SMS,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.setForwardingMethod(ForwardingMethod.SMS)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Configuration Fields ───────────────────────
        Text(
            text = "SECURE PROTOCOLS",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                AnimatedVisibility(
                    visible = forwardingMethod == ForwardingMethod.WEBHOOK,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        SecureTextField(
                            value = webhookUrl,
                            onValueChange = { viewModel.setWebhookUrl(it) },
                            label = "Secure Webhook URL",
                            placeholder = "https://your-webhook.example.com/api",
                            icon = Icons.Outlined.Link,
                            isSecret = false
                        )
                    }
                }

                AnimatedVisibility(
                    visible = forwardingMethod == ForwardingMethod.TELEGRAM,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        SecureTextField(
                            value = telegramToken,
                            onValueChange = { viewModel.setTelegramToken(it) },
                            label = "Bot Access Token",
                            placeholder = "123456:ABC-DEF1234ghIkl-zyx57W2v1u",
                            icon = Icons.Outlined.Key,
                            isSecret = true
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SecureTextField(
                            value = telegramChatId,
                            onValueChange = { viewModel.setTelegramChatId(it) },
                            label = "Chat Target ID",
                            placeholder = "-1001234567890",
                            icon = Icons.Outlined.Chat,
                            isSecret = false
                        )
                    }
                }

                AnimatedVisibility(
                    visible = forwardingMethod == ForwardingMethod.SMS,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        SecureTextField(
                            value = smsNumber,
                            onValueChange = { viewModel.setSmsNumber(it) },
                            label = "Target Phone Number",
                            placeholder = "+91 98765 43210",
                            icon = Icons.Outlined.Phone,
                            isSecret = false
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Test Connection ────────────────────────────────
        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.sendTestMessage()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            enabled = !isTesting
        ) {
            if (isTesting) {
                Text("Verifying Protocol...", style = MaterialTheme.typography.labelLarge)
            } else {
                Icon(Icons.Outlined.OnlinePrediction, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Verify Connection", style = MaterialTheme.typography.labelLarge)
            }
        }

        // Test result
        AnimatedVisibility(
            visible = testResult != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            val isSuccess = testResult == TestResult.SUCCESS
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSuccess)
                        PayForwardColors.StatusSuccess.copy(alpha = 0.2f)
                    else PayForwardColors.StatusError.copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isSuccess) Icons.Filled.CheckCircle else Icons.Filled.Error,
                        contentDescription = null,
                        tint = if (isSuccess) PayForwardColors.StatusSuccess else PayForwardColors.StatusError,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isSuccess) "Connection Verified & Payload Delivered!"
                        else "Transmission Failed. Check endpoints & tokens.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── Anti-Spoofing Filter ────────────────────────────
        Text(
            text = "ANTI-SPOOFING FILTER",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enforce Sender Verification",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Block unauthorized senders and generic 10-digit numbers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                    Switch(
                        checked = isAntiSpoofingEnabled,
                        onCheckedChange = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.setAntiSpoofingEnabled(it) 
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                        )
                    )
                }

                AnimatedVisibility(visible = isAntiSpoofingEnabled) {
                    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "TRUSTED SENDER IDs",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (trustedSenderIds.isEmpty()) {
                            Text(
                                text = "No trusted senders added. All alphanumeric senders will be passed, but 10-digit numbers are blocked.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        } else {
                            // FlowRow for senders
                            @OptIn(ExperimentalLayoutApi::class)
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                trustedSenderIds.forEach { sender ->
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                MaterialTheme.colorScheme.secondaryContainer,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(start = 12.dp, end = 4.dp, top = 6.dp, bottom = 6.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = sender,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            IconButton(
                                                onClick = { viewModel.removeTrustedSender(sender) },
                                                modifier = Modifier.size(20.dp)
                                            ) {
                                                Icon(
                                                    Icons.Filled.Close,
                                                    contentDescription = "Remove",
                                                    modifier = Modifier.size(14.dp),
                                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = spoofSenderId,
                                onValueChange = { spoofSenderId = it.uppercase() },
                                modifier = Modifier.weight(1f),
                                label = { Text("Sender ID") },
                                placeholder = { Text("e.g. AX-ICICIB") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = {
                                    val id = spoofSenderId.trim()
                                    if (id.isNotEmpty()) {
                                        viewModel.addTrustedSender(id)
                                        spoofSenderId = ""
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(56.dp).padding(top = 8.dp)
                            ) {
                                Text("Add")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
