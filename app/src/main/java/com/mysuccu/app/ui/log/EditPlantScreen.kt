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

private fun Modifier.dashedBorderEdit(color: Color, cornerRadius: Dp) = this.drawBehind {
    drawRoundRect(
        color = color,
        cornerRadius = CornerRadius(cornerRadius.toPx()),
        style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditPlantScreen(
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 假设这些是读取回来的老数据
    var plantName by remember { mutableStateOf("我的多肉") }
    var arrivalDate by remember { mutableStateOf("2023-10-15") }
    var pottingDate by remember { mutableStateOf("2023-10-16") }
    var pricePlant by remember { mutableStateOf("45.0") }
    var pricePot by remember { mutableStateOf("10.0") }
    var priceSoil by remember { mutableStateOf("5.0") }

    var currencySymbol by remember { mutableStateOf("￥") }
    var showCurrencyMenu by remember { mutableStateOf(false) }
    val currencyOptions = listOf("￥" to R.string.rmb, "$" to R.string.usd, "€" to R.string.eur, "£" to R.string.gbp, "RM" to R.string.rm_currency, "HK$" to R.string.hkd_currency)
    val totalCost = (pricePlant.toDoubleOrNull() ?: 0.0) + (pricePot.toDoubleOrNull() ?: 0.0) + (priceSoil.toDoubleOrNull() ?: 0.0)

    var currentPlantPath by remember { mutableStateOf(listOf("花园大厅", "阳台")) }
    var showFolderPicker by remember { mutableStateOf(false) }
    val folderPickerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

    val sunlightOptions = listOf(stringResource(R.string.sunlight_full) to R.drawable.sun_line, stringResource(R.string.sunlight_partial) to R.drawable.sun_cloudy_line, stringResource(R.string.sunlight_bright) to R.drawable.sparkling_2_line, stringResource(R.string.sunlight_shade) to R.drawable.cloudy_line)
    var selectedSunlight by remember { mutableStateOf(sunlightOptions.first().first) }

    val environmentOptions = listOf(stringResource(R.string.env_indoor_light) to R.drawable.home_office_line, stringResource(R.string.env_indoor_diffuse) to R.drawable.sun_line, stringResource(R.string.env_outdoor_exposed) to R.drawable.map_pin_line, stringResource(R.string.env_balcony_sheltered) to R.drawable.cloud_windy_line)
    var selectedEnvironment by remember { mutableStateOf(environmentOptions.last().first) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f), shadowElevation = 4.dp) {
                Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 8.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.primary) }
                    Text(stringResource(R.string.edit), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    IconButton(onClick = onSave) { Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary) }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            // 1. 照片上传
            item {
                EditGlassCard {
                    Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(RoundedCornerShape(12.dp)).dashedBorderEdit(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), 12.dp).clickable { }, contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(64.dp).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(painterResource(R.drawable.ic_addimage_custom), null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                            }
                            Text(stringResource(R.string.upload_image), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // 2. 基础信息
            item {
                EditGlassCard(title = stringResource(R.string.basic_info), icon = painterResource(R.drawable.ic_plant_nav)) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(stringResource(R.string.plant_name_hint), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            EditGlassTextField(value = plantName, onValueChange = { plantName = it })
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(stringResource(R.string.plant_arrive_date), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            EditGlassDateField(value = arrivalDate, onValueChange = { arrivalDate = it }, icon = painterResource(R.drawable.ic_arrival_custom))
                            Text(stringResource(R.string.plant_potting_date), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(top = 8.dp))
                            EditGlassDateField(value = pottingDate, onValueChange = { pottingDate = it }, icon = painterResource(R.drawable.ic_potting_custom))
                        }
                    }
                }
            }

            // 3. 归档位置
            item {
                EditGlassCard(title = stringResource(R.string.record_file), icon = painterResource(R.drawable.ic_folder)) {
                    Surface(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { showFolderPicker = true }, color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(R.drawable.ic_folder), null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(12.dp))
                            Text(currentPlantPath.joinToString(" / "), color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                            Icon(painterResource(R.drawable.ic_edit_custom), null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // 4. 花费
            item {
                EditGlassCard(
                    title = stringResource(R.string.plant_cost_total), icon = painterResource(R.drawable.ic_price_tag_custom),
                    action = {
                        Box {
                            Surface(modifier = Modifier.clickable { showCurrencyMenu = true }, shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) { Text(currencySymbol, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            }
                            DropdownMenu(expanded = showCurrencyMenu, onDismissRequest = { showCurrencyMenu = false }) {
                                currencyOptions.forEach { (symbol, nameRes) -> DropdownMenuItem(text = { Text("$symbol  ${stringResource(nameRes)}", fontWeight = FontWeight.Bold) }, onClick = { currencySymbol = symbol; showCurrencyMenu = false }) }
                            }
                        }
                    }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        EditLedgerItem(stringResource(R.string.plant_price), pricePlant, currencySymbol) { pricePlant = it }
                        EditLedgerItem(stringResource(R.string.pot_cost), pricePot, currencySymbol) { pricePot = it }
                        EditLedgerItem(stringResource(R.string.soil_cost), priceSoil, currencySymbol) { priceSoil = it }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.plant_cost_total), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("$currencySymbol ${"%.2f".format(totalCost)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // 5. 养护设置
            item {
                EditGlassCard(title = stringResource(R.string.care_preferences), icon = painterResource(R.drawable.ic_user_settings)) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(stringResource(R.string.care_tasks), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                careTasks.forEach { (label, iconRes) -> EditGlassChipWithIcon(label, painterResource(iconRes), selectedTasks.contains(label)) { selectedTasks = if (selectedTasks.contains(label)) selectedTasks - label else selectedTasks + label } }
                            }
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(stringResource(R.string.sunlight_req), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                sunlightOptions.forEach { (label, iconRes) -> EditGlassRadioButtonWithIcon(label, painterResource(iconRes), selectedSunlight == label, { selectedSunlight = label }) }
                            }
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(stringResource(R.string.env_req), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                environmentOptions.forEach { (label, iconRes) -> EditGlassRadioButtonWithIcon(label, painterResource(iconRes), selectedEnvironment == label, { selectedEnvironment = label }) }
                            }
                        }
                    }
                }
            }
        }

        if (showFolderPicker) {
            ModalBottomSheet(onDismissRequest = { showFolderPicker = false }, sheetState = folderPickerSheetState, containerColor = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxHeight(0.85f)) {
                EditFolderPickerContent(initialPath = currentPlantPath, onSave = { newPath -> currentPlantPath = newPath; scope.launch { showFolderPicker = false; snackbarHostState.showSnackbar("Path updated") } })
            }
        }
    }
}

// ================== Shared Private Components for Edit Screen ==================

@Composable
private fun EditFolderPickerContent(initialPath: List<String>, onSave: (List<String>) -> Unit) {
    var currentPath by remember { mutableStateOf(initialPath) }
    val subFolders = remember(currentPath) { if (currentPath.size > 3) listOf<String>() else listOf("阳台", "景天科", "多肉大棚") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.record_file), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            IconButton(onClick = { /* 新建文件夹 */ }) { Icon(painterResource(R.drawable.ic_folder_add), null, tint = MaterialTheme.colorScheme.primary) }
        }
        LazyRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            items(currentPath.size) { index ->
                val isLast = index == currentPath.size - 1
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = currentPath[index], style = MaterialTheme.typography.bodyLarge, fontWeight = if (isLast) FontWeight.Bold else FontWeight.Normal, color = if (isLast) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { currentPath = currentPath.subList(0, index + 1) }.padding(4.dp))
                    if (!isLast) Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.size(18.dp))
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
            items(subFolders) { folder ->
                Row(modifier = Modifier.fillMaxWidth().clickable { currentPath = currentPath + folder }.padding(horizontal = 24.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.ic_folder), null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp)); Spacer(Modifier.width(16.dp)); Text(folder, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f)); Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
        Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface, shadowElevation = 16.dp) {
            Button(onClick = { onSave(currentPath) }, modifier = Modifier.fillMaxWidth().padding(24.dp).height(52.dp), shape = RoundedCornerShape(16.dp)) { Text(stringResource(R.string.confirm), fontWeight = FontWeight.Bold, fontSize = 16.sp) }
        }
    }
}

@Composable
private fun EditGlassCard(modifier: Modifier = Modifier, title: String? = null, icon: Painter? = null, action: @Composable (() -> Unit)? = null, content: @Composable () -> Unit) {
    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)), shadowElevation = 2.dp) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (title != null) { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) { if (icon != null) Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)); Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }; action?.invoke() } }
            content()
        }
    }
}

@Composable
private fun EditGlassTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, keyboardOptions: KeyboardOptions = KeyboardOptions.Default) {
    OutlinedTextField(value = value, onValueChange = onValueChange, modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), keyboardOptions = keyboardOptions, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface), singleLine = true)
}

@Composable
private fun EditGlassDateField(value: String, onValueChange: (String) -> Unit, icon: Painter) {
    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)), modifier = Modifier.fillMaxWidth().height(52.dp)) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)); Spacer(modifier = Modifier.width(8.dp))
            TextField(value = value, onValueChange = onValueChange, modifier = Modifier.weight(1f), singleLine = true, colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent))
        }
    }
}

@Composable
private fun EditLedgerItem(label: String, value: String, symbol: String, onValueChange: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(symbol, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(end = 8.dp))
        EditGlassTextField(value = value, onValueChange = onValueChange, modifier = Modifier.width(100.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
    }
}

@Composable
private fun EditGlassChipWithIcon(text: String, icon: Painter, isSelected: Boolean, onClick: () -> Unit) {
    Surface(modifier = Modifier.clickable { onClick() }, shape = RoundedCornerShape(20.dp), color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text, style = MaterialTheme.typography.labelMedium, color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// 这里补充了刚才丢失的单选按钮组件
@Composable
private fun EditGlassRadioButtonWithIcon(label: String, icon: Painter, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.clickable { onClick() }, shape = RoundedCornerShape(12.dp), color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 4.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, textAlign = TextAlign.Center)
        }
    }
}