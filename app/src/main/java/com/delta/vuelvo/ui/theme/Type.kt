package com.delta.vuelvo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// The mockups use "Plus Jakarta Sans". To keep the build dependency-free we fall
// back to the platform sans here. Swap VuelvoFontFamily for a bundled font/res or
// a Google-Fonts downloadable family to match the design exactly.
val VuelvoFontFamily = FontFamily.Default

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = VuelvoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    )
)
