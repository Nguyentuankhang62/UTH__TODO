package com.uth.uthtodo

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.uth.uthtodo.model.AppAppearanceState
import com.uth.uthtodo.navigation.AppNavGraph
import com.uth.uthtodo.ui.theme.UthTodoTheme

// Activity khởi chạy ứng dụng và cập nhật thanh trạng thái theo giao diện Sáng/Tối
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var appearanceState by remember { mutableStateOf(AppAppearanceState()) }
            val isDark = appearanceState.resolveDarkTheme(isSystemInDarkTheme())

            LaunchedEffect(isDark) {
                if (isDark) {
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
                        navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
                    )
                } else {
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
                        navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    )
                }
            }

            UthTodoTheme(appearanceState = appearanceState) {
                AppNavGraph(
                    appearanceState = appearanceState,
                    onAppearanceChange = { appearanceState = it }
                )
            }
        }
    }
}
