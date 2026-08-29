package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class CassThemeMode {
    SIGNATURE_DARK,
    AMOLED_BLACK,
    LUXURY_LIGHT,
    SYSTEM
}

private val CassSignatureDarkColorScheme = darkColorScheme(
    primary = CassGold,
    onPrimary = CassObsidian,
    primaryContainer = CassGoldDark,
    onPrimaryContainer = CassGoldLight,
    secondary = CassSilver,
    onSecondary = CassObsidian,
    secondaryContainer = CassSurfaceElevated,
    onSecondaryContainer = CassSilverLight,
    tertiary = CassEmerald,
    onTertiary = CassObsidian,
    background = CassObsidian,
    onBackground = CassSilverLight,
    surface = CassCharcoal,
    onSurface = CassSilverLight,
    surfaceVariant = CassSurface,
    onSurfaceVariant = CassSilverMuted,
    outline = CassBorder,
    outlineVariant = CassBorderGold,
    error = CassCrimson,
    onError = Color.White
)

private val CassAmoledColorScheme = darkColorScheme(
    primary = CassGoldBright,
    onPrimary = Color.Black,
    primaryContainer = CassGoldDark,
    onPrimaryContainer = CassGoldLight,
    secondary = CassSilverLight,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF141414),
    onSecondaryContainer = CassSilver,
    tertiary = CassEmerald,
    onTertiary = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF0F0F0F),
    onSurfaceVariant = CassSilverMuted,
    outline = Color(0xFF262626),
    outlineVariant = CassBorderGold,
    error = CassCrimson,
    onError = Color.White
)

private val CassLuxuryLightColorScheme = lightColorScheme(
    primary = CassGoldDark,
    onPrimary = Color.White,
    primaryContainer = CassGoldLight,
    onPrimaryContainer = CassObsidian,
    secondary = CassSilverDark,
    onSecondary = Color.White,
    secondaryContainer = CassLightSurfaceElevated,
    onSecondaryContainer = CassLightText,
    tertiary = CassEmerald,
    onTertiary = Color.White,
    background = CassLightBg,
    onBackground = CassLightText,
    surface = CassLightSurface,
    onSurface = CassLightText,
    surfaceVariant = CassLightSurfaceElevated,
    onSurfaceVariant = CassLightTextSecondary,
    outline = CassLightBorder,
    outlineVariant = CassBorderGold,
    error = CassCrimson,
    onError = Color.White
)

@Composable
fun CassEasyTheme(
    themeMode: CassThemeMode = CassThemeMode.SIGNATURE_DARK,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val colorScheme = when (themeMode) {
        CassThemeMode.SIGNATURE_DARK -> CassSignatureDarkColorScheme
        CassThemeMode.AMOLED_BLACK -> CassAmoledColorScheme
        CassThemeMode.LUXURY_LIGHT -> CassLuxuryLightColorScheme
        CassThemeMode.SYSTEM -> if (isSystemDark) CassSignatureDarkColorScheme else CassLuxuryLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

