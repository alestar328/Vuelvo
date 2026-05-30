package com.delta.vuelvo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.delta.vuelvo.ui.VuelvoApp
import com.delta.vuelvo.ui.theme.VuelvoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VuelvoTheme {
                VuelvoApp()
            }
        }
    }
}
