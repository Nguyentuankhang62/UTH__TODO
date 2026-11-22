package com.uth.uthtodo.model

// Loại thông báo tương ứng với biểu tượng và màu nền hiển thị
enum class NotificationType {
    UPCOMING,
    OVERDUE,
    ACHIEVEMENT,
    REMINDER
}

// Mô hình OOP đại diện cho một mục thông báo trong UTH Todo
data class NotificationItem(
    override val id: Long,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timeLabel: String,
    val isUnread: Boolean = false
) : IdentifiableItem

// Nguồn dữ liệu OOP cung cấp danh sách thông báo mẫu cho ViewModel
class NotificationRepository {

    // Trả về danh sách 4 thông báo mẫu theo thiết kế Slide 51
    fun getSampleNotifications(): List<NotificationItem> = listOf(
        NotificationItem(
            id = 1L,
            type = NotificationType.UPCOMING,
            title = "Sắp đến hạn",
            message = "Ôn chương 3 – Cấu trúc dữ liệu lúc 14:00.",
            timeLabel = "13:45",
            isUnread = true
        ),
        NotificationItem(
            id = 2L,
            type = NotificationType.OVERDUE,
            title = "Đã quá hạn",
            message = "Bài đọc tham khảo môn Mạng máy tính chưa xong.",
            timeLabel = "10:10",
            isUnread = true
        ),
        NotificationItem(
            id = 3L,
            type = NotificationType.ACHIEVEMENT,
            title = "Làm tốt lắm!",
            message = "Bạn đã xong 2 việc trong sáng nay.",
            timeLabel = "11:30",
            isUnread = false
        ),
        NotificationItem(
            id = 4L,
            type = NotificationType.REMINDER,
            title = "Nhắc việc",
            message = "Họp nhóm đồ án trong tuần này.",
            timeLabel = "Hôm qua",
            isUnread = false
        )
    )
}
