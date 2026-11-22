package com.uth.uthtodo.viewmodel

import com.uth.uthtodo.data.ApiResult
import com.uth.uthtodo.data.TaskApiClient
import com.uth.uthtodo.model.TaskCategory
import com.uth.uthtodo.model.TaskItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Thống kê tiến độ hôm nay và số việc còn lại theo nhóm
data class HomeStats(
    val doneToday: Int = 0,
    val totalToday: Int = 0,
    val progressPercent: Int = 0,
    val studyRemaining: Int = 0,
    val lifeRemaining: Int = 0,
    val encouragementText: String = "Bắt đầu từng việc nhỏ nhé!"
)

// Trạng thái giao diện cho màn hình Trang chủ (UI + API)
data class HomeFlowUiState(
    val isLoading: Boolean = false,
    val updatingTaskId: Long? = null,
    val allTasks: List<TaskItem> = emptyList(),
    val todayTasks: List<TaskItem> = emptyList(),
    val stats: HomeStats = HomeStats(),
    val errorMessage: String? = null,
    val actionMessage: String? = null
)

// Bộ tính toán thống kê OOP tách biệt để dễ kiểm thử đơn vị
object HomeStatsCalculator {

    const val DEFAULT_TODAY_DATE = "2026-10-20"

    // Lọc công việc của ngày hôm nay và tính phần trăm tiến độ
    fun buildStats(allTasks: List<TaskItem>, todayDate: String = DEFAULT_TODAY_DATE): Pair<List<TaskItem>, HomeStats> {
        val todayTasks = allTasks.filter { it.date == todayDate }
            .ifEmpty { allTasks.take(4) }

        val doneToday = todayTasks.count { it.done }
        val totalToday = todayTasks.size
        val percent = if (totalToday == 0) 0 else (doneToday * 100) / totalToday
        val studyRemaining = allTasks.count { it.category == TaskCategory.STUDY && !it.done }
        val lifeRemaining = allTasks.count { it.category == TaskCategory.LIFE && !it.done }

        val encouragement = when {
            totalToday == 0 -> "Hôm nay chưa có công việc nào."
            doneToday == totalToday -> "Tuyệt vời! Bạn đã xong hết việc hôm nay."
            percent >= 50 -> "Đã hơn nửa chặng, cố lên!"
            else -> "Bắt đầu từng việc nhỏ nhé!"
        }

        return todayTasks to HomeStats(
            doneToday = doneToday,
            totalToday = totalToday,
            progressPercent = percent,
            studyRemaining = studyRemaining,
            lifeRemaining = lifeRemaining,
            encouragementText = encouragement
        )
    }
}

// ViewModel MVVM điều phối gọi API GET /tasks và PATCH /tasks/{id} cho Trang chủ
class HomeFlowViewModel(
    private val apiClient: TaskApiClient = TaskApiClient()
) {
    private val _uiState = MutableStateFlow(HomeFlowUiState(isLoading = true))
    val uiState: StateFlow<HomeFlowUiState> = _uiState.asStateFlow()

    // Tải danh sách công việc từ REST API
    fun loadTasks(scope: CoroutineScope) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, actionMessage = null) }
        scope.launch {
            when (val result = apiClient.getTasks()) {
                is ApiResult.Success -> {
                    val (todayTasks, stats) = HomeStatsCalculator.buildStats(result.data)
                    _uiState.value = HomeFlowUiState(
                        isLoading = false,
                        allTasks = result.data,
                        todayTasks = todayTasks,
                        stats = stats
                    )
                }

                is ApiResult.Error -> {
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

    // Đánh dấu hoàn thành/chưa xong qua PATCH /tasks/{id} và cập nhật khi server xác nhận
    fun toggleTaskDone(task: TaskItem, scope: CoroutineScope) {
        if (_uiState.value.updatingTaskId != null) return
        _uiState.update { it.copy(updatingTaskId = task.id, actionMessage = null) }
        scope.launch {
            val targetDone = !task.done
            when (val result = apiClient.patchTaskDone(task.id, targetDone)) {
                is ApiResult.Success -> {
                    val updatedList = _uiState.value.allTasks.map { item ->
                        if (item.id == task.id) item.copy(done = targetDone) else item
                    }
                    val (todayTasks, stats) = HomeStatsCalculator.buildStats(updatedList)
                    _uiState.update {
                        it.copy(
                            updatingTaskId = null,
                            allTasks = updatedList,
                            todayTasks = todayTasks,
                            stats = stats,
                            actionMessage = null
                        )
                    }
                }

                is ApiResult.Error -> {
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
}
