package com.uth.uthtodo

import com.uth.uthtodo.model.SubTask
import com.uth.uthtodo.navigation.AppRoute
import com.uth.uthtodo.viewmodel.SubtaskListHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Kiểm thử đơn vị cho bộ xử lý việc nhỏ và tuyến điều hướng Chi tiết công việc (Bài 8)
class TaskDetailViewModelTest {

    @Test
    fun subtaskListHelper_togglesAppendsAndRemovesSubtasks() {
        val initial = listOf(
            SubTask("Đọc lại slide bài giảng", true),
            SubTask("Làm 10 bài tập cuối chương", false)
        )

        assertEquals(0.5f, SubtaskListHelper.progressFraction(initial), 0.001f)

        val toggled = SubtaskListHelper.toggleAt(initial, 1)
        assertTrue(toggled[1].done)
        assertEquals(1.0f, SubtaskListHelper.progressFraction(toggled), 0.001f)

        val appended = SubtaskListHelper.append(toggled, "  Tóm tắt kiến thức  ")
        assertEquals(3, appended.size)
        assertEquals("Tóm tắt kiến thức", appended[2].title)
        assertFalse(appended[2].done)

        val removed = SubtaskListHelper.removeAt(appended, 0)
        assertEquals(2, removed.size)
    }

    @Test
    fun taskDetailRoute_buildsPathWithId() {
        assertEquals("task_detail/2", AppRoute.TaskDetail.createRoute(2L))
    }
}
