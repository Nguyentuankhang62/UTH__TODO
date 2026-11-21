package com.uth.uthtodo.model

// Loại minh họa hiển thị cho từng trang Onboarding
enum class OnboardingIllustration {
    TASK_LIST,
    CATEGORY_GROUPS,
    REMINDER_BELL
}

// Lớp OOP đại diện cho một bước trong luồng giới thiệu ứng dụng
data class OnboardingStep(
    val stepIndex: Int,
    val title: String,
    val description: String,
    val primaryButtonText: String,
    val illustration: OnboardingIllustration
)

// Nguồn dữ liệu OOP cung cấp danh sách các bước Onboarding và dữ liệu mẫu
class OnboardingRepository {

    // Trả về 3 bước Onboarding theo thiết kế UTH Todo V2
    fun getSteps(): List<OnboardingStep> = listOf(
        OnboardingStep(
            stepIndex = 0,
            title = "Mọi việc, gọn trong một chỗ",
            description = "Ghi lại bài tập, việc nhà, lịch hẹn. Không còn quên việc nào.",
            primaryButtonText = "Tiếp tục",
            illustration = OnboardingIllustration.TASK_LIST
        ),
        OnboardingStep(
            stepIndex = 1,
            title = "Chia theo Học tập và Cuộc sống",
            description = "Mỗi việc có nhóm riêng, nhìn là biết hôm nay cần lo gì.",
            primaryButtonText = "Tiếp tục",
            illustration = OnboardingIllustration.CATEGORY_GROUPS
        ),
        OnboardingStep(
            stepIndex = 2,
            title = "Nhắc đúng lúc",
            description = "Bật thông báo để UTH Todo nhắc bạn trước hạn chót.",
            primaryButtonText = "Bắt đầu ngay",
            illustration = OnboardingIllustration.REMINDER_BELL
        )
    )

    // Trả về danh sách công việc mẫu theo mô hình OOP để minh họa sau Onboarding
    fun getSampleTasks(): List<TaskItem> = listOf(
        TaskItem(
            id = 1,
            title = "Nộp bài tập Lập trình di động",
            note = "Nộp file .zip lên hệ thống của trường.",
            category = TaskCategory.STUDY,
            date = "2026-10-20",
            time = "09:00",
            priority = TaskPriority.HIGH,
            remind = true,
            done = true,
            subtasks = listOf(
                SubTask("Hoàn thiện màn hình đăng nhập", true),
                SubTask("Chụp ảnh màn hình app", true),
                SubTask("Nén file và nộp bài", true)
            )
        ),
        TaskItem(
            id = 2,
            title = "Ôn chương 3 – Cấu trúc dữ liệu",
            note = "Tập trung phần cây nhị phân và bảng băm.",
            category = TaskCategory.STUDY,
            date = "2026-10-20",
            time = "14:00",
            priority = TaskPriority.MEDIUM,
            remind = true,
            done = false,
            subtasks = listOf(
                SubTask("Đọc lại slide bài giảng", true),
                SubTask("Làm 10 bài tập cuối chương", false)
            )
        ),
        TaskItem(
            id = 3,
            title = "Đi chợ mua đồ cho tuần",
            category = TaskCategory.LIFE,
            date = "2026-10-20",
            time = "17:30",
            priority = TaskPriority.LOW,
            remind = false,
            done = false
        )
    )
}
