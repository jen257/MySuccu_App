package com.mysuccu.app.ui.settings

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mysuccu.app.R
import com.mysuccu.app.ui.components.SuccuPullToRefresh
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    onBack: () -> Unit,
    onSaveQrCode: () -> Unit // 🚀 1. 核心新增：向外暴露真正保存图片到相册的接口
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showContactBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showWhatsAppDialog by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current
    val copiedMessage = stringResource(R.string.premium_copied)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 24.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showContactBottomSheet = true },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 14.dp, horizontal = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = stringResource(R.string.premium_monthly_title), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = stringResource(R.string.premium_monthly_price), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1.2f)
                                .clickable { showContactBottomSheet = true },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 14.dp, horizontal = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = stringResource(R.string.premium_lifetime_title), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = stringResource(R.string.premium_lifetime_price), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        SuccuPullToRefresh(
            isRefreshing = isRefreshing,
            onRefresh = { isRefreshing = true; scope.launch { delay(1000); isRefreshing = false } },
            modifier = Modifier.padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                            Icon(painter = painterResource(id = R.drawable.ic_vip_crown_custom), contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.premium_title), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(R.string.premium_subtitle), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.outline, textAlign = TextAlign.Center)
                    }
                }

                item {
                    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)) {
                        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            PremiumFeatureRow(stringResource(R.string.premium_feature_1))
                            PremiumFeatureRow(stringResource(R.string.premium_feature_2))
                            PremiumFeatureRow(stringResource(R.string.premium_feature_3))
                            PremiumFeatureRow(stringResource(R.string.premium_feature_4))
                        }
                    }
                }

                item {
                    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f), border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondaryContainer)) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                                Icon(painter = painterResource(id = R.drawable.ic_file_shield_custom), contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = stringResource(R.string.premium_disclaimer_title), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Text(text = stringResource(R.string.premium_disclaimer_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                        }
                    }
                }
            }
        }
    }

    if (showContactBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showContactBottomSheet = false },
            sheetState = bottomSheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 8.dp, bottom = 48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(R.string.premium_contact_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(bottom = 24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString("jenniec257"))
                            scope.launch {
                                bottomSheetState.hide()
                                showContactBottomSheet = false
                                snackbarHostState.showSnackbar(copiedMessage)
                            }
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                    ) {
                        Icon(painter = painterResource(id = R.drawable.ic_copy_custom), contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.premium_wechat), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    FilledTonalButton(
                        onClick = { showWhatsAppDialog = true },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                    ) {
                        Text(stringResource(R.string.premium_whatsapp), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showWhatsAppDialog) {
        AlertDialog(
            onDismissRequest = { showWhatsAppDialog = false },
            title = { Text(text = stringResource(R.string.premium_whatsapp_qr_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    val context = LocalContext.current
                    val savedMessage = stringResource(R.string.premium_qr_saved_toast)

                    Image(
                        painter = painterResource(id = R.drawable.whatsapp_qrcode),
                        contentDescription = "WhatsApp QR Code",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(220.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        // 🚀 2. 核心联动：长按瞬间不仅弹窗，同时触发原生保存流
                                        onSaveQrCode()
                                        Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                    )
                    Text(text = stringResource(R.string.premium_whatsapp_qr_hint), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline, textAlign = TextAlign.Center, lineHeight = 16.sp, modifier = Modifier.padding(horizontal = 8.dp))
                }
            },
            confirmButton = {
                TextButton(onClick = { showWhatsAppDialog = false }) {
                    Text(stringResource(R.string.close), fontWeight = FontWeight.Bold)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun PremiumFeatureRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}