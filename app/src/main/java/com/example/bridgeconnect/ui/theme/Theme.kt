package com.example.bridgeconnect.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.bridgeconnect.data.AppThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = BronzeMuted,
    onPrimary = MidnightBackground,
    primaryContainer = SlateSurfaceVariant,
    onPrimaryContainer = TextWhite,
    secondary = SapphirePrimary,
    onSecondary = Color.White,
    tertiary = CopperPrimary,
    onTertiary = Color.White,
    background = MidnightBackground,
    onBackground = TextWhite,
    surface = SlateSurface,
    onSurface = TextWhite,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = TextMuted
)

private val LightColorScheme = lightColorScheme(
    primary = BronzeMuted,
    onPrimary = Color.White,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = Color(0xFF1E293B),
    secondary = SapphireDark,
    onSecondary = Color.White,
    tertiary = CopperDark,
    onTertiary = Color.White,
    background = LightBg,
    onBackground = Color(0xFF0F172A),
    surface = LightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF64748B)
)

@Composable
fun BridgeConnectTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}