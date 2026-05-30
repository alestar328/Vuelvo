package com.delta.vuelvo.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.delta.vuelvo.data.Reward
import com.delta.vuelvo.ui.theme.VuAccent
import com.delta.vuelvo.ui.theme.VuAccentDeep
import com.delta.vuelvo.ui.theme.VuBg
import com.delta.vuelvo.ui.theme.VuCard
import com.delta.vuelvo.ui.theme.VuInk
import com.delta.vuelvo.ui.theme.VuInk2
import com.delta.vuelvo.ui.theme.VuStampEmpty

@Composable
fun RedeemSheet(reward: Reward, onConfirm: () -> Unit, onClose: () -> Unit) {
    val enter = remember { Animatable(0f) }
    LaunchedEffect(Unit) { enter.animateTo(1f, tween(280)) }

    Box(
        Modifier
            .fillMaxSize()
            .alpha(enter.value)
            .background(Color(0x6B14101E))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClose() },
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .offset(y = ((1f - enter.value) * 40).dp)
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(VuCard)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { /* swallow */ }
                .navigationBarsPadding()
                .padding(start = 22.dp, end = 22.dp, top = 12.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier.padding(bottom = 22.dp).size(width = 40.dp, height = 5.dp)
                    .clip(RoundedCornerShape(99.dp)).background(VuStampEmpty),
            )
            Text(reward.commerce.uppercase(), fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.6.sp, color = VuAccentDeep)
            Text(
                reward.name,
                modifier = Modifier.padding(top = 3.dp, bottom = 18.dp),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp,
                color = VuInk,
            )

            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(VuBg).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    Modifier.clip(RoundedCornerShape(18.dp)).background(Color.White).padding(16.dp),
                ) {
                    FakeQR(seed = reward.id.length * 13 + reward.name.length)
                }
                Text(
                    "Muestra este código en el comercio",
                    modifier = Modifier.padding(top = 16.dp),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = VuInk2,
                )
                Text(
                    "VLV·${reward.id.uppercase()}·26",
                    modifier = Modifier.padding(top = 6.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                    fontFamily = FontFamily.Monospace,
                    color = VuInk,
                )
            }

            Text(
                "Marcar como canjeada",
                modifier = Modifier
                    .padding(top = 18.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(VuAccent, VuAccentDeep)))
                    .clickable { onConfirm() }
                    .padding(vertical = 16.dp),
                fontSize = 16.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

/** Deterministic pseudo-QR (matches vuelvo-rewards.jsx FakeQR). */
@Composable
private fun FakeQR(seed: Int, sizeDp: Int = 168) {
    val ink = VuInk
    val accent = VuAccent
    Canvas(Modifier.size(sizeDp.dp)) {
        val n = 13
        val cell = size.width / n
        var s = (seed * 9301 + 49297).toLong()
        fun rnd(): Double {
            s = (s * 9301 + 49297) % 233280
            return s / 233280.0
        }
        val r = cell * 0.18f
        for (y in 0 until n) {
            for (x in 0 until n) {
                val finder = (x < 3 && y < 3) || (x > n - 4 && y < 3) || (x < 3 && y > n - 4)
                if (finder) continue
                if (rnd() > 0.52) {
                    drawRoundRect(
                        color = ink,
                        topLeft = Offset(x * cell, y * cell),
                        size = Size(cell, cell),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r),
                    )
                }
            }
        }
        fun finder(fx: Int, fy: Int) {
            drawRoundRect(ink, Offset(fx * cell, fy * cell), Size(cell * 3, cell * 3), androidx.compose.ui.geometry.CornerRadius(cell * 0.6f, cell * 0.6f))
            drawRoundRect(Color.White, Offset((fx + 0.55f) * cell, (fy + 0.55f) * cell), Size(cell * 1.9f, cell * 1.9f), androidx.compose.ui.geometry.CornerRadius(cell * 0.4f, cell * 0.4f))
            drawRoundRect(accent, Offset((fx + 0.95f) * cell, (fy + 0.95f) * cell), Size(cell * 1.1f, cell * 1.1f), androidx.compose.ui.geometry.CornerRadius(cell * 0.25f, cell * 0.25f))
        }
        finder(0, 0); finder(n - 3, 0); finder(0, n - 3)
    }
}
