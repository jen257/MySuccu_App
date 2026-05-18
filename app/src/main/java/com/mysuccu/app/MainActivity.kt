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
import com.mysuccu.app.ui.weather.WeatherScreen // 🚀 别忘了导入天气页面
import com.mysuccu.app.ui.theme.MySuccuAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MySuccuAppTheme {
                // 1. 启动页状态管理
                var isSplashFinished by remember { mutableStateOf(false) }

                // 2. 页面导航状态管理
                // 新增 "weather" 状态
                var currentScreen by remember { mutableStateOf("home") }

                if (!isSplashFinished) {
                    SplashScreen(
                        onSplashFinished = { isSplashFinished = true }
                    )
                } else {
                    when (currentScreen) {
                        "home" -> {
                            HomeScreen(
                                onPlantClick = { currentScreen = "detail" },
                                // 🚀 告诉首页：当点击底部天气按钮时，把状态切到天气
                                onNavigateToWeather = { currentScreen = "weather" }
                            )
                        }
                        "detail" -> {
                            PlantDetailScreen(
                                onBack = { currentScreen = "home" }
                            )
                        }
                        "weather" -> {
                            WeatherScreen(
                                // 🚀 告诉天气页：当点击底部首页按钮时，把状态切回首页
                                onNavigateToHome = { currentScreen = "home" }
                            )
                        }
                    }
                }
            }
        }
    }
}