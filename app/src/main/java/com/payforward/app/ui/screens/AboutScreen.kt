package com.payforward.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // App icon replacement -> Glowing text
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "< \\ >",
                style = MaterialTheme.typography.displayLarge,
                color = com.payforward.app.ui.theme.PayForwardColors.EnergyGreen,
                fontWeight = FontWeight.Black,
                letterSpacing = 8.sp,
                modifier = Modifier
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(com.payforward.app.ui.theme.PayForwardColors.EnergyGreen.copy(alpha = 0.2f), androidx.compose.ui.graphics.Color.Transparent)
                        ),
                        shape = CircleShape
                    )
                    .padding(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "PayForward Pro",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "v1.3.0",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Developer's Mission card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.FormatQuote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "I developed this app for payment verification directly through the user's phone, empowering businesses to verify transactions automatically without paying hefty fees to third-party services or gateways.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Credits card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Developed by",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Silicon Developer",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val telegramIcon = rememberTelegramIcon()
        val githubIcon = rememberGitHubIcon()

        // Links
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                LinkItem(
                    icon = telegramIcon,
                    label = "Telegram",
                    value = "t.me/Silicon_official",
                    onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Silicon_official"))
                        )
                    }
                )

                Divider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                LinkItem(
                    icon = githubIcon,
                    label = "GitHub",
                    value = "github.com/silicon-developer",
                    onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/silicon-developer"))
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Made with ❤ for the community",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

    }
}

@Composable
fun rememberTelegramIcon(): ImageVector {
    return androidx.compose.runtime.remember {
        ImageVector.Builder(
            name = "Telegram",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color.Black)) {
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
}

@Composable
fun rememberGitHubIcon(): ImageVector {
    return androidx.compose.runtime.remember {
        ImageVector.Builder(
            name = "GitHub",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color.Black)) {
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
}

