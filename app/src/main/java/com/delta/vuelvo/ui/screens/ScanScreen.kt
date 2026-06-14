package com.delta.vuelvo.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.delta.vuelvo.domain.ScanResult
import com.delta.vuelvo.ui.components.Stamps
import com.delta.vuelvo.ui.viewmodel.ExternalScan
import com.delta.vuelvo.ui.icons.VuelvoIcons
import com.delta.vuelvo.ui.theme.VuAccent
import com.delta.vuelvo.ui.theme.VuAccentDeep
import com.delta.vuelvo.ui.theme.VuAccentLight
import com.delta.vuelvo.ui.theme.VuAccentSoft
import com.delta.vuelvo.ui.theme.VuBg
import com.delta.vuelvo.ui.theme.VuInk
import com.delta.vuelvo.ui.theme.VuInk2
import com.delta.vuelvo.ui.theme.VuInk3
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class Phase { IDLE, SCANNING, SUCCESS }

@Composable
fun ScanScreen(
    onScan: suspend () -> ScanResult,
    pendingScan: ExternalScan?,
    onPendingConsumed: () -> Unit,
    onReward: (ScanResult) -> Unit,
    bottomInset: androidx.compose.ui.unit.Dp,
) {
    var phase by remember { mutableStateOf(Phase.IDLE) }
    var result by remember { mutableStateOf<ScanResult?>(null) }
    val scope = rememberCoroutineScope()

    fun resetScan() {
        phase = Phase.IDLE
        result = null
        onPendingConsumed()
    }

    // Show results pushed in from outside (NFC tag / deep link) without the simulated read.
    LaunchedEffect(pendingScan) {
        val res = pendingScan?.result ?: return@LaunchedEffect
        if (res.gotReward) {
            // Completing a card hands off to the full-screen celebration above the tab bar.
            onReward(res)
            resetScan()
            return@LaunchedEffect
        }
        result = res
        phase = Phase.SUCCESS
        delay(2800)
        resetScan()
    }

    fun tap() {
        if (phase == Phase.SUCCESS) { resetScan(); return }
        if (phase != Phase.IDLE) return
        phase = Phase.SCANNING
        scope.launch {
            delay(1300)
            val res = onScan()
            if (res.gotReward) {
                onReward(res)
                phase = Phase.IDLE
                result = null
            } else {
                result = res
                phase = Phase.SUCCESS
                delay(2800)
                resetScan()
            }
        }
    }

    Box(Modifier.fillMaxSize().background(VuBg)) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(start = 24.dp, end = 24.dp, bottom = bottomInset),
        ) {
            Column(Modifier.padding(top = 20.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    com.delta.vuelvo.ui.components.VuelvoMark(26.dp)
                    Text("Vuelvo", fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold, letterSpacing = (-0.3).sp, color = VuInk)
                }
                Text(
                    "Escanear",
                    modifier = Modifier.padding(top = 16.dp),
                    fontSize = 33.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                    letterSpacing = (-0.9).sp,
                    color = VuInk,
                )
            }

            Column(
                Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { tap() },
                ) {
                    NfcTarget(phase)
                }

                Column(
                    Modifier.padding(top = 18.dp).widthIn(max = 300.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val done = phase == Phase.SUCCESS
                    Text(
                        title(phase, result),
                        fontSize = 21.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                        letterSpacing = (-0.4).sp,
                        textAlign = TextAlign.Center,
                        color = if (done) VuAccentDeep else VuInk,
                    )
                    Text(
                        subtitle(phase, result),
                        modifier = Modifier.padding(top = 8.dp),
                        fontSize = 14.5.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                        lineHeight = 21.sp,
                        textAlign = TextAlign.Center,
                        color = VuInk2,
                    )

                    if (phase == Phase.SUCCESS && result != null) {
                        Box(Modifier.padding(top = 16.dp)) {
                            Stamps(
                                count = result!!.newCount,
                                max = result!!.max,
                                size = 20.dp,
                                gap = 7.dp,
                                popIndex = result!!.newCount - 1,
                                accentEmpty = true,
                            )
                        }
                    }

                }
            }

            Text(
                if (phase == Phase.IDLE) "Toca el círculo para simular el escaneo" else " ",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 12.5.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = VuInk3,
            )
        }
    }
}

private fun title(phase: Phase, r: ScanResult?): String = when (phase) {
    Phase.IDLE -> "Listo para escanear"
    Phase.SCANNING -> "Leyendo tag NFC…"
    Phase.SUCCESS -> if (r != null) "+1 sello · ${r.cardName}" else ""
}

private fun subtitle(phase: Phase, r: ScanResult?): String = when (phase) {
    Phase.IDLE -> "Acerca la parte superior de tu teléfono al tag NFC del comercio para sumar un sello."
    Phase.SCANNING -> "Mantén el teléfono cerca del tag."
    Phase.SUCCESS -> if (r != null) "Llevas ${r.newCount} de ${r.max} sellos." else ""
}

@Composable
private fun NfcTarget(phase: Phase) {
    val done = phase == Phase.SUCCESS
    val reading = phase == Phase.SCANNING

    // idle floating
    val floatT = rememberInfiniteTransition(label = "float")
    val floatY by floatT.animateFloatNullable(phase == Phase.IDLE)

    Box(
        Modifier.size(230.dp).offset(y = floatY.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (phase == Phase.IDLE) {
            PulseRing(0)
            PulseRing(600)
            PulseRing(1200)
        }
        // static halo
        Box(
            Modifier.size(188.dp).clip(CircleShape)
                .background(if (done) Color.Transparent else VuAccentSoft),
        )
        // core disc
        Box(
            Modifier
                .size(132.dp)
                .clip(CircleShape)
                .background(
                    if (done) Brush.linearGradient(listOf(VuAccentLight, VuAccent, VuAccentDeep))
                    else Brush.linearGradient(listOf(Color.White, Color(0xFFF3EEFE))),
                ),
            contentAlignment = Alignment.Center,
        ) {
            when {
                reading -> CircularProgressIndicator(
                    Modifier.size(52.dp),
                    color = VuAccent,
                    strokeWidth = 4.dp,
                    trackColor = VuAccentSoft,
                )
                done -> {
                    val s = remember { Animatable(0f) }
                    LaunchedEffect(Unit) {
                        s.animateTo(1f, keyframes { durationMillis = 500; 0f at 0; 1.2f at 300; 1f at 500 })
                    }
                    Icon(VuelvoIcons.Check, null, Modifier.size(62.dp).scale(s.value), tint = Color.White)
                }
                else -> Icon(VuelvoIcons.Nfc, null, Modifier.size(62.dp), tint = VuAccent)
            }
        }
    }
}

@Composable
private fun InfiniteTransition.animateFloatNullable(active: Boolean) =
    animateFloat(
        initialValue = 0f,
        targetValue = if (active) -6f else 0f,
        animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Reverse),
        label = "floatY",
    )

@Composable
private fun PulseRing(delayMillis: Int) {
    val t = rememberInfiniteTransition(label = "ring")
    val p by t.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            initialStartOffset = StartOffset(delayMillis),
        ),
        label = "ringP",
    )
    val scale = lerp(0.55f, 1.6f, p)
    val alpha = if (p < 0.3f) lerp(0f, 0.5f, p / 0.3f) else lerp(0.5f, 0f, (p - 0.3f) / 0.7f)
    Box(
        Modifier
            .size(230.dp)
            .scale(scale)
            .alpha(alpha)
            .border(2.dp, VuAccent, CircleShape),
    )
}
