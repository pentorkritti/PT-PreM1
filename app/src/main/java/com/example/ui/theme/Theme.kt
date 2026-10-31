package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val PastelLightColorScheme = lightColorScheme(
    primary = PtSkyBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF075985),
    secondary = PtRosePink,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFCE7F3),
    onSecondaryContainer = Color(0xFF9D174D),
    tertiary = PtLavender,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF3E8FF),
    onTertiaryContainer = Color(0xFF581C87),
    background = PastelSkyTop,
    onBackground = PtDeepNavy,
    surface = GlassWhiteSurface,
    onSurface = PtDeepNavy,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    error = NegativeRedAlert,
    onError = Color.White,
    errorContainer = NegativeRedSurface,
    onErrorContainer = Color(0xFF991B1B),
    outline = Color(0xFFCBD5E1)
)

private val PremiumUiColorScheme = lightColorScheme(
    primary = PremiumCrystalPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF312E81),
    secondary = PremiumCrystalSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFDF2F8),
    onSecondaryContainer = Color(0xFF831843),
    tertiary = PremiumCrystalGold,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF78350F),
    background = PremiumSurface,
    onBackground = Color(0xFF1E1B4B),
    surface = Color(0xFAFFFFFF),
    onSurface = Color(0xFF1E1B4B),
    surfaceVariant = Color(0xFFEEF2FF),
    onSurfaceVariant = Color(0xFF4338CA),
    error = NegativeRedAlert,
    onError = Color.White,
    errorContainer = NegativeRedSurface,
    onErrorContainer = Color(0xFF991B1B),
    outline = Color(0xFFC7D2FE)
)

val PtShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun MyApplicationTheme(
    premiumUiEnabled: Boolean = false,
    content: @Composable () -> Unit
) {
    val scheme = if (premiumUiEnabled) PremiumUiColorScheme else PastelLightColorScheme
    MaterialTheme(
        colorScheme = scheme,
        typography = Typography,
        shapes = PtShapes,
        content = content
    )
}
