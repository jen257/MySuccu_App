package com.mysuccu.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import com.mysuccu.app.ui.home.components.AddPlantCard
import com.mysuccu.app.ui.home.components.PlantCard
import com.mysuccu.app.ui.home.components.shimmerEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onPlantClick: () -> Unit) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isLoading by remember { mutableStateOf(true) }

    // 🚀 动态的文件夹层级路径 (模拟用户当前所在的 科 > 属 > 品种)
    // 实际开发中，这会从 ViewModel 获取，比如用户点击了某个分类后，往这个 list 里 add 一级
    val currentFolderPath = remember { mutableStateListOf(R.string.filter_all.toString(), "番杏科", "肉锥属", "安珍") }

    LaunchedEffect(Unit) {
        delay(300)
        isLoading = false
        // 因为 R.string.filter_all 需要 context，初始化时如果拿到的是资源 ID 的 string 形式，
        // 这里可以做个简单的文本替换，演示用直接写 "全部多肉"
        currentFolderPath[0] = "全部多肉"
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (!isLoading) {
                            Text(
                                text = stringResource(id = R.string.onboarding_title),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                actions = {
                    // 🚀 筛选：用于处理价格、天数等属性过滤
                    IconButton(onClick = {
                        scope.launch { snackbarHostState.showSnackbar("打开高级筛选: 天数、价格等") }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_filter_custom),
                            contentDescription = "Filter by price or days",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    // 🚀 搜索：用于输入关键词搜索多肉名字或标签
                    IconButton(onClick = {
                        scope.launch { snackbarHostState.showSnackbar("打开搜索栏: 搜索肉肉名字或标签") }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search_custom),
                            contentDescription = "Search by name or tag",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(painterResource(id = R.drawable.ic_plant_nav), null, Modifier.size(24.dp)) },
                    label = { Text(stringResource(id = R.string.nav_home)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(painterResource(id = R.drawable.ic_weather_nav), null, Modifier.size(24.dp)) },
                    label = { Text(stringResource(id = R.string.nav_weather)) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(painterResource(id = R.drawable.ic_calendar_nav), null, Modifier.size(24.dp)) },
                    label = { Text(stringResource(id = R.string.nav_calendar)) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(painterResource(id = R.drawable.ic_me_nav), null, Modifier.size(24.dp)) },
                    label = { Text(stringResource(id = R.string.nav_profile)) }
                )
            }
        },
        floatingActionButton = {
            if (!isLoading) {
                FloatingActionButton(
                    onClick = { /* TODO: 导航到添加页面 */ },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape
                ) { Icon(Icons.Default.Add, null) }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp)) {

            // 🚀 动态横向滑动分类栏 (Breadcrumbs Navigation)
            if (isLoading) {
                Box(Modifier.padding(vertical = 12.dp).height(32.dp).width(120.dp).clip(RoundedCornerShape(50)).shimmerEffect())
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    itemsIndexed(currentFolderPath) { index, folderName ->
                        val isLastItem = index == currentFolderPath.lastIndex

                        SuggestionChip(
                            onClick = {
                                // TODO: 点击后，截断路径到当前层级，并刷新列表
                                scope.launch { snackbarHostState.showSnackbar("返回到层级: $folderName") }
                            },
                            label = { Text(folderName) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                // 最后一级（当前所在层级）高亮显示
                                containerColor = if (isLastItem) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                labelColor = if (isLastItem) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            ),
                            border = if (isLastItem) null else SuggestionChipDefaults.suggestionChipBorder(enabled = true)
                        )

                        // 不是最后一个元素时，绘制 ">" 分隔符 (已替换为基础库自带的图标)
                        if (!isLastItem) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "Next Level",
                                modifier = Modifier.padding(horizontal = 4.dp).size(16.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }

            // 3x3 植物网格
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                if (isLoading) {
                    items(9) { PlantCard(isLoading = true) }
                } else {
                    item {
                        PlantCard(
                            plantName = "Burgeri 1",
                            statusText = stringResource(id = R.string.status_healthy),
                            days = 120,
                            statusType = 0,
                            modifier = Modifier.clickable { onPlantClick() }
                        )
                    }
                    item {
                        PlantCard(
                            plantName = "Lola",
                            statusText = stringResource(id = R.string.status_warning),
                            days = 340,
                            statusType = 1,
                            modifier = Modifier.clickable { onPlantClick() }
                        )
                    }
                    item {
                        PlantCard(
                            plantName = "Optica",
                            statusText = stringResource(id = R.string.status_danger),
                            days = 8,
                            statusType = 2,
                            modifier = Modifier.clickable { onPlantClick() }
                        )
                    }

                    // 始终在末尾显示添加卡片
                    item { AddPlantCard() }
                }
            }
        }
    }
}