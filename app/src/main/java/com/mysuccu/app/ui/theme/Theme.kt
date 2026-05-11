package com.mysuccu.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun MySuccuAppTheme(
    themeId: Int = 8, // 🚀 这里的 ID 将决定全 App 的视觉灵魂
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeId) {
        1 -> lightColorScheme(
            primary = Theme1Primary, primaryContainer = Theme1Container,
            secondary = Theme1Accent2, secondaryContainer = Theme1Status1,
            tertiaryContainer = Theme1Status2, background = BackgroundLight, surface = SurfaceLight
        )
        2 -> lightColorScheme(
            primary = Theme2Primary, primaryContainer = Theme2Container,
            secondary = Theme2Accent2, secondaryContainer = Theme2Status1,
            tertiaryContainer = Theme2Status2, background = BackgroundLight, surface = SurfaceLight
        )
        3 -> lightColorScheme(
            primary = Theme3Primary, primaryContainer = Theme3Container,
            secondary = Theme3Accent2, secondaryContainer = Theme3Status1,
            tertiaryContainer = Theme3Status2, background = BackgroundLight, surface = SurfaceLight
        )
        4 -> lightColorScheme(
            primary = Theme4Primary, primaryContainer = Theme4Container,
            secondary = Theme4Accent2, secondaryContainer = Theme4Status1,
            tertiaryContainer = Theme4Status2, background = BackgroundLight, surface = SurfaceLight
        )
        5 -> lightColorScheme(
            primary = Theme5Primary, primaryContainer = Theme5Container,
            secondary = Theme5Accent2, secondaryContainer = Theme5Status1,
            tertiaryContainer = Theme5Status2, background = BackgroundLight, surface = SurfaceLight
        )
        6 -> lightColorScheme(
            primary = Theme6Primary, primaryContainer = Theme6Container,
            secondary = Theme6Accent2, secondaryContainer = Theme6Status1,
            tertiaryContainer = Theme6Status2, background = BackgroundLight, surface = SurfaceLight
        )
        7 -> lightColorScheme(
            primary = Theme7Primary, primaryContainer = Theme7Container,
            secondary = Theme7Accent2, secondaryContainer = Theme7Status1,
            tertiaryContainer = Theme7Status2, background = BackgroundLight, surface = SurfaceLight
        )
        8 -> lightColorScheme(
            primary = ThemeOfficialPrimary, // 深海蓝
            primaryContainer = ThemeOfficialContainer, // 芽绿
            secondary = ThemeOfficialAccent2, // 灰绿
            secondaryContainer = ThemeOfficialStatus1, // 粉
            tertiaryContainer = ThemeOfficialStatus2, // 紫
            background = BackgroundLight,
            surface = SurfaceLight,
            onPrimary = ThemeOfficialAccent1,
            onSurface = TextPrimary,
            onSurfaceVariant = TextSecondary
        )
        9 -> lightColorScheme(
            primary = Theme9Primary, primaryContainer = Theme9Container,
            secondary = Theme9Accent2, secondaryContainer = Theme9Status1,
            tertiaryContainer = Theme9Status2, background = BackgroundLight, surface = SurfaceLight
        )
        else -> lightColorScheme(primary = ThemeOfficialPrimary)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}