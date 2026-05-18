package com.mysuccu.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mysuccu.app.R
import com.mysuccu.app.ui.components.SuccuPullToRefresh
import com.mysuccu.app.ui.home.components.AddPlantCard
import com.mysuccu.app.ui.home.components.PlantCard
import com.mysuccu.app.ui.home.components.shimmerEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class MockPlant(
    val name: String, val statusRes: Int, val days: Int, val type: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPlantClick: () -> Unit,
    onNavigateToWeather: () -> Unit,
    onNavigateToCalendar: () -> Unit // 🚀 接收跳转日历的事件
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }

    val currentFolderPath = remember { mutableStateListOf(R.string.filter_all.toString(), "番杏科", "肉锥属", "安珍") }
    val mockPlants = remember {
        listOf(
            MockPlant("灯泡 Burgeri", R.string.status_healthy, 120, 0),
            MockPlant("露娜莲 Lola", R.string.status_warning, 340, 1),
            MockPlant("红大内 Optica", R.string.status_danger, 8, 2),
            MockPlant("生石花 Lithops", R.string.status_healthy, 45, 0),
            MockPlant("冰灯玉露 Haworthia", R.string.status_healthy, 210, 0),
            MockPlant("桃蛋 Momotaro", R.string.status_warning, 15, 1),
            MockPlant("熊童子 Bear's Paw", R.string.status_healthy, 600, 0),
            MockPlant("黑乌木 Ebony", R.string.status_danger, 2, 2),
            MockPlant("白牡丹 White Peony", R.string.status_healthy, 88, 0),
            MockPlant("乙女心 Jelly Bean", R.string.status_warning, 55, 1)
        )
    }

    LaunchedEffect(Unit) { delay(300); isLoading = false; currentFolderPath[0] = "全部多肉" }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = stringResource(id = R.string.app_name), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        if (!isLoading) Text(text = stringResource(id = R.string.onboarding_title), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }
                },
                actions = {
                    IconButton(onClick = { scope.launch { snackbarHostState.showSnackbar("打开高级筛选") } }) { Icon(painterResource(id = R.drawable.ic_filter_custom), null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp)) }
                    IconButton(onClick = { scope.launch { snackbarHostState.showSnackbar("打开搜索栏") } }) { Icon(painterResource(id = R.drawable.ic_search_custom), null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp)) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface, modifier = Modifier.shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))) {
                NavigationBarItem(selected = true, onClick = { }, icon = { Icon(painterResource(id = R.drawable.ic_plant_nav), null, Modifier.size(24.dp)) }, label = { Text(stringResource(id = R.string.nav_home)) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.primary, indicatorColor = MaterialTheme.colorScheme.primaryContainer))
                NavigationBarItem(selected = false, onClick = onNavigateToWeather, icon = { Icon(painterResource(id = R.drawable.ic_weather_nav), null, Modifier.size(24.dp)) }, label = { Text(stringResource(id = R.string.nav_weather)) })
                NavigationBarItem(selected = false, onClick = onNavigateToCalendar, icon = { Icon(painterResource(id = R.drawable.ic_calendar_nav), null, Modifier.size(24.dp)) }, label = { Text(stringResource(id = R.string.nav_calendar)) }) // 🚀 绑定日历跳转
                NavigationBarItem(selected = false, onClick = { }, icon = { Icon(painterResource(id = R.drawable.ic_me_nav), null, Modifier.size(24.dp)) }, label = { Text(stringResource(id = R.string.nav_profile)) })
            }
        },
        floatingActionButton = {
            if (!isLoading) FloatingActionButton(onClick = { }, containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary, shape = CircleShape) { Icon(Icons.Default.Add, null) }
        }
    ) { innerPadding ->
        SuccuPullToRefresh(
            isRefreshing = isRefreshing,
            onRefresh = { isRefreshing = true; scope.launch { delay(1200); isRefreshing = false } },
            modifier = Modifier.padding(innerPadding)
        ) {
            LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 100.dp)) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    if (isLoading) Box(Modifier.padding(vertical = 12.dp).height(32.dp).width(120.dp).clip(RoundedCornerShape(50)).shimmerEffect())
                    else {
                        LazyRow(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            itemsIndexed(currentFolderPath) { index, folderName ->
                                val isLastItem = index == currentFolderPath.lastIndex
                                SuggestionChip(
                                    onClick = { }, label = { Text(folderName) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = if (isLastItem) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface, labelColor = if (isLastItem) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface),
                                    border = if (isLastItem) null else SuggestionChipDefaults.suggestionChipBorder(enabled = true)
                                )
                                if (!isLastItem) Icon(Icons.Default.KeyboardArrowRight, null, modifier = Modifier.padding(horizontal = 4.dp).size(16.dp), tint = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
                if (isLoading) items(12) { PlantCard(isLoading = true) }
                else {
                    items(mockPlants) { plant -> PlantCard(plantName = plant.name, statusText = stringResource(id = plant.statusRes), days = plant.days, statusType = plant.type, modifier = Modifier.clickable { onPlantClick() }) }
                    item { AddPlantCard() }
                }
            }
        }
    }
}