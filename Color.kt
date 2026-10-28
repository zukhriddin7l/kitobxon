package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Warm Literary Editorial Palette — Samarkand Emerald, Parchment Ivory & Terracotta Amber
val EmeraldPrimaryLight = Color(0xFF125446)
val OnEmeraldPrimaryLight = Color(0xFFFFFFFF)
val EmeraldContainerLight = Color(0xFFCDEBE3)
val OnEmeraldContainerLight = Color(0xFF06211B)

val AmberSecondaryLight = Color(0xFFB85D19)
val OnAmberSecondaryLight = Color(0xFFFFFFFF)
val AmberContainerLight = Color(0xFFFFDCC4)
val OnAmberContainerLight = Color(0xFF361400)

val LazuliTertiaryLight = Color(0xFF284B7A)
val OnLazuliTertiaryLight = Color(0xFFFFFFFF)
val LazuliContainerLight = Color(0xFFD6E3FF)
val OnLazuliContainerLight = Color(0xFF001B3E)

val ParchmentBackgroundLight = Color(0xFFFAF6F0)
val OnParchmentBackgroundLight = Color(0xFF1F1A17)
val ParchmentSurfaceLight = Color(0xFFFFFCF7)
val OnParchmentSurfaceLight = Color(0xFF1F1A17)
val LinenSurfaceVariantLight = Color(0xFFEDE5D8)
val OnLinenSurfaceVariantLight = Color(0xFF4D453D)
val WarmOutlineLight = Color(0xFF80756B)
val WarmOutlineVariantLight = Color(0xFFD3C8B9)

// Dark Theme — Cozy Midnight Library
val EmeraldPrimaryDark = Color(0xFF7AD6C4)
val OnEmeraldPrimaryDark = Color(0xFF00382E)
val EmeraldContainerDark = Color(0xFF0C5144)
val OnEmeraldContainerDark = Color(0xFFCDEBE3)

val AmberSecondaryDark = Color(0xFFFFB782)
val OnAmberSecondaryDark = Color(0xFF542300)
val AmberContainerDark = Color(0xFF793800)
val OnAmberContainerDark = Color(0xFFFFDCC4)

val LazuliTertiaryDark = Color(0xFFA9C7FF)
val OnLazuliTertiaryDark = Color(0xFF003063)
val LazuliContainerDark = Color(0xFF093968)
val OnLazuliContainerDark = Color(0xFFD6E3FF)

val LibraryBackgroundDark = Color(0xFF141210)
val OnLibraryBackgroundDark = Color(0xFFEBE2D8)
val LibrarySurfaceDark = Color(0xFF1C1916)
val OnLibrarySurfaceDark = Color(0xFFEBE2D8)
val LibrarySurfaceVariantDark = Color(0xFF332D27)
val OnLibrarySurfaceVariantDark = Color(0xFFD0C5B9)
val WarmOutlineDark = Color(0xFF998F84)
val WarmOutlineVariantDark = Color(0xFF4C453D)

// E-Book Reader Paper Palettes
data class ReaderPaperPalette(
    val background: Color,
    val surface: Color,
    val onBackground: Color,
    val secondaryText: Color,
    val accent: Color
)

object ReaderPalettes {
    val Parchment = ReaderPaperPalette(
        background = Color(0xFFFAF3E3),
        surface = Color(0xFFF2E7CF),
        onBackground = Color(0xFF241D17),
        secondaryText = Color(0xFF66584B),
        accent = Color(0xFF125446)
    )
    val CrispDay = ReaderPaperPalette(
        background = Color(0xFFFFFFFF),
        surface = Color(0xFFF3F4F6),
        onBackground = Color(0xFF18181B),
        secondaryText = Color(0xFF52525B),
        accent = Color(0xFF125446)
    )
    val Sepia = ReaderPaperPalette(
        background = Color(0xFFEFE2C8),
        surface = Color(0xFFE3D2B2),
        onBackground = Color(0xFF3A2A1D),
        secondaryText = Color(0xFF6E5742),
        accent = Color(0xFF9C4B14)
    )
    val Night = ReaderPaperPalette(
        background = Color(0xFF131316),
        surface = Color(0xFF1F1F24),
        onBackground = Color(0xFFE4DFD7),
        secondaryText = Color(0xFFA09A90),
        accent = Color(0xFF7AD6C4)
    )
}

// Curated Clothbound Hardcover Palette Presets
object BookCoverSwatches {
    val DeepRuby = Color(0xFF6E1E2A)
    val RoyalIndigo = Color(0xFF1E3557)
    val ForestMoss = Color(0xFF1D4D3E)
    val TerracottaClay = Color(0xFF8E3B1C)
    val PlumVelvet = Color(0xFF4A2545)
    val MidnightSlate = Color(0xFF23313F)
    val GoldFoil = Color(0xFFE8C07A)
    val CreamFoil = Color(0xFFFDF7EC)
}
