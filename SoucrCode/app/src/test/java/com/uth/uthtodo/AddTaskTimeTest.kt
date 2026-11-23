package com.uth.uthtodo

import com.uth.uthtodo.viewmodel.AddTaskViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

// Kiểm thử chọn giờ, định dạng và tách chuỗi thời gian trên form Thêm việc mới
class AddTaskTimeTest {

    @Test
    fun formatTime_padsZeroAndClampsOutOfRangeValues() {
        assertEquals("08:05", AddTaskViewModel.formatTime(8, 5))
        assertEquals("00:00", AddTaskViewModel.formatTime(-3, -1))
        assertEquals("23:59", AddTaskViewModel.formatTime(29, 75))
    }

    @Test
    fun parseTime_handlesValidAndInvalidInputs() {
        assertEquals(14 to 35, AddTaskViewModel.parseTime("14:35"))
        assertEquals(19 to 0, AddTaskViewModel.parseTime("invalid"))
    }

    @Test
    fun onTimeSelect_updatesUiStateTime() {
        val viewModel = AddTaskViewModel()
        viewModel.onTimeSelect(7, 45)
        assertEquals("07:45", viewModel.uiState.value.time)
    }
}
