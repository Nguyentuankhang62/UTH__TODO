package com.uth.uthtodo

import com.uth.uthtodo.model.OnboardingIllustration
import com.uth.uthtodo.model.OnboardingRepository
import com.uth.uthtodo.model.SubTask
import com.uth.uthtodo.model.TaskCategory
import com.uth.uthtodo.model.TaskItem
import com.uth.uthtodo.model.TaskPriority
import com.uth.uthtodo.model.UserProfile
import com.uth.uthtodo.navigation.AppRoute
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// Kiểm thử đơn vị cho mô hình OOP và dữ liệu Onboarding của Bài 1
class OnboardingAndOopTest {

    @Test
    fun onboardingRepository_returnsThreeOrderedSteps() {
        val repository = OnboardingRepository()
        val steps = repository.getSteps()

        assertEquals(3, steps.size)
        assertEquals(OnboardingIllustration.TASK_LIST, steps[0].illustration)
        assertEquals(OnboardingIllustration.CATEGORY_GROUPS, steps[1].illustration)
        assertEquals(OnboardingIllustration.REMINDER_BELL, steps[2].illustration)
        assertEquals("Tiếp tục", steps[0].primaryButtonText)
        assertEquals("Bắt đầu ngay", steps[2].primaryButtonText)
    }

    @Test
    fun taskItem_computesSubtaskProgressCorrectly() {
        val task = TaskItem(
            id = 2,
            title = "Ôn chương 3",
            category = TaskCategory.fromKey("study"),
            date = "2026-10-20",
            time = "14:00",
            priority = TaskPriority.fromKey("high"),
            subtasks = listOf(
                SubTask("Đọc slide", true),
                SubTask("Làm bài tập", false)
            )
        )

        assertEquals(TaskCategory.STUDY, task.category)
        assertEquals(TaskPriority.HIGH, task.priority)
        assertEquals(1, task.completedSubtaskCount())
        assertEquals("1/2", task.subtaskProgressText())
    }

    @Test
    fun userProfile_extractsInitialsAndRoutesAreDistinct() {
        val user = UserProfile(uid = "u1", displayName = "Tuấn Khang", email = "tuankhang@ut.edu.vn")
        assertEquals("TK", user.initials())

        val routes = setOf(AppRoute.Splash.route, AppRoute.Onboarding.route, AppRoute.HomeFlow.route)
        assertTrue(routes.size == 3)
    }
}
