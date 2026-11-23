package com.uth.uthtodo.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uth.uthtodo.R
import com.uth.uthtodo.model.TaskCategory
import com.uth.uthtodo.model.TaskItem
import com.uth.uthtodo.model.TaskPriority
import com.uth.uthtodo.ui.components.MainTab
import com.uth.uthtodo.ui.components.UthBottomBar
import com.uth.uthtodo.ui.theme.DarkLifeBadgeBg
import com.uth.uthtodo.ui.theme.DarkLifeBadgeText
import com.uth.uthtodo.ui.theme.DarkStudyBadgeBg
import com.uth.uthtodo.ui.theme.DarkStudyBadgeText
import com.uth.uthtodo.ui.theme.LifeBadgeBg
import com.uth.uthtodo.ui.theme.LifeBadgeText
import com.uth.uthtodo.ui.theme.LocalIsDarkTheme
import com.uth.uthtodo.ui.theme.StudyBadgeBg
import com.uth.uthtodo.ui.theme.StudyBadgeText
import com.uth.uthtodo.viewmodel.TaskListFilter
import com.uth.uthtodo.viewmodel.TaskListFilterEngine
import com.uth.uthtodo.viewmodel.TaskListViewModel

// Màn hình Danh sách công việc hỗ trợ giao diện Sáng/Tối, tìm kiếm và lọc nhóm qua API
@Composable
fun TaskListScreen(
    onOpenHome: () -> Unit,
    onOpenTaskDetail: (Long) -> Unit,
    onOpenAddTask: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val colorScheme = MaterialTheme.colorScheme
    val viewModel = remember { TaskListViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadTasks(coroutineScope)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
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
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Danh sách",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )
                Text(
                    text = "${uiState.remainingCount} việc chưa xong",
                    fontSize = 13.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(14.dp),
                color = colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colorScheme.onSurfaceVariant),
                        modifier = Modifier.size(18.dp)
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        if (uiState.searchQuery.isEmpty()) {
                            Text(
                                text = "Tìm công việc",
                                fontSize = 14.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                        BasicTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchChange(it) },
                            singleLine = true,
                            cursorBrush = SolidColor(colorScheme.primary),
                            textStyle = TextStyle(fontSize = 14.sp, color = colorScheme.onSurface),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskListFilter.entries.forEach { filter ->
                    val selected = uiState.selectedFilter == filter
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (selected) colorScheme.onBackground else colorScheme.surface,
                        border = if (selected) null else BorderStroke(1.dp, colorScheme.outlineVariant),
                        modifier = Modifier.clickable { viewModel.onFilterSelect(filter) }
                    ) {
                        Text(
                            text = filter.label,
                            color = if (selected) colorScheme.background else colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorScheme.primary)
                }
            } else if (uiState.errorMessage != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = uiState.errorMessage.orEmpty(),
                            color = Color(0xFFEF4444),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.loadTasks(coroutineScope) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                        ) {
                            Text("Thử lại")
                        }
                    }
                }
            } else {
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

                if (uiState.todoTasks.isEmpty() && uiState.doneTasks.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = colorScheme.surface
                    ) {
                        Text(
                            text = "Không tìm thấy công việc phù hợp.",
                            fontSize = 14.sp,
                            color = colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                } else {
                    if (uiState.todoTasks.isNotEmpty()) {
                        Text(
                            text = "CẦN LÀM",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        uiState.todoTasks.forEach { task ->
                            TaskListTodoCard(
                                task = task,
                                isDark = isDark,
                                isUpdating = uiState.updatingTaskId == task.id,
                                onToggleDone = { viewModel.toggleTaskDone(task, coroutineScope) },
                                onOpenDetail = { onOpenTaskDetail(task.id) }
                            )
                        }
                    }

                    if (uiState.doneTasks.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "ĐÃ XONG",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        uiState.doneTasks.forEach { task ->
                            TaskListDoneCard(
                                task = task,
                                isDark = isDark,
                                isUpdating = uiState.updatingTaskId == task.id,
                                onToggleDone = { viewModel.toggleTaskDone(task, coroutineScope) },
                                onOpenDetail = { onOpenTaskDetail(task.id) }
                            )
                        }
                    }
                }
            }
        }

        UthBottomBar(
            selectedTab = MainTab.TASK_LIST,
            onOpenHome = onOpenHome,
            onOpenTaskList = {},
            onOpenAddTask = onOpenAddTask,
            onOpenNotifications = onOpenNotifications,
            onOpenSettings = onOpenSettings
        )
    }
}

@Composable
private fun TaskListTodoCard(
    task: TaskItem,
    isDark: Boolean,
    isUpdating: Boolean,
    onToggleDone: () -> Unit,
    onOpenDetail: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val isStudy = task.category == TaskCategory.STUDY
    val badgeBg = if (isStudy) {
        if (isDark) DarkStudyBadgeBg else StudyBadgeBg
    } else {
        if (isDark) DarkLifeBadgeBg else LifeBadgeBg
    }
    val badgeText = if (isStudy) {
        if (isDark) DarkStudyBadgeText else StudyBadgeText
    } else {
        if (isDark) DarkLifeBadgeText else LifeBadgeText
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetail() },
        shape = RoundedCornerShape(16.dp),
        color = colorScheme.surface,
        shadowElevation = if (isDark) 0.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clickable(enabled = !isUpdating) { onToggleDone() },
                contentAlignment = Alignment.Center
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        color = colorScheme.primary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .border(1.8.dp, colorScheme.outlineVariant, CircleShape)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = task.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = badgeBg
                    ) {
                        Text(
                            text = task.category.displayName,
                            color = badgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = TaskListFilterEngine.formatDueLabel(task.date, task.time),
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            if (task.priority == TaskPriority.HIGH) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDark) Color(0xFF3F1D1D) else Color(0xFFFEE2E2)
                ) {
                    Text(
                        text = "Gấp",
                        color = if (isDark) Color(0xFFFCA5A5) else Color(0xFFDC2626),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskListDoneCard(
    task: TaskItem,
    isDark: Boolean,
    isUpdating: Boolean,
    onToggleDone: () -> Unit,
    onOpenDetail: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetail() },
        shape = RoundedCornerShape(16.dp),
        color = colorScheme.surface,
        shadowElevation = if (isDark) 0.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clickable(enabled = !isUpdating) { onToggleDone() },
                contentAlignment = Alignment.Center
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        color = colorScheme.primary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_check),
                            contentDescription = null,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Text(
                text = task.title,
                fontSize = 14.sp,
                color = colorScheme.onSurfaceVariant,
                textDecoration = TextDecoration.LineThrough,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
