package com.uth.uthtodo

import com.uth.uthtodo.viewmodel.NotificationUiState
import com.uth.uthtodo.viewmodel.NotificationViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Kiểm thử đơn vị cho NotificationViewModel (MVVM) và trạng thái EmptyView của Bài 7
class NotificationViewModelTest {

    @Test
    fun initialState_containsFourNotificationsAndTwoUnread() {
        val viewModel = NotificationViewModel()
        val state = viewModel.uiState.value

        assertEquals(4, state.notifications.size)
        assertEquals(2, state.unreadCount)
        assertEquals(NotificationUiState.AUTO_CLEAR_SECONDS, state.secondsRemaining)
        assertFalse(state.isEmpty)
    }

    @Test
    fun markAllAsRead_andClearNotifications_transitionToEmptyView() {
        val viewModel = NotificationViewModel()

        viewModel.markAllAsRead()
        assertEquals(0, viewModel.uiState.value.unreadCount)
        assertFalse(viewModel.uiState.value.isEmpty)

        viewModel.clearNotifications()
        assertTrue(viewModel.uiState.value.isEmpty)
        assertEquals(0, viewModel.uiState.value.secondsRemaining)
    }
}
