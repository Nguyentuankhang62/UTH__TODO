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
import com.uth.uthtodo.model.AppAppearanceState
import com.uth.uthtodo.model.OnboardingRepository
import com.uth.uthtodo.model.PasswordResetFlowData
import com.uth.uthtodo.model.UserProfile
import com.uth.uthtodo.ui.screens.AddTaskScreen
import com.uth.uthtodo.ui.screens.ConfirmResetScreen
import com.uth.uthtodo.ui.screens.CreateNewPasswordScreen
import com.uth.uthtodo.ui.screens.FirebaseServicesScreen
import com.uth.uthtodo.ui.screens.ForgetPasswordScreen
import com.uth.uthtodo.ui.screens.HomeFlowScreen
import com.uth.uthtodo.ui.screens.LoginScreen
import com.uth.uthtodo.ui.screens.NotificationsScreen
import com.uth.uthtodo.ui.screens.OnboardingScreen
import com.uth.uthtodo.ui.screens.ProfileScreen
import com.uth.uthtodo.ui.screens.SettingsScreen
import com.uth.uthtodo.ui.screens.SplashScreen
import com.uth.uthtodo.ui.screens.TaskDetailScreen
import com.uth.uthtodo.ui.screens.TaskListScreen
import com.uth.uthtodo.ui.screens.VerifyCodeScreen

private const val KEY_RESULT_EMAIL = "result_email"
private const val KEY_RESULT_OTP = "result_otp"
private const val KEY_RESULT_PASS = "result_pass"

// Định nghĩa các tuyến điều hướng liền mạch của ứng dụng UTH Todo
sealed class AppRoute(val route: String) {
    data object Splash : AppRoute("splash")
    data object Onboarding : AppRoute("onboarding")
    data object Login : AppRoute("login")
    data object HomeFlow : AppRoute("home_flow")
    data object TaskList : AppRoute("task_list")
    data object Notifications : AppRoute("notifications")
    data object Settings : AppRoute("settings")
    data object Profile : AppRoute("profile")
    data object FirebaseServices : AppRoute("firebase_services")
    data object AddTask : AppRoute("add_task")

    data object TaskDetail : AppRoute("task_detail/{taskId}") {
        // Tạo route mở chi tiết công việc theo mã id
        fun createRoute(taskId: Long): String = "task_detail/$taskId"
    }

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

// Bộ điều hướng chính nối liền mạch toàn bộ tính năng của ứng dụng UTH Todo
@Composable
fun AppNavGraph(
    appearanceState: AppAppearanceState = AppAppearanceState(),
    onAppearanceChange: (AppAppearanceState) -> Unit = {},
    navController: NavHostController = rememberNavController()
) {
    val repository = remember { OnboardingRepository() }
    val steps = remember(repository) { repository.getSteps() }
    val authManager = remember { FirebaseAuthManager() }
    var authUiState by remember {
        mutableStateOf(
            AuthUiState(currentUser = authManager.getCurrentUserProfile())
        )
    }

    val defaultUser = remember {
        UserProfile(
            uid = "guest",
            displayName = "Tuấn Khang",
            email = "tuankhang@ut.edu.vn",
            isGuest = true
        )
    }

    // Hàm hỗ trợ chuyển tab thanh điều hướng dưới không chồng chất backstack
    val navigateToTab: (String) -> Unit = { targetRoute ->
        navController.navigate(targetRoute) {
            popUpTo(AppRoute.HomeFlow.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    val performSignOut: () -> Unit = {
        authManager.signOut()
        // Xóa toàn bộ state tài khoản cũ ngay khi đăng xuất
        authUiState = AuthUiState(providerType = AuthProviderType.EMAIL)
        navController.navigate(AppRoute.Login.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoute.Splash.route
    ) {
        composable(AppRoute.Splash.route) {
            SplashScreen(
                onFinished = {
                    val destination = if (authUiState.currentUser != null) {
                        AppRoute.HomeFlow.route
                    } else {
                        AppRoute.Onboarding.route
                    }
                    navController.navigate(destination) {
                        popUpTo(AppRoute.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoute.Onboarding.route) {
            OnboardingScreen(
                steps = steps,
                onFinishOnboarding = {
                    val destination = if (authUiState.currentUser != null) {
                        AppRoute.HomeFlow.route
                    } else {
                        AppRoute.Login.route
                    }
                    navController.navigate(destination) {
                        popUpTo(AppRoute.Onboarding.route) { inclusive = true }
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
                        navController.navigate(AppRoute.HomeFlow.route) {
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
                onForgotPassword = {
                    navController.navigate(AppRoute.ForgetPassword.route)
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

        composable(AppRoute.HomeFlow.route) {
            val activeUser = authUiState.currentUser ?: defaultUser
            HomeFlowScreen(
                displayName = activeUser.displayName,
                avatarText = activeUser.initials(),
                onProfileClick = { navController.navigate(AppRoute.Profile.route) },
                onOpenTaskList = { navigateToTab(AppRoute.TaskList.route) },
                onOpenTaskDetail = { id -> navController.navigate(AppRoute.TaskDetail.createRoute(id)) },
                onOpenAddTask = { navController.navigate(AppRoute.AddTask.route) },
                onOpenNotifications = { navigateToTab(AppRoute.Notifications.route) },
                onOpenSettings = { navigateToTab(AppRoute.Settings.route) }
            )
        }

        composable(AppRoute.TaskList.route) {
            TaskListScreen(
                onOpenHome = { navigateToTab(AppRoute.HomeFlow.route) },
                onOpenTaskDetail = { id -> navController.navigate(AppRoute.TaskDetail.createRoute(id)) },
                onOpenAddTask = { navController.navigate(AppRoute.AddTask.route) },
                onOpenNotifications = { navigateToTab(AppRoute.Notifications.route) },
                onOpenSettings = { navigateToTab(AppRoute.Settings.route) }
            )
        }

        composable(AppRoute.Notifications.route) {
            NotificationsScreen(
                onOpenHome = { navigateToTab(AppRoute.HomeFlow.route) },
                onOpenTaskList = { navigateToTab(AppRoute.TaskList.route) },
                onOpenAddTask = { navController.navigate(AppRoute.AddTask.route) },
                onOpenSettings = { navigateToTab(AppRoute.Settings.route) }
            )
        }

        composable(AppRoute.Settings.route) {
            val activeUser = authUiState.currentUser ?: defaultUser
            SettingsScreen(
                user = activeUser,
                appearanceState = appearanceState,
                onAppearanceChange = onAppearanceChange,
                onOpenProfile = { navController.navigate(AppRoute.Profile.route) },
                onOpenHome = { navigateToTab(AppRoute.HomeFlow.route) },
                onOpenTaskList = { navigateToTab(AppRoute.TaskList.route) },
                onOpenAddTask = { navController.navigate(AppRoute.AddTask.route) },
                onOpenNotifications = { navigateToTab(AppRoute.Notifications.route) }
            )
        }

        composable(AppRoute.Profile.route) {
            val activeState = if (authUiState.currentUser != null) {
                authUiState
            } else {
                authUiState.copy(currentUser = defaultUser, providerType = AuthProviderType.GUEST)
            }
            ProfileScreen(
                uiState = activeState,
                onBack = { navController.popBackStack() },
                onSignOut = performSignOut
            )
        }

        composable(AppRoute.FirebaseServices.route) {
            FirebaseServicesScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AppRoute.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: 2L
            TaskDetailScreen(
                taskId = taskId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoute.AddTask.route) {
            AddTaskScreen(
                onCancel = { navController.popBackStack() },
                onTaskCreated = { createdId ->
                    navController.navigate(AppRoute.TaskDetail.createRoute(createdId)) {
                        popUpTo(AppRoute.AddTask.route) { inclusive = true }
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
