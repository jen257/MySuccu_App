package com.mysuccu.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.mysuccu.app.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    // 核心逻辑：页面加载后，开始 3 秒倒计时
    LaunchedEffect(key1 = true) {
        delay(3000L)
        onSplashFinished()
    }

    // 核心视觉：全屏铺满你的启动图片
    Image(
        painter = painterResource(id = R.drawable.splash_screen_image),
        contentDescription = "启动页全屏大图",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}