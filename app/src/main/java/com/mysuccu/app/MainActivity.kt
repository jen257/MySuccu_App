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
import com.mysuccu.app.ui.screens.PlantDetailScreen
import com.mysuccu.app.ui.screens.SplashScreen
import com.mysuccu.app.ui.theme.MySuccuAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MySuccuAppTheme {
                // 1. 启动页状态管理
                var isSplashFinished by remember { mutableStateOf(false) }

                // 2. 页面导航状态管理 (临时方案，用于 UI 测试)
                // "home" 代表首页, "detail" 代表详情页
                var currentScreen by remember { mutableStateOf("home") }

                if (!isSplashFinished) {
                    // 展示启动页
                    SplashScreen(
                        onSplashFinished = {
                            isSplashFinished = true
                        }
                    )
                } else {
                    // 启动页结束后，根据 currentScreen 决定展示哪个 UI 界面
                    when (currentScreen) {
                        "home" -> {
                            HomeScreen(
                                onPlantClick = {
                                    // 当点击首页的任何多肉卡片时，跳转到详情
                                    currentScreen = "detail"
                                }
                            )
                        }
                        "detail" -> {
                            PlantDetailScreen(
                                onBack = {
                                    // 点击返回按钮，回到首页
                                    currentScreen = "home"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}