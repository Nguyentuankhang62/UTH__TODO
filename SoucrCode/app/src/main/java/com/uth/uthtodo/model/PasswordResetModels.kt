package com.uth.uthtodo.model

// Mô hình OOP lưu trữ dữ liệu truyền qua lại giữa 4 màn hình đặt lại mật khẩu
data class PasswordResetFlowData(
    val email: String = "",
    val otp: String = "",
    val password: String = ""
) {
    // Tạo chuỗi che mật khẩu hiển thị ở màn hình xác nhận
    fun maskedPassword(): String = "*".repeat(password.length.coerceAtLeast(8))
}

// Kết quả kiểm tra hợp lệ cho từng bước trong luồng Data Flow
sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
}

// Lớp OOP kiểm tra dữ liệu đầu vào cho 4 màn hình quên mật khẩu
class PasswordResetValidator {

    // Kiểm tra định dạng email cơ bản
    fun validateEmail(email: String): ValidationResult {
        val trimmed = email.trim()
        return when {
            trimmed.isEmpty() -> ValidationResult.Invalid("Vui lòng nhập Email.")
            !trimmed.contains("@") || !trimmed.contains(".") ->
                ValidationResult.Invalid("Email không đúng định dạng.")
            else -> ValidationResult.Valid
        }
    }

    // Kiểm tra mã OTP 6 chữ số
    fun validateOtp(otp: String): ValidationResult {
        val trimmed = otp.trim()
        return when {
            trimmed.length != 6 || trimmed.any { !it.isDigit() } ->
                ValidationResult.Invalid("Vui lòng nhập đủ 6 chữ số OTP.")
            else -> ValidationResult.Valid
        }
    }

    // Kiểm tra mật khẩu mới và xác nhận mật khẩu trùng khớp
    fun validatePasswords(password: String, confirmPassword: String): ValidationResult {
        return when {
            password.length < 6 ->
                ValidationResult.Invalid("Mật khẩu phải có ít nhất 6 ký tự.")
            password != confirmPassword ->
                ValidationResult.Invalid("Mật khẩu xác nhận không khớp.")
            else -> ValidationResult.Valid
        }
    }
}
