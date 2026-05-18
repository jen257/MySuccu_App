package com.mysuccu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.mysuccu.app.ui.home.HomeScreen
import com.mysuccu.app.ui.archive.PlantDetailScreen
import com.mysuccu.app.ui.navigation.SplashScreen
import com.mysuccu.app.ui.weather.WeatherScreen
import com.mysuccu.app.ui.calendar.CalendarScreen
import com.mysuccu.app.ui.settings.SettingsScreen
import com.mysuccu.app.ui.theme.MySuccuAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MySuccuAppTheme {
                var isSplashFinished by remember { mutableStateOf(false) }
                var currentScreen by remember { mutableStateOf("home") }

                if (!isSplashFinished) {
                    SplashScreen(onSplashFinished = { isSplashFinished = true })
                } else {
                    when (currentScreen) {
                        "home" -> HomeScreen(
                            onPlantClick = { currentScreen = "detail" },
                            onNavigateToWeather = { currentScreen = "weather" },
                            onNavigateToCalendar = { currentScreen = "calendar" },
                            onNavigateToProfile = { currentScreen = "profile" }
                        )
                        "detail" -> PlantDetailScreen(
                            onBack = { currentScreen = "home" }
                        )
                        "weather" -> WeatherScreen(
                            onNavigateToHome = { currentScreen = "home" },
                            onNavigateToCalendar = { currentScreen = "calendar" },
                            onNavigateToProfile = { currentScreen = "profile" }
                        )
                        "calendar" -> CalendarScreen(
                            onNavigateToHome = { currentScreen = "home" },
                            onNavigateToWeather = { currentScreen = "weather" },
                            onNavigateToProfile = { currentScreen = "profile" }
                        )
                        "profile" -> SettingsScreen(
                            onNavigateToHome = { currentScreen = "home" },
                            onNavigateToWeather = { currentScreen = "weather" },
                            onNavigateToCalendar = { currentScreen = "calendar" },
                            onNavigateToPremium = { }, // 留空未来对接内测买断页
                            onNavigateToTheme = { }    // 留空未来对接色卡选择页
                        )
                    }
                }
            }
        }
    }
}