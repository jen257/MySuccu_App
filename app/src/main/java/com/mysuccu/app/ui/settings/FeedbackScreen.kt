package com.mysuccu.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mysuccu.app.R
import kotlinx.coroutines.launch

// 保持数据类结构
data class FeedbackOption(val labelResId: Int, val iconResId: Int)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FeedbackScreen(
    viewModel: SettingsViewModel = viewModel(),
    onBack: () -> Unit
) {
    val feedbackTypes = listOf(
        FeedbackOption(R.string.feedback_type_feature, R.drawable.lightbulb_ai_line),
        FeedbackOption(R.string.feedback_type_bug, R.drawable.ic_bug_custom),
        FeedbackOption(R.string.feedback_type_data, R.drawable.ic_edit_custom),
        FeedbackOption(R.string.feedback_type_other, R.drawable.ic_other_custom)
    )

    var selectedType by remember { mutableStateOf(feedbackTypes[0]) }
    var feedbackContent by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }
    var isAnonymous by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val successMsg = stringResource(R.string.feedback_success)
    val errorMsg = stringResource(R.string.feedback_error)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.feedback_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 类型选择
            Text(stringResource(R.string.feedback_type_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                feedbackTypes.forEach { option ->
                    FilterChip(
                        selected = selectedType == option,
                        onClick = { selectedType = option },
                        label = { Text(stringResource(option.labelResId)) },
                        leadingIcon = { Icon(painterResource(option.iconResId), null, modifier = Modifier.size(16.dp)) }
                    )
                }
            }

            // 内容
            OutlinedTextField(
                value = feedbackContent,
                onValueChange = { feedbackContent = it },
                modifier = Modifier.fillMaxWidth().height(180.dp),
                placeholder = { Text(stringResource(R.string.feedback_hint)) },
                shape = RoundedCornerShape(16.dp)
            )

            // 联系方式开关
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(stringResource(R.string.feedback_anonymous), fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.feedback_anonymous_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
                Switch(checked = isAnonymous, onCheckedChange = { isAnonymous = it; if(it) contactInfo = "" })
            }

            OutlinedTextField(
                value = contactInfo,
                onValueChange = { contactInfo = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.feedback_contact_title)) },
                placeholder = { Text(stringResource(R.string.feedback_contact_hint)) },
                enabled = !isAnonymous,
                shape = RoundedCornerShape(12.dp)
            )

            // 提交按钮 - 这里安全处理
            Button(
                onClick = {
                    scope.launch {
                        isSubmitting = true
                        val typeName = "" // 这里填入逻辑获取类型名
                        try {
                            val success = viewModel.submitFeedback(typeName, feedbackContent, contactInfo, isAnonymous)
                            if (success) {
                                snackbarHostState.showSnackbar(successMsg)
                                onBack()
                            } else {
                                snackbarHostState.showSnackbar(errorMsg)
                            }
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar(errorMsg)
                        }
                        isSubmitting = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = feedbackContent.isNotBlank() && !isSubmitting
            ) {
                if (isSubmitting) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                else Text(stringResource(R.string.feedback_submit))
            }
        }
    }
}