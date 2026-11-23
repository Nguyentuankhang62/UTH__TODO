package com.uth.uthtodo.ui.screens

import android.widget.Toast
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uth.uthtodo.R
import com.uth.uthtodo.model.SubTask
import com.uth.uthtodo.model.TaskCategory
import com.uth.uthtodo.model.TaskPriority
import com.uth.uthtodo.ui.theme.DarkLifeBadgeBg
import com.uth.uthtodo.ui.theme.DarkLifeBadgeText
import com.uth.uthtodo.ui.theme.DarkStudyBadgeBg
import com.uth.uthtodo.ui.theme.DarkStudyBadgeText
import com.uth.uthtodo.ui.theme.LifeBadgeBg
import com.uth.uthtodo.ui.theme.LifeBadgeText
import com.uth.uthtodo.ui.theme.LocalIsDarkTheme
import com.uth.uthtodo.ui.theme.StudyBadgeBg
import com.uth.uthtodo.ui.theme.StudyBadgeText
import com.uth.uthtodo.viewmodel.SubtaskListHelper
import com.uth.uthtodo.viewmodel.TaskDetailViewModel
import com.uth.uthtodo.viewmodel.TaskListFilterEngine

// Màn hình Chi tiết công việc hỗ trợ Sáng/Tối và kết nối GET, PATCH, DELETE /tasks/{id}
@Composable
fun TaskDetailScreen(
    taskId: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkTheme.current
    val colorScheme = MaterialTheme.colorScheme
    val viewModel = remember { TaskDetailViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId, coroutineScope)
    }

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            Toast.makeText(context, "Đã xóa công việc", Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xóa công việc?", fontWeight = FontWeight.Bold) },
            text = { Text("Công việc này cùng toàn bộ việc nhỏ bên trong sẽ bị xóa vĩnh viễn.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteTask(coroutineScope)
                    }
                ) {
                    Text("Xóa", color = Color(0xFFDC2626), fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "< Trang chủ",
                color = colorScheme.primary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(vertical = 6.dp)
            )
            Text(
                text = "Sửa",
                color = colorScheme.primary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable {
                        Toast.makeText(context, "Chức năng sửa nhanh đang đồng bộ qua API", Toast.LENGTH_SHORT).show()
                    }
                    .padding(vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colorScheme.primary)
            }
        } else if (uiState.errorMessage != null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
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
                            onClick = { viewModel.loadTask(taskId, coroutineScope) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                        ) {
                            Text("Thử lại")
                        }
                    }
                }
            }
        } else {
            val task = uiState.task ?: return
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
            val priorityText = when (task.priority) {
                TaskPriority.HIGH -> "Ưu tiên cao"
                TaskPriority.MEDIUM -> "Ưu tiên vừa"
                TaskPriority.LOW -> "Ưu tiên thấp"
            }
            val progress = SubtaskListHelper.progressFraction(task.subtasks)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = badgeBg
                    ) {
                        Text(
                            text = task.category.displayName,
                            color = badgeText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = priorityText,
                            color = colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = task.title,
                    fontSize = 24.sp,
                    lineHeight = 31.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = colorScheme.surface,
                    shadowElevation = if (isDark) 0.dp else 1.dp
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        DetailInfoRow(
                            iconRes = R.drawable.ic_calendar,
                            label = "Hạn chót",
                            value = TaskListFilterEngine.formatDueLabel(task.date, task.time)
                        )
                        HorizontalDivider(
                            color = colorScheme.outlineVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        DetailInfoRow(
                            iconRes = R.drawable.ic_bell,
                            label = "Nhắc nhở",
                            value = if (task.remind) "Trước 15 phút" else "Không nhắc"
                        )
                    }
                }

                if (task.note.isNotBlank()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Ghi chú",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onSurfaceVariant
                        )
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = colorScheme.surface,
                            shadowElevation = if (isDark) 0.dp else 1.dp
                        ) {
                            Text(
                                text = task.note,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = colorScheme.onSurface,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                if (uiState.actionMessage != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = if (uiState.isActionError) Color(0xFFFEE2E2) else Color(0xFFDCFCE7)
                    ) {
                        Text(
                            text = uiState.actionMessage.orEmpty(),
                            color = if (uiState.isActionError) Color(0xFFB91C1C) else Color(0xFF166534),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Việc nhỏ",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${task.completedSubtaskCount()}/${task.subtasks.size}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(colorScheme.primary)
                        )
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = colorScheme.surface,
                        shadowElevation = if (isDark) 0.dp else 1.dp
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            task.subtasks.forEachIndexed { index, sub ->
                                SubtaskRow(
                                    subTask = sub,
                                    enabled = !uiState.isMutating,
                                    onToggle = { viewModel.toggleSubtask(index, coroutineScope) },
                                    onDelete = { viewModel.deleteSubtask(index, coroutineScope) }
                                )
                                HorizontalDivider(
                                    color = colorScheme.outlineVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(horizontal = 14.dp)
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "+",
                                    fontSize = 18.sp,
                                    color = colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                                Box(modifier = Modifier.weight(1f)) {
                                    if (uiState.newSubtaskTitle.isEmpty()) {
                                        Text(
                                            text = "Thêm việc nhỏ",
                                            fontSize = 14.sp,
                                            color = colorScheme.onSurfaceVariant
                                        )
                                    }
                                    BasicTextField(
                                        value = uiState.newSubtaskTitle,
                                        onValueChange = { viewModel.onNewSubtaskTitleChange(it) },
                                        singleLine = true,
                                        cursorBrush = SolidColor(colorScheme.primary),
                                        textStyle = TextStyle(fontSize = 14.sp, color = colorScheme.onSurface),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Text(
                                    text = "Thêm",
                                    color = if (uiState.newSubtaskTitle.isNotBlank() && !uiState.isMutating) {
                                        colorScheme.primary
                                    } else {
                                        colorScheme.onSurfaceVariant
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.clickable(
                                        enabled = uiState.newSubtaskTitle.isNotBlank() && !uiState.isMutating
                                    ) {
                                        viewModel.addSubtask(coroutineScope)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.toggleTaskDone(coroutineScope) },
                enabled = !uiState.isMutating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                if (uiState.isMutating) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = if (task.done) "Đánh dấu chưa xong" else "Đánh dấu hoàn thành",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            TextButton(
                onClick = { showDeleteDialog = true },
                enabled = !uiState.isMutating,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Xóa công việc",
                    color = Color(0xFFEF4444),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun DetailInfoRow(iconRes: Int, label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                colorFilter = ColorFilter.tint(colorScheme.onSurfaceVariant),
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                fontSize = 14.sp,
                color = colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface
        )
    }
}

@Composable
private fun SubtaskRow(
    subTask: SubTask,
    enabled: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onToggle() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (subTask.done) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.6.dp, colorScheme.outlineVariant, RoundedCornerShape(6.dp))
            )
        }

        Text(
            text = subTask.title,
            fontSize = 14.sp,
            color = if (subTask.done) colorScheme.onSurfaceVariant else colorScheme.onSurface,
            textDecoration = if (subTask.done) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.weight(1f)
        )

        Image(
            painter = painterResource(id = R.drawable.ic_delete),
            contentDescription = "Xóa việc nhỏ",
            colorFilter = ColorFilter.tint(colorScheme.onSurfaceVariant),
            modifier = Modifier
                .size(18.dp)
                .clickable(enabled = enabled) { onDelete() }
        )
    }
}
