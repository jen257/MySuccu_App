package com.mysuccu.app

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.mysuccu.app.data.remote.SupabaseModule
import com.mysuccu.app.ui.navigation.AppNavHost
import com.mysuccu.app.ui.theme.MySuccuAppTheme
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.OutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as MySuccuApplication
        lifecycleScope.launch(Dispatchers.IO) {
            val currentUser = SupabaseModule.client.auth.currentUserOrNull()
            if (currentUser != null) {
                app.plantRepository.syncAllPendingData(currentUser.id)
            }
        }

        enableEdgeToEdge()
        setContent {
            MySuccuAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavHost(navController = navController)
                }
            }
        }
    }

    /**
     *  工业级原生函数：将本地 R.drawable.whatsapp_qrcode 真正写入手机系统相册
     * 确保没有加 private 关键字，允许外部直接穿透调用
     */
    fun saveQrCodeToGallery() {
        // 使用生命周期协程，在异步 IO 线程执行磁盘写入，防止卡死 UI 界面
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 1. 解析本地图片资源
                val bitmap = BitmapFactory.decodeResource(resources, R.drawable.whatsapp_qrcode) ?: return@launch

                // 2. 配置文件元数据描述
                val filename = "mysuccu_whatsapp_${System.currentTimeMillis()}.jpg"
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    // Android 10 (API 29) 及以上版本归入沙盒公共文件夹
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MySuccu")
                        put(MediaStore.Images.Media.IS_PENDING, 1) // 锁定管道
                    }
                }

                // 3. 插入媒体库索引，获取内容解算器的 Uri 门牌号
                val contentResolver = applicationContext.contentResolver
                val imageUri: Uri? = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )

                // 4. 开辟数据流通道硬核写入二进制
                imageUri?.let { uri ->
                    val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
                    outputStream.use { stream ->
                        if (stream != null) {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        }
                    }

                    // 写入完毕后解除 Pending 锁定，通知系统相册刷新视图
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentValues.clear()
                        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                        contentResolver.update(uri, contentValues, null, null)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}