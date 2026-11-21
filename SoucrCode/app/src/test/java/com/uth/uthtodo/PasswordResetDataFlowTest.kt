package com.uth.uthtodo

import com.uth.uthtodo.model.PasswordResetFlowData
import com.uth.uthtodo.model.PasswordResetValidator
import com.uth.uthtodo.model.ValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// Kiểm thử đơn vị cho mô hình OOP và bộ kiểm tra hợp lệ của Bài 2 Data Flow
class PasswordResetDataFlowTest {

    private val validator = PasswordResetValidator()

    @Test
    fun validator_validatesEmailOtpAndPasswords() {
        assertTrue(validator.validateEmail("uth@gmail.com") is ValidationResult.Valid)
        assertTrue(validator.validateEmail("invalid-email") is ValidationResult.Invalid)

        assertTrue(validator.validateOtp("123456") is ValidationResult.Valid)
        assertTrue(validator.validateOtp("123") is ValidationResult.Invalid)

        assertTrue(validator.validatePasswords("123456", "123456") is ValidationResult.Valid)
        assertTrue(validator.validatePasswords("123456", "654321") is ValidationResult.Invalid)
    }

    @Test
    fun passwordResetFlowData_masksPasswordProperly() {
        val data = PasswordResetFlowData(
            email = "uth@gmail.com",
            otp = "123456",
            password = "secret"
        )
        assertEquals("********", data.maskedPassword())
    }
}
