package com.uth.uthtodo

import com.uth.uthtodo.auth.AuthProviderType
import com.uth.uthtodo.auth.AuthResult
import com.uth.uthtodo.auth.AuthUiState
import com.uth.uthtodo.model.UserProfile
import com.uth.uthtodo.navigation.AppRoute
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// Kiểm thử đơn vị cho mô hình OOP và trạng thái xác thực Firebase của Bài 3
class FirebaseAuthModelsTest {

    @Test
    fun authUiState_clearsUserOnSignOutReset() {
        val signedInState = AuthUiState(
            isLoading = false,
            currentUser = UserProfile("uid_1", "Minh Anh", "minhanh@example.com"),
            providerType = AuthProviderType.GOOGLE,
            successMessage = "Đăng nhập thành công!"
        )
        assertEquals("MA", signedInState.currentUser?.initials())

        val signedOutState = AuthUiState()
        assertNull(signedOutState.currentUser)
        assertNull(signedOutState.errorMessage)
        assertNull(signedOutState.successMessage)
    }

    @Test
    fun authResult_distinguishesSuccessAndError() {
        val error = AuthResult.Error("Email hoặc mật khẩu không chính xác.")
        assertTrue(error is AuthResult.Error)
        assertEquals("Email hoặc mật khẩu không chính xác.", error.message)

        val authRoutes = setOf(AppRoute.Login.route, AppRoute.AuthHome.route, AppRoute.Profile.route)
        assertEquals(3, authRoutes.size)
    }
}
