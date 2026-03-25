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

    val haptic = LocalHapticFeedback.current

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
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Transmitting Protocol...", style = MaterialTheme.typography.labelLarge)
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

        // ── Anti-Spoofing Checker ────────────────────────────
        Text(
            text = "ANTI-SPOOFING SCANNER",
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
                Text(
                    text = "Verify Sender Identity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enter a 6-character TRAI sender ID to check if it matches official bank formats (e.g. AD-HDFCBK).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = spoofSenderId,
                    onValueChange = { spoofSenderId = it.uppercase() },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Sender ID") },
                    placeholder = { Text("e.g. AX-ICICIB") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(Icons.Outlined.QrCodeScanner, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        val id = spoofSenderId.trim()
                        if (id.isEmpty()) {
                            spoofResult = "Please enter a Sender ID"
                            spoofIsSafe = false
                            return@Button
                        }
                        
                        // Basic spoofing check logic
                        if (id.length in 6..9) {
                            val pattern = Regex("^[A-Z]{2}-[A-Z0-9]{3,}$")
                            if (pattern.matches(id) || id.contains("HDFC") || id.contains("ICICI") || id.contains("SBI") || id.contains("PAYTM")) {
                                spoofResult = "Identity Verified: Standard Bank Format."
                                spoofIsSafe = true
                            } else {
                                spoofResult = "Caution: Does not match standard trusted header patterns."
                                spoofIsSafe = false
                            }
                        } else if (id.matches(Regex("^[0-9]+$"))) {
                            spoofResult = "Warning: Numeric sender IDs are often promotional or unsafe."
                            spoofIsSafe = false
                        } else {
                            spoofResult = "Unrecognized Format: Treat with caution."
                            spoofIsSafe = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Run Diagnostics")
                }

                if (spoofResult != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (spoofIsSafe) PayForwardColors.StatusSuccess.copy(alpha = 0.1f)
                            else PayForwardColors.StatusWarning.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (spoofIsSafe) Icons.Filled.GppGood else Icons.Filled.GppBad,
                                contentDescription = null,
                                tint = if (spoofIsSafe) PayForwardColors.StatusSuccess else PayForwardColors.StatusWarning
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = spoofResult!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
