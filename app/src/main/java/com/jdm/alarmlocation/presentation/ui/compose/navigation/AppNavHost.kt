package com.jdm.alarmlocation.presentation.ui.compose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jdm.alarmlocation.presentation.ui.compose.create.CreateTimeScreen
import com.jdm.alarmlocation.presentation.ui.compose.detail.DetailScreen
import com.jdm.alarmlocation.presentation.ui.compose.login.LoginScreen
import com.jdm.alarmlocation.presentation.ui.compose.onboarding.OnboardingScreen
import com.jdm.alarmlocation.presentation.ui.compose.splash.SplashScreen

/**
 * 앱 전역 네비게이션. 목록/충전/마이는 [MainTabsScreen] 의 공용 하단 네비로 통일되어 있고,
 * 생성/상세는 그 위로 push 되는 별도 라우트다. 지도(위치 선택) 단계는 XML+MVVM 액티비티로 분리되어
 * [onStartCreate]/[onStartEdit] 콜백을 통해 호스트 액티비티가 ActivityResult로 처리한다.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    onStartCreate: () -> Unit,
    onStartEdit: (Long) -> Unit,
    onExit: () -> Unit,
) {
    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToList = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onExit = onExit,
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Routes.ONBOARDING) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onNavigateToList = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.MAIN) {
            MainTabsScreen(
                onNavigateToCreate = onStartCreate,
                onNavigateToDetail = { id -> navController.navigate(Routes.detail(id)) },
                onLoggedOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.CREATE_TIME) {
            CreateTimeScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack(Routes.MAIN, inclusive = false) },
            )
        }

        composable(
            route = Routes.DETAIL_PATTERN,
            arguments = listOf(navArgument(Routes.DETAIL_ARG_ID) { type = NavType.LongType }),
        ) {
            DetailScreen(
                onBack = { navController.popBackStack() },
                onEdit = { id -> onStartEdit(id) },
                onDeleted = { navController.popBackStack(Routes.MAIN, inclusive = false) },
            )
        }
    }
}
