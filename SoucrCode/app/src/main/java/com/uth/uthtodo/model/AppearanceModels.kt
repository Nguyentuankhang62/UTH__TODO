package com.uth.uthtodo.model

// Chế độ giao diện người dùng có thể chọn trong Cài đặt
enum class AppThemeMode(val label: String) {
    LIGHT("Sáng"),
    DARK("Tối"),
    SYSTEM("Tự động")
}

// Màu chủ đạo hiển thị cho nút chính, thẻ tiến độ và mục đang chọn
enum class AppAccentColor(val label: String, val hex: Long) {
    BLUE("Xanh dương", 0xFF2563EB),
    GREEN("Xanh lá", 0xFF0F766E),
    ORANGE("Cam đất", 0xFFC2410C),
    PURPLE("Tím", 0xFF7C3AED)
}

// Trạng thái giao diện và thông báo dùng chung toàn ứng dụng
data class AppAppearanceState(
    val themeMode: AppThemeMode = AppThemeMode.LIGHT,
    val accentColor: AppAccentColor = AppAccentColor.BLUE,
    val fontScale: Float = 1.0f,
    val remindEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val dailySummaryEnabled: Boolean = false,
    val languageLabel: String = "Tiếng Việt"
) {
    // Kiểm tra xem có đang bật giao diện tối theo tùy chọn và hệ thống hay không
    fun resolveDarkTheme(isSystemDark: Boolean): Boolean = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> isSystemDark
    }

    // Giới hạn tỉ lệ cỡ chữ trong khoảng an toàn để không vỡ bố cục
    fun withClampedFontScale(scale: Float): AppAppearanceState =
        copy(fontScale = scale.coerceIn(MIN_FONT_SCALE, MAX_FONT_SCALE))

    companion object {
        const val MIN_FONT_SCALE = 0.85f
        const val MAX_FONT_SCALE = 1.20f
    }
}
