package com.uth.uthtodo.viewmodel

import com.uth.uthtodo.data.ApiResult
import com.uth.uthtodo.data.TaskApiClient
import com.uth.uthtodo.model.SubTask
import com.uth.uthtodo.model.TaskItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Trạng thái giao diện cho màn hình Chi tiết công việc (UI + API)
data class TaskDetailUiState(
    val isLoading: Boolean = false,
    val isMutating: Boolean = false,
    val task: TaskItem? = null,
    val newSubtaskTitle: String = "",
    val errorMessage: String? = null,
    val actionMessage: String? = null,
    val isActionError: Boolean = false,
    val isDeleted: Boolean = false
)

// Bộ xử lý OOP thuần cho danh sách việc nhỏ để dễ kiểm thử đơn vị
object SubtaskListHelper {

    // Đảo trạng thái hoàn thành của việc nhỏ tại vị trí index
    fun toggleAt(subtasks: List<SubTask>, index: Int): List<SubTask> =
        subtasks.mapIndexed { i, item ->
            if (i == index) item.copy(done = !item.done) else item
        }

    // Xóa việc nhỏ tại vị trí index
    fun removeAt(subtasks: List<SubTask>, index: Int): List<SubTask> =
        subtasks.filterIndexed { i, _ -> i != index }

    // Thêm việc nhỏ mới vào cuối danh sách nếu nội dung không trống
    fun append(subtasks: List<SubTask>, title: String): List<SubTask> {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return subtasks
        return subtasks + SubTask(title = trimmed, done = false)
    }

    // Tính tỷ lệ tiến độ việc nhỏ trong khoảng 0f..1f
    fun progressFraction(subtasks: List<SubTask>): Float {
        if (subtasks.isEmpty()) return 0f
        return subtasks.count { it.done }.toFloat() / subtasks.size.toFloat()
    }
}

// ViewModel MVVM điều phối GET, PATCH và DELETE cho màn hình Chi tiết công việc
class TaskDetailViewModel(
    private val apiClient: TaskApiClient = TaskApiClient()
) {
    private val _uiState = MutableStateFlow(TaskDetailUiState(isLoading = true))
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    // Tải chi tiết công việc theo mã (GET /tasks/{id})
    fun loadTask(taskId: Long, scope: CoroutineScope) {
        _uiState.update {
            it.copy(isLoading = true, errorMessage = null, actionMessage = null)
        }
        scope.launch {
            when (val result = apiClient.getTaskById(taskId)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, task = result.data, errorMessage = null)
                    }
                }

                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    // Cập nhật nội dung ô nhập việc nhỏ mới
    fun onNewSubtaskTitleChange(value: String) {
        _uiState.update { it.copy(newSubtaskTitle = value) }
    }

    // Đánh dấu hoàn thành hoặc bỏ hoàn thành công việc (PATCH /tasks/{id})
    fun toggleTaskDone(scope: CoroutineScope) {
        val currentTask = _uiState.value.task ?: return
        if (_uiState.value.isMutating) return
        _uiState.update { it.copy(isMutating = true, actionMessage = null) }
        scope.launch {
            val targetDone = !currentTask.done
            when (val result = apiClient.patchTaskDone(currentTask.id, targetDone)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isMutating = false,
                            task = currentTask.copy(done = targetDone),
                            actionMessage = if (targetDone) {
                                "Đã đánh dấu hoàn thành công việc."
                            } else {
                                "Đã chuyển công việc về trạng thái chưa xong."
                            },
                            isActionError = false
                        )
                    }
                }

                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isMutating = false,
                            actionMessage = result.message,
                            isActionError = true
                        )
                    }
                }
            }
        }
    }

    // Tick hoàn thành một việc nhỏ và gửi mảng subtasks lên server (PATCH /tasks/{id})
    fun toggleSubtask(index: Int, scope: CoroutineScope) {
        val currentTask = _uiState.value.task ?: return
        val updatedSubtasks = SubtaskListHelper.toggleAt(currentTask.subtasks, index)
        submitSubtasks(currentTask, updatedSubtasks, clearInput = false, scope = scope)
    }

    // Xóa một việc nhỏ và đồng bộ mảng subtasks lên server (PATCH /tasks/{id})
    fun deleteSubtask(index: Int, scope: CoroutineScope) {
        val currentTask = _uiState.value.task ?: return
        val updatedSubtasks = SubtaskListHelper.removeAt(currentTask.subtasks, index)
        submitSubtasks(currentTask, updatedSubtasks, clearInput = false, scope = scope)
    }

    // Thêm việc nhỏ mới từ ô nhập và đồng bộ lên server (PATCH /tasks/{id})
    fun addSubtask(scope: CoroutineScope) {
        val currentTask = _uiState.value.task ?: return
        val trimmed = _uiState.value.newSubtaskTitle.trim()
        if (trimmed.isEmpty()) return
        val updatedSubtasks = SubtaskListHelper.append(currentTask.subtasks, trimmed)
        submitSubtasks(currentTask, updatedSubtasks, clearInput = true, scope = scope)
    }

    // Xóa công việc hiện tại (DELETE /tasks/{id})
    fun deleteTask(scope: CoroutineScope) {
        val currentTask = _uiState.value.task ?: return
        if (_uiState.value.isMutating) return
        _uiState.update { it.copy(isMutating = true, actionMessage = null) }
        scope.launch {
            when (val result = apiClient.deleteTask(currentTask.id)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(isMutating = false, isDeleted = true)
                    }
                }

                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isMutating = false,
                            actionMessage = result.message,
                            isActionError = true
                        )
                    }
                }
            }
        }
    }

    private fun submitSubtasks(
        currentTask: TaskItem,
        updatedSubtasks: List<SubTask>,
        clearInput: Boolean,
        scope: CoroutineScope
    ) {
        if (_uiState.value.isMutating) return
        _uiState.update { it.copy(isMutating = true, actionMessage = null) }
        scope.launch {
            when (val result = apiClient.patchSubtasks(currentTask.id, updatedSubtasks)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isMutating = false,
                            task = currentTask.copy(subtasks = updatedSubtasks),
                            newSubtaskTitle = if (clearInput) "" else it.newSubtaskTitle,
                            actionMessage = null,
                            isActionError = false
                        )
                    }
                }

                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isMutating = false,
                            actionMessage = result.message,
                            isActionError = true
                        )
                    }
                }
            }
        }
    }
}
