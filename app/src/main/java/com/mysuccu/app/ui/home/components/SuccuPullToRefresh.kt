package com.mysuccu.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mysuccu.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccuPullToRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val refreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = refreshState,
        modifier = modifier.fillMaxSize(),
        indicator = {
            // 1. 获取下拉的比例进度 (0.0 到 1.0+)
            val fraction = refreshState.distanceFraction

            // 2. 下拉时的平滑跟随旋转动画
            val pullRotation by animateFloatAsState(
                targetValue = fraction * 360f,
                label = "pullRotation"
            )

            // 3. 正在刷新时的无限循环旋转动画
            val infiniteTransition = rememberInfiniteTransition(label = "refreshTransition")
            val spinRotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "spinRotation"
            )

            // 4. 完全自定义的加载指示器容器
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    // 🚀 核心：通过 graphicsLayer 控制指示器的上下滑动和透明度
                    .graphicsLayer {
                        // 隐藏在屏幕顶部上方（-40dp），随着下拉滑出，最多停留在距离顶部 60dp 处
                        val yOffset = if (isRefreshing) 60.dp.toPx() else (fraction * 100.dp.toPx() - 40.dp.toPx()).coerceAtMost(60.dp.toPx())
                        translationY = yOffset
                        // 下拉初期加上透明度渐隐渐现效果
                        alpha = if (isRefreshing) 1f else (fraction * 2f).coerceIn(0f, 1f)
                    }
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(10.dp), // 内边距决定了白色圆圈的大小
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_refresh_custom),
                    contentDescription = "Refresh",
                    modifier = Modifier
                        .size(24.dp)
                        // 🚀 根据状态切换旋转模式：刷新中转圈，平时跟随手指
                        .rotate(if (isRefreshing) spinRotation else pullRotation),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) {
        content()
    }
}