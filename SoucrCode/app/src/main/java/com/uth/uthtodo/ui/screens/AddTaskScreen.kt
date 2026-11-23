package com.uth.uthtodo.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uth.uthtodo.R
import com.uth.uthtodo.model.AppPermissionType
import com.uth.uthtodo.model.TaskCategory
import com.uth.uthtodo.model.TaskPriority
import com.uth.uthtodo.ui.components.ContextualPermissionDialog
import com.uth.uthtodo.ui.components.FlexibleTimePickerDialog
import com.uth.uthtodo.ui.components.isAppPermissionGranted
import com.uth.uthtodo.ui.theme.LocalIsDarkTheme
import com.uth.uthtodo.viewmodel.AddTaskViewModel
import com.uth.uthtodo.viewmodel.TaskDatePreset

// Màn hình Thêm mới công việc bám sát thiết kế Figma và gửi POST /tasks
@Composable
fun AddTaskScreen(
    onCancel: () -> Unit,
    onTaskCreated: (Long) -> Unit
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkTheme.current
    val colorScheme = MaterialTheme.colorScheme
    val viewModel = remember { AddTaskViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    var showNotificationPrompt by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    if (showTimePicker) {
        val (initHour, initMinute) = AddTaskViewModel.parseTime(uiState.time)
        FlexibleTimePickerDialog(
            initialHour = initHour,
            initialMinute = initMinute,
            onDismiss = { showTimePicker = false },
            onConfirm = { hour, minute ->
                viewModel.onTimeSelect(hour, minute)
                showTimePicker = false
            }
        )
    }

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

    LaunchedEffect(uiState.createdTask) {
        val created = uiState.createdTask ?: return@LaunchedEffect
        Toast.makeText(context, "Đã tạo công việc mới", Toast.LENGTH_SHORT).show()
        onTaskCreated(created.id)
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
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Hủy",
                color = colorScheme.primary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { onCancel() }
                    .padding(vertical = 6.dp)
            )
            Text(
                text = "Việc mới",
                color = colorScheme.onBackground,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
            Text(
                text = "Lưu",
                color = if (uiState.canSave) colorScheme.primary else colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable(enabled = uiState.canSave) { viewModel.saveTask(coroutineScope) }
                    .padding(vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = colorScheme.surface,
                shadowElevation = if (isDark) 0.dp else 1.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Tên công việc",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (uiState.title.isEmpty()) {
                                Text(
                                    text = "Ví dụ: Ôn chương 4 – Giải thuật",
                                    fontSize = 16.sp,
                                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                                )
                            }
                            BasicTextField(
                                value = uiState.title,
                                onValueChange = { viewModel.onTitleChange(it) },
                                singleLine = true,
                                cursorBrush = SolidColor(colorScheme.primary),
                                textStyle = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colorScheme.onSurface
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    HorizontalDivider(
                        color = colorScheme.outlineVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Ghi chú",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                        ) {
                            if (uiState.note.isEmpty()) {
                                Text(
                                    text = "Thêm ghi chú (không bắt buộc)",
                                    fontSize = 14.sp,
                                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                                )
                            }
                            BasicTextField(
                                value = uiState.note,
                                onValueChange = { viewModel.onNoteChange(it) },
                                cursorBrush = SolidColor(colorScheme.primary),
                                textStyle = TextStyle(fontSize = 14.sp, color = colorScheme.onSurface),
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            if (uiState.errorMessage != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEE2E2)
                ) {
                    Text(
                        text = uiState.errorMessage.orEmpty(),
                        color = Color(0xFFB91C1C),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Nhóm",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TaskCategory.entries.forEach { category ->
                        val selected = uiState.category == category
                        SelectableOptionChip(
                            modifier = Modifier.weight(1f),
                            label = category.displayName,
                            selected = selected,
                            onClick = { viewModel.onCategorySelect(category) }
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Ngày",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskDatePreset.entries.forEach { preset ->
                        val selected = uiState.datePreset == preset
                        SelectableOptionChip(
                            modifier = Modifier.weight(1f),
                            label = preset.label,
                            selected = selected,
                            onClick = { viewModel.onDatePresetSelect(preset) }
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = colorScheme.surface,
                shadowElevation = if (isDark) 0.dp else 1.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTimePicker = true }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_clock),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(colorScheme.onSurfaceVariant),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Giờ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = uiState.time,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colorScheme.onSurface
                                )
                                Image(
                                    painter = painterResource(id = R.drawable.ic_clock),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(colorScheme.onSurfaceVariant),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        color = colorScheme.outlineVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_bell),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(colorScheme.onSurfaceVariant),
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = "Nhắc tôi",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colorScheme.onSurface
                                )
                                Text(
                                    text = "Trước 15 phút",
                                    fontSize = 12.sp,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = uiState.remind,
                            onCheckedChange = { enabled ->
                                viewModel.onRemindToggle(enabled)
                                if (enabled && !isAppPermissionGranted(context, AppPermissionType.NOTIFICATION)) {
                                    showNotificationPrompt = true
                                }
                            },
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
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Mức ưu tiên",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurfaceVariant
                )
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TaskPriority.entries.forEach { priority ->
                            val selected = uiState.priority == priority
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.onPrioritySelect(priority) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) colorScheme.surface else Color.Transparent,
                                shadowElevation = if (selected && !isDark) 1.dp else 0.dp
                            ) {
                                Text(
                                    text = priority.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                                    color = if (selected) colorScheme.onSurface else colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = { viewModel.saveTask(coroutineScope) },
            enabled = uiState.canSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = Color.White,
                disabledContainerColor = colorScheme.surfaceVariant,
                disabledContentColor = colorScheme.onSurfaceVariant
            )
        ) {
            if (uiState.isSubmitting) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = "Lưu công việc",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SelectableOptionChip(
    modifier: Modifier = Modifier,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = if (selected) colorScheme.primaryContainer else colorScheme.surface,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) colorScheme.primary else colorScheme.outlineVariant
        )
    ) {
        Text(
            text = label,
            color = if (selected) colorScheme.onPrimaryContainer else colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp)
        )
    }
}
