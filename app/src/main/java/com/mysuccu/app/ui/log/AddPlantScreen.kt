package com.mysuccu.app.ui.log

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mysuccu.app.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun Modifier.dashedBorder(color: Color, cornerRadius: Dp) = this.drawBehind {
    drawRoundRect(
        color = color,
        cornerRadius = CornerRadius(cornerRadius.toPx()),
        style = Stroke(
            width = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddPlantScreen(
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val todayDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 状态数据
    var plantName by remember { mutableStateOf("") }
    var arrivalDate by remember { mutableStateOf(todayDate) }
    var pottingDate by remember { mutableStateOf(todayDate) }
    var pricePlant by remember { mutableStateOf("") }
    var pricePot by remember { mutableStateOf("") }
    var priceSoil by remember { mutableStateOf("") }

    // 货币
    var currencySymbol by remember { mutableStateOf("￥") }
    var showCurrencyMenu by remember { mutableStateOf(false) }
    val currencyOptions = listOf("￥" to R.string.rmb, "$" to R.string.usd, "€" to R.string.eur, "£" to R.string.gbp, "RM" to R.string.rm_currency, "HK$" to R.string.hkd_currency)
    val totalCost = (pricePlant.toDoubleOrNull() ?: 0.0) + (pricePot.toDoubleOrNull() ?: 0.0) + (priceSoil.toDoubleOrNull() ?: 0.0)

    // 文件夹状态
    var currentPlantPath by remember { mutableStateOf(listOf("花园大厅")) }
    var showFolderPicker by remember { mutableStateOf(false) }
    val folderPickerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // 养护选项
    val careTasks = listOf(
        stringResource(R.string.action_water) to R.drawable.ic_water_drop_custom,
        stringResource(R.string.action_fertilize) to R.drawable.ic_fertilize_custom,
        stringResource(R.string.action_medicine) to R.drawable.ic_medicine_custom,
        stringResource(R.string.action_bug) to R.drawable.ic_bug_custom,
        stringResource(R.string.action_prune) to R.drawable.ic_removeskin_custom,
        stringResource(R.string.action_repot) to R.drawable.ic_potting_custom,
        stringResource(R.string.action_trim_roots) to R.drawable.ic_tag_custom,
        stringResource(R.string.action_other) to R.drawable.ic_other_custom
    )
    var selectedTasks by remember { mutableStateOf(setOf<String>()) }

    val sunlightOptions = listOf(
        stringResource(R.string.sunlight_full) to R.drawable.sun_line,
        stringResource(R.string.sunlight_partial) to R.drawable.sun_cloudy_line,
        stringResource(R.string.sunlight_bright) to R.drawable.sparkling_2_line,
        stringResource(R.string.sunlight_shade) to R.drawable.cloudy_line
    )
    var selectedSunlight by remember { mutableStateOf(sunlightOptions.first().first) }

    val environmentOptions = listOf(
        stringResource(R.string.env_indoor_light) to R.drawable.home_office_line,
        stringResource(R.string.env_indoor_diffuse) to R.drawable.sun_line,
        stringResource(R.string.env_outdoor_exposed) to R.drawable.map_pin_line,
        stringResource(R.string.env_balcony_sheltered) to R.drawable.cloud_windy_line
    )
    var selectedEnvironment by remember { mutableStateOf(environmentOptions.last().first) }

    var isArchived by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f), shadowElevation = 4.dp) {
                Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 8.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.primary) }
                    Text(stringResource(R.string.add), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    IconButton(onClick = onSave) { Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary) }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. 照片上传
            item {
                GlassCard {
                    Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(RoundedCornerShape(12.dp)).dashedBorder(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), 12.dp).clickable { }, contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(64.dp).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(painterResource(R.drawable.ic_addimage_custom), null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                            }
                            Text(stringResource(R.string.upload_image), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(stringResource(R.string.plant_headshot), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // 2. 基础信息
            item {
                GlassCard(title = stringResource(R.string.basic_info), icon = painterResource(R.drawable.ic_plant_nav)) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Nickname", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            GlassTextField(value = plantName, onValueChange = { plantName = it }, placeholder = stringResource(R.string.plant_name_hint))
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Arrival & Potting Dates", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                GlassDateField(value = arrivalDate, onValueChange = { arrivalDate = it }, icon = painterResource(R.drawable.ic_arrival_custom), placeholder = "Select Arrival Date")
                                Text(stringResource(R.string.plant_arrive_date), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(start = 4.dp))
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                GlassDateField(value = pottingDate, onValueChange = { pottingDate = it }, icon = painterResource(R.drawable.ic_potting_custom), placeholder = "Select Potting Date")
                                Text(stringResource(R.string.plant_potting_date), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(start = 4.dp))
                            }
                        }
                    }
                }
            }

            // 3. 归档位置
            item {
                GlassCard(title = stringResource(R.string.record_file), icon = painterResource(R.drawable.ic_folder)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { showFolderPicker = true },
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
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
                            Icon(painterResource(R.drawable.ic_edit_custom), null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // 4. 花费
            item {
                GlassCard(
                    title = "Investment",
                    icon = painterResource(R.drawable.ic_price_tag_custom),
                    action = {
                        Box {
                            Surface(modifier = Modifier.clickable { showCurrencyMenu = true }, shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) { Text(currencySymbol, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            }
                            DropdownMenu(expanded = showCurrencyMenu, onDismissRequest = { showCurrencyMenu = false }) {
                                currencyOptions.forEach { (symbol, nameRes) ->
                                    DropdownMenuItem(text = { Text("$symbol  ${stringResource(nameRes)}", fontWeight = FontWeight.Bold) }, onClick = { currencySymbol = symbol; showCurrencyMenu = false })
                                }
                            }
                        }
                    }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        LedgerItem(stringResource(R.string.plant_price), pricePlant, currencySymbol) { pricePlant = it }
                        LedgerItem(stringResource(R.string.pot_cost), pricePot, currencySymbol) { pricePot = it }
                        LedgerItem(stringResource(R.string.soil_cost), priceSoil, currencySymbol) { priceSoil = it }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("$currencySymbol ${"%.2f".format(totalCost)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // 5. 养护设置
            item {
                GlassCard(title = stringResource(R.string.care_preferences), icon = painterResource(R.drawable.ic_user_settings)) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(stringResource(R.string.care_tasks), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                careTasks.forEach { (label, iconRes) ->
                                    GlassChipWithIcon(label, painterResource(iconRes), selectedTasks.contains(label)) {
                                        selectedTasks = if (selectedTasks.contains(label)) selectedTasks - label else selectedTasks + label
                                    }
                                }
                            }
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(stringResource(R.string.sunlight_req), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                sunlightOptions.forEach { (label, iconRes) ->
                                    GlassRadioButtonWithIcon(label, painterResource(iconRes), selectedSunlight == label, { selectedSunlight = label })
                                }
                            }
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(stringResource(R.string.env_req), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                environmentOptions.forEach { (label, iconRes) ->
                                    GlassRadioButtonWithIcon(label, painterResource(iconRes), selectedEnvironment == label, { selectedEnvironment = label })
                                }
                            }
                        }
                    }
                }
            }

            // 6. 归档开关
            item {
                GlassCard {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.archive_plant), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(stringResource(R.string.archive_plant_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }
                        Switch(
                            checked = isArchived,
                            onCheckedChange = { isArchived = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.surface, checkedTrackColor = MaterialTheme.colorScheme.primary, uncheckedThumbColor = MaterialTheme.colorScheme.outline, uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }
                }
            }
        }

        // 底部弹出文件夹选择器
        if (showFolderPicker) {
            ModalBottomSheet(
                onDismissRequest = { showFolderPicker = false },
                sheetState = folderPickerSheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxHeight(0.85f)
            ) {
                FolderPickerContent(
                    initialPath = currentPlantPath,
                    onSave = { newPath ->
                        currentPlantPath = newPath
                        scope.launch { showFolderPicker = false; snackbarHostState.showSnackbar("已选择路径: ${newPath.last()}") }
                    }
                )
            }
        }
    }
}

// ================== Shared Private Components ==================

@Composable
private fun FolderPickerContent(initialPath: List<String>, onSave: (List<String>) -> Unit) {
    var currentPath by remember { mutableStateOf(initialPath) }
    val subFolders = remember(currentPath) {
        if (currentPath.size > 3) listOf<String>() else listOf("阳台", "景天科", "多肉大棚")
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("选择存放位置", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            IconButton(onClick = { /* 新建文件夹 */ }) { Icon(painterResource(R.drawable.ic_folder_add), null, tint = MaterialTheme.colorScheme.primary) }
        }

        LazyRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            items(currentPath.size) { index ->
                val isLast = index == currentPath.size - 1
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = currentPath[index],
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isLast) FontWeight.Bold else FontWeight.Normal,
                        color = if (isLast) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { currentPath = currentPath.subList(0, index + 1) }.padding(4.dp)
                    )
                    if (!isLast) Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.size(18.dp))
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))

        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (subFolders.isEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(top = 48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(painterResource(R.drawable.ic_folder), null, tint = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("空文件夹", color = MaterialTheme.colorScheme.outline)
                    }
                }
            } else {
                items(subFolders) { folder ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { currentPath = currentPath + folder }.padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(painterResource(R.drawable.ic_folder), null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(16.dp))
                        Text(folder, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }

        Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface, shadowElevation = 16.dp) {
            Button(onClick = { onSave(currentPath) }, modifier = Modifier.fillMaxWidth().padding(24.dp).height(52.dp), shape = RoundedCornerShape(16.dp)) {
                Text("选定 ${currentPath.last()}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun GlassCard(modifier: Modifier = Modifier, title: String? = null, icon: Painter? = null, action: @Composable (() -> Unit)? = null, content: @Composable () -> Unit) {
    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)), shadowElevation = 2.dp) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (title != null) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (icon != null) Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    action?.invoke()
                }
            }
            content()
        }
    }
}

// 🚀 已修复：Modifier 参数放到前面
@Composable
private fun GlassTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, placeholder: String = "", keyboardOptions: KeyboardOptions = KeyboardOptions.Default) {
    OutlinedTextField(value = value, onValueChange = onValueChange, placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.outline) }, modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), keyboardOptions = keyboardOptions, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface), singleLine = true)
}

@Composable
private fun GlassDateField(value: String, onValueChange: (String) -> Unit, icon: Painter, placeholder: String) {
    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)), modifier = Modifier.fillMaxWidth().height(52.dp)) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)); Spacer(modifier = Modifier.width(8.dp))
            TextField(value = value, onValueChange = onValueChange, placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline) }, modifier = Modifier.weight(1f), singleLine = true, colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent))
        }
    }
}

@Composable
private fun LedgerItem(label: String, value: String, symbol: String, onValueChange: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(symbol, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(end = 8.dp))
        GlassTextField(value = value, onValueChange = onValueChange, modifier = Modifier.width(100.dp), placeholder = "0.00", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
    }
}

@Composable
private fun GlassChipWithIcon(text: String, icon: Painter, isSelected: Boolean, onClick: () -> Unit) {
    Surface(modifier = Modifier.clickable { onClick() }, shape = RoundedCornerShape(20.dp), color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant); Spacer(modifier = Modifier.width(6.dp))
            Text(text, style = MaterialTheme.typography.labelMedium, color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun GlassRadioButtonWithIcon(label: String, icon: Painter, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.clickable { onClick() }, shape = RoundedCornerShape(12.dp), color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 4.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant); Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, textAlign = TextAlign.Center)
        }
    }
}