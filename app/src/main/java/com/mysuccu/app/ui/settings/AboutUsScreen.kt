package com.mysuccu.app.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mysuccu.app.R

// 定义每条内容的结构
data class AboutSection(val titleRes: Int, val contentRes: Int, val defaultExpanded: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(onBack: () -> Unit) {
    // 组装要在列表中显示的数据
    val sections = listOf(
        AboutSection(R.string.about_title_intro, R.string.about_desc_intro, true), // 第一条默认展开
        AboutSection(R.string.about_title_data, R.string.about_desc_data),
        AboutSection(R.string.about_title_privacy, R.string.about_desc_privacy),
        AboutSection(R.string.about_title_no_ads, R.string.about_desc_no_ads),
        AboutSection(R.string.about_title_focus, R.string.about_desc_focus),
        AboutSection(R.string.about_title_premium, R.string.about_desc_premium),
        AboutSection(R.string.about_title_developer, R.string.about_desc_developer)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_about_us), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.padding(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 顶部 App Logo 和名称展示区域
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(80.dp).clip(RoundedCornerShape(20.dp)).background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        // 这里使用了你的植物 Icon 作为临时 Logo，未来有正式 Logo 可以替换
                        Icon(painterResource(R.drawable.ic_plant_nav), contentDescription = "App Logo", modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "MySuccu", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "Version 1.0.0", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                }
            }

            // 折叠列表渲染
            items(sections) { section ->
                ExpandableAboutItem(
                    title = stringResource(id = section.titleRes),
                    content = stringResource(id = section.contentRes),
                    defaultExpanded = section.defaultExpanded
                )
            }

            // 底部留白
            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}

// 独立的折叠卡片组件 (100% 遵守 Material 3 颜色规范)
@Composable
fun ExpandableAboutItem(title: String, content: String, defaultExpanded: Boolean) {
    var isExpanded by remember { mutableStateOf(defaultExpanded) }

    Surface(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable { isExpanded = !isExpanded },
        color = if (isExpanded) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (isExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 标题栏
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            }

            // 内容展开区 (附带流畅动画)
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(300)),
                exit = shrinkVertically(animationSpec = tween(300))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}