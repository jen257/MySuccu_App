package com.mysuccu.app.ui.calendar

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mysuccu.app.R
import com.mysuccu.app.ui.components.SuccuPullToRefresh
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

// 🚀 自定义 Shimmer 扩展
fun Modifier.shimmerEffect(): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "calendar_shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "calendar_shimmer_alpha"
    )
    this.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha))
}

data class DayData(val date: String, val dots: List<Color> = emptyList())

data class CareLog(
    val title: String,
    val time: String,
    val desc: String,
    val isAlert: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToWeather: () -> Unit,
    onNavigateToProfile: () -> Unit // 🚀 补齐底栏跳转接口
) {
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val calendarInstance = remember { Calendar.getInstance() }
    var selectedDate by remember { mutableStateOf(calendarInstance.time) }

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
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(id = R.string.calendar_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: 打开搜索 */ }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                NavigationBarItem(selected = false, onClick = onNavigateToHome, icon = { Icon(painterResource(id = R.drawable.ic_plant_nav), null, Modifier.size(24.dp)) }, label = { Text(stringResource(id = R.string.nav_home)) })
                NavigationBarItem(selected = false, onClick = onNavigateToWeather, icon = { Icon(painterResource(id = R.drawable.ic_weather_nav), null, Modifier.size(24.dp)) }, label = { Text(stringResource(id = R.string.nav_weather)) })
                NavigationBarItem(selected = true, onClick = { }, icon = { Icon(painterResource(id = R.drawable.ic_calendar_nav), null, Modifier.size(24.dp)) }, label = { Text(stringResource(id = R.string.nav_calendar)) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.primary, indicatorColor = MaterialTheme.colorScheme.primaryContainer))
                NavigationBarItem(selected = false, onClick = onNavigateToProfile, icon = { Icon(painterResource(id = R.drawable.ic_me_nav), null, Modifier.size(24.dp)) }, label = { Text(stringResource(id = R.string.nav_profile)) })
            }
        }
    ) { innerPadding ->
        SuccuPullToRefresh(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                scope.launch {
                    isLoading = true
                    delay(1200)
                    isLoading = false
                    isRefreshing = false
                }
            },
            modifier = Modifier.padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (isLoading) {
                    item { CalendarSkeletonSection() }
                } else {
                    item { StatisticsSection() }
                    item {
                        CalendarViewSection(
                            selectedDate = selectedDate,
                            onDaySelected = { dayString ->
                                val newCal = Calendar.getInstance().apply { time = selectedDate }
                                newCal.set(Calendar.DAY_OF_MONTH, dayString.toInt())
                                selectedDate = newCal.time
                            }
                        )
                    }
                    item { LogListSection(selectedDate = selectedDate) }
                }
            }
        }
    }
}

@Composable
fun CalendarSkeletonSection() {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(2) {
                Surface(modifier = Modifier.weight(1f).height(72.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).shimmerEffect())
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.width(32.dp).height(20.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                            Box(modifier = Modifier.width(64.dp).height(12.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                        }
                    }
                }
            }
        }

        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).shimmerEffect())
                    Box(modifier = Modifier.width(120.dp).height(24.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                    Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).shimmerEffect())
                }
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    repeat(7) { Box(modifier = Modifier.width(20.dp).height(14.dp).clip(RoundedCornerShape(2.dp)).shimmerEffect()) }
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            repeat(7) { Box(modifier = Modifier.weight(1f).aspectRatio(1f).clip(RoundedCornerShape(12.dp)).shimmerEffect()) }
                        }
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.width(140.dp).height(20.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect().padding(bottom = 4.dp))
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect())
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Box(modifier = Modifier.width(120.dp).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                            Box(modifier = Modifier.width(50.dp).height(14.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                        }
                        Box(modifier = Modifier.fillMaxWidth(0.8f).height(14.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticsSection() {
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(modifier = Modifier.weight(1f).fillMaxHeight(), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(painter = painterResource(id = R.drawable.ic_water_drop_custom), contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(text = "12", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(text = stringResource(id = R.string.stats_watered), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                }
            }
        }

        Surface(modifier = Modifier.weight(1f).fillMaxHeight(), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.errorContainer, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(painter = painterResource(id = R.drawable.ic_potting_custom), contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(text = "2", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(text = stringResource(id = R.string.stats_repotted), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun CalendarViewSection(selectedDate: Date, onDaySelected: (String) -> Unit) {
    val currentMonthYear = remember(selectedDate) {
        android.text.format.DateFormat.format("MMMM yyyy", selectedDate).toString()
    }
    val selectedDayStr = remember(selectedDate) {
        val cal = Calendar.getInstance().apply { time = selectedDate }
        cal.get(Calendar.DAY_OF_MONTH).toString()
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* TODO: 上个月 */ }) { Icon(Icons.Default.KeyboardArrowLeft, null, tint = MaterialTheme.colorScheme.primary) }
                Text(text = currentMonthYear, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                IconButton(onClick = { /* TODO: 下个月 */ }) { Icon(Icons.Default.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.primary) }
            }

            val weekDays = listOf("S", "M", "T", "W", "T", "F", "S")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                weekDays.forEach { day -> Text(text = day, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.weight(1f).padding(bottom = 8.dp), textAlign = TextAlign.Center) }
            }

            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                CalendarRow(selectedDayStr = selectedDayStr, onDaySelected = onDaySelected, days = listOf(DayData("1"), DayData("2"), DayData("3"), DayData("4"), DayData("5"), DayData("6", dots = listOf(MaterialTheme.colorScheme.primary)), DayData("7")))
                CalendarRow(selectedDayStr = selectedDayStr, onDaySelected = onDaySelected, days = listOf(DayData("8"), DayData("9", dots = listOf(MaterialTheme.colorScheme.tertiary)), DayData("10"), DayData("11"), DayData("12", dots = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)), DayData("13"), DayData("14")))
                CalendarRow(selectedDayStr = selectedDayStr, onDaySelected = onDaySelected, days = listOf(DayData("15", dots = listOf(MaterialTheme.colorScheme.error)), DayData("16"), DayData("17"), DayData("18", dots = listOf(MaterialTheme.colorScheme.primary)), DayData("19"), DayData("20"), DayData("21")))
            }

            Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)).padding(16.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                LegendItem(MaterialTheme.colorScheme.primary, stringResource(R.string.legend_watering))
                Spacer(Modifier.width(16.dp))
                LegendItem(MaterialTheme.colorScheme.tertiary, stringResource(R.string.legend_fertilizing))
                Spacer(Modifier.width(16.dp))
                LegendItem(MaterialTheme.colorScheme.error, stringResource(R.string.legend_alert))
            }
        }
    }
}

@Composable
fun CalendarRow(days: List<DayData>, selectedDayStr: String, onDaySelected: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        days.forEach { dayData ->
            val isSelected = dayData.date == selectedDayStr
            Box(
                modifier = Modifier.weight(1f).aspectRatio(1f).padding(2.dp).clip(RoundedCornerShape(12.dp)).background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent).clickable { onDaySelected(dayData.date) },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = dayData.date, style = MaterialTheme.typography.bodyMedium, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                    if (dayData.dots.isNotEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.padding(top = 2.dp)) {
                            dayData.dots.forEach { originalColor ->
                                val dotColorToUse = if (isSelected) Color.White.copy(alpha = 0.8f) else originalColor
                                Box(modifier = Modifier.size(4.dp).background(dotColorToUse, CircleShape))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.size(6.dp).background(color, CircleShape))
        Text(text = text, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
    }
}

@Composable
fun LogListSection(selectedDate: Date) {
    val context = LocalContext.current
    val currentDateFormatted = remember(selectedDate) {
        android.text.format.DateFormat.getMediumDateFormat(context).format(selectedDate)
    }

    val dailyLogs = remember(selectedDate) {
        val cal = Calendar.getInstance().apply { time = selectedDate }
        val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
        val logs = mutableListOf<CareLog>()

        if (dayOfMonth % 2 == 0) logs.add(CareLog("Watered Burgeri", "08:30 AM", "Routine bottom watering. Soil was completely dry."))
        if (dayOfMonth % 3 == 0) logs.add(CareLog("Took a Photo of Lola", "11:15 AM", "Lola is showing beautiful pink edges today! Uploaded to gallery."))
        if (dayOfMonth % 5 == 0) logs.add(CareLog("Fertilized Lithops", "02:00 PM", "Added diluted slow-release fertilizer."))
        if (dayOfMonth == 15) logs.add(CareLog("Alert: Root Rot Risk", "04:30 PM", "Noticed soft transparent leaves on Momotaro. Stopped watering.", isAlert = true))
        if (dayOfMonth == 1) logs.add(CareLog("Added New Plant", "09:00 AM", "Created a new profile for Ice Plant (Haworthia)."))

        logs
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.logs_for_date, currentDateFormatted),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
        )

        if (dailyLogs.isEmpty()) {
            EmptyLogState()
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                dailyLogs.forEach { log ->
                    LogItemCard(title = log.title, time = log.time, desc = log.desc, isAlert = log.isAlert)
                }
            }
        }
    }
}

@Composable
fun EmptyLogState() {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(160.dp).background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f), CircleShape), contentAlignment = Alignment.Center) {
            Icon(painter = painterResource(id = R.drawable.ic_plant_nav), contentDescription = null, modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = stringResource(id = R.string.empty_log_title), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = stringResource(id = R.string.empty_log_desc), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary), shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp), elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 0.dp)) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(id = R.string.action_add_log).uppercase(), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
    }
}

@Composable
fun LogItemCard(title: String, time: String, desc: String, isAlert: Boolean) {
    Surface(modifier = Modifier.fillMaxWidth().clickable { }, shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)), shadowElevation = 1.dp) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Text(text = title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Surface(color = if (isAlert) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), shape = RoundedCornerShape(4.dp)) {
                        Text(text = time, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
                Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}