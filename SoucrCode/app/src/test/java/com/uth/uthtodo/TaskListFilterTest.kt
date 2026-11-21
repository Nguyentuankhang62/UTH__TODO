package com.uth.uthtodo

import com.uth.uthtodo.model.TaskCategory
import com.uth.uthtodo.model.TaskItem
import com.uth.uthtodo.model.TaskPriority
import com.uth.uthtodo.viewmodel.TaskListFilter
import com.uth.uthtodo.viewmodel.TaskListFilterEngine
import org.junit.Assert.assertEquals
import org.junit.Test

// Kiểm thử đơn vị cho bộ lọc, tìm kiếm và định dạng thời hạn của Bài 6 Danh sách
class TaskListFilterTest {

    private val sampleTasks = listOf(
        TaskItem(4, "Tập thể dục 30 phút", "", TaskCategory.LIFE, "2026-10-20", "06:30", TaskPriority.MEDIUM, true, true),
        TaskItem(1, "Nộp bài tập Lập trình di động", "", TaskCategory.STUDY, "2026-10-20", "09:00", TaskPriority.HIGH, true, true),
        TaskItem(2, "Ôn chương 3 – Cấu trúc dữ liệu", "", TaskCategory.STUDY, "2026-10-20", "14:00", TaskPriority.MEDIUM, true, false),
        TaskItem(3, "Đi chợ mua đồ cho tuần", "", TaskCategory.LIFE, "2026-10-20", "17:30", TaskPriority.LOW, false, false),
        TaskItem(6, "Gọi điện về nhà", "", TaskCategory.LIFE, "2026-10-21", "21:00", TaskPriority.LOW, true, false),
        TaskItem(5, "Họp nhóm đồ án", "", TaskCategory.STUDY, "2026-10-25", "20:00", TaskPriority.HIGH, true, false)
    )

    @Test
    fun filterAndGroup_splitsTodoAndDoneByCategoryAndQuery() {
        val (allTodo, allDone) = TaskListFilterEngine.filterAndGroup(sampleTasks, "", TaskListFilter.ALL)
        assertEquals(4, allTodo.size)
        assertEquals(2, allDone.size)

        val (studyTodo, studyDone) = TaskListFilterEngine.filterAndGroup(sampleTasks, "", TaskListFilter.STUDY)
        assertEquals(2, studyTodo.size)
        assertEquals(1, studyDone.size)

        val (searchTodo, searchDone) = TaskListFilterEngine.filterAndGroup(sampleTasks, "đồ án", TaskListFilter.ALL)
        assertEquals(1, searchTodo.size)
        assertEquals(0, searchDone.size)
    }

    @Test
    fun formatDueLabel_formatsTodayTomorrowAndThisWeek() {
        assertEquals("Hôm nay, 14:00", TaskListFilterEngine.formatDueLabel("2026-10-20", "14:00"))
        assertEquals("Ngày mai, 21:00", TaskListFilterEngine.formatDueLabel("2026-10-21", "21:00"))
        assertEquals("Tuần này, 20:00", TaskListFilterEngine.formatDueLabel("2026-10-25", "20:00"))
    }
}
