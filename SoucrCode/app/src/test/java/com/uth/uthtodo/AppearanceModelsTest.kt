package com.uth.uthtodo

import com.uth.uthtodo.model.AppAccentColor
import com.uth.uthtodo.model.AppAppearanceState
import com.uth.uthtodo.model.AppThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Kiểm thử logic chọn giao diện Sáng/Tối/Tự động, màu chủ đạo và giới hạn cỡ chữ
class AppearanceModelsTest {

    @Test
    fun resolveDarkTheme_respectsLightDarkAndSystemModes() {
        val light = AppAppearanceState(themeMode = AppThemeMode.LIGHT)
        val dark = AppAppearanceState(themeMode = AppThemeMode.DARK)
        val system = AppAppearanceState(themeMode = AppThemeMode.SYSTEM)

        assertFalse(light.resolveDarkTheme(isSystemDark = true))
        assertTrue(dark.resolveDarkTheme(isSystemDark = false))
        assertTrue(system.resolveDarkTheme(isSystemDark = true))
        assertFalse(system.resolveDarkTheme(isSystemDark = false))
    }

    @Test
    fun withClampedFontScale_clampsWithinSafeBounds() {
        val state = AppAppearanceState()
        assertEquals(AppAppearanceState.MIN_FONT_SCALE, state.withClampedFontScale(0.5f).fontScale, 0.001f)
        assertEquals(AppAppearanceState.MAX_FONT_SCALE, state.withClampedFontScale(2.0f).fontScale, 0.001f)
        assertEquals(1.05f, state.withClampedFontScale(1.05f).fontScale, 0.001f)
    }

    @Test
    fun accentColors_hasFourFigmaOptions() {
        assertEquals(4, AppAccentColor.entries.size)
        assertEquals(0xFF2563EB, AppAccentColor.BLUE.hex)
    }
}
