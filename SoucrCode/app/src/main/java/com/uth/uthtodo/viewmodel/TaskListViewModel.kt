package com.uth.uthtodo.viewmodel

import com.uth.uthtodo.data.ListApiResult
import com.uth.uthtodo.data.TaskListApiClient
import com.uth.uthtodo.model.TaskCategory
import com.uth.uthtodo.model.TaskItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Bộ lọc nhóm công việc trên màn hình Danh sách
enum class TaskListFilter(val label: String) {
    ALL("Tất cả"),
    STUDY("Học tập"),
    LIFE("Cuộc sống")
}

// Trạng thái giao diện cho màn hình Danh sách (UI + API)
data class TaskListUiState(
    val isLoading: Boolean = false,
    val updatingTaskId: Long? = null,
    val allTasks: List<TaskItem> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: TaskListFilter = TaskListFilter.ALL,
    val todoTasks: List<TaskItem> = emptyList(),
    val doneTasks: List<TaskItem> = emptyList(),
    val remainingCount: Int = 0,
    val errorMessage: String? = null,
    val actionMessage: String? = null
)

// Bộ xử lý OOP lọc, tìm kiếm và định dạng thời hạn hiển thị cho màn hình Danh sách
object TaskListFilterEngine {

    // Lọc danh sách theo từ khóa + nhóm và tách thành 2 nhóm Cần làm / Đã xong
    fun filterAndGroup(
        tasks: List<TaskItem>,
        query: String,
        filter: TaskListFilter
    ): Pair<List<TaskItem>, List<TaskItem>> {
        val keyword = query.trim().lowercase()
        val filtered = tasks.filter { task ->
            val matchCategory = when (filter) {
                TaskListFilter.ALL -> true
                TaskListFilter.STUDY -> task.category == TaskCategory.STUDY
                TaskListFilter.LIFE -> task.category == TaskCategory.LIFE
            }
            val matchKeyword = keyword.isEmpty() ||
                task.title.lowercase().contains(keyword) ||
                task.note.lowercase().contains(keyword)
            matchCategory && matchKeyword
        }
        return filtered.filter { !it.done } to filtered.filter { it.done }
    }

    // Chuyển ngày yyyy-MM-dd và giờ HH:mm sang nhãn thân thiện tiếng Việt
    fun formatDueLabel(date: String, time: String): String {
        val dayLabel = when (date) {
            "2026-10-20" -> "Hôm nay"
            "2026-10-21" -> "Ngày mai"
            "2026-10-25" -> "Tuần này"
            else -> date
        }
        return if (time.isBlank()) dayLabel else "$dayLabel, $time"
    }
}

// ViewModel MVVM điều phối tải, lọc, tìm kiếm và tick hoàn thành cho màn hình Danh sách
class TaskListViewModel(
    private val apiClient: TaskListApiClient = TaskListApiClient()
) {
    private val _uiState = MutableStateFlow(TaskListUiState(isLoading = true))
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    // Tải toàn bộ công việc từ API GET /tasks
    fun loadTasks(scope: CoroutineScope) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, actionMessage = null) }
        scope.launch {
            when (val result = apiClient.getTasks()) {
                is ListApiResult.Success -> {
                    applyStateWithTasks(result.data)
                }

                is ListApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    // Cập nhật từ khóa tìm kiếm tức thì trên danh sách đã tải
    fun onSearchChange(newQuery: String) {
        val current = _uiState.value
        val (todo, done) = TaskListFilterEngine.filterAndGroup(
            current.allTasks,
            newQuery,
            current.selectedFilter
        )
        _uiState.value = current.copy(
            searchQuery = newQuery,
            todoTasks = todo,
            doneTasks = done
        )
    }

    // Đổi bộ lọc nhóm công việc (Tất cả / Học tập / Cuộc sống)
    fun onFilterSelect(filter: TaskListFilter) {
        val current = _uiState.value
        val (todo, done) = TaskListFilterEngine.filterAndGroup(
            current.allTasks,
            current.searchQuery,
            filter
        )
        _uiState.value = current.copy(
            selectedFilter = filter,
            todoTasks = todo,
            doneTasks = done
        )
    }

    // Tick hoàn thành/bỏ hoàn thành công việc qua PATCH /tasks/{id}
    fun toggleTaskDone(task: TaskItem, scope: CoroutineScope) {
        if (_uiState.value.updatingTaskId != null) return
        _uiState.update { it.copy(updatingTaskId = task.id, actionMessage = null) }
        scope.launch {
            val targetDone = !task.done
            when (val result = apiClient.patchTaskDone(task.id, targetDone)) {
                is ListApiResult.Success -> {
                    val updatedList = _uiState.value.allTasks.map { item ->
                        if (item.id == task.id) item.copy(done = targetDone) else item
                    }
                    applyStateWithTasks(updatedList)
                }

                is ListApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            updatingTaskId = null,
                            actionMessage = result.message
                        )
                    }
                }
            }
        }
    }

    private fun applyStateWithTasks(tasks: List<TaskItem>) {
        val current = _uiState.value
        val (todo, done) = TaskListFilterEngine.filterAndGroup(
            tasks,
            current.searchQuery,
            current.selectedFilter
        )
        _uiState.value = current.copy(
            isLoading = false,
            updatingTaskId = null,
            allTasks = tasks,
            todoTasks = todo,
            doneTasks = done,
            remainingCount = tasks.count { !it.done },
            errorMessage = null
        )
    }
}
