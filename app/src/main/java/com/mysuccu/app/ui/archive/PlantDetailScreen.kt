package com.mysuccu.app.ui.archive

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(
    onBack: () -> Unit,
    onEditClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val manageSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isRefreshing by remember { mutableStateOf(false) }
    var showCareSheet by remember { mutableStateOf(false) }
    var showManageSheet by remember { mutableStateOf(false) }

    // 核心状态数据 (纯展示)
    val plantName by remember { mutableStateOf("我的橙梦露") }
    val currentStatusRes by remember { mutableIntStateOf(R.string.status_healthy) }
    val currencySymbol by remember { mutableStateOf("￥") }

    val tags = remember { mutableStateListOf("2026", "Rare", "Needs Repot") }
    val currentPlantPath by remember { mutableStateOf(listOf("花园大厅", "阳台", "景天科")) }
    val environment = stringResource(R.string.env_closed_balcony)
    val hasImages by remember { mutableStateOf(false) }
    val careDays by remember { mutableIntStateOf(120) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.padding(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                    ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.primary) }
                },
                actions = {
                    IconButton(
                        onClick = { showManageSheet = true },
                        modifier = Modifier.padding(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                    ) { Icon(Icons.Default.MoreVert, null, tint = MaterialTheme.colorScheme.primary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            Button(
                onClick = { showCareSheet = true },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(painterResource(R.drawable.ic_record_custom), null, Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.record_status), fontWeight = FontWeight.Bold)
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        SuccuPullToRefresh(
            isRefreshing = isRefreshing,
            onRefresh = { isRefreshing = true; scope.launch { delay(1200); isRefreshing = false } },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                // 1. 顶部大图区域 (浏览大图)
                item {
                    Box(Modifier.fillMaxWidth().height(380.dp).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)).clickable { /* TODO: 浏览全屏大图 */ }) {
                        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                            if (!hasImages) {
                                Icon(painterResource(R.drawable.ic_addimage_custom), null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                                Spacer(Modifier.height(8.dp))
                                Text(stringResource(R.string.plant_headshot), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, MaterialTheme.colorScheme.background.copy(alpha = 0.8f)), startY = 600f)))
                        Surface(Modifier.align(Alignment.BottomEnd).padding(bottom = 40.dp, end = 24.dp), shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)) {
                            Text(stringResource(R.string.view_photos), Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                // 2. 核心信息面板 (纯展示)
                item {
                    Column(modifier = Modifier.padding(horizontal = 24.dp).offset(y = (-32).dp)) {
                        Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                // 名字与状态
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                    Text(text = plantName.ifEmpty { stringResource(R.string.plant_name_hint) }, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = if(plantName.isEmpty()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface)

                                    // 纯展示状态标签
                                    Surface(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(stringResource(currentStatusRes), modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(Modifier.height(16.dp))

                                // 数据网格 (纯展示)
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                                        DetailStatItem(modifier = Modifier.weight(1f), painter = painterResource(R.drawable.ic_daysl_custom), label = stringResource(R.string.plant_care_duration), value = stringResource(R.string.days_format, careDays))
                                        DetailStatItem(Modifier.weight(1f), painterResource(R.drawable.ic_potting_custom), stringResource(R.string.plant_potting_date), "2023.10.15")
                                    }
                                    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                                        DetailStatItem(modifier = Modifier.weight(1f), painter = painterResource(R.drawable.ic_price_tag_custom), label = stringResource(R.string.plant_price), value = "$currencySymbol 45.00")
                                        DetailStatItem(Modifier.weight(1f), painterResource(R.drawable.ic_arrival_custom), stringResource(R.string.plant_arrive_date), "2023.10.12")
                                    }
                                }

                                Spacer(Modifier.height(20.dp))

                                // 环境标签 (纯展示)
                                Text(stringResource(R.string.env_req), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Spacer(Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                            Icon(painterResource(R.drawable.cloud_windy_line), null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                                            Spacer(Modifier.width(6.dp))
                                            Text(environment, color = MaterialTheme.colorScheme.onSecondaryContainer, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(Modifier.height(20.dp))

                                // Tag 系统 (纯展示)
                                if (tags.isNotEmpty()) {
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        items(tags) { tag ->
                                            Box(
                                                modifier = Modifier.border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), RoundedCornerShape(8.dp)).padding(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Text("# $tag", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. 记录故事 (纯展示)
                item {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Text(stringResource(R.string.description), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Surface(Modifier.fillMaxWidth().padding(top = 8.dp), color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
                            Text(stringResource(R.string.onboarding_title), Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // 4. 归档位置 (面包屑纯展示)
                item {
                    Column(modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp)) {
                        Text(stringResource(R.string.record_file), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(painterResource(R.drawable.ic_folder), null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = currentPlantPath.joinToString(" / "),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // 5. 里程碑 & 6. 养护记录
                item { Text(stringResource(R.string.milestone_title), Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 12.dp), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                items(count = 2) { index -> DetailTimelineItem(title = if(index == 0) stringResource(R.string.milestone_new_leaf) else stringResource(R.string.milestone_cluster), date = "05.11", content = stringResource(R.string.view_photos), isLatest = index == 0, isLast = false, painter = painterResource(id = R.drawable.ic_plant_nav)) }
                item { Text(stringResource(R.string.sort_latest_water), Modifier.padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 12.dp), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                items(count = 2) { index -> DetailTimelineItem(title = if(index == 0) stringResource(R.string.action_water) else stringResource(R.string.action_fertilize), date = stringResource(R.string.days_ago_format, index + 3), content = "", isLatest = false, isLast = index == 1, painter = if(index == 0) painterResource(R.drawable.ic_water_drop_custom) else painterResource(R.drawable.ic_fertilize_custom)) }
                item { Spacer(Modifier.height(120.dp)) }
            }
        }

        // --- Bottom Sheets 区域 ---

        if (showCareSheet) { ModalBottomSheet(onDismissRequest = { showCareSheet = false }, sheetState = sheetState) { LogCareBottomSheetContent(onActionClick = { scope.launch { showCareSheet = false; snackbarHostState.showSnackbar("Recorded: $it") } }, onMilestoneClick = { showCareSheet = false }) } }

        // 右上角管理菜单弹窗 (集成了编辑按钮)
        if (showManageSheet) {
            ModalBottomSheet(
                onDismissRequest = { showManageSheet = false },
                sheetState = manageSheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                ManagementBottomSheetContent(
                    onEditClick = {
                        scope.launch { showManageSheet = false }
                        onEditClick() // 触发回调跳转到 EditPlantScreen
                    },
                    onActionClick = { scope.launch { showManageSheet = false } }
                )
            }
        }
    }
}

// ================= 组件区 =================

@Composable
fun DetailStatItem(modifier: Modifier, painter: Painter, label: String, value: String) {
    Box(modifier = modifier.clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f)).border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)).padding(12.dp)) {
        Column {
            Row(verticalAlignment = Alignment.Top) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(painter, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun ManagementBottomSheetContent(onEditClick: () -> Unit, onActionClick: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
        ManagementItem(painterResource(R.drawable.ic_edit_custom), stringResource(R.string.edit), MaterialTheme.colorScheme.onSurface, onEditClick)
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ManagementItem(painterResource(R.drawable.ic_dead_custom), stringResource(R.string.status_dead), MaterialTheme.colorScheme.onSurface) { onActionClick("dead") }
        ManagementItem(painterResource(R.drawable.ic_sold_custom), stringResource(R.string.status_sold), MaterialTheme.colorScheme.onSurface) { onActionClick("sold") }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ManagementItem(painterResource(R.drawable.ic_delete_custom), stringResource(R.string.delete), MaterialTheme.colorScheme.error) { onActionClick("delete") }
    }
}

@Composable
fun ManagementItem(painter: Painter, label: String, textColor: Color, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 24.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(painter, null, Modifier.size(24.dp), tint = if(textColor == MaterialTheme.colorScheme.error) textColor else MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, color = textColor, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun LogCareBottomSheetContent(onActionClick: (String) -> Unit, onMilestoneClick: () -> Unit) {
    val careActions = listOf(Pair(stringResource(R.string.action_water), R.drawable.ic_water_drop_custom), Pair(stringResource(R.string.action_fertilize), R.drawable.ic_fertilize_custom), Pair(stringResource(R.string.action_medicine), R.drawable.ic_medicine_custom), Pair(stringResource(R.string.action_bug), R.drawable.ic_bug_custom), Pair(stringResource(R.string.action_prune), R.drawable.ic_removeskin_custom), Pair(stringResource(R.string.action_repot), R.drawable.ic_potting_custom), Pair(stringResource(R.string.action_trim_roots), R.drawable.ic_cut_custom), Pair(stringResource(R.string.action_other), R.drawable.ic_other_custom))
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp).fillMaxWidth()) {
        Text(stringResource(R.string.sort_latest_water), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.height(20.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.wrapContentHeight(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp), userScrollEnabled = false) {
            items(careActions) { action ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)).clickable { onActionClick(action.first) }.padding(8.dp)) {
                    Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) { Icon(painterResource(action.second), null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary) }
                    Spacer(Modifier.height(4.dp))
                    Text(text = action.first, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface, maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, lineHeight = 14.sp, modifier = Modifier.fillMaxWidth())
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        Text(stringResource(R.string.milestone_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.height(12.dp))
        Button(onClick = onMilestoneClick, modifier = Modifier.fillMaxWidth().height(64.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
            Icon(painterResource(R.drawable.ic_plant_nav), null, tint = MaterialTheme.colorScheme.onSecondaryContainer); Spacer(Modifier.width(12.dp))
            Column { Text(stringResource(R.string.milestone_title), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer); Text(stringResource(R.string.action_photo), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)) }
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun DetailTimelineItem(title: String, date: String, content: String, isLatest: Boolean, isLast: Boolean, painter: Painter? = null) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(24.dp)) {
            Box(Modifier.size(12.dp).clip(CircleShape).background(if (isLatest) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant))
            if (!isLast) Box(Modifier.width(2.dp).height(90.dp).background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)))
        }
        Spacer(Modifier.width(16.dp))
        Surface(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) { if (painter != null) { Icon(painter, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.width(8.dp)) }; Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold) }
                    Text(date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (content.isNotEmpty()) Text(content, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}