package com.uth.uthtodo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uth.uthtodo.model.OnboardingRepository
import com.uth.uthtodo.ui.screens.HomePreviewScreen
import com.uth.uthtodo.ui.screens.OnboardingScreen
import com.uth.uthtodo.ui.screens.SplashScreen

// Định nghĩa các tuyến điều hướng của ứng dụng theo mô hình OOP
sealed class AppRoute(val route: String) {
    data object Splash : AppRoute("splash")
    data object Onboarding : AppRoute("onboarding")
    data object HomePreview : AppRoute("home_preview")
}

// Bộ điều hướng chính kết nối màn hình Khởi động, Onboarding và Trang xem trước
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
                onReplayOnboarding = {
                    navController.navigate(AppRoute.Onboarding.route) {
                        popUpTo(AppRoute.HomePreview.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
