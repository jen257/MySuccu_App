package com.mysuccu.app.ui.navigation

/**
 * 全局路由字典 (Sealed Class)
 * 将所有页面的跳转路径定义在这里，绝不使用硬编码字符串散落在各处
 */
sealed class NavRoutes(val route: String) {
    // 基础骨架页
    object Splash : NavRoutes("splash")
    object Home : NavRoutes("home")
    object Weather : NavRoutes("weather")
    object Calendar : NavRoutes("calendar")
    object Profile : NavRoutes("profile")

    // 二级详情页
    object Detail : NavRoutes("detail")

    // 商业化与高定页
    object Premium : NavRoutes("premium")
    object Theme : NavRoutes("theme")

    // 核心表单页
    object AddPlant : NavRoutes("add_plant")
    object AddLog : NavRoutes("add_log")

    // 鉴权与账号页
    object Login : NavRoutes("login")
    object Account : NavRoutes("account")
}