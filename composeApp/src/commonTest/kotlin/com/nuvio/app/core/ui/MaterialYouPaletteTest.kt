package com.nuvio.app.core.ui

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class MaterialYouPaletteTest {

    private val testScheme = darkColorScheme(
        primary = Color(0xFF123456),
        tertiary = Color(0xFF654321),
        onPrimary = Color(0xFFFFFFFF),
        onTertiary = Color(0xFFEEEEEE),
        primaryContainer = Color(0xFF102030),
        surface = Color(0xFF1F1F1F),
        surfaceContainer = Color(0xFF2A2A2A),
        surfaceContainerHigh = Color(0xFF353535),
    )

    @Test
    fun fromMaterialYouWithoutAmoledUsesSurfaceBackground() {
        val palette = ThemeColorPalette.fromMaterialYou(testScheme, amoled = false)

        assertEquals(testScheme.primary, palette.secondary)
        assertEquals(testScheme.tertiary, palette.secondaryVariant)
        assertEquals(listOf(testScheme.primary, testScheme.tertiary), palette.accentGradient)
        assertEquals(testScheme.onPrimary, palette.onSecondary)
        assertEquals(testScheme.onTertiary, palette.onSecondaryVariant)
        assertEquals(testScheme.primary, palette.focusRing)
        assertEquals(testScheme.primaryContainer, palette.focusBackground)
        assertEquals(testScheme.surface, palette.background)
        assertEquals(testScheme.surfaceContainer, palette.backgroundElevated)
        assertEquals(testScheme.surfaceContainerHigh, palette.backgroundCard)
        assertEquals("#123456", palette.nativeAccentHex)
    }

    @Test
    fun fromMaterialYouWithAmoledForcesPureBlackBackground() {
        val palette = ThemeColorPalette.fromMaterialYou(testScheme, amoled = true)

        assertEquals(Color.Black, palette.background)
        assertEquals(testScheme.primary, palette.secondary)
        assertEquals(testScheme.surfaceContainer, palette.backgroundElevated)
        assertEquals(testScheme.surfaceContainerHigh, palette.backgroundCard)
    }
}
