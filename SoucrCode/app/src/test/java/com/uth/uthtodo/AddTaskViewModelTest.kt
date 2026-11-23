package com.uth.uthtodo

import com.uth.uthtodo.model.TaskCategory
import com.uth.uthtodo.model.TaskPriority
import com.uth.uthtodo.viewmodel.AddTaskViewModel
import com.uth.uthtodo.viewmodel.TaskDatePreset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Kiểm thử đơn vị cho AddTaskViewModel (MVVM) và CreateTaskRequest của Bài 9
class AddTaskViewModelTest {

    @Test
    fun addTaskViewModel_updatesFieldsAndBuildsRequest() {
        val viewModel = AddTaskViewModel()

        assertFalse(viewModel.uiState.value.canSave)

        viewModel.onTitleChange("Ôn chương 4 – Giải thuật")
        viewModel.onNoteChange("Xem lại quy hoạch động")
        viewModel.onCategorySelect(TaskCategory.STUDY)
        viewModel.onDatePresetSelect(TaskDatePreset.TOMORROW)
        viewModel.onPrioritySelect(TaskPriority.HIGH)
        viewModel.onRemindToggle(false)

        val state = viewModel.uiState.value
        assertTrue(state.canSave)

        val request = state.toRequest()
        assertEquals("Ôn chương 4 – Giải thuật", request.title)
        assertEquals("Xem lại quy hoạch động", request.note)
        assertEquals(TaskCategory.STUDY, request.category)
        assertEquals("2026-10-21", request.date)
        assertEquals(TaskPriority.HIGH, request.priority)
        assertFalse(request.remind)
    }

    @Test
    fun addTaskViewModel_cyclesTimePresets() {
        val viewModel = AddTaskViewModel()
        assertEquals("19:00", viewModel.uiState.value.time)
        viewModel.onCycleTime()
        assertEquals("20:30", viewModel.uiState.value.time)
    }
}
