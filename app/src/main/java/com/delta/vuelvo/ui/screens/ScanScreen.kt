package com.delta.vuelvo.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseIn
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
import androidx.compose.foundation.layout.BoxWithConstraints
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
import com.delta.vuelvo.ui.ScanResult
import com.delta.vuelvo.ui.components.Stamps
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

private enum class Phase { IDLE, SCANNING, SUCCESS, REWARD }

@Composable
fun ScanScreen(onScan: () -> ScanResult, bottomInset: androidx.compose.ui.unit.Dp) {
    var phase by remember { mutableStateOf(Phase.IDLE) }
    var result by remember { mutableStateOf<ScanResult?>(null) }
    val scope = rememberCoroutineScope()

    fun tap() {
        if (phase == Phase.SUCCESS || phase == Phase.REWARD) {
            phase = Phase.IDLE; result = null; return
        }
        if (phase != Phase.IDLE) return
        phase = Phase.SCANNING
        scope.launch {
            delay(1300)
            val res = onScan()
            result = res
            phase = if (res.gotReward) Phase.REWARD else Phase.SUCCESS
            delay(if (res.gotReward) 4200 else 2800)
            phase = Phase.IDLE
            result = null
        }
    }

    Box(Modifier.fillMaxSize().background(VuBg)) {
        if (phase == Phase.REWARD) Confetti()

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
                    val done = phase == Phase.SUCCESS || phase == Phase.REWARD
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

                    if (phase == Phase.REWARD && result != null) {
                        Row(
                            Modifier
                                .padding(top = 14.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(Brush.linearGradient(listOf(VuAccent, VuAccentDeep)))
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(VuelvoIcons.Gift, null, Modifier.size(18.dp), tint = Color.White)
                            Text(result!!.reward ?: "", fontSize = 14.5.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color.White)
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
    Phase.REWARD -> "¡Recompensa conseguida!"
}

private fun subtitle(phase: Phase, r: ScanResult?): String = when (phase) {
    Phase.IDLE -> "Acerca la parte superior de tu teléfono al tag NFC del comercio para sumar un sello."
    Phase.SCANNING -> "Mantén el teléfono cerca del tag."
    Phase.SUCCESS -> if (r != null) "Llevas ${r.newCount} de ${r.max} sellos." else ""
    Phase.REWARD -> if (r != null) "${r.reward} en ${r.cardName}. Disponible en Recompensas." else ""
}

@Composable
private fun NfcTarget(phase: Phase) {
    val done = phase == Phase.SUCCESS || phase == Phase.REWARD
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

@Composable
private fun Confetti() {
    val colors = listOf(
        Color(0xFF9B5CFF), Color(0xFF7B3CE6), Color(0xFFFFC24B),
        Color(0xFFFF6FA5), Color(0xFF4CD3A5), Color(0xFFB083FF),
    )
    data class Piece(val x: Float, val delay: Int, val sizeDp: Float, val color: Color)
    val pieces = remember {
        List(26) {
            Piece(
                x = Math.random().toFloat(),
                delay = (Math.random() * 400).toInt(),
                sizeDp = 6f + (Math.random() * 7f).toFloat(),
                color = colors[it % colors.size],
            )
        }
    }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val w = maxWidth
        val h = maxHeight
        pieces.forEach { p ->
            val anim = remember { Animatable(0f) }
            LaunchedEffect(Unit) {
                delay(p.delay.toLong())
                anim.animateTo(1f, tween(1400, easing = EaseIn))
            }
            Box(
                Modifier
                    .offset(x = w * p.x, y = h * 0.34f + (220.dp * anim.value))
                    .size(width = p.sizeDp.dp, height = (p.sizeDp * 1.4f).dp)
                    .alpha(1f - anim.value)
                    .scale(1f)
                    .clip(RoundedCornerShape(2.dp))
                    .background(p.color),
            )
        }
    }
}
