package com.payforward.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.payforward.app.data.Keyword
import com.payforward.app.ui.theme.PayForwardColors
import com.payforward.app.ui.viewmodels.KeywordsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeywordsScreen(viewModel: KeywordsViewModel = viewModel()) {
    val keywords by viewModel.keywords.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    val haptic = LocalHapticFeedback.current

    var newKeyword by remember { mutableStateOf("") }

    Scaffold(
        containerColor = PayForwardColors.DeepBlack,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Keywords",
                            style = MaterialTheme.typography.headlineMedium,
                            color = PayForwardColors.TextPrimary
                        )
                        Text(
                            text = "${keywords.size} trigger words active",
                            style = MaterialTheme.typography.bodySmall,
                            color = PayForwardColors.TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PayForwardColors.DeepBlack
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    newKeyword = ""
                    viewModel.showAddKeywordDialog()
                },
                containerColor = PayForwardColors.NeonBlue,
                contentColor = PayForwardColors.DeepBlack,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Keyword")
            }
        }
    ) { padding ->
        if (keywords.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.Key,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = PayForwardColors.TextTertiary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No keywords yet",
                        style = MaterialTheme.typography.titleLarge,
                        color = PayForwardColors.TextSecondary
                    )
                    Text(
                        text = "Tap + to add trigger keywords",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PayForwardColors.TextTertiary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 88.dp, top = 8.dp)
            ) {
                // Default keywords section
                val defaultKeywords = keywords.filter { it.isDefault }
                val customKeywords = keywords.filter { !it.isDefault }

                if (defaultKeywords.isNotEmpty()) {
                    item {
                        Text(
                            text = "DEFAULT KEYWORDS",
                            style = MaterialTheme.typography.labelMedium,
                            color = PayForwardColors.TextTertiary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(
                        items = defaultKeywords,
                        key = { it.id }
                    ) { keyword ->
                        KeywordItem(
                            keyword = keyword,
                            onToggle = { viewModel.toggleKeyword(keyword) },
                            onDelete = null, // Can't delete defaults
                            haptic = haptic
                        )
                    }
                }

                if (customKeywords.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "CUSTOM KEYWORDS",
                            style = MaterialTheme.typography.labelMedium,
                            color = PayForwardColors.TextTertiary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(
                        items = customKeywords,
                        key = { it.id }
                    ) { keyword ->
                        SwipeToDismissKeyword(
                            keyword = keyword,
                            onToggle = { viewModel.toggleKeyword(keyword) },
                            onDelete = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.deleteKeyword(keyword)
                            },
                            haptic = haptic
                        )
                    }
                }
            }
        }
    }

    // Add keyword dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissAddKeywordDialog() },
            containerColor = PayForwardColors.CardDark,
            titleContentColor = PayForwardColors.TextPrimary,
            textContentColor = PayForwardColors.TextSecondary,
            title = {
                Text(
                    text = "Add Keyword",
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter a trigger keyword to match in incoming messages.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PayForwardColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = newKeyword,
                        onValueChange = { newKeyword = it },
                        label = { Text("Keyword") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PayForwardColors.NeonBlue,
                            focusedLabelColor = PayForwardColors.NeonBlue,
                            cursorColor = PayForwardColors.NeonBlue,
                            unfocusedBorderColor = PayForwardColors.DarkBorder,
                            unfocusedLabelColor = PayForwardColors.TextSecondary,
                            focusedTextColor = PayForwardColors.TextPrimary,
                            unfocusedTextColor = PayForwardColors.TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.addKeyword(newKeyword) },
                    enabled = newKeyword.isNotBlank()
                ) {
                    Text("Add", color = PayForwardColors.NeonBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissAddKeywordDialog() }) {
                    Text("Cancel", color = PayForwardColors.TextSecondary)
                }
            }
        )
    }
}

@Composable
fun KeywordItem(
    keyword: Keyword,
    onToggle: () -> Unit,
    onDelete: (() -> Unit)?,
    haptic: androidx.compose.ui.hapticfeedback.HapticFeedback
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (keyword.isEnabled)
                PayForwardColors.CardDark
            else
                PayForwardColors.CardDark.copy(alpha = 0.5f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (keyword.isEnabled) PayForwardColors.DarkBorder
            else PayForwardColors.DarkBorder.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (keyword.isDefault)
                            PayForwardColors.NeonPurple.copy(alpha = 0.12f)
                        else
                            PayForwardColors.NeonBlue.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (keyword.isDefault) Icons.Filled.Lock else Icons.Filled.Tag,
                    contentDescription = null,
                    tint = if (keyword.isDefault) PayForwardColors.NeonPurple
                    else PayForwardColors.NeonBlue,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = keyword.word,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (keyword.isEnabled) PayForwardColors.TextPrimary
                    else PayForwardColors.TextTertiary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (keyword.isDefault) "Default" else "Custom",
                    style = MaterialTheme.typography.bodySmall,
                    color = PayForwardColors.TextTertiary
                )
            }

            Switch(
                checked = keyword.isEnabled,
                onCheckedChange = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onToggle()
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = PayForwardColors.DeepBlack,
                    checkedTrackColor = PayForwardColors.NeonGreen,
                    uncheckedThumbColor = PayForwardColors.TextSecondary,
                    uncheckedTrackColor = PayForwardColors.CardDark,
                    uncheckedBorderColor = PayForwardColors.DarkBorder
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissKeyword(
    keyword: Keyword,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    haptic: androidx.compose.ui.hapticfeedback.HapticFeedback
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PayForwardColors.ErrorRed.copy(alpha = 0.15f))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = PayForwardColors.ErrorRed,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        enableDismissFromStartToEnd = false
    ) {
        KeywordItem(
            keyword = keyword,
            onToggle = onToggle,
            onDelete = onDelete,
            haptic = haptic
        )
    }
}
