package com.uth.uthtodo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uth.uthtodo.R
import com.uth.uthtodo.ui.theme.DarkBottomBar
import com.uth.uthtodo.ui.theme.LocalIsDarkTheme

// Danh sách tab của thanh điều hướng dưới theo đúng thiết kế Figma
enum class MainTab {
    HOME,
    TASK_LIST,
    NOTIFICATIONS,
    SETTINGS
}

// Thanh điều hướng dưới dùng chung cho 4 tab chính, hỗ trợ Sáng/Tối và màu chủ đạo
@Composable
fun UthBottomBar(
    selectedTab: MainTab,
    onOpenHome: () -> Unit,
    onOpenTaskList: () -> Unit,
    onOpenAddTask: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val primary = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant
    val barBg = if (isDark) DarkBottomBar else Color.White

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = barBg,
        shadowElevation = if (isDark) 0.dp else 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavTabItem(
                iconRes = R.drawable.ic_home,
                label = "Trang chủ",
                selected = selectedTab == MainTab.HOME,
                activeColor = primary,
                inactiveColor = inactiveColor,
                onClick = onOpenHome
            )
            BottomNavTabItem(
                iconRes = R.drawable.ic_list,
                label = "Danh sách",
                selected = selectedTab == MainTab.TASK_LIST,
                activeColor = primary,
                inactiveColor = inactiveColor,
                onClick = onOpenTaskList
            )

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(primary)
                    .clickable { onOpenAddTask() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Thêm việc mới",
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier.size(22.dp)
                )
            }

            BottomNavTabItem(
                iconRes = R.drawable.ic_bell,
                label = "Thông báo",
                selected = selectedTab == MainTab.NOTIFICATIONS,
                activeColor = primary,
                inactiveColor = inactiveColor,
                onClick = onOpenNotifications
            )
            BottomNavTabItem(
                iconRes = R.drawable.ic_settings,
                label = "Cài đặt",
                selected = selectedTab == MainTab.SETTINGS,
                activeColor = primary,
                inactiveColor = inactiveColor,
                onClick = onOpenSettings
            )
        }
    }
}

@Composable
private fun BottomNavTabItem(
    iconRes: Int,
    label: String,
    selected: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    onClick: () -> Unit
) {
    val tint = if (selected) activeColor else inactiveColor
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            colorFilter = ColorFilter.tint(tint),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = tint,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
