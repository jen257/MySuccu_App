package com.mysuccu.app.ui.navigation

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mysuccu.app.MainActivity // 🚀 确保引入
import com.mysuccu.app.ui.archive.PlantDetailScreen
import com.mysuccu.app.ui.calendar.CalendarScreen
import com.mysuccu.app.ui.home.HomeScreen
import com.mysuccu.app.ui.settings.SettingsScreen
import com.mysuccu.app.ui.weather.WeatherScreen
import com.mysuccu.app.ui.log.AddPlantScreen
import com.mysuccu.app.ui.log.AddLogScreen
import com.mysuccu.app.ui.settings.PremiumScreen
import com.mysuccu.app.ui.settings.ThemeSelectScreen

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
                onNavigateToTheme = { navController.navigate(NavRoutes.Theme.route) }
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

        // 🚀 核心修复点：使用 findMainActivity 溯源器解包 Context，无缝联动真正的相册写入流
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
    }
}

/**
 * 🚀 辅助防爆核心：专门解包系统的 ContextWrapper 伪装
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