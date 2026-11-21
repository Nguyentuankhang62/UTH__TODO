package com.uth.uthtodo.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.uth.uthtodo.model.UserProfile

// Loại phương thức đăng nhập đang sử dụng
enum class AuthProviderType(val badgeText: String) {
    GOOGLE("Đã liên kết tài khoản Google"),
    EMAIL("Đã liên kết tài khoản Email"),
    GUEST("Đang dùng chế độ Khách")
}

// Kết quả trả về từ Firebase Authentication theo mô hình OOP
sealed class AuthResult {
    data class Success(
        val user: UserProfile,
        val provider: AuthProviderType,
        val message: String
    ) : AuthResult()

    data class Error(val message: String) : AuthResult()
}

// Trạng thái giao diện cho phân hệ xác thực
data class AuthUiState(
    val isLoading: Boolean = false,
    val currentUser: UserProfile? = null,
    val providerType: AuthProviderType = AuthProviderType.EMAIL,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

// Lớp OOP quản lý đăng nhập, đăng ký và đăng xuất với Firebase Authentication
class FirebaseAuthManager(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    // Lấy thông tin tài khoản hiện tại nếu đã đăng nhập
    fun getCurrentUserProfile(): UserProfile? {
        val user = firebaseAuth.currentUser ?: return null
        return mapFirebaseUser(user)
    }

    // Đăng nhập bằng Email và Mật khẩu qua Firebase Auth
    fun signInWithEmail(
        email: String,
        password: String,
        onResult: (AuthResult) -> Unit
    ) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isEmpty() || password.isEmpty()) {
            onResult(AuthResult.Error("Vui lòng nhập đầy đủ Email và Mật khẩu."))
            return
        }

        firebaseAuth.signInWithEmailAndPassword(trimmedEmail, password)
            .addOnSuccessListener { result ->
                val profile = mapFirebaseUser(result.user, fallbackEmail = trimmedEmail)
                onResult(
                    AuthResult.Success(
                        user = profile,
                        provider = AuthProviderType.EMAIL,
                        message = "Đăng nhập thành công!"
                    )
                )
            }
            .addOnFailureListener { exception ->
                onResult(AuthResult.Error(mapAuthException(exception)))
            }
    }

    // Đăng ký tài khoản mới bằng Email và Mật khẩu qua Firebase Auth
    fun registerWithEmail(
        email: String,
        password: String,
        onResult: (AuthResult) -> Unit
    ) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isEmpty() || password.length < 6) {
            onResult(AuthResult.Error("Email không được trống và mật khẩu tối thiểu 6 ký tự."))
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(trimmedEmail, password)
            .addOnSuccessListener { result ->
                val profile = mapFirebaseUser(result.user, fallbackEmail = trimmedEmail)
                onResult(
                    AuthResult.Success(
                        user = profile,
                        provider = AuthProviderType.EMAIL,
                        message = "Tạo tài khoản và đăng nhập thành công!"
                    )
                )
            }
            .addOnFailureListener { exception ->
                onResult(AuthResult.Error(mapAuthException(exception)))
            }
    }

    // Đăng nhập ẩn danh (dùng thử không cần tài khoản) qua Firebase Auth
    fun signInAsGuest(onResult: (AuthResult) -> Unit) {
        firebaseAuth.signInAnonymously()
            .addOnSuccessListener { result ->
                val profile = UserProfile(
                    uid = result.user?.uid ?: "guest",
                    displayName = "Minh Anh",
                    email = "guest@uthtodo.app",
                    isGuest = true
                )
                onResult(
                    AuthResult.Success(
                        user = profile,
                        provider = AuthProviderType.GUEST,
                        message = "Đăng nhập chế độ dùng thử thành công!"
                    )
                )
            }
            .addOnFailureListener { exception ->
                onResult(AuthResult.Error(mapAuthException(exception)))
            }
    }

    // Đăng xuất khỏi Firebase Auth và xóa phiên hiện tại
    fun signOut() {
        firebaseAuth.signOut()
    }

    private fun mapFirebaseUser(
        user: FirebaseUser?,
        fallbackEmail: String = "minhanh@example.com"
    ): UserProfile {
        val email = user?.email?.takeIf { it.isNotBlank() } ?: fallbackEmail
        val defaultName = email.substringBefore("@").replaceFirstChar { it.uppercase() }
        val name = user?.displayName?.takeIf { it.isNotBlank() } ?: defaultName
        return UserProfile(
            uid = user?.uid.orEmpty(),
            displayName = name,
            email = email,
            isGuest = user?.isAnonymous ?: false
        )
    }

    // Chuyển mã lỗi Firebase Auth sang thông báo tiếng Việt dễ hiểu
    fun mapAuthException(exception: Exception): String {
        val code = (exception as? FirebaseAuthException)?.errorCode
        return when (code) {
            "ERROR_INVALID_EMAIL" -> "Địa chỉ Email không đúng định dạng."
            "ERROR_WRONG_PASSWORD", "ERROR_INVALID_CREDENTIAL" ->
                "Email hoặc mật khẩu không chính xác."
            "ERROR_USER_NOT_FOUND" -> "Tài khoản không tồn tại trên hệ thống."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Email này đã được đăng ký trước đó."
            "ERROR_WEAK_PASSWORD" -> "Mật khẩu quá yếu, cần ít nhất 6 ký tự."
            "ERROR_NETWORK_REQUEST_FAILED" ->
                "Lỗi kết nối mạng. Vui lòng kiểm tra Internet và thử lại."
            "ERROR_OPERATION_NOT_ALLOWED" ->
                "Phương thức đăng nhập này chưa được bật trên Firebase Console."
            else -> exception.localizedMessage ?: "Đăng nhập thất bại. Vui lòng thử lại."
        }
    }
}
