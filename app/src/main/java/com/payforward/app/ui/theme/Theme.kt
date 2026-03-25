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
import com.payforward.app.service.ThemeMode

object PayForwardColors {
    // Pro Vibrant Brand Colors
    val BrandPrimary = Color(0xFF0052FF)     // Deep trust blue
    val BrandSecondary = Color(0xFF00D4FF)   // Cyan pop
    val BrandTertiary = Color(0xFF8A2BE2)    // Premium purple
    
    // Anti-Gravity Core Effects
    val EnergyGreen = Color(0xFF00FFAA)      // Luminous, pure gravity core green
    val EnergyPulse = Color(0xFF00FF88)      // Core pulse
    
    // Premium Light Surface
    val LightBackground = Color(0xFFF7F8F9)  // Textured off-white
    val LightSurface = Color(0xFFFFFFFF)     // Clean white
    val LightCard = Color(0xFFF3F5F7)        // Slightly darker for contrast
    val LightGlass = Color(0xB3FFFFFF)       // 70% glassmorphism
    val LightBorder = Color(0xFFE8ECEF)      // Subtle structure
    
    // Premium Dark Surface
    val DeepBlack = Color(0xFF08080C)        // Total void
    val DarkSurface = Color(0xFF0F0F16)      // Base structure
    val CardDark = Color(0xFF16161F)         // Elevated core
    val CardDarkElevated = Color(0xFF1D1D28) 
    val DarkGlass = Color(0x6616161F)        // 40% glassmorphism
    val DarkBorder = Color(0xFF232332)       // Defined edges

    // Status
    val StatusSuccess = Color(0xFF00E676)
    val StatusError = Color(0xFFFF453A)
    val StatusWarning = Color(0xFFFF9F0A)
    val StatusInfo = Color(0xFF0A84FF)
    
    // Data Ticker
    val DataTickerBgLight = Color(0xFFE4E8EB)
    val DataTickerBgDark = Color(0xFF1B1B26)

    // Text - Light Mode
    val TextPrimaryLight = Color(0xFF11141A) // Near black
    val TextSecondaryLight = Color(0xFF5E6573) // Professional gray
    val TextTertiaryLight = Color(0xFF8A93A5)
    
    // Text - Dark Mode
    val TextPrimaryDark = Color(0xFFF1F3F5)  // Sharp white
    val TextSecondaryDark = Color(0xFF9098A9)  // Distinct gray
    val TextTertiaryDark = Color(0xFF6A7285)
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
    displayLarge = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 42.sp, letterSpacing = (-1).sp),
    headlineLarge = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
    titleLarge = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    titleMedium = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Medium, fontSize = 15.sp),
    bodyLarge = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, letterSpacing = 0.5.sp),
    labelMedium = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Medium, fontSize = 10.sp, letterSpacing = 0.5.sp),
)

// ── Dark Color Scheme ──────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary = PayForwardColors.BrandSecondary,
    onPrimary = PayForwardColors.DeepBlack,
    primaryContainer = PayForwardColors.BrandSecondary.copy(alpha = 0.15f),
    onPrimaryContainer = PayForwardColors.BrandSecondary,
    secondary = PayForwardColors.EnergyGreen,
    onSecondary = PayForwardColors.DeepBlack,
    secondaryContainer = PayForwardColors.EnergyGreen.copy(alpha = 0.15f),
    onSecondaryContainer = PayForwardColors.EnergyGreen,
    tertiary = PayForwardColors.BrandTertiary,
    onTertiary = Color.White,
    background = PayForwardColors.DeepBlack,
    onBackground = PayForwardColors.TextPrimaryDark,
    surface = PayForwardColors.DarkSurface,
    onSurface = PayForwardColors.TextPrimaryDark,
    surfaceVariant = PayForwardColors.CardDark,
    onSurfaceVariant = PayForwardColors.TextSecondaryDark,
    error = PayForwardColors.StatusError,
    onError = Color.White,
    outline = PayForwardColors.DarkBorder,
    outlineVariant = PayForwardColors.DarkGlass,
)

// ── Light Color Scheme ─────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary = PayForwardColors.BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = PayForwardColors.BrandPrimary.copy(alpha = 0.15f),
    onPrimaryContainer = PayForwardColors.BrandPrimary,
    secondary = PayForwardColors.EnergyGreen,
    onSecondary = PayForwardColors.DeepBlack,
    secondaryContainer = PayForwardColors.EnergyGreen.copy(alpha = 0.2f),
    onSecondaryContainer = PayForwardColors.DeepBlack,
    tertiary = PayForwardColors.BrandTertiary,
    onTertiary = Color.White,
    background = PayForwardColors.LightBackground,
    onBackground = PayForwardColors.TextPrimaryLight,
    surface = PayForwardColors.LightSurface,
    onSurface = PayForwardColors.TextPrimaryLight,
    surfaceVariant = PayForwardColors.LightCard,
    onSurfaceVariant = PayForwardColors.TextSecondaryLight,
    error = PayForwardColors.StatusError,
    onError = Color.White,
    outline = PayForwardColors.LightBorder,
    outlineVariant = PayForwardColors.LightGlass,
)

// ── Theme Composable ───────────────────────────────────────────────────
@Composable
fun PayForwardTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val useDarkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val bgColor = if (useDarkTheme) PayForwardColors.DeepBlack else PayForwardColors.LightBackground
            window.statusBarColor = bgColor.toArgb()
            window.navigationBarColor = bgColor.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !useDarkTheme
                isAppearanceLightNavigationBars = !useDarkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PayForwardTypography,
        content = content
    )
}
