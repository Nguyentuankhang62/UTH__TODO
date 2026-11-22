package com.uth.uthtodo.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.uth.uthtodo.auth.AuthProviderType
import com.uth.uthtodo.auth.AuthResult
import com.uth.uthtodo.auth.AuthUiState
import com.uth.uthtodo.auth.FirebaseAuthManager
import com.uth.uthtodo.model.OnboardingRepository
import com.uth.uthtodo.model.PasswordResetFlowData
import com.uth.uthtodo.ui.screens.AuthHomeScreen
import com.uth.uthtodo.ui.screens.ConfirmResetScreen
import com.uth.uthtodo.ui.screens.CreateNewPasswordScreen
import com.uth.uthtodo.ui.screens.FirebaseServicesScreen
import com.uth.uthtodo.ui.screens.ForgetPasswordScreen
import com.uth.uthtodo.ui.screens.HomeFlowScreen
import com.uth.uthtodo.ui.screens.HomePreviewScreen
import com.uth.uthtodo.ui.screens.LoginScreen
import com.uth.uthtodo.ui.screens.NotificationsScreen
import com.uth.uthtodo.ui.screens.OnboardingScreen
import com.uth.uthtodo.ui.screens.ProfileScreen
import com.uth.uthtodo.ui.screens.SplashScreen
import com.uth.uthtodo.ui.screens.TaskListScreen
import com.uth.uthtodo.ui.screens.VerifyCodeScreen

private const val KEY_RESULT_EMAIL = "result_email"
private const val KEY_RESULT_OTP = "result_otp"
private const val KEY_RESULT_PASS = "result_pass"

// Định nghĩa các tuyến điều hướng của ứng dụng theo mô hình OOP
sealed class AppRoute(val route: String) {
    data object Splash : AppRoute("splash")
    data object Onboarding : AppRoute("onboarding")
    data object HomePreview : AppRoute("home_preview")
    data object Login : AppRoute("login")
    data object AuthHome : AppRoute("auth_home")
    data object Profile : AppRoute("profile")
    data object FirebaseServices : AppRoute("firebase_services")
    data object HomeFlow : AppRoute("home_flow")
    data object TaskList : AppRoute("task_list")
    data object Notifications : AppRoute("notifications")
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
    val authManager = remember { FirebaseAuthManager() }
    var authUiState by remember {
        mutableStateOf(
            AuthUiState(currentUser = authManager.getCurrentUserProfile())
        )
    }

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
                onOpenFirebaseAuth = {
                    val destination = if (authUiState.currentUser != null) {
                        AppRoute.AuthHome.route
                    } else {
                        AppRoute.Login.route
                    }
                    navController.navigate(destination)
                },
                onOpenFirebaseServices = {
                    navController.navigate(AppRoute.FirebaseServices.route)
                },
                onOpenHomeFlow = {
                    navController.navigate(AppRoute.HomeFlow.route)
                },
                onOpenTaskList = {
                    navController.navigate(AppRoute.TaskList.route)
                },
                onOpenNotifications = {
                    navController.navigate(AppRoute.Notifications.route)
                },
                onReplayOnboarding = {
                    navController.navigate(AppRoute.Onboarding.route) {
                        popUpTo(AppRoute.HomePreview.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoute.Login.route) {
            val handleAuthResult: (AuthResult) -> Unit = { result ->
                when (result) {
                    is AuthResult.Success -> {
                        authUiState = AuthUiState(
                            isLoading = false,
                            currentUser = result.user,
                            providerType = result.provider,
                            successMessage = result.message
                        )
                        navController.navigate(AppRoute.AuthHome.route) {
                            popUpTo(AppRoute.Login.route) { inclusive = true }
                        }
                    }

                    is AuthResult.Error -> {
                        authUiState = authUiState.copy(
                            isLoading = false,
                            errorMessage = result.message,
                            successMessage = null
                        )
                    }
                }
            }

            LoginScreen(
                uiState = authUiState,
                onEmailSignIn = { email, password ->
                    authUiState = authUiState.copy(isLoading = true, errorMessage = null)
                    authManager.signInWithEmail(email, password, handleAuthResult)
                },
                onEmailRegister = { email, password ->
                    authUiState = authUiState.copy(isLoading = true, errorMessage = null)
                    authManager.registerWithEmail(email, password, handleAuthResult)
                },
                onGoogleSignIn = {
                    authUiState = authUiState.copy(
                        isLoading = false,
                        errorMessage = "Cần liên kết SHA-1 và bật Google Sign-In trên Firebase Console để dùng trên thiết bị."
                    )
                },
                onGuestSignIn = {
                    authUiState = authUiState.copy(isLoading = true, errorMessage = null)
                    authManager.signInAsGuest(handleAuthResult)
                }
            )
        }

        composable(AppRoute.AuthHome.route) {
            val currentUser = authUiState.currentUser
            if (currentUser != null) {
                AuthHomeScreen(
                    user = currentUser,
                    tasks = sampleTasks,
                    bannerMessage = authUiState.successMessage,
                    onProfileClick = {
                        navController.navigate(AppRoute.Profile.route)
                    }
                )
            }
        }

        composable(AppRoute.Profile.route) {
            ProfileScreen(
                uiState = authUiState,
                onBack = { navController.popBackStack() },
                onSignOut = {
                    authManager.signOut()
                    // Xóa toàn bộ state tài khoản cũ ngay khi đăng xuất
                    authUiState = AuthUiState(providerType = AuthProviderType.EMAIL)
                    navController.navigate(AppRoute.Login.route) {
                        popUpTo(AppRoute.AuthHome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoute.FirebaseServices.route) {
            FirebaseServicesScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoute.HomeFlow.route) {
            val currentUser = authUiState.currentUser
            HomeFlowScreen(
                displayName = currentUser?.displayName ?: "bạn",
                avatarText = currentUser?.initials() ?: "K",
                onProfileClick = {
                    if (currentUser != null) {
                        navController.navigate(AppRoute.Profile.route)
                    } else {
                        navController.navigate(AppRoute.Login.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoute.TaskList.route) {
            TaskListScreen(
                onOpenHome = {
                    navController.navigate(AppRoute.HomeFlow.route) {
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoute.Notifications.route) {
            NotificationsScreen(
                onOpenHome = {
                    navController.navigate(AppRoute.HomeFlow.route) {
                        launchSingleTop = true
                    }
                },
                onOpenTaskList = {
                    navController.navigate(AppRoute.TaskList.route) {
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() }
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
