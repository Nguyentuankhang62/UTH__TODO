package com.uth.uthtodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.uth.uthtodo.navigation.AppNavGraph
import com.uth.uthtodo.ui.theme.UthTodoTheme

// Activity khởi chạy ứng dụng và thiết lập tràn viền an toàn cho Compose
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UthTodoTheme {
                AppNavGraph()
            }
        }
    }
}
