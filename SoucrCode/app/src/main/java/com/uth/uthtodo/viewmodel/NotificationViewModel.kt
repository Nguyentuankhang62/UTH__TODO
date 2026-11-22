package com.uth.uthtodo.viewmodel

import com.uth.uthtodo.model.NotificationItem
import com.uth.uthtodo.model.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Trạng thái giao diện cho màn hình Thông báo theo kiến trúc MVVM
data class NotificationUiState(
    val notifications: List<NotificationItem> = emptyList(),
    val secondsRemaining: Int = AUTO_CLEAR_SECONDS
) {
    // Kiểm tra danh sách rỗng để chuyển sang giao diện EmptyView
    val isEmpty: Boolean get() = notifications.isEmpty()

    // Đếm số thông báo chưa đọc hiện có
    val unreadCount: Int get() = notifications.count { it.isUnread }

    companion object {
        const val AUTO_CLEAR_SECONDS = 10
    }
}

// ViewModel MVVM quản lý danh sách thông báo và tự động đưa về rỗng sau 10 giây
class NotificationViewModel(
    private val repository: NotificationRepository = NotificationRepository()
) {
    private val _uiState = MutableStateFlow(
        NotificationUiState(
            notifications = repository.getSampleNotifications(),
            secondsRemaining = NotificationUiState.AUTO_CLEAR_SECONDS
        )
    )
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    // Bắt đầu đếm ngược 10 giây rồi đưa danh sách thông báo về rỗng
    fun startAutoClearTimer(
        scope: CoroutineScope,
        durationSeconds: Int = NotificationUiState.AUTO_CLEAR_SECONDS
    ) {
        countdownJob?.cancel()
        countdownJob = scope.launch {
            for (remaining in durationSeconds downTo 1) {
                _uiState.update { it.copy(secondsRemaining = remaining) }
                delay(1000L)
            }
            clearNotifications()
        }
    }

    // Đánh dấu toàn bộ thông báo là đã đọc
    fun markAllAsRead() {
        _uiState.update { state ->
            state.copy(
                notifications = state.notifications.map { it.copy(isUnread = false) }
            )
        }
    }

    // Đánh dấu một thông báo cụ thể là đã đọc
    fun markAsRead(id: Long) {
        _uiState.update { state ->
            state.copy(
                notifications = state.notifications.map { item ->
                    if (item.id == id) item.copy(isUnread = false) else item
                }
            )
        }
    }

    // Đưa danh sách thông báo về rỗng để hiển thị EmptyView
    fun clearNotifications() {
        countdownJob?.cancel()
        _uiState.value = NotificationUiState(
            notifications = emptyList(),
            secondsRemaining = 0
        )
    }

    // Nạp lại danh sách mẫu và khởi động lại bộ đếm 10 giây
    fun reloadSample(scope: CoroutineScope) {
        _uiState.value = NotificationUiState(
            notifications = repository.getSampleNotifications(),
            secondsRemaining = NotificationUiState.AUTO_CLEAR_SECONDS
        )
        startAutoClearTimer(scope)
    }
}
