package com.uth.uthtodo

import android.Manifest
import com.uth.uthtodo.model.AppPermissionType
import com.uth.uthtodo.model.PermissionGrantStatus
import com.uth.uthtodo.model.PermissionsUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Kiểm thử đơn vị cho mô hình OOP xử lý quyền theo ngữ cảnh của Bài 10
class PermissionsModelsTest {

    @Test
    fun permissionsUiState_tracksContextualPermissionsCorrectly() {
        val initial = PermissionsUiState()
        assertEquals(3, initial.items.size)
        assertEquals(0, initial.grantedCount)

        val grantedNotification = initial.updateStatus(
            AppPermissionType.NOTIFICATION,
            PermissionGrantStatus.GRANTED,
            "Đã cấp quyền Thông báo"
        )
        assertEquals(1, grantedNotification.grantedCount)
        assertEquals("Đã cấp quyền Thông báo", grantedNotification.statusMessage)

        val skippedCamera = grantedNotification.updateStatus(
            AppPermissionType.CAMERA,
            PermissionGrantStatus.SKIPPED
        )
        assertEquals(1, skippedCamera.grantedCount)
        assertEquals(
            PermissionGrantStatus.SKIPPED,
            skippedCamera.items.first { it.type == AppPermissionType.CAMERA }.status
        )
    }

    @Test
    fun permissionRuntimeMapping_matchesAndroidSdkLevel() {
        assertFalse(AppPermissionType.NOTIFICATION.requiresRuntimePrompt(sdkInt = 32))
        assertTrue(AppPermissionType.NOTIFICATION.requiresRuntimePrompt(sdkInt = 33))
        assertEquals(
            Manifest.permission.READ_MEDIA_IMAGES,
            AppPermissionType.MEDIA_IMAGES.runtimePermission(sdkInt = 33)
        )
        assertEquals(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            AppPermissionType.MEDIA_IMAGES.runtimePermission(sdkInt = 32)
        )
    }
}
