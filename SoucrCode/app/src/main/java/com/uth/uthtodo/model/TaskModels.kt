package com.uth.uthtodo.model

// Nhóm phân loại công việc theo chuẩn API của UTH Todo
enum class TaskCategory(val key: String, val displayName: String) {
    STUDY("study", "Học tập"),
    LIFE("life", "Cuộc sống");

    companion object {
        // Chuyển chuỗi category từ API thành enum an toàn
        fun fromKey(key: String): TaskCategory =
            entries.firstOrNull { it.key.equals(key, ignoreCase = true) } ?: STUDY
    }
}

// Mức độ ưu tiên của công việc
enum class TaskPriority(val key: String, val displayName: String) {
    LOW("low", "Thấp"),
    MEDIUM("medium", "Vừa"),
    HIGH("high", "Cao");

    companion object {
        // Chuyển chuỗi priority từ API thành enum an toàn
        fun fromKey(key: String): TaskPriority =
            entries.firstOrNull { it.key.equals(key, ignoreCase = true) } ?: MEDIUM
    }
}

// Giao diện chung cho các thực thể có mã định danh duy nhất
interface IdentifiableItem {
    val id: Long
}

// Việc nhỏ nằm bên trong một công việc lớn
data class SubTask(
    val title: String,
    val done: Boolean = false
)

// Đối tượng công việc chính dùng xuyên suốt các màn hình
data class TaskItem(
    override val id: Long,
    val title: String,
    val note: String = "",
    val category: TaskCategory = TaskCategory.STUDY,
    val date: String,
    val time: String,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val remind: Boolean = false,
    val done: Boolean = false,
    val subtasks: List<SubTask> = emptyList()
) : IdentifiableItem {

    // Đếm số việc nhỏ đã hoàn thành
    fun completedSubtaskCount(): Int = subtasks.count { it.done }

    // Trả về chuỗi tiến độ việc nhỏ dạng "x/y" hoặc rỗng nếu không có việc nhỏ
    fun subtaskProgressText(): String =
        if (subtasks.isEmpty()) "" else "${completedSubtaskCount()}/${subtasks.size}"
}

// Hồ sơ người dùng hiện tại phục vụ phân tách dữ liệu theo tài khoản
data class UserProfile(
    val uid: String,
    val displayName: String,
    val email: String,
    val isGuest: Boolean = false
) {
    // Lấy 2 ký tự viết tắt từ tên hiển thị cho ảnh đại diện
    fun initials(): String {
        val parts = displayName.trim().split(" ").filter { it.isNotEmpty() }
        return when {
            parts.size >= 2 -> "${parts.first().first()}${parts.last().first()}".uppercase()
            parts.size == 1 -> parts.first().take(2).uppercase()
            else -> "UT"
        }
    }
}
