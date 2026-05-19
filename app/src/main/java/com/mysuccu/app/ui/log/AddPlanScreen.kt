package com.mysuccu.app.ui.log

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mysuccu.app.R
import com.mysuccu.app.ui.components.SuccuPullToRefresh
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // 表单状态管理
    var plantName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    // 下拉框状态
    var expandedCategory by remember { mutableStateOf(false) }
    val categories = listOf(stringResource(R.string.filter_aizoaceae), stringResource(R.string.filter_conophytum), "景天科")
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.add),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        // ArrowBack 是核心库自带，不会报错
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    TextButton(onClick = onSave) {
                        Text(
                            text = stringResource(R.string.save),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            )
        }
    ) { innerPadding ->
        SuccuPullToRefresh(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                scope.launch {
                    delay(1000)
                    isRefreshing = false
                }
            },
            modifier = Modifier.padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. 顶部照片上传区域
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { /* TODO: 唤起相机或相册 */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                // 🚀 致命修复1：弃用 CameraAlt，改用你绝美的高定图标
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_addimage_custom),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Text(text = stringResource(R.string.plant_headshot), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // 2. 基础信息表单
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = plantName,
                            onValueChange = { plantName = it },
                            label = { Text(stringResource(R.string.plant_name_hint)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedCategory,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text(stringResource(R.string.plant_category)) },
                                trailingIcon = {
                                    // 🚀 致命修复2：弃用 ArrowDropUp，换成绝对安全的 KeyboardArrow 核心图标
                                    Icon(
                                        imageVector = if (expandedCategory) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedCategory = !expandedCategory },
                                enabled = false,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )

                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { expandedCategory = !expandedCategory }
                            )

                            DropdownMenu(
                                expanded = expandedCategory,
                                onDismissRequest = { expandedCategory = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .background(MaterialTheme.colorScheme.surface)
                            ) {
                                categories.forEach { selectionOption ->
                                    DropdownMenuItem(
                                        text = { Text(selectionOption) },
                                        onClick = {
                                            selectedCategory = selectionOption
                                            expandedCategory = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. 日期与价格表单
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        FormClickableRow(
                            // 🚀 致命修复3：弃用 DateRange，接入你的“到家”高定图标
                            icon = painterResource(id = R.drawable.ic_arrival_custom),
                            label = stringResource(R.string.plant_arrive_date),
                            value = "2023-10-12",
                            onClick = { /* TODO: 唤起 DatePicker */ }
                        )

                        FormClickableRow(
                            // 🚀 致命修复4：弃用 EventAvailable，接入你的“上盆”高定图标
                            icon = painterResource(id = R.drawable.ic_potting_custom),
                            label = stringResource(R.string.plant_potting_date),
                            value = "2023-10-15",
                            onClick = { /* TODO: 唤起 DatePicker */ }
                        )

                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text(stringResource(R.string.plant_price)) },
                            // 🚀 致命修复5：弃用 AttachMoney，接入你的“价格标签”高定图标
                            leadingIcon = {
                                Icon(painterResource(id = R.drawable.ic_price_tag_custom), contentDescription = null, modifier = Modifier.size(24.dp))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                }
            }
        }
    }
}

// 🚀 参数升级：将 ImageVector 改为了通用的 Painter，完美兼容你的 R.drawable.xxx
@Composable
fun FormClickableRow(icon: Painter, label: String, value: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Text(text = label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                // 🚀 致命修复6：弃用扩展包的 ChevronRight，换成绝对安全的核心箭头图标
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
            }
        }
    }
}