package com.uth.uthtodo.model

import android.Manifest
import android.os.Build

// 3 quyền runtime thực sự phục vụ cho UTH Todo, xin đúng ngữ cảnh sử dụng
enum class AppPermissionType(
    val title: String,
    val contextLabel: String,
    val description: String,
    val actionLabel: String
) {
    NOTIFICATION(
        title = "Thông báo",
        contextLabel = "Xin khi mới vào ứng dụng hoặc bật Nhắc tôi",
        description = "Bật thông báo để UTH Todo nhắc bạn trước hạn chót 15 phút và cảnh báo công việc sắp đến hạn.",
        actionLabel = "Bật thông báo"
    ),
    CAMERA(
        title = "Camera",
        contextLabel = "Xin khi bấm Chụp ảnh ghi chú trong Việc mới",
        description = "Cấp quyền Camera khi bạn muốn chụp nhanh bảng học, bài tập hoặc đính kèm ảnh vào công việc.",
        actionLabel = "Cho phép Camera"
    ),
    MEDIA_IMAGES(
        title = "Thư viện ảnh",
        contextLabel = "Xin khi bấm Chọn ảnh tài liệu từ thư viện",
        description = "Cho phép truy cập thư viện ảnh khi bạn muốn đính kèm ảnh chụp slide hoặc tài liệu học tập.",
        actionLabel = "Cho phép thư viện ảnh"
    );

    // Lấy đúng tên quyền hệ thống theo phiên bản Android hiện tại
    fun runtimePermission(sdkInt: Int = Build.VERSION.SDK_INT): String = when (this) {
        NOTIFICATION -> Manifest.permission.POST_NOTIFICATIONS
        CAMERA -> Manifest.permission.CAMERA
        MEDIA_IMAGES -> if (sdkInt >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    // Kiểm tra quyền có cần xin ở runtime trên phiên bản Android này không
    fun requiresRuntimePrompt(sdkInt: Int = Build.VERSION.SDK_INT): Boolean =
        !(this == NOTIFICATION && sdkInt < Build.VERSION_CODES.TIRAMISU)
}

// Trạng thái cấp quyền của từng quyền thiết bị
enum class PermissionGrantStatus(val label: String) {
    NOT_REQUESTED("Chưa cấp"),
    GRANTED("Đã cấp quyền"),
    SKIPPED("Để sau"),
    DENIED("Đã từ chối")
}

// Mô hình OOP lưu trạng thái của một quyền cụ thể
data class PermissionItemState(
    val type: AppPermissionType,
    val status: PermissionGrantStatus = PermissionGrantStatus.NOT_REQUESTED
)

// Trạng thái tổng hợp cho màn hình quản lý quyền thiết bị
data class PermissionsUiState(
    val items: List<PermissionItemState> = AppPermissionType.entries.map { PermissionItemState(it) },
    val activeRationale: AppPermissionType? = null,
    val statusMessage: String? = null
) {
    // Đếm số quyền đã được người dùng cấp phép
    val grantedCount: Int get() = items.count { it.status == PermissionGrantStatus.GRANTED }

    // Cập nhật trạng thái mới cho một loại quyền cụ thể
    fun updateStatus(
        type: AppPermissionType,
        newStatus: PermissionGrantStatus,
        message: String? = null
    ): PermissionsUiState = copy(
        items = items.map { item ->
            if (item.type == type) item.copy(status = newStatus) else item
        },
        activeRationale = null,
        statusMessage = message
    )
}
