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
import com.uth.uthtodo.ui.components.isAppPermissionGranted
import com.uth.uthtodo.ui.theme.BorderLight
import com.uth.uthtodo.ui.theme.PrimaryBlue
import com.uth.uthtodo.ui.theme.SoftBlueCard
import com.uth.uthtodo.ui.theme.SurfaceMuted
import com.uth.uthtodo.ui.theme.TextPrimary
import com.uth.uthtodo.ui.theme.TextSecondary
import com.uth.uthtodo.viewmodel.AddTaskViewModel
import com.uth.uthtodo.viewmodel.TaskDatePreset

// Màn hình Thêm mới công việc theo mô hình MVVM và gửi POST /tasks
@Composable
fun AddTaskScreen(
    onCancel: () -> Unit,
    onTaskCreated: (Long) -> Unit
) {
    val context = LocalContext.current
    val viewModel = remember { AddTaskViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    var activePermissionPrompt by remember { mutableStateOf<AppPermissionType?>(null) }
    var pendingPermissionType by remember { mutableStateOf<AppPermissionType?>(null) }
    var attachmentStatus by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        val target = pendingPermissionType ?: return@rememberLauncherForActivityResult
        pendingPermissionType = null
        if (granted) {
            attachmentStatus = when (target) {
                AppPermissionType.CAMERA -> "Đã cấp quyền Camera — sẵn sàng chụp ảnh ghi chú."
                AppPermissionType.MEDIA_IMAGES -> "Đã cấp quyền Thư viện ảnh — sẵn sàng chọn tài liệu."
                AppPermissionType.NOTIFICATION -> "Đã bật quyền Thông báo nhắc việc trước 15 phút."
            }
        } else {
            Toast.makeText(
                context,
                "Bạn có thể bật lại quyền ${target.title} trong Cài đặt ứng dụng.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val currentPrompt = activePermissionPrompt
    if (currentPrompt != null) {
        ContextualPermissionDialog(
            permissionType = currentPrompt,
            onAllow = {
                activePermissionPrompt = null
                if (!currentPrompt.requiresRuntimePrompt()) {
                    attachmentStatus = "Đã bật quyền ${currentPrompt.title}."
                } else {
                    pendingPermissionType = currentPrompt
                    permissionLauncher.launch(currentPrompt.runtimePermission())
                }
            },
            onSkip = { activePermissionPrompt = null }
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
            .background(SurfaceMuted)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Hủy",
                color = PrimaryBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { onCancel() }
                    .padding(vertical = 6.dp)
            )
            Text(
                text = "Việc mới",
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
            Text(
                text = "Lưu",
                color = if (uiState.canSave) PrimaryBlue else TextSecondary.copy(alpha = 0.6f),
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
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Tên công việc",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (uiState.title.isEmpty()) {
                                Text(
                                    text = "Ví dụ: Ôn chương 4 – Giải thuật",
                                    fontSize = 16.sp,
                                    color = TextSecondary.copy(alpha = 0.65f)
                                )
                            }
                            BasicTextField(
                                value = uiState.title,
                                onValueChange = { viewModel.onTitleChange(it) },
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    HorizontalDivider(
                        color = BorderLight.copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Ghi chú",
                            fontSize = 12.sp,
                            color = TextSecondary
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
                                    color = TextSecondary.copy(alpha = 0.65f)
                                )
                            }
                            BasicTextField(
                                value = uiState.note,
                                onValueChange = { viewModel.onNoteChange(it) },
                                textStyle = TextStyle(fontSize = 14.sp, color = TextPrimary),
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
                    color = TextSecondary
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
                    color = TextSecondary
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
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onCycleTime() }
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
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Giờ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceMuted
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
                                    color = TextPrimary
                                )
                                Image(
                                    painter = painterResource(id = R.drawable.ic_clock),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        color = BorderLight.copy(alpha = 0.7f),
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
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = "Nhắc tôi",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Trước 15 phút",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = uiState.remind,
                            onCheckedChange = { enabled ->
                                viewModel.onRemindToggle(enabled)
                                if (enabled && !isAppPermissionGranted(context, AppPermissionType.NOTIFICATION)) {
                                    activePermissionPrompt = AppPermissionType.NOTIFICATION
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF22C55E),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFCBD5E1)
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
                    color = TextSecondary
                )
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFEDF0F5)
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
                                color = if (selected) Color.White else Color.Transparent,
                                shadowElevation = if (selected) 1.dp else 0.dp
                            ) {
                                Text(
                                    text = priority.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                                    color = if (selected) TextPrimary else TextSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Đính kèm tài liệu (tùy chọn)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (isAppPermissionGranted(context, AppPermissionType.CAMERA)) {
                                    attachmentStatus = "Camera đã sẵn sàng chụp ảnh ghi chú."
                                } else {
                                    activePermissionPrompt = AppPermissionType.CAMERA
                                }
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_camera),
                                contentDescription = "Chụp ảnh",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "Chụp ảnh",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (isAppPermissionGranted(context, AppPermissionType.MEDIA_IMAGES)) {
                                    attachmentStatus = "Thư viện ảnh đã sẵn sàng chọn tài liệu."
                                } else {
                                    activePermissionPrompt = AppPermissionType.MEDIA_IMAGES
                                }
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_gallery),
                                contentDescription = "Thư viện ảnh",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "Thư viện ảnh",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                        }
                    }
                }

                if (attachmentStatus != null) {
                    Text(
                        text = attachmentStatus.orEmpty(),
                        fontSize = 12.sp,
                        color = PrimaryBlue
                    )
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
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFE5E7EB),
                disabledContentColor = TextSecondary
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
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = if (selected) SoftBlueCard else Color.White,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) PrimaryBlue else BorderLight
        )
    ) {
        Text(
            text = label,
            color = if (selected) PrimaryBlue else TextPrimary,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp)
        )
    }
}
