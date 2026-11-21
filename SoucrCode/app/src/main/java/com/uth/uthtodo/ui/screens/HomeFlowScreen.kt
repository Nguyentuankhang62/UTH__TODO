package com.uth.uthtodo.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uth.uthtodo.R
import com.uth.uthtodo.model.TaskCategory
import com.uth.uthtodo.model.TaskItem
import com.uth.uthtodo.ui.theme.BorderLight
import com.uth.uthtodo.ui.theme.LifeBadgeBg
import com.uth.uthtodo.ui.theme.LifeBadgeText
import com.uth.uthtodo.ui.theme.PrimaryBlue
import com.uth.uthtodo.ui.theme.StudyBadgeBg
import com.uth.uthtodo.ui.theme.StudyBadgeText
import com.uth.uthtodo.ui.theme.SurfaceMuted
import com.uth.uthtodo.ui.theme.TextPrimary
import com.uth.uthtodo.ui.theme.TextSecondary
import com.uth.uthtodo.viewmodel.HomeFlowViewModel

// Màn hình Trang chủ (Tuần 04) tải danh sách công việc và thống kê trực tiếp từ REST API
@Composable
fun HomeFlowScreen(
    displayName: String = "bạn",
    avatarText: String = "K",
    onProfileClick: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel = remember { HomeFlowViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadTasks(coroutineScope)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceMuted)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.clickable { onProfileClick() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(StudyBadgeBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = avatarText,
                            color = StudyBadgeText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Column {
                        Text(
                            text = "Thứ Ba, 20 tháng 10",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "Chào $displayName!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { viewModel.loadTasks(coroutineScope) },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_bell),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 9.dp)
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444))
                    )
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                        Text(
                            text = "Đang tải danh sách công việc...",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else if (uiState.errorMessage != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = uiState.errorMessage.orEmpty(),
                            color = Color(0xFFB91C1C),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.loadTasks(coroutineScope) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("Thử lại")
                        }
                    }
                }
            } else {
                val stats = uiState.stats

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = PrimaryBlue
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Tiến độ hôm nay",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${stats.doneToday}/${stats.totalToday} việc",
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stats.encouragementText,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp
                            )
                        }

                        ProgressRing(percent = stats.progressPercent)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeCategoryStatCard(
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.ic_study,
                        iconBg = StudyBadgeBg,
                        title = "Học tập",
                        subtitle = "${stats.studyRemaining} việc còn lại"
                    )
                    HomeCategoryStatCard(
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.ic_life,
                        iconBg = LifeBadgeBg,
                        title = "Cuộc sống",
                        subtitle = "${stats.lifeRemaining} việc còn lại"
                    )
                }

                if (uiState.actionMessage != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEE2E2)
                    ) {
                        Text(
                            text = uiState.actionMessage.orEmpty(),
                            color = Color(0xFFB91C1C),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Việc hôm nay",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Xem tất cả (${uiState.allTasks.size})",
                        fontSize = 13.sp,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (uiState.todayTasks.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White
                    ) {
                        Text(
                            text = "Hôm nay chưa có công việc nào.",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                } else {
                    uiState.todayTasks.forEach { task ->
                        HomeApiTaskRow(
                            task = task,
                            isUpdating = uiState.updatingTaskId == task.id,
                            onToggleDone = { viewModel.toggleTaskDone(task, coroutineScope) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        HomeFlowBottomBar(onBackToMenu = onBack)
    }
}

@Composable
private fun ProgressRing(percent: Int) {
    Box(
        modifier = Modifier.size(68.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            drawArc(
                color = Color.White.copy(alpha = 0.25f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke
            )
            drawArc(
                color = Color.White,
                startAngle = -90f,
                sweepAngle = 360f * (percent / 100f),
                useCenter = false,
                style = stroke
            )
        }
        Text(
            text = "$percent%",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun HomeCategoryStatCard(
    modifier: Modifier = Modifier,
    iconRes: Int,
    iconBg: Color,
    title: String,
    subtitle: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    contentDescription = title,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun HomeApiTaskRow(
    task: TaskItem,
    isUpdating: Boolean,
    onToggleDone: () -> Unit
) {
    val isStudy = task.category == TaskCategory.STUDY
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isUpdating) { onToggleDone() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isUpdating) {
                CircularProgressIndicator(
                    color = PrimaryBlue,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(22.dp)
                )
            } else if (task.done) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = null,
                        modifier = Modifier.size(13.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .border(1.8.dp, BorderLight, CircleShape)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = task.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (task.done) TextSecondary else TextPrimary,
                    textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isStudy) StudyBadgeBg else LifeBadgeBg
                    ) {
                        Text(
                            text = task.category.displayName,
                            color = if (isStudy) StudyBadgeText else LifeBadgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = task.time,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Image(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun HomeFlowBottomBar(onBackToMenu: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeFlowNavItem(iconRes = R.drawable.ic_home, label = "Trang chủ", selected = true)
            HomeFlowNavItem(iconRes = R.drawable.ic_list, label = "Danh sách", selected = false)

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            }

            HomeFlowNavItem(iconRes = R.drawable.ic_bell, label = "Thông báo", selected = false)
            Box(modifier = Modifier.clickable { onBackToMenu() }) {
                HomeFlowNavItem(iconRes = R.drawable.ic_settings, label = "Cài đặt", selected = false)
            }
        }
    }
}

@Composable
private fun HomeFlowNavItem(iconRes: Int, label: String, selected: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (selected) PrimaryBlue else TextSecondary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
