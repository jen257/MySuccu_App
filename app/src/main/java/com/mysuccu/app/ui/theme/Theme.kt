package com.mysuccu.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 默认使用的色彩配方 (生机绿主题)
// 🚀 修改点：将映射关系默认指向 ThemeOfficial 体系
private val LightColorScheme = lightColorScheme(
    primary = ThemeOfficialPrimary,            // 深海蓝 #1A3D59
    primaryContainer = ThemeOfficialContainer,  // 芽绿 #D2DC97
    background = BackgroundLight,               // 纯白 #FFFFFF
    surface = SurfaceLight,                     // 纯白 #FFFFFF
    onPrimary = Color.White,
    onBackground = TextPrimary,                 // 苹果深空灰 #1D1D1F
    onSurface = TextPrimary,
    surfaceVariant = DividerGray,               // 分割线 #E5E5EA
    onSurfaceVariant = TextSecondary,           // 辅助灰 #86868B

    // 状态色映射 (根据你的 Color.kt 变量名)
    error = StatusDanger,                       // 粉色 #F0B5BB (用于警示)
    errorContainer = StatusWarning              // 紫色 #EBD5EC (用于提醒)
)

private val DarkColorScheme = LightColorScheme

@Composable
fun MySuccuAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}