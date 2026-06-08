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
import com.mysuccu.app.ui.log.EditPlantScreen
import com.mysuccu.app.ui.settings.PremiumScreen
import com.mysuccu.app.ui.settings.ThemeSelectScreen
import com.mysuccu.app.ui.settings.LoginScreen
import com.mysuccu.app.ui.settings.AccountSettingsScreen
import com.mysuccu.app.ui.settings.AboutUsScreen
import com.mysuccu.app.ui.settings.FeedbackScreen

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
                onNavigateToAccount = { navController.navigate(NavRoutes.Account.route) },
                onNavigateToAboutUs = { navController.navigate(NavRoutes.AboutUs.route) },
                onNavigateToFeedback = { navController.navigate(NavRoutes.Feedback.route) }
            )
        }

        composable(NavRoutes.Detail.route) {
            PlantDetailScreen(
                onBack = { navController.popBackStack() },
                onEditClick = { navController.navigate(NavRoutes.EditPlant.route) }
            )
        }

        composable(NavRoutes.AddPlant.route) {
            AddPlantScreen(
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.EditPlant.route) {
            EditPlantScreen(
                onBack = { navController.popBackStack() },
                onSave = {
                    navController.popBackStack()
                }
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

        composable(NavRoutes.Account.route) {
            AccountSettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.AboutUs.route) {
            AboutUsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Feedback.route) {
            FeedbackScreen(onBack = { navController.popBackStack() })
        }
    }
}

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