package com.uth.uthtodo.viewmodel

import com.uth.uthtodo.data.ApiResult
import com.uth.uthtodo.data.CreateTaskRequest
import com.uth.uthtodo.data.TaskApiClient
import com.uth.uthtodo.model.TaskCategory
import com.uth.uthtodo.model.TaskItem
import com.uth.uthtodo.model.TaskPriority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Tùy chọn ngày nhanh trên form Thêm mới công việc
enum class TaskDatePreset(val label: String, val apiDate: String) {
    TODAY("Hôm nay", "2026-10-20"),
    TOMORROW("Ngày mai", "2026-10-21"),
    THIS_WEEK("Tuần này", "2026-10-25")
}

// Trạng thái giao diện cho màn hình Thêm mới công việc theo kiến trúc MVVM
data class AddTaskUiState(
    val title: String = "",
    val note: String = "",
    val category: TaskCategory = TaskCategory.STUDY,
    val datePreset: TaskDatePreset = TaskDatePreset.TODAY,
    val time: String = "19:00",
    val remind: Boolean = true,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val createdTask: TaskItem? = null
) {
    // Chỉ cho phép lưu khi tên công việc không để trống và không đang gửi request
    val canSave: Boolean get() = title.trim().isNotEmpty() && !isSubmitting

    // Chuyển trạng thái form sang mô hình OOP CreateTaskRequest gửi API
    fun toRequest(): CreateTaskRequest = CreateTaskRequest(
        title = title.trim(),
        note = note.trim(),
        category = category,
        date = datePreset.apiDate,
        time = time,
        priority = priority,
        remind = remind
    )
}

// ViewModel MVVM quản lý trạng thái form và gửi POST /tasks tạo công việc mới
class AddTaskViewModel(
    private val apiClient: TaskApiClient = TaskApiClient()
) {
    private val availableTimes = listOf("08:00", "14:00", "17:30", "19:00", "20:30", "21:00")

    private val _uiState = MutableStateFlow(AddTaskUiState())
    val uiState: StateFlow<AddTaskUiState> = _uiState.asStateFlow()

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value, errorMessage = null) }
    }

    fun onNoteChange(value: String) {
        _uiState.update { it.copy(note = value) }
    }

    fun onCategorySelect(category: TaskCategory) {
        _uiState.update { it.copy(category = category) }
    }

    fun onDatePresetSelect(preset: TaskDatePreset) {
        _uiState.update { it.copy(datePreset = preset) }
    }

    // Chuyển nhanh qua các khung giờ gợi ý trong ngày
    fun onCycleTime() {
        _uiState.update { state ->
            val currentIdx = availableTimes.indexOf(state.time)
            val nextTime = availableTimes[(currentIdx + 1) % availableTimes.size]
            state.copy(time = nextTime)
        }
    }

    fun onRemindToggle(enabled: Boolean) {
        _uiState.update { it.copy(remind = enabled) }
    }

    fun onPrioritySelect(priority: TaskPriority) {
        _uiState.update { it.copy(priority = priority) }
    }

    // Gửi POST /tasks, giữ nguyên form nếu gặp lỗi để người dùng thử lại
    fun saveTask(scope: CoroutineScope) {
        val current = _uiState.value
        if (current.title.trim().isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Tên công việc không được để trống.") }
            return
        }
        if (current.isSubmitting) return

        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        scope.launch {
            when (val result = apiClient.createTask(current.toRequest())) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(isSubmitting = false, createdTask = result.data, errorMessage = null)
                    }
                }

                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(isSubmitting = false, errorMessage = result.message)
                    }
                }
            }
        }
    }
}
