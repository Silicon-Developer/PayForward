package com.payforward.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.payforward.app.service.ForwardingMethod
import com.payforward.app.ui.theme.PayForwardColors
import com.payforward.app.ui.viewmodels.SettingsViewModel
import com.payforward.app.ui.viewmodels.TestResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val forwardingMethod by viewModel.forwardingMethod.collectAsState()
    val webhookUrl by viewModel.webhookUrl.collectAsState()
    val telegramToken by viewModel.telegramToken.collectAsState()
    val telegramChatId by viewModel.telegramChatId.collectAsState()
    val smsNumber by viewModel.smsNumber.collectAsState()
    val testResult by viewModel.testResult.collectAsState()
    val isTesting by viewModel.isTesting.collectAsState()

    val haptic = LocalHapticFeedback.current

    Scaffold(
        containerColor = PayForwardColors.DeepBlack,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.headlineMedium,
                            color = PayForwardColors.TextPrimary
                        )
                        Text(
                            text = "Configure forwarding",
                            style = MaterialTheme.typography.bodySmall,
                            color = PayForwardColors.TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PayForwardColors.DeepBlack
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ── Forwarding Method ──────────────────────────
            Text(
                text = "FORWARDING METHOD",
                style = MaterialTheme.typography.labelMedium,
                color = PayForwardColors.TextTertiary,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            ForwardingMethodCard(
                title = "Webhook",
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
                title = "Telegram Bot",
                description = "Forward via Telegram Bot API",
                icon = Icons.Outlined.Send,
                selected = forwardingMethod == ForwardingMethod.TELEGRAM,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.setForwardingMethod(ForwardingMethod.TELEGRAM)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ForwardingMethodCard(
                title = "SMS Forward",
                description = "Forward as SMS to another number",
                icon = Icons.Outlined.Sms,
                selected = forwardingMethod == ForwardingMethod.SMS,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.setForwardingMethod(ForwardingMethod.SMS)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Configuration Fields ───────────────────────
            Text(
                text = "CONFIGURATION",
                style = MaterialTheme.typography.labelMedium,
                color = PayForwardColors.TextTertiary,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PayForwardColors.CardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, PayForwardColors.DarkBorder)
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
                                label = "Webhook URL",
                                placeholder = "https://your-webhook.example.com/endpoint",
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
                                label = "Bot Token",
                                placeholder = "123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11",
                                icon = Icons.Outlined.Key,
                                isSecret = true
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            SecureTextField(
                                value = telegramChatId,
                                onValueChange = { viewModel.setTelegramChatId(it) },
                                label = "Chat ID",
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
                                label = "Phone Number",
                                placeholder = "+91 98765 43210",
                                icon = Icons.Outlined.Phone,
                                isSecret = false
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Test Button ────────────────────────────────
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
                    containerColor = PayForwardColors.NeonBlue.copy(alpha = 0.15f),
                    contentColor = PayForwardColors.NeonBlue
                ),
                enabled = !isTesting
            ) {
                if (isTesting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = PayForwardColors.NeonBlue,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Sending Test...")
                } else {
                    Icon(Icons.Outlined.Science, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Send Test Message",
                        style = MaterialTheme.typography.labelLarge
                    )
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
                            PayForwardColors.SuccessGreen.copy(alpha = 0.1f)
                        else PayForwardColors.ErrorRed.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSuccess) Icons.Filled.CheckCircle
                            else Icons.Filled.Error,
                            contentDescription = null,
                            tint = if (isSuccess) PayForwardColors.SuccessGreen
                            else PayForwardColors.ErrorRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isSuccess) "Test message sent successfully!"
                            else "Failed to send. Check your configuration.",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSuccess) PayForwardColors.SuccessGreen
                            else PayForwardColors.ErrorRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Security Notice ────────────────────────────
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PayForwardColors.NeonGreen.copy(alpha = 0.05f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    PayForwardColors.NeonGreen.copy(alpha = 0.15f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = PayForwardColors.NeonGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Privacy & Security",
                            style = MaterialTheme.typography.titleMedium,
                            color = PayForwardColors.NeonGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "All credentials are stored using AES-256 encryption on your device. " +
                                    "Messages are processed locally and only sent to your configured destination. " +
                                    "No data is shared with third parties.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PayForwardColors.TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ForwardingMethodCard(
    title: String,
    description: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val accentColor = if (selected) PayForwardColors.NeonBlue else PayForwardColors.TextSecondary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) PayForwardColors.NeonBlue.copy(alpha = 0.08f)
            else PayForwardColors.CardDark
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) PayForwardColors.NeonBlue.copy(alpha = 0.4f)
            else PayForwardColors.DarkBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
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

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (selected) PayForwardColors.TextPrimary
                    else PayForwardColors.TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = PayForwardColors.TextTertiary
                )
            }

            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = PayForwardColors.NeonBlue,
                    unselectedColor = PayForwardColors.TextTertiary
                )
            )
        }
    }
}

@Composable
fun SecureTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    isSecret: Boolean
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder, color = PayForwardColors.TextTertiary) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PayForwardColors.TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (isSecret) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff
                        else Icons.Outlined.Visibility,
                        contentDescription = "Toggle visibility",
                        tint = PayForwardColors.TextSecondary
                    )
                }
            }
        },
        visualTransformation = if (isSecret && !passwordVisible)
            PasswordVisualTransformation()
        else VisualTransformation.None,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PayForwardColors.NeonBlue,
            focusedLabelColor = PayForwardColors.NeonBlue,
            cursorColor = PayForwardColors.NeonBlue,
            unfocusedBorderColor = PayForwardColors.DarkBorder,
            unfocusedLabelColor = PayForwardColors.TextSecondary,
            focusedTextColor = PayForwardColors.TextPrimary,
            unfocusedTextColor = PayForwardColors.TextPrimary
        )
    )
}
