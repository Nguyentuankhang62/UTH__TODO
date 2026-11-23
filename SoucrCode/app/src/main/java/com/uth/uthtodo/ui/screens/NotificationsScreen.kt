package com.uth.uthtodo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uth.uthtodo.R
import com.uth.uthtodo.model.NotificationItem
import com.uth.uthtodo.model.NotificationType
import com.uth.uthtodo.ui.components.MainTab
import com.uth.uthtodo.ui.components.UthBottomBar
import com.uth.uthtodo.ui.theme.LocalIsDarkTheme
import com.uth.uthtodo.ui.theme.SoftBlueCard
import com.uth.uthtodo.viewmodel.NotificationViewModel

// Màn hình Thông báo hỗ trợ Sáng/Tối và tự chuyển sang EmptyView sau 10 giây
@Composable
fun NotificationsScreen(
    onOpenHome: () -> Unit,
    onOpenTaskList: () -> Unit,
    onOpenAddTask: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val colorScheme = MaterialTheme.colorScheme
    val viewModel = remember { NotificationViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.startAutoClearTimer(coroutineScope)
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
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Thông báo",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )

                if (!uiState.isEmpty) {
                    Text(
                        text = "Đánh dấu đã đọc",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.primary,
                        modifier = Modifier
                            .clickable { viewModel.markAllAsRead() }
                            .padding(vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isEmpty) {
                NotificationEmptyView(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    onAddTaskClick = onOpenAddTask,
                    onReloadSampleClick = { viewModel.reloadSample(coroutineScope) }
                )
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = colorScheme.surface,
                        shadowElevation = if (isDark) 0.dp else 1.dp
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            uiState.notifications.forEachIndexed { index, item ->
                                NotificationRow(
                                    item = item,
                                    isDark = isDark,
                                    onClick = { viewModel.markAsRead(item.id) }
                                )
                                if (index < uiState.notifications.lastIndex) {
                                    HorizontalDivider(
                                        color = colorScheme.outlineVariant.copy(alpha = 0.7f),
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "Tự động chuyển sang giao diện trống sau ${uiState.secondsRemaining} giây...",
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }
            }
        }

        UthBottomBar(
            selectedTab = MainTab.NOTIFICATIONS,
            onOpenHome = onOpenHome,
            onOpenTaskList = onOpenTaskList,
            onOpenAddTask = onOpenAddTask,
            onOpenNotifications = {},
            onOpenSettings = onOpenSettings
        )
    }
}

@Composable
private fun NotificationRow(
    item: NotificationItem,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val (iconRes, iconBg) = when (item.type) {
        NotificationType.UPCOMING -> R.drawable.ic_alarm to if (isDark) Color(0xFF1E293B) else Color(0xFFEFF6FF)
        NotificationType.OVERDUE -> R.drawable.ic_warning to if (isDark) Color(0xFF3F1D1D) else Color(0xFFFEF2F2)
        NotificationType.ACHIEVEMENT -> R.drawable.ic_check_green to if (isDark) Color(0xFF143324) else Color(0xFFECFDF5)
        NotificationType.REMINDER -> R.drawable.ic_alarm to if (isDark) Color(0xFF1E293B) else Color(0xFFEEF2FF)
    }
    val rowBg = if (item.isUnread) {
        if (isDark) colorScheme.primary.copy(alpha = 0.08f) else Color(0xFFF8FAFF)
    } else {
        colorScheme.surface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = item.title,
                modifier = Modifier.size(18.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = item.timeLabel,
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                    if (item.isUnread) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(colorScheme.primary)
                        )
                    }
                }
            }

            Text(
                text = item.message,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

// Giao diện trống hiển thị sau khi danh sách thông báo về rỗng
@Composable
private fun NotificationEmptyView(
    modifier: Modifier = Modifier,
    onAddTaskClick: () -> Unit,
    onReloadSampleClick: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(116.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(116.dp)
                    .clip(CircleShape)
                    .background(if (isDark) colorScheme.primaryContainer else SoftBlueCard)
            )
            Surface(
                modifier = Modifier.size(68.dp),
                shape = CircleShape,
                color = colorScheme.surface,
                shadowElevation = if (isDark) 0.dp else 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_bell),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colorScheme.onSurface),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 14.dp, bottom = 14.dp)
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF16A34A)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Chưa có thông báo nào",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Khi có việc sắp đến hạn, quá hạn hoặc lời nhắc, bạn sẽ thấy chúng ở đây.",
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(22.dp))

        Button(
            onClick = onAddTaskClick,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = Color.White
            ),
            modifier = Modifier.height(48.dp)
        ) {
            Text(
                text = "+  Thêm việc cần làm",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        TextButton(onClick = onReloadSampleClick) {
            Text(
                text = "Cài đặt nhắc việc",
                color = colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
