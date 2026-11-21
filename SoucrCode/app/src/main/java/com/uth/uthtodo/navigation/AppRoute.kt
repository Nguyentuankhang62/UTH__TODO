package com.uth.uthtodo.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.uth.uthtodo.model.OnboardingRepository
import com.uth.uthtodo.model.PasswordResetFlowData
import com.uth.uthtodo.ui.screens.ConfirmResetScreen
import com.uth.uthtodo.ui.screens.CreateNewPasswordScreen
import com.uth.uthtodo.ui.screens.ForgetPasswordScreen
import com.uth.uthtodo.ui.screens.HomePreviewScreen
import com.uth.uthtodo.ui.screens.OnboardingScreen
import com.uth.uthtodo.ui.screens.SplashScreen
import com.uth.uthtodo.ui.screens.VerifyCodeScreen

private const val KEY_RESULT_EMAIL = "result_email"
private const val KEY_RESULT_OTP = "result_otp"
private const val KEY_RESULT_PASS = "result_pass"

// Định nghĩa các tuyến điều hướng của ứng dụng theo mô hình OOP
sealed class AppRoute(val route: String) {
    data object Splash : AppRoute("splash")
    data object Onboarding : AppRoute("onboarding")
    data object HomePreview : AppRoute("home_preview")
    data object ForgetPassword : AppRoute("forget_password")

    data object VerifyCode : AppRoute("verify_code/{email}") {
        // Tạo route truyền email sang màn hình nhập OTP
        fun createRoute(email: String): String = "verify_code/${Uri.encode(email)}"
    }

    data object CreateNewPassword : AppRoute("create_password/{email}/{otp}") {
        // Tạo route truyền email và otp sang màn hình đặt mật khẩu mới
        fun createRoute(email: String, otp: String): String =
            "create_password/${Uri.encode(email)}/${Uri.encode(otp)}"
    }

    data object ConfirmReset : AppRoute("confirm_reset/{email}/{otp}/{password}") {
        // Tạo route truyền đầy đủ email, otp và mật khẩu sang màn hình xác nhận
        fun createRoute(email: String, otp: String, password: String): String =
            "confirm_reset/${Uri.encode(email)}/${Uri.encode(otp)}/${Uri.encode(password)}"
    }
}

// Bộ điều hướng chính kết nối luồng Onboarding và luồng Data Flow quên mật khẩu
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val repository = remember { OnboardingRepository() }
    val steps = remember(repository) { repository.getSteps() }
    val sampleTasks = remember(repository) { repository.getSampleTasks() }

    NavHost(
        navController = navController,
        startDestination = AppRoute.Splash.route
    ) {
        composable(AppRoute.Splash.route) {
            SplashScreen(
                onFinished = {
                    navController.navigate(AppRoute.Onboarding.route) {
                        popUpTo(AppRoute.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoute.Onboarding.route) {
            OnboardingScreen(
                steps = steps,
                onFinishOnboarding = {
                    navController.navigate(AppRoute.HomePreview.route) {
                        popUpTo(AppRoute.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoute.HomePreview.route) {
            HomePreviewScreen(
                sampleTasks = sampleTasks,
                onOpenDataFlow = {
                    navController.navigate(AppRoute.ForgetPassword.route)
                },
                onReplayOnboarding = {
                    navController.navigate(AppRoute.Onboarding.route) {
                        popUpTo(AppRoute.HomePreview.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoute.ForgetPassword.route) { backStackEntry ->
            val savedHandle = backStackEntry.savedStateHandle
            val returnedEmail by savedHandle.getStateFlow<String?>(KEY_RESULT_EMAIL, null)
                .collectAsStateWithLifecycle()
            val returnedOtp by savedHandle.getStateFlow<String?>(KEY_RESULT_OTP, null)
                .collectAsStateWithLifecycle()
            val returnedPass by savedHandle.getStateFlow<String?>(KEY_RESULT_PASS, null)
                .collectAsStateWithLifecycle()

            val returnedData = if (
                returnedEmail != null && returnedOtp != null && returnedPass != null
            ) {
                PasswordResetFlowData(
                    email = returnedEmail.orEmpty(),
                    otp = returnedOtp.orEmpty(),
                    password = returnedPass.orEmpty()
                )
            } else {
                null
            }

            ForgetPasswordScreen(
                returnedData = returnedData,
                onBackToHome = { navController.popBackStack() },
                onNext = { email ->
                    navController.navigate(AppRoute.VerifyCode.createRoute(email))
                }
            )
        }

        composable(
            route = AppRoute.VerifyCode.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = Uri.decode(backStackEntry.arguments?.getString("email").orEmpty())
            VerifyCodeScreen(
                email = email,
                onBack = { navController.popBackStack() },
                onNext = { otp ->
                    navController.navigate(AppRoute.CreateNewPassword.createRoute(email, otp))
                }
            )
        }

        composable(
            route = AppRoute.CreateNewPassword.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("otp") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = Uri.decode(backStackEntry.arguments?.getString("email").orEmpty())
            val otp = Uri.decode(backStackEntry.arguments?.getString("otp").orEmpty())
            CreateNewPasswordScreen(
                onBack = { navController.popBackStack() },
                onNext = { password ->
                    navController.navigate(AppRoute.ConfirmReset.createRoute(email, otp, password))
                }
            )
        }

        composable(
            route = AppRoute.ConfirmReset.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("otp") { type = NavType.StringType },
                navArgument("password") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = Uri.decode(backStackEntry.arguments?.getString("email").orEmpty())
            val otp = Uri.decode(backStackEntry.arguments?.getString("otp").orEmpty())
            val password = Uri.decode(backStackEntry.arguments?.getString("password").orEmpty())
            val flowData = PasswordResetFlowData(email = email, otp = otp, password = password)

            ConfirmResetScreen(
                flowData = flowData,
                onBack = { navController.popBackStack() },
                onSubmit = { submitted ->
                    val targetEntry = navController.getBackStackEntry(AppRoute.ForgetPassword.route)
                    targetEntry.savedStateHandle[KEY_RESULT_EMAIL] = submitted.email
                    targetEntry.savedStateHandle[KEY_RESULT_OTP] = submitted.otp
                    targetEntry.savedStateHandle[KEY_RESULT_PASS] = submitted.password
                    navController.popBackStack(AppRoute.ForgetPassword.route, inclusive = false)
                }
            )
        }
    }
}
