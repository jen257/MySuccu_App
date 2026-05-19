package com.mysuccu.app.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mysuccu.app.R
import com.mysuccu.app.ui.components.SuccuPullToRefresh
// 🚀 核心：直接导入你在 Color.kt 中定义的所有 Figma 级原色！
import com.mysuccu.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 构造色卡的数据模型
data class ThemePalettePreview(
    val nameResId: Int,
    val primaryColor: Color,
    val containerColor: Color,
    val accentColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectScreen(onBack: () -> Unit) {
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 当前选中的主题 (默认第8组官方款)
    var selectedThemeIndex by remember { mutableIntStateOf(7) }

    // 🚀 100% 还原企划书的 9 组高定色卡数据
    val themes = listOf(
        ThemePalettePreview(R.string.theme_1, Theme1Primary, Theme1Container, Theme1Accent1),
        ThemePalettePreview(R.string.theme_2, Theme2Primary, Theme2Container, Theme2Accent1),
        ThemePalettePreview(R.string.theme_3, Theme3Primary, Theme3Container, Theme3Accent1),
        ThemePalettePreview(R.string.theme_4, Theme4Primary, Theme4Container, Theme4Accent1),
        ThemePalettePreview(R.string.theme_5, Theme5Primary, Theme5Container, Theme5Accent1),
        ThemePalettePreview(R.string.theme_6, Theme6Primary, Theme6Container, Theme6Accent1),
        ThemePalettePreview(R.string.theme_7, Theme7Primary, Theme7Container, Theme7Accent1),
        ThemePalettePreview(R.string.theme_8, ThemeOfficialPrimary, ThemeOfficialContainer, ThemeOfficialAccent1),
        ThemePalettePreview(R.string.theme_9, Theme9Primary, Theme9Container, Theme9Accent1)
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.theme_select_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            )
        }
    ) { innerPadding ->
        SuccuPullToRefresh(
            isRefreshing = isRefreshing,
            onRefresh = { isRefreshing = true; scope.launch { delay(800); isRefreshing = false } },
            modifier = Modifier.padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(themes) { index, theme ->
                    ThemeCard(
                        theme = theme,
                        isSelected = index == selectedThemeIndex,
                        onClick = {
                            selectedThemeIndex = index
                            scope.launch { snackbarHostState.showSnackbar("已应用该色卡！(需后续配合 ViewModel 生效)") }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeCard(theme: ThemePalettePreview, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        // 动态：被选中时边框加粗并高亮
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // 🚀 色块预览：严格使用 Color.kt 中的真实颜色！
                Row(modifier = Modifier.clip(RoundedCornerShape(8.dp)).border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.3f), RoundedCornerShape(8.dp))) {
                    Box(modifier = Modifier.size(width = 24.dp, height = 36.dp).background(theme.primaryColor))
                    Box(modifier = Modifier.size(width = 24.dp, height = 36.dp).background(theme.containerColor))
                    Box(modifier = Modifier.size(width = 24.dp, height = 36.dp).background(theme.accentColor))
                }

                Text(
                    text = stringResource(id = theme.nameResId),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }

            if (isSelected) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}