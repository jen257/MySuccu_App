package com.mysuccu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mysuccu.app.ui.home.HomeScreen
import com.mysuccu.app.ui.screens.SplashScreen
import com.mysuccu.app.ui.theme.MySuccuAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MySuccuAppTheme {
                var isSplashFinished by remember { mutableStateOf(false) }
                if (!isSplashFinished) {
                    // 如果没结束，就展示我们的全屏启动页
                    SplashScreen(
                        onSplashFinished = {
                            isSplashFinished = true
                        }
                    )
                } else {
                    // 状态变成 true 后，无缝切换到我们刚刚画好的 3x3 首页！
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MySuccuAppTheme {
        Greeting("Android")
    }
}