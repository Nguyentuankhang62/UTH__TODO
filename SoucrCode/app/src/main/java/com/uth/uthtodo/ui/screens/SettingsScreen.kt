package com.uth.uthtodo.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uth.uthtodo.R
import com.uth.uthtodo.model.AppAccentColor
import com.uth.uthtodo.model.AppAppearanceState
import com.uth.uthtodo.model.AppPermissionType
import com.uth.uthtodo.model.AppThemeMode
import com.uth.uthtodo.model.UserProfile
import com.uth.uthtodo.ui.components.ContextualPermissionDialog
import com.uth.uthtodo.ui.components.MainTab
import com.uth.uthtodo.ui.components.UthBottomBar
import com.uth.uthtodo.ui.components.isAppPermissionGranted
import com.uth.uthtodo.ui.theme.DarkStudyBadgeBg
import com.uth.uthtodo.ui.theme.DarkStudyBadgeText
import com.uth.uthtodo.ui.theme.LocalIsDarkTheme
import com.uth.uthtodo.ui.theme.StudyBadgeBg
import com.uth.uthtodo.ui.theme.StudyBadgeText

// Màn hình Cài đặt theo đúng thiết kế Figma (6. Cài đặt - đổi giao diện)
@Composable
fun SettingsScreen(
    user: UserProfile,
    appearanceState: AppAppearanceState,
    onAppearanceChange: (AppAppearanceState) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenTaskList: () -> Unit,
    onOpenAddTask: () -> Unit,
    onOpenNotifications: () -> Unit
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkTheme.current
    val colorScheme = MaterialTheme.colorScheme
    var showNotificationPrompt by remember { mutableStateOf(false) }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    if (showNotificationPrompt) {
        ContextualPermissionDialog(
            permissionType = AppPermissionType.NOTIFICATION,
            onAllow = {
                showNotificationPrompt = false
                if (AppPermissionType.NOTIFICATION.requiresRuntimePrompt()) {
                    notificationLauncher.launch(AppPermissionType.NOTIFICATION.runtimePermission())
                }
            },
            onSkip = { showNotificationPrompt = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cài đặt",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isDark) DarkStudyBadgeBg else StudyBadgeBg)
                        .clickable { onOpenProfile() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.initials(),
                        color = if (isDark) DarkStudyBadgeText else StudyBadgeText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "GIAO DIỆN",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp,
                    color = colorScheme.onSurfaceVariant
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = colorScheme.surface,
                    shadowElevation = if (isDark) 0.dp else 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AppThemeMode.entries.forEach { mode ->
                                ThemePreviewOption(
                                    modifier = Modifier.weight(1f),
                                    mode = mode,
                                    selected = appearanceState.themeMode == mode,
                                    accentColor = colorScheme.primary,
                                    onClick = {
                                        onAppearanceChange(appearanceState.copy(themeMode = mode))
                                    }
                                )
                            }
                        }

                        HorizontalDivider(color = colorScheme.outlineVariant.copy(alpha = 0.7f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Màu chủ đạo",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AppAccentColor.entries.forEach { accent ->
                                    val selected = appearanceState.accentColor == accent
                                    val swatchColor = Color(accent.hex)
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .then(
                                                if (selected) {
                                                    Modifier.border(2.dp, swatchColor, CircleShape)
                                                } else {
                                                    Modifier
                                                }
                                            )
                                            .clickable {
                                                onAppearanceChange(appearanceState.copy(accentColor = accent))
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(if (selected) 22.dp else 24.dp)
                                                .clip(CircleShape)
                                                .background(swatchColor)
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = colorScheme.outlineVariant.copy(alpha = 0.7f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Cỡ chữ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface
                            )
                            Row(
                                modifier = Modifier.width(172.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "A",
                                    fontSize = 12.sp,
                                    color = colorScheme.onSurfaceVariant
                                )
                                Slider(
                                    value = appearanceState.fontScale,
                                    onValueChange = { scale ->
                                        onAppearanceChange(appearanceState.withClampedFontScale(scale))
                                    },
                                    valueRange = AppAppearanceState.MIN_FONT_SCALE..AppAppearanceState.MAX_FONT_SCALE,
                                    modifier = Modifier.weight(1f),
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color.White,
                                        activeTrackColor = colorScheme.primary,
                                        inactiveTrackColor = colorScheme.outlineVariant
                                    )
                                )
                                Text(
                                    text = "A",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "THÔNG BÁO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp,
                    color = colorScheme.onSurfaceVariant
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = colorScheme.surface,
                    shadowElevation = if (isDark) 0.dp else 1.dp
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SettingsSwitchRow(
                            title = "Nhắc việc",
                            subtitle = "Báo trước hạn chót 15 phút",
                            checked = appearanceState.remindEnabled,
                            onCheckedChange = { enabled ->
                                onAppearanceChange(appearanceState.copy(remindEnabled = enabled))
                                if (enabled && !isAppPermissionGranted(context, AppPermissionType.NOTIFICATION)) {
                                    showNotificationPrompt = true
                                }
                            }
                        )
                        HorizontalDivider(
                            color = colorScheme.outlineVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        SettingsSwitchRow(
                            title = "Âm thanh",
                            subtitle = "Phát âm khi có thông báo",
                            checked = appearanceState.soundEnabled,
                            onCheckedChange = { enabled ->
                                onAppearanceChange(appearanceState.copy(soundEnabled = enabled))
                            }
                        )
                        HorizontalDivider(
                            color = colorScheme.outlineVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        SettingsSwitchRow(
                            title = "Tổng kết cuối ngày",
                            subtitle = "Gửi lúc 21:00 mỗi tối",
                            checked = appearanceState.dailySummaryEnabled,
                            onCheckedChange = { enabled ->
                                onAppearanceChange(appearanceState.copy(dailySummaryEnabled = enabled))
                            }
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "CHUNG",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp,
                    color = colorScheme.onSurfaceVariant
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Toast.makeText(
                                context,
                                "Ngôn ngữ hiện tại: ${appearanceState.languageLabel}",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                    shape = RoundedCornerShape(18.dp),
                    color = colorScheme.surface,
                    shadowElevation = if (isDark) 0.dp else 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ngôn ngữ",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.onSurface
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = appearanceState.languageLabel,
                                fontSize = 14.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                            Image(
                                painter = painterResource(id = R.drawable.ic_chevron_right),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(colorScheme.onSurfaceVariant),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        UthBottomBar(
            selectedTab = MainTab.SETTINGS,
            onOpenHome = onOpenHome,
            onOpenTaskList = onOpenTaskList,
            onOpenAddTask = onOpenAddTask,
            onOpenNotifications = onOpenNotifications,
            onOpenSettings = {}
        )
    }
}

@Composable
private fun ThemePreviewOption(
    modifier: Modifier = Modifier,
    mode: AppThemeMode,
    selected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(74.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) accentColor else colorScheme.outlineVariant
            ),
            color = Color.Transparent
        ) {
            when (mode) {
                AppThemeMode.LIGHT -> ThemePreviewMiniCanvas(
                    bgColor = Color(0xFFF3F4F6),
                    headerColor = Color(0xFFD1D5DB),
                    cardColor = Color.White
                )

                AppThemeMode.DARK -> ThemePreviewMiniCanvas(
                    bgColor = Color(0xFF0A0A0C),
                    headerColor = Color(0xFF3F3F46),
                    cardColor = Color(0xFF222226)
                )

                AppThemeMode.SYSTEM -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(Color(0xFFF3F4F6))
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(Color(0xFF0A0A0C))
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.55f)
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF9CA3AF).copy(alpha = 0.65f))
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(5.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(Color.White)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(Color(0xFF27272A))
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(5.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(Color.White)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(Color(0xFF27272A))
                                )
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = mode.label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) accentColor else colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ThemePreviewMiniCanvas(
    bgColor: Color,
    headerColor: Color,
    cardColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(headerColor)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(cardColor)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(cardColor)
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF22C55E),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = colorScheme.outlineVariant,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
