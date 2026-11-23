package com.uth.uthtodo.firebase

import android.content.Context
import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.storage.FirebaseStorage

// Trạng thái hoạt động của từng dịch vụ Firebase
enum class FirebaseServiceStatus(val label: String) {
    ACTIVE("Đang hoạt động"),
    READY("Đã cấu hình"),
    WARNING("Cần kiểm tra")
}

// Mô hình OOP đại diện cho một dịch vụ Firebase được tích hợp trong ứng dụng
data class FirebaseServiceItem(
    val id: String,
    val name: String,
    val category: String,
    val status: FirebaseServiceStatus,
    val detail: String
)

// Trạng thái tổng hợp của màn hình kiểm tra các dịch vụ Firebase
data class FirebaseServicesState(
    val projectId: String = "uth-3845a",
    val services: List<FirebaseServiceItem> = emptyList(),
    val fcmToken: String = "Chưa lấy token",
    val remoteBannerText: String = DEFAULT_WELCOME_BANNER,
    val lastActionMessage: String? = null,
    val isError: Boolean = false,
    val isBusy: Boolean = false
) {
    companion object {
        const val KEY_WELCOME_BANNER = "welcome_banner_text"
        const val DEFAULT_WELCOME_BANNER = "Sắp xếp việc học và cuộc sống gọn gàng mỗi ngày!"
    }
}

// Lớp OOP điều phối và kiểm thử các dịch vụ Firebase trong UTH Todo
class FirebaseServicesManager(context: Context) {

    private val appContext = context.applicationContext
    private val analytics = FirebaseAnalytics.getInstance(appContext)
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val crashlytics = FirebaseCrashlytics.getInstance()
    private val messaging = FirebaseMessaging.getInstance()
    private val remoteConfig = FirebaseRemoteConfig.getInstance()
    private val storage = FirebaseStorage.getInstance()

    init {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(60)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(
            mapOf(
                FirebaseServicesState.KEY_WELCOME_BANNER to FirebaseServicesState.DEFAULT_WELCOME_BANNER
            )
        )
    }

    // Tổng hợp danh sách 7 dịch vụ Firebase đang tích hợp
    fun buildSnapshot(
        fcmToken: String = "Chưa lấy token",
        remoteBannerText: String = remoteConfig.getString(FirebaseServicesState.KEY_WELCOME_BANNER)
            .ifBlank { FirebaseServicesState.DEFAULT_WELCOME_BANNER }
    ): FirebaseServicesState {
        val projectId = FirebaseApp.getInstance().options.projectId ?: "uth-3845a"
        val currentUser = auth.currentUser
        val authDetail = if (currentUser != null) {
            "Đã đăng nhập (UID: ${currentUser.uid.take(8)}...)"
        } else {
            "Sẵn sàng xác thực Email và Anonymous"
        }

        val items = listOf(
            FirebaseServiceItem(
                id = "auth",
                name = "Firebase Authentication",
                category = "Develop",
                status = if (currentUser != null) FirebaseServiceStatus.ACTIVE else FirebaseServiceStatus.READY,
                detail = authDetail
            ),
            FirebaseServiceItem(
                id = "analytics",
                name = "Firebase Analytics",
                category = "Analytics",
                status = FirebaseServiceStatus.ACTIVE,
                detail = "Ghi nhận lượt mở màn hình và thao tác người dùng"
            ),
            FirebaseServiceItem(
                id = "firestore",
                name = "Cloud Firestore",
                category = "Develop",
                status = FirebaseServiceStatus.READY,
                detail = "Đồng bộ công việc theo UID người dùng"
            ),
            FirebaseServiceItem(
                id = "crashlytics",
                name = "Firebase Crashlytics",
                category = "Develop",
                status = FirebaseServiceStatus.ACTIVE,
                detail = "Thu thập ngoại lệ và log lỗi thời gian thực"
            ),
            FirebaseServiceItem(
                id = "fcm",
                name = "Cloud Messaging (FCM)",
                category = "Grow",
                status = if (fcmToken.length > 20) FirebaseServiceStatus.ACTIVE else FirebaseServiceStatus.READY,
                detail = if (fcmToken.length > 20) "Token: ${fcmToken.take(18)}..." else fcmToken
            ),
            FirebaseServiceItem(
                id = "remote_config",
                name = "Firebase Remote Config",
                category = "Develop",
                status = FirebaseServiceStatus.ACTIVE,
                detail = remoteBannerText
            ),
            FirebaseServiceItem(
                id = "storage",
                name = "Cloud Storage",
                category = "Develop",
                status = FirebaseServiceStatus.READY,
                detail = "Bucket: ${storage.reference.bucket}"
            )
        )

        return FirebaseServicesState(
            projectId = projectId,
            services = items,
            fcmToken = fcmToken,
            remoteBannerText = remoteBannerText
        )
    }

    // Ghi sự kiện thử nghiệm lên Firebase Analytics
    fun logTestEvent(eventName: String, screenName: String): String {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString("app_module", "uth_todo_firebase")
        }
        analytics.logEvent(eventName, bundle)
        return "Đã gửi sự kiện Analytics: $eventName"
    }

    // Tải và kích hoạt cấu hình từ Firebase Remote Config
    fun fetchRemoteConfig(onResult: (Boolean, String, String) -> Unit) {
        remoteConfig.fetchAndActivate()
            .addOnSuccessListener { updated ->
                val banner = remoteConfig.getString(FirebaseServicesState.KEY_WELCOME_BANNER)
                    .ifBlank { FirebaseServicesState.DEFAULT_WELCOME_BANNER }
                val msg = if (updated) {
                    "Đã tải cấu hình mới từ Remote Config."
                } else {
                    "Remote Config đã kết nối (đang dùng giá trị mặc định/đã đồng bộ)."
                }
                onResult(true, banner, msg)
            }
            .addOnFailureListener { e ->
                onResult(
                    false,
                    FirebaseServicesState.DEFAULT_WELCOME_BANNER,
                    "Không tải được Remote Config: ${e.localizedMessage ?: "Lỗi mạng"}"
                )
            }
    }

    // Lấy mã định danh thiết bị từ Firebase Cloud Messaging
    fun fetchFcmToken(onResult: (Boolean, String, String) -> Unit) {
        messaging.token
            .addOnSuccessListener { token ->
                onResult(true, token, "Đã lấy FCM Token thành công (${token.take(16)}...).")
            }
            .addOnFailureListener { e ->
                onResult(false, "Chưa lấy token", "Lỗi lấy FCM Token: ${e.localizedMessage ?: "Không rõ"}")
            }
    }

    // Ghi log ngoại lệ không gây sập ứng dụng lên Firebase Crashlytics
    fun recordNonFatalTestLog(): String {
        val uid = auth.currentUser?.uid ?: "guest"
        crashlytics.setUserId(uid)
        crashlytics.log("Manual non-fatal test triggered from FirebaseServicesScreen")
        crashlytics.recordException(IllegalStateException("UTH Todo Crashlytics non-fatal verification"))
        return "Đã ghi nhận log Non-fatal gửi lên Firebase Crashlytics (UID: ${uid.take(8)})."
    }

    // Thử đồng bộ một bản ghi kiểm tra vào Cloud Firestore theo UID hiện tại
    fun syncSampleToFirestore(onResult: (Boolean, String) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onResult(false, "Vui lòng đăng nhập ở Bài 3 trước khi đồng bộ Firestore theo tài khoản.")
            return
        }
        val payload = hashMapOf(
            "title" to "Kiểm tra đồng bộ UTH Todo",
            "category" to "study",
            "updatedAt" to System.currentTimeMillis()
        )
        firestore.collection("users")
            .document(uid)
            .collection("diagnostics")
            .document("ping")
            .set(payload)
            .addOnSuccessListener {
                onResult(true, "Đã đồng bộ dữ liệu kiểm tra lên Cloud Firestore thành công.")
            }
            .addOnFailureListener { e ->
                onResult(false, "Firestore chưa bật hoặc bị chặn quyền: ${e.localizedMessage ?: "Lỗi kết nối"}")
            }
    }
}
