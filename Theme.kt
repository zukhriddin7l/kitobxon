package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimaryLight,
    onPrimary = OnEmeraldPrimaryLight,
    primaryContainer = EmeraldContainerLight,
    onPrimaryContainer = OnEmeraldContainerLight,
    secondary = AmberSecondaryLight,
    onSecondary = OnAmberSecondaryLight,
    secondaryContainer = AmberContainerLight,
    onSecondaryContainer = OnAmberContainerLight,
    tertiary = LazuliTertiaryLight,
    onTertiary = OnLazuliTertiaryLight,
    tertiaryContainer = LazuliContainerLight,
    onTertiaryContainer = OnLazuliContainerLight,
    background = ParchmentBackgroundLight,
    onBackground = OnParchmentBackgroundLight,
    surface = ParchmentSurfaceLight,
    onSurface = OnParchmentSurfaceLight,
    surfaceVariant = LinenSurfaceVariantLight,
    onSurfaceVariant = OnLinenSurfaceVariantLight,
    outline = WarmOutlineLight,
    outlineVariant = WarmOutlineVariantLight
)

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimaryDark,
    onPrimary = OnEmeraldPrimaryDark,
    primaryContainer = EmeraldContainerDark,
    onPrimaryContainer = OnEmeraldContainerDark,
    secondary = AmberSecondaryDark,
    onSecondary = OnAmberSecondaryDark,
    secondaryContainer = AmberContainerDark,
    onSecondaryContainer = OnAmberContainerDark,
    tertiary = LazuliTertiaryDark,
    onTertiary = OnLazuliTertiaryDark,
    tertiaryContainer = LazuliContainerDark,
    onTertiaryContainer = OnLazuliContainerDark,
    background = LibraryBackgroundDark,
    onBackground = OnLibraryBackgroundDark,
    surface = LibrarySurfaceDark,
    onSurface = OnLibrarySurfaceDark,
    surfaceVariant = LibrarySurfaceVariantDark,
    onSurfaceVariant = OnLibrarySurfaceVariantDark,
    outline = WarmOutlineDark,
    outlineVariant = WarmOutlineVariantDark
)

val KitobxonShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun KitobxonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = KitobxonShapes,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    KitobxonTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
