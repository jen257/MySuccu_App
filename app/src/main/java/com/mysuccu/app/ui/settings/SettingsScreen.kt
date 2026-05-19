package com.mysuccu.app.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mysuccu.app.R
import com.mysuccu.app.ui.components.SuccuPullToRefresh
import com.mysuccu.app.ui.weather.shimmerEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToWeather: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateToTheme: () -> Unit,
    onNavigateToAccount: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val isProUser = false
    val currentPlantCount = 12
    val maxPlantCount = 20

    // 🚀 核心优化：获取当前的默认多语言昵称
    val defaultUserName = stringResource(id = R.string.default_username)
    // 使用 null 代表用户从未修改过昵称。这样切换语言时，它会自动读取新的 defaultUserName！
    var customUserName by remember { mutableStateOf<String?>(null) }
    val displayUserName = customUserName ?: defaultUserName
    val isDefaultName = customUserName == null

    var userAvatarUri by remember { mutableStateOf<Uri?>(null) }

    var showEditNameDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) userAvatarUri = uri }
    )

    LaunchedEffect(Unit) {
        delay(1000)
        isLoading = false
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.nav_profile),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToAccount) {
                        Icon(painter = painterResource(id = R.drawable.ic_user_settings), contentDescription = "Account Settings", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
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
                NavigationBarItem(selected = false, onClick = onNavigateToCalendar, icon = { Icon(painterResource(id = R.drawable.ic_calendar_nav), null, Modifier.size(24.dp)) }, label = { Text(stringResource(id = R.string.nav_calendar)) })
                NavigationBarItem(selected = true, onClick = { }, icon = { Icon(painterResource(id = R.drawable.ic_me_nav), null, Modifier.size(24.dp)) }, label = { Text(stringResource(id = R.string.nav_profile)) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.primary, indicatorColor = MaterialTheme.colorScheme.primaryContainer))
            }
        }
    ) { innerPadding ->
        SuccuPullToRefresh(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                scope.launch { isLoading = true; delay(1200); isLoading = false; isRefreshing = false }
            },
            modifier = Modifier.padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (isLoading) {
                    item { ProfileSkeleton() }
                } else {
                    item {
                        UserHeaderSection(
                            isProUser = isProUser,
                            userName = displayUserName,
                            isDefaultName = isDefaultName, // 🚀 传入状态判断颜色
                            userAvatarUri = userAvatarUri,
                            onEditAvatar = {
                                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            onEditName = {
                                // 打开弹窗时，如果还是默认昵称，输入框留空，否则填入旧昵称
                                tempName = if (isDefaultName) "" else displayUserName
                                showEditNameDialog = true
                            }
                        )
                    }

                    if (!isProUser) item { PremiumBannerCard(onUpgradeClick = onNavigateToPremium) }

                    item { BentoStatusSection(currentCount = currentPlantCount, maxCount = maxPlantCount, onFinanceClick = {}) }

                    item {
                        SettingsGroup(title = stringResource(id = R.string.settings_theme)) {
                            SettingsRow(icon = painterResource(id = R.drawable.ic_palette_custom), title = stringResource(id = R.string.settings_theme), onClick = onNavigateToTheme)
                        }
                    }

                    item {
                        SettingsGroup(title = stringResource(id = R.string.settings_group_data)) {
                            SettingsRow(icon = painterResource(id = R.drawable.ic_cloud_refresh__custom), title = stringResource(id = R.string.settings_sync), showSwitch = true)
                            SettingsRow(icon = painterResource(id = R.drawable.ic_download_custom), title = stringResource(id = R.string.settings_export), onClick = {})
                        }
                    }

                    item {
                        SettingsGroup(title = stringResource(id = R.string.settings_group_about)) {
                            SettingsRow(icon = painterResource(id = R.drawable.ic_info), title = stringResource(id = R.string.settings_about_us), onClick = {})
                            SettingsRow(icon = painterResource(id = R.drawable.ic_feedback), title = stringResource(id = R.string.settings_feedback), onClick = {})
                            SettingsRow(icon = painterResource(id = R.drawable.ic_log_out_custom), title = stringResource(id = R.string.settings_logout), titleColor = MaterialTheme.colorScheme.error, onClick = {})
                        }
                    }
                }
            }
        }

        if (showEditNameDialog) {
            AlertDialog(
                onDismissRequest = { showEditNameDialog = false },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(24.dp),
                title = {
                    Text(text = stringResource(R.string.edit_profile_name), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                },
                text = {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { if (it.length <= 15) tempName = it },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text(stringResource(R.string.name_input_hint), color = MaterialTheme.colorScheme.outline) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // 🚀 保存时逻辑判断：非空则保存自定义名字；如果清空了，则退回默认名字状态
                            if (tempName.isNotBlank()) {
                                customUserName = tempName
                            } else {
                                customUserName = null
                            }
                            showEditNameDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(stringResource(R.string.save), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditNameDialog = false }) {
                        Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.outline)
                    }
                }
            )
        }
    }
}

@Composable
fun UserHeaderSection(
    isProUser: Boolean,
    userName: String,
    isDefaultName: Boolean, // 🚀 接收状态
    userAvatarUri: Uri?,
    onEditAvatar: () -> Unit,
    onEditName: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Box(modifier = Modifier.size(72.dp), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onEditAvatar() },
                contentAlignment = Alignment.Center
            ) {
                if (userAvatarUri != null) {
                    AsyncImage(
                        model = userAvatarUri,
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_default_user_profile),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-4).dp, y = (-4).dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .border(1.5.dp, MaterialTheme.colorScheme.background, CircleShape)
                    .clickable { onEditAvatar() },
                contentAlignment = Alignment.Center
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_edit_custom), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(10.dp))
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onEditName() }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .offset(x = (-4).dp)
            ) {
                // 🚀 核心样式：未编辑过较浅+常规字体；编辑过变深+加粗！
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = if (isDefaultName) FontWeight.Medium else FontWeight.Bold,
                    color = if (isDefaultName) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit_custom),
                    contentDescription = "Edit Name",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
            }

            Surface(
                color = if (isProUser) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = stringResource(id = if (isProUser) R.string.user_pro_tier else R.string.user_free_tier),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isProUser) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ... 剩下的组件 (PremiumBannerCard, BentoStatusSection 等保持原样)
@Composable
fun PremiumBannerCard(onUpgradeClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onUpgradeClick() },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 4.dp
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = stringResource(id = R.string.settings_pro_title), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = stringResource(id = R.string.settings_pro_desc), color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
            }
            Icon(painter = painterResource(id = R.drawable.ic_vip_crown_custom), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun BentoStatusSection(currentCount: Int, maxCount: Int, onFinanceClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Surface(modifier = Modifier.weight(1.2f).fillMaxHeight(), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = stringResource(id = R.string.settings_garden_capacity), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(text = "$currentCount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(text = " / $maxCount", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(bottom = 2.dp))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(progress = { currentCount.toFloat() / maxCount.toFloat() }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape), color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.surfaceVariant)
            }
        }
        Surface(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onFinanceClick() }, shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(painter = painterResource(id = R.drawable.ic_pie_chart_custom), contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(id = R.string.settings_finance_overview), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(text = stringResource(id = R.string.settings_finance_desc), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, fontSize = 10.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(start = 8.dp), letterSpacing = 1.sp)
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))) {
            Column(modifier = Modifier.fillMaxWidth(), content = content)
        }
    }
}

@Composable
fun SettingsRow(icon: Painter, title: String, titleColor: Color = Color.Unspecified, showSwitch: Boolean = false, onClick: (() -> Unit)? = null) {
    var checked by remember { mutableStateOf(true) }
    Row(
        modifier = Modifier.fillMaxWidth().height(56.dp).clickable(enabled = onClick != null) { onClick?.invoke() }.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(painter = icon, contentDescription = null, tint = if (titleColor != Color.Unspecified) titleColor else MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), modifier = Modifier.size(22.dp))
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = if (titleColor != Color.Unspecified) titleColor else MaterialTheme.colorScheme.onSurface)
        }
        if (showSwitch) {
            Switch(checked = checked, onCheckedChange = { checked = it }, colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.surface, checkedTrackColor = MaterialTheme.colorScheme.primary))
        } else if (onClick != null) {
            Icon(Icons.Default.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun ProfileSkeleton() {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.size(64.dp).clip(CircleShape).shimmerEffect())
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.width(120.dp).height(20.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                Box(modifier = Modifier.width(80.dp).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(20.dp)).shimmerEffect())
        Row(modifier = Modifier.fillMaxWidth().height(72.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.weight(1.2f).fillMaxHeight().clip(RoundedCornerShape(16.dp)).shimmerEffect())
            Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(16.dp)).shimmerEffect())
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.width(100.dp).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
            Box(modifier = Modifier.fillMaxWidth().height(112.dp).clip(RoundedCornerShape(16.dp)).shimmerEffect())
        }
    }
}