package com.mysuccu.app.ui.weather

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mysuccu.app.R
import com.mysuccu.app.ui.components.SuccuPullToRefresh
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 🚀 1. 自定义 Modifier：完美还原 HTML 中的 animate-skeleton 呼吸效果
fun Modifier.shimmerEffect(): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )
    this.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha))
}

data class WeatherActionItem(
    val iconRes: Int, val titleRes: Int, val descRes: Int, val isVisible: MutableState<Boolean> = mutableStateOf(true)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    // 🚀 2. 页面加载与刷新状态控制
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // 🚀 3. 模拟初次进入页面的网络加载 (1.5秒后切到真实数据)
    LaunchedEffect(Unit) {
        delay(1500)
        isLoading = false
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer))
                        Spacer(Modifier.width(12.dp))
                        Text(stringResource(R.string.weather_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = { IconButton(onClick = { }) { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface, modifier = Modifier.shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))) {
                NavigationBarItem(selected = false, onClick = onNavigateToHome, icon = { Icon(painterResource(id = R.drawable.ic_plant_nav), null, Modifier.size(24.dp)) }, label = { Text(stringResource(id = R.string.nav_home)) })
                NavigationBarItem(selected = true, onClick = { }, icon = { Icon(painterResource(id = R.drawable.ic_weather_nav), null, Modifier.size(24.dp)) }, label = { Text(stringResource(id = R.string.nav_weather)) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.primary, indicatorColor = MaterialTheme.colorScheme.primaryContainer))
                NavigationBarItem(selected = false, onClick = onNavigateToCalendar, icon = { Icon(painterResource(id = R.drawable.ic_calendar_nav), null, Modifier.size(24.dp)) }, label = { Text(stringResource(id = R.string.nav_calendar)) })
                NavigationBarItem(selected = false, onClick = onNavigateToProfile, icon = { Icon(painterResource(id = R.drawable.ic_me_nav), null, Modifier.size(24.dp)) }, label = { Text(stringResource(id = R.string.nav_profile)) })
            }
        }
    ) { innerPadding ->
        SuccuPullToRefresh(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                scope.launch {
                    // 下拉刷新时也展示骨架屏
                    isLoading = true
                    delay(1200)
                    isLoading = false
                    isRefreshing = false
                }
            },
            modifier = Modifier.padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 🚀 4. 条件渲染：加载中显示骨架屏，否则显示真实 UI
                if (isLoading) {
                    item { WeatherSkeletonSection() }
                } else {
                    item { HeroWeatherSection() }
                    item { HourlyForecastSection() }
                    item { AiInsightCard() }
                    item { ActionChecklistSection() }
                    item { ForecastSection() }
                }
            }
        }
    }
}

// ==========================================
// 🚀 骨架屏组件 (Skeleton Screen)
// ==========================================
@Composable
fun WeatherSkeletonSection() {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {

        // 1. 顶部天气卡片 & 环境卡片 骨架
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Column {
                            Box(modifier = Modifier.width(100.dp).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(modifier = Modifier.width(80.dp).height(40.dp).clip(RoundedCornerShape(8.dp)).shimmerEffect())
                        }
                        Box(modifier = Modifier.size(56.dp).clip(CircleShape).shimmerEffect())
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Box(modifier = Modifier.width(60.dp).height(32.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                        Box(modifier = Modifier.width(60.dp).height(32.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                        Box(modifier = Modifier.width(60.dp).height(32.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                    }
                }
            }

            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Box(modifier = Modifier.width(120.dp).height(20.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                        Box(modifier = Modifier.width(40.dp).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.width(200.dp).height(24.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.width(160.dp).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                }
            }
        }

        // 2. AI 建议卡片 骨架
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
            Row(modifier = Modifier.padding(24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).shimmerEffect())
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.width(150.dp).height(20.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                    Box(modifier = Modifier.fillMaxWidth().height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                    Box(modifier = Modifier.fillMaxWidth(0.8f).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                }
            }
        }

        // 3. 待办事项列表 骨架
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.width(150.dp).height(24.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect().padding(bottom = 8.dp))
            repeat(2) {
                Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(24.dp).clip(CircleShape).shimmerEffect())
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.width(120.dp).height(20.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                            Box(modifier = Modifier.width(200.dp).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 🚀 以下为真实的 UI 组件
// ==========================================

@Composable
fun HeroWeatherSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.map_pin_line),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.weather_location_mock),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                            Text(
                                text = "35°C",
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(16.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(painterResource(id = R.drawable.temp_hot_line), null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("38°", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(painterResource(id = R.drawable.temp_cold_line), null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("22°", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.sun_line),
                        contentDescription = "Sunny",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    WeatherStat(stringResource(R.string.weather_humidity), "45%")
                    WeatherStat(stringResource(R.string.weather_season), stringResource(R.string.season_summer))
                    WeatherStat(
                        stringResource(R.string.weather_uv_index),
                        stringResource(R.string.weather_uv_high),
                        isHighlight = true
                    )
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.weather_my_environment),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    TextButton(
                        onClick = { /* TODO: 编辑环境 */ },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.edit),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.env_closed_balcony),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = stringResource(R.string.env_closed_balcony_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun HourlyForecastSection() {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.time_line),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.weather_hourly_forecast),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            val nowText = stringResource(R.string.forecast_now)
            val hourlyData = remember(nowText) {
                listOf(
                    Triple(nowText, R.drawable.cloudy_line, "35°"),
                    Triple("18:00", R.drawable.cloud_windy_line, "32°"),
                    Triple("20:00", R.drawable.moon_line, "28°"),
                    Triple("22:00", R.drawable.moon_cloudy_line, "25°"),
                    Triple("00:00", R.drawable.foggy_line, "22°"),
                    Triple("02:00", R.drawable.drizzle_line, "21°"),
                    Triple("04:00", R.drawable.rainy_line, "20°")
                )
            }

            LazyRow(
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(hourlyData) { (time, iconRes, temp) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = time, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Icon(painterResource(id = iconRes), contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                        Text(text = temp, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
fun AiInsightCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.lightbulb_ai_line),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(28.dp)
                )
            }
            Column {
                Text(
                    text = stringResource(R.string.weather_insight_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.weather_insight_desc_hot),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                    lineHeight = 24.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionChecklistSection() {
    val actions = remember {
        mutableStateListOf(
            WeatherActionItem(R.drawable.sun_cloudy_line, R.string.action_shade_needed, R.string.action_shade_desc),
            WeatherActionItem(R.drawable.ic_water_drop_custom, R.string.action_avoid_water, R.string.action_avoid_water_desc),
            WeatherActionItem(R.drawable.windy_line, R.string.action_ventilate, R.string.action_ventilate_desc)
        )
    }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.sparkling_2_line),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.weather_actions_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            actions.forEach { action ->
                key(action.titleRes) {
                    AnimatedVisibility(
                        visible = action.isVisible.value,
                        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut()
                    ) {
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                    action.isVisible.value = false // 触发视觉动画
                                    true
                                } else false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                                    MaterialTheme.colorScheme.errorContainer
                                } else Color.Transparent
                                Box(
                                    Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)).background(color).padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(painterResource(id = R.drawable.ic_delete_custom), null, tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        ) {
                            ActionCard(
                                iconPainter = painterResource(id = action.iconRes),
                                title = stringResource(action.titleRes),
                                desc = stringResource(action.descRes),
                                borderColor = if (action.titleRes == R.string.action_ventilate) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondaryContainer,
                                onLongClick = { action.isVisible.value = false }
                            )
                        }
                    }

                    LaunchedEffect(action.isVisible.value) {
                        if (!action.isVisible.value) {
                            delay(350) // 等待动画播完
                            actions.remove(action) // 静默移除数据源
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ForecastSection() {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
            Icon(Icons.Default.DateRange, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.weather_forecast_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                ForecastRow(stringResource(R.string.forecast_tomorrow), painterResource(R.drawable.heavy_showers_line), "22°", stringResource(R.string.tag_high_humidity), isSecondaryTag = true)
                HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                ForecastRow(stringResource(R.string.forecast_wed), painterResource(R.drawable.thunderstorms_line), "20°", stringResource(R.string.warning_take_inside), isPrimaryTag = true)
                HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                ForecastRow(stringResource(R.string.forecast_thu), painterResource(R.drawable.hail_line), "15°", stringResource(R.string.warning_physical_damage), isSecondaryTag = true)
                HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                ForecastRow(stringResource(R.string.forecast_fri), painterResource(R.drawable.snowy_line), "5°", stringResource(R.string.warning_frost), isPrimaryTag = true)
                HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                ForecastRow(stringResource(R.string.forecast_sat), painterResource(R.drawable.showers_line), "18°", stringResource(R.string.tag_monitor_soil), isTextTag = true, isOutline = true)
            }
        }
    }
}

@Composable
fun WeatherStat(label: String, value: String, isHighlight: Boolean = false) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
            color = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActionCard(
    iconPainter: Painter,
    title: String,
    desc: String,
    borderColor: Color,
    onLongClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { /* TODO: 查看建议详情 */ },
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.fillMaxHeight().width(4.dp).background(borderColor))
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                Icon(iconPainter, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                    Text(text = desc, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

@Composable
fun ForecastRow(
    day: String,
    iconPainter: Painter,
    temp: String,
    tag: String,
    isPrimaryTag: Boolean = false,
    isSecondaryTag: Boolean = false,
    isTextTag: Boolean = false,
    isOutline: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = day, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.Center) {
            Icon(iconPainter, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(8.dp))
            Text(text = temp, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
            if (isTextTag) {
                Text(
                    text = tag,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isOutline) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
                )
            } else {
                Surface(
                    color = if (isPrimaryTag) MaterialTheme.colorScheme.primary else if (isSecondaryTag) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                ) {
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isPrimaryTag) MaterialTheme.colorScheme.onPrimary else if (isSecondaryTag) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}