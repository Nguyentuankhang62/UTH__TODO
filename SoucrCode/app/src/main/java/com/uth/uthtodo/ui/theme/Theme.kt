package com.uth.uthtodo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.uth.uthtodo.model.AppAppearanceState

// Cung cấp trạng thái dark mode để các màn hình chọn màu badge và icon phù hợp
val LocalIsDarkTheme = staticCompositionLocalOf { false }

// Chủ đề giao diện động hỗ trợ Sáng/Tối/Tự động, màu chủ đạo và cỡ chữ
@Composable
fun UthTodoTheme(
    appearanceState: AppAppearanceState = AppAppearanceState(),
    content: @Composable () -> Unit
) {
    val isDark = appearanceState.resolveDarkTheme(isSystemInDarkTheme())
    val accent = Color(appearanceState.accentColor.hex)

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = accent,
            onPrimary = Color.White,
            primaryContainer = accent.copy(alpha = 0.22f),
            onPrimaryContainer = Color(0xFF93C5FD),
            background = DarkBackground,
            onBackground = DarkTextPrimary,
            surface = DarkSurface,
            onSurface = DarkTextPrimary,
            surfaceVariant = DarkSurfaceElevated,
            onSurfaceVariant = DarkTextSecondary,
            outline = DarkBorder,
            outlineVariant = DarkBorder
        )
    } else {
        lightColorScheme(
            primary = accent,
            onPrimary = Color.White,
            primaryContainer = accent.copy(alpha = 0.12f),
            onPrimaryContainer = accent,
            background = SurfaceMuted,
            onBackground = TextPrimary,
            surface = BackgroundWhite,
            onSurface = TextPrimary,
            surfaceVariant = Color(0xFFEDF0F5),
            onSurfaceVariant = TextSecondary,
            outline = BorderLight,
            outlineVariant = BorderLight
        )
    }

    val currentDensity = LocalDensity.current
    val scaledDensity = Density(
        density = currentDensity.density,
        fontScale = currentDensity.fontScale * appearanceState.fontScale
    )

    CompositionLocalProvider(
        LocalIsDarkTheme provides isDark,
        LocalDensity provides scaledDensity
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = UthTypography,
            content = content
        )
    }
}
