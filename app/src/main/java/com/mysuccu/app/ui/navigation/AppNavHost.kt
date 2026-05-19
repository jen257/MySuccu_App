package com.mysuccu.app.ui.navigation

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mysuccu.app.MainActivity
import com.mysuccu.app.ui.archive.PlantDetailScreen
import com.mysuccu.app.ui.calendar.CalendarScreen
import com.mysuccu.app.ui.home.HomeScreen
import com.mysuccu.app.ui.settings.SettingsScreen
import com.mysuccu.app.ui.weather.WeatherScreen
import com.mysuccu.app.ui.log.AddPlantScreen
import com.mysuccu.app.ui.log.AddLogScreen
import com.mysuccu.app.ui.settings.PremiumScreen
import com.mysuccu.app.ui.settings.ThemeSelectScreen
import com.mysuccu.app.ui.settings.LoginScreen
import com.mysuccu.app.ui.settings.AccountSettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = NavRoutes.Splash.route
) {
    val navigateToBottomTab: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable(NavRoutes.Splash.route) {
            SplashScreen(onSplashFinished = {
                navController.navigate(NavRoutes.Home.route) {
                    popUpTo(NavRoutes.Splash.route) { inclusive = true }
                }
            })
        }

        composable(NavRoutes.Home.route) {
            HomeScreen(
                onPlantClick = { navController.navigate(NavRoutes.Detail.route) },
                onNavigateToWeather = { navigateToBottomTab(NavRoutes.Weather.route) },
                onNavigateToCalendar = { navigateToBottomTab(NavRoutes.Calendar.route) },
                onNavigateToProfile = { navigateToBottomTab(NavRoutes.Profile.route) },
                onNavigateToAddPlant = { navController.navigate(NavRoutes.AddPlant.route) }
            )
        }

        composable(NavRoutes.Weather.route) {
            WeatherScreen(
                onNavigateToHome = { navigateToBottomTab(NavRoutes.Home.route) },
                onNavigateToCalendar = { navigateToBottomTab(NavRoutes.Calendar.route) },
                onNavigateToProfile = { navigateToBottomTab(NavRoutes.Profile.route) }
            )
        }

        composable(NavRoutes.Calendar.route) {
            CalendarScreen(
                onNavigateToHome = { navigateToBottomTab(NavRoutes.Home.route) },
                onNavigateToWeather = { navigateToBottomTab(NavRoutes.Weather.route) },
                onNavigateToProfile = { navigateToBottomTab(NavRoutes.Profile.route) }
            )
        }

        composable(NavRoutes.Profile.route) {
            SettingsScreen(
                onNavigateToHome = { navigateToBottomTab(NavRoutes.Home.route) },
                onNavigateToWeather = { navigateToBottomTab(NavRoutes.Weather.route) },
                onNavigateToCalendar = { navigateToBottomTab(NavRoutes.Calendar.route) },
                onNavigateToPremium = { navController.navigate(NavRoutes.Premium.route) },
                onNavigateToTheme = { navController.navigate(NavRoutes.Theme.route) },
                // 🚀 核心修复：补充你在 SettingsScreen 新加的回调，打通右上角按钮！
                onNavigateToAccount = { navController.navigate(NavRoutes.Account.route) }
            )
        }

        composable(NavRoutes.Detail.route) {
            PlantDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.AddPlant.route) {
            AddPlantScreen(
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.AddLog.route) {
            AddLogScreen(
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Premium.route) {
            PremiumScreen(
                onBack = { navController.popBackStack() },
                onSaveQrCode = {
                    navController.context.findMainActivity()?.saveQrCodeToGallery()
                }
            )
        }

        composable(NavRoutes.Theme.route) {
            ThemeSelectScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Login.route) {
            LoginScreen(
                onBack = { navController.popBackStack() },
                onLoginSuccess = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 成功挂载：账号与安全管理页
        composable(NavRoutes.Account.route) {
            AccountSettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * 辅助防爆核心：专门解包系统的 ContextWrapper 伪装
 * 确保顺着环境树一定能拿到最根本的 MainActivity 实例
 */
fun Context.findMainActivity(): MainActivity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is MainActivity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}