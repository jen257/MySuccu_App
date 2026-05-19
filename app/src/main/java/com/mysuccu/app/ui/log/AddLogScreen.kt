package com.mysuccu.app.ui.log

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mysuccu.app.R
import com.mysuccu.app.ui.components.SuccuPullToRefresh
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddLogScreen(
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var notes by remember { mutableStateOf("") }

    // 🚀 从词典抓取动作选项
    val actions = listOf(
        stringResource(R.string.action_water),
        stringResource(R.string.action_fertilize),
        stringResource(R.string.action_repot),
        stringResource(R.string.action_bug),
        stringResource(R.string.action_photo),
        stringResource(R.string.action_other)
    )
    var selectedAction by remember { mutableStateOf(actions[0]) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.record_status),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    Button(
                        onClick = onSave,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(stringResource(R.string.save), fontWeight = FontWeight.Bold)
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
                // 1. 动作类型选择区 (FlowRow 实现自动换行的流式布局)
                item {
                    Text(
                        text = "选择养护动作",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        actions.forEach { action ->
                            val isSelected = selectedAction == action
                            Surface(
                                onClick = { selectedAction = action },
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
                            ) {
                                Text(
                                    text = action,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }

                // 2. 备注输入区
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text(stringResource(R.string.description)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        shape = RoundedCornerShape(16.dp),
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }
            }
        }
    }
}