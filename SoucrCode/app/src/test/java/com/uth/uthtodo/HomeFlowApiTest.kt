package com.uth.uthtodo

import com.uth.uthtodo.model.TaskCategory
import com.uth.uthtodo.model.TaskItem
import com.uth.uthtodo.model.TaskPriority
import com.uth.uthtodo.viewmodel.HomeStatsCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

// Kiểm thử đơn vị cho bộ tính thống kê Trang chủ từ dữ liệu API Bài 5
class HomeFlowApiTest {

    @Test
    fun homeStatsCalculator_computesTodayProgressAndRemainingCounts() {
        val sampleTasks = listOf(
            TaskItem(4, "Tập thể dục 30 phút", "", TaskCategory.LIFE, "2026-10-20", "06:30", TaskPriority.MEDIUM, true, true),
            TaskItem(1, "Nộp bài tập Lập trình di động", "", TaskCategory.STUDY, "2026-10-20", "09:00", TaskPriority.HIGH, true, true),
            TaskItem(2, "Ôn chương 3", "", TaskCategory.STUDY, "2026-10-20", "14:00", TaskPriority.MEDIUM, true, false),
            TaskItem(3, "Đi chợ mua đồ", "", TaskCategory.LIFE, "2026-10-20", "17:30", TaskPriority.LOW, false, false),
            TaskItem(6, "Gọi điện về nhà", "", TaskCategory.LIFE, "2026-10-21", "21:00", TaskPriority.LOW, true, false),
            TaskItem(5, "Họp nhóm đồ án", "", TaskCategory.STUDY, "2026-10-25", "20:00", TaskPriority.HIGH, true, false)
        )

        val (todayTasks, stats) = HomeStatsCalculator.buildStats(sampleTasks)

        assertEquals(4, todayTasks.size)
        assertEquals(2, stats.doneToday)
        assertEquals(4, stats.totalToday)
        assertEquals(50, stats.progressPercent)
        assertEquals(2, stats.studyRemaining)
        assertEquals(2, stats.lifeRemaining)
        assertEquals("Đã hơn nửa chặng, cố lên!", stats.encouragementText)
    }

    @Test
    fun homeStatsCalculator_handlesEmptyListSafely() {
        val (todayTasks, stats) = HomeStatsCalculator.buildStats(emptyList())

        assertEquals(0, todayTasks.size)
        assertEquals(0, stats.progressPercent)
        assertEquals("Hôm nay chưa có công việc nào.", stats.encouragementText)
    }
}
