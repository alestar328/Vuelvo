package com.delta.vuelvo.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val VuelvoColorScheme = lightColorScheme(
    primary = VuAccent,
    onPrimary = Color.White,
    secondary = VuAccentDeep,
    background = VuBg,
    onBackground = VuInk,
    surface = VuCard,
    onSurface = VuInk,
)

@Composable
fun VuelvoTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = true
            controller.isAppearanceLightNavigationBars = true
        }
    }
    MaterialTheme(
        colorScheme = VuelvoColorScheme,
        typography = Typography,
        content = content,
    )
}
