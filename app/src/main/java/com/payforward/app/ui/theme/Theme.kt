package com.payforward.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// ── Color Palette ──────────────────────────────────────────────────────
object PayForwardColors {
    // Neon accents
    val NeonBlue = Color(0xFF00D4FF)
    val NeonGreen = Color(0xFF00FF88)
    val NeonPurple = Color(0xFFBB86FC)
    val NeonPink = Color(0xFFFF0080)
    val NeonOrange = Color(0xFFFF6B35)

    // Dark surfaces
    val DeepBlack = Color(0xFF0A0A0F)
    val DarkSurface = Color(0xFF12121A)
    val CardDark = Color(0xFF1A1A2E)
    val CardDarkElevated = Color(0xFF1F1F35)
    val DarkBorder = Color(0xFF2A2A3E)

    // Status colors
    val SuccessGreen = Color(0xFF00E676)
    val ErrorRed = Color(0xFFFF5252)
    val WarningAmber = Color(0xFFFFAB00)
    val PendingBlue = Color(0xFF448AFF)

    // Text
    val TextPrimary = Color(0xFFE8E8F0)
    val TextSecondary = Color(0xFF8888AA)
    val TextTertiary = Color(0xFF555577)
}

// ── Typography ─────────────────────────────────────────────────────────
val googleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = com.payforward.app.R.array.com_google_android_gms_fonts_certs
)

val InterFont = GoogleFont("Inter")

val InterFontFamily = FontFamily(
    Font(googleFont = InterFont, fontProvider = googleFontProvider, weight = FontWeight.Light),
    Font(googleFont = InterFont, fontProvider = googleFontProvider, weight = FontWeight.Normal),
    Font(googleFont = InterFont, fontProvider = googleFontProvider, weight = FontWeight.Medium),
    Font(googleFont = InterFont, fontProvider = googleFontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = InterFont, fontProvider = googleFontProvider, weight = FontWeight.Bold),
    Font(googleFont = InterFont, fontProvider = googleFontProvider, weight = FontWeight.ExtraBold),
)

val PayForwardTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 42.sp,
        letterSpacing = (-1).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp
    ),
    titleLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    titleMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        letterSpacing = 0.5.sp
    ),
)

// ── Dark Color Scheme ──────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary = PayForwardColors.NeonBlue,
    onPrimary = PayForwardColors.DeepBlack,
    primaryContainer = PayForwardColors.NeonBlue.copy(alpha = 0.15f),
    onPrimaryContainer = PayForwardColors.NeonBlue,

    secondary = PayForwardColors.NeonGreen,
    onSecondary = PayForwardColors.DeepBlack,
    secondaryContainer = PayForwardColors.NeonGreen.copy(alpha = 0.15f),
    onSecondaryContainer = PayForwardColors.NeonGreen,

    tertiary = PayForwardColors.NeonPurple,
    onTertiary = PayForwardColors.DeepBlack,

    background = PayForwardColors.DeepBlack,
    onBackground = PayForwardColors.TextPrimary,

    surface = PayForwardColors.DarkSurface,
    onSurface = PayForwardColors.TextPrimary,
    surfaceVariant = PayForwardColors.CardDark,
    onSurfaceVariant = PayForwardColors.TextSecondary,

    error = PayForwardColors.ErrorRed,
    onError = Color.White,

    outline = PayForwardColors.DarkBorder,
    outlineVariant = PayForwardColors.DarkBorder.copy(alpha = 0.5f),
)

// ── Theme Composable ───────────────────────────────────────────────────
@Composable
fun PayForwardTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = PayForwardColors.DeepBlack.toArgb()
            window.navigationBarColor = PayForwardColors.DeepBlack.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PayForwardTypography,
        content = content
    )
}
