package com.delta.vuelvo.ui.screens

import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.delta.vuelvo.data.Reward
import com.delta.vuelvo.data.vector
import com.delta.vuelvo.ui.components.VuelvoMark
import com.delta.vuelvo.ui.icons.VuelvoIcons
import com.delta.vuelvo.ui.theme.VuAccentDeep
import com.delta.vuelvo.ui.theme.VuAccentSoft
import com.delta.vuelvo.ui.theme.VuBg
import com.delta.vuelvo.ui.theme.VuInk
import com.delta.vuelvo.ui.theme.VuInk2
import com.delta.vuelvo.ui.theme.VuInk3
import com.delta.vuelvo.ui.theme.VuStampEmpty
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val PassGradient = Brush.linearGradient(
    listOf(Color(0xFFA968FF), Color(0xFF9B5CFF), Color(0xFF7B3CE6)),
)

/**
 * "Pase en vivo" redemption (design option C, `vuelvo-redeem.jsx`): a prep sheet with a
 * slide-to-activate, then a full-screen live pass the clerk only has to glance at.
 */
@Composable
fun RedeemFlow(reward: Reward, onConfirm: () -> Unit, onClose: () -> Unit) {
    var live by remember { mutableStateOf(false) }
    if (live) {
        LivePass(reward = reward, onRedeemed = onConfirm, onClose = onClose)
    } else {
        RedeemPrep(reward = reward, onActivate = { live = true }, onClose = onClose)
    }
}

@Composable
private fun RedeemPrep(reward: Reward, onActivate: () -> Unit, onClose: () -> Unit) {
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
                .background(Color.White)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { /* swallow */ }
                .navigationBarsPadding()
                .padding(start = 22.dp, end = 22.dp, top = 12.dp, bottom = 38.dp),
        ) {
            Box(
                Modifier
                    .padding(bottom = 22.dp)
                    .align(Alignment.CenterHorizontally)
                    .size(width = 40.dp, height = 5.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(VuStampEmpty),
            )

            // reward header
            Row(
                Modifier.fillMaxWidth().padding(bottom = 22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(reward.tile),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(reward.icon.vector, null, Modifier.size(30.dp), tint = reward.ink)
                }
                Column(Modifier.weight(1f)) {
                    Text(reward.name, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp, color = VuInk)
                    Text(reward.commerce, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = VuInk2)
                }
            }

            // how-it-works steps
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(VuBg)
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                StepRow(1, "Activa el pase al pagar")
                StepRow(2, "Enséñale la pantalla al dependiente")
                StepRow(3, "Solo tiene que mirarla — sin escáner")
            }

            SlideToActivate(onConfirm = onActivate)

            Text(
                "El pase caduca a los 60 s para evitar capturas de pantalla",
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = VuInk3,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun StepRow(n: Int, text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            Modifier.size(24.dp).clip(CircleShape).background(VuAccentSoft),
            contentAlignment = Alignment.Center,
        ) {
            Text("$n", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = VuAccentDeep)
        }
        Text(
            text,
            fontSize = 14.5.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 20.sp,
            color = VuInk,
        )
    }
}

@Composable
private fun SlideToActivate(onConfirm: () -> Unit) {
    val density = LocalDensity.current
    val thumb = 58.dp
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    var maxOffset by remember { mutableStateOf(0f) }

    BoxWithConstraints(
        Modifier
            .fillMaxWidth()
            .height(66.dp)
            .clip(RoundedCornerShape(19.dp))
            .background(VuBg)
            .border(1.5.dp, VuStampEmpty, RoundedCornerShape(19.dp)),
    ) {
        val trackPx = with(density) { maxWidth.toPx() }
        val thumbPx = with(density) { thumb.toPx() }
        maxOffset = (trackPx - thumbPx - with(density) { 8.dp.toPx() }).coerceAtLeast(0f)
        val pct = if (maxOffset > 0) offsetX.value / maxOffset else 0f

        // fill
        Box(
            Modifier
                .fillMaxSize()
                .alpha(pct)
                .background(Brush.linearGradient(listOf(Color(0xFF9B5CFF), VuAccentDeep))),
        )
        Text(
            "Desliza para activar el pase",
            modifier = Modifier.fillMaxSize().alpha(1f - pct * 0.5f).padding(start = thumb),
            fontSize = 15.5.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.2).sp,
            color = if (pct > 0.4f) Color.White else VuInk2,
            textAlign = TextAlign.Center,
        )

        Box(
            Modifier
                .offset { androidx.compose.ui.unit.IntOffset(offsetX.value.toInt() + with(density) { 4.dp.roundToPx() }, 0) }
                .align(Alignment.CenterStart)
                .size(thumb)
                .clip(RoundedCornerShape(15.dp))
                .background(Color.White)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        scope.launch {
                            offsetX.snapTo((offsetX.value + delta).coerceIn(0f, maxOffset))
                            if (offsetX.value >= maxOffset - 1f && maxOffset > 0f) onConfirm()
                        }
                    },
                    onDragStopped = {
                        if (offsetX.value < maxOffset - 1f) offsetX.animateTo(0f, tween(220))
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(VuelvoIcons.Chevron, null, Modifier.size(25.dp), tint = VuAccentDeep)
        }
    }
}

@Composable
private fun LivePass(reward: Reward, onRedeemed: () -> Unit, onClose: () -> Unit) {
    val duration = 60
    var left by remember { mutableStateOf(duration) }
    var clock by remember { mutableStateOf(nowClock()) }
    var phase by remember { mutableStateOf(PassPhase.LIVE) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            clock = nowClock()
            if (phase == PassPhase.LIVE) {
                left -= 1
                if (left <= 0) { left = 0; phase = PassPhase.EXPIRED }
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(PassGradient),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 28.dp),
        ) {
            // top bar
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                VuelvoMark(26.dp)
                Text(
                    "Vuelvo",
                    modifier = Modifier.padding(start = 9.dp).weight(1f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.3).sp,
                    color = Color.White,
                )
                Box(
                    Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x29FFFFFF))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { onClose() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("×", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }

            if (phase == PassPhase.DONE) {
                PassDone(reward, onRedeemed)
            } else {
                PassActive(reward, phase, left, clock, onReactivate = { left = duration; phase = PassPhase.LIVE }, onValidated = { phase = PassPhase.DONE })
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.ColumnScope.PassActive(
    reward: Reward,
    phase: PassPhase,
    left: Int,
    clock: String,
    onReactivate: () -> Unit,
    onValidated: () -> Unit,
) {
    val live = phase == PassPhase.LIVE
    Column(
        Modifier.weight(1f).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // spinning ring centerpiece
        Box(Modifier.size(176.dp), contentAlignment = Alignment.Center) {
            if (live) {
                Sonar(0)
                Sonar(800)
                Sonar(1600)
            }
            Box(Modifier.size(150.dp).clip(CircleShape).border(3.dp, Color(0x38FFFFFF), CircleShape))
            val spinT = rememberInfiniteTransition(label = "spin")
            val spin by spinT.animateFloat(
                0f, 360f,
                infiniteRepeatable(tween(2400, easing = LinearEasing), RepeatMode.Restart),
                label = "rot",
            )
            val rot = if (live) spin else 0f
            Box(
                Modifier
                    .size(150.dp)
                    .rotate(rot)
                    .alpha(if (live) 1f else 0.3f)
                    .border(3.dp, ArcBrush, CircleShape),
            )
            Box(
                Modifier.size(104.dp).clip(CircleShape).background(Color(0x2EFFFFFF)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(reward.icon.vector, null, Modifier.size(50.dp), tint = Color.White)
            }
        }

        Text(
            if (live) "CANJE ACTIVO" else "PASE CADUCADO",
            modifier = Modifier.padding(top = 26.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.6.sp,
            color = Color.White.copy(alpha = 0.85f),
        )
        Text(
            reward.name,
            modifier = Modifier.padding(top = 6.dp),
            fontSize = 31.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.7).sp,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
        Row(
            Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(reward.commerce, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.92f))
            Box(
                Modifier.size(18.dp).clip(CircleShape).background(Color(0x40FFFFFF)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(VuelvoIcons.Check, null, Modifier.size(11.dp), tint = Color.White)
            }
        }

        // status pills / reactivate
        Box(Modifier.padding(top = 24.dp)) {
            if (live) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Pill {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF7BF5B0)))
                        Text("En vivo", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Pill {
                        Icon(VuelvoIcons.Nfc, null, Modifier.size(17.dp), tint = Color.White)
                        Text("${left}s", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            } else {
                Row(
                    Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { onReactivate() }
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("↻ Reactivar pase", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = VuAccentDeep)
                }
            }
        }

        // live timestamp watermark
        Text(
            "$clock · ${reward.commerce.uppercase()}",
            modifier = Modifier.padding(top = 16.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            fontFamily = FontFamily.Monospace,
            color = Color.White.copy(alpha = 0.7f),
        )
    }

    Column {
        Text(
            "Enséñale esta pantalla al dependiente.\nLa animación cambia en tiempo real: no se puede falsear con una foto.",
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            fontSize = 14.5.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 21.sp,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
        )
        Text(
            "El dependiente lo ha validado",
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (live) 1f else 0.5f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable(
                    enabled = live,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onValidated() }
                .padding(vertical = 17.dp),
            fontSize = 16.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color = VuAccentDeep,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun androidx.compose.foundation.layout.ColumnScope.PassDone(reward: Reward, onRedeemed: () -> Unit) {
    LaunchedEffect(Unit) { delay(1100); onRedeemed() }
    Column(
        Modifier.weight(1f).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val s = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            s.animateTo(1f, keyframes { durationMillis = 500; 0f at 0; 1.2f at 300; 1f at 500 })
        }
        Box(
            Modifier.size(96.dp).clip(CircleShape).background(Color(0x33FFFFFF)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(VuelvoIcons.Check, null, Modifier.size(50.dp).scale(s.value), tint = Color.White)
        }
        Text(
            "¡Disfrútalo!",
            modifier = Modifier.padding(top = 18.dp),
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp,
            color = Color.White,
        )
        Text(
            "${reward.name} canjeado",
            modifier = Modifier.padding(top = 6.dp),
            fontSize = 15.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White.copy(alpha = 0.9f),
        )
    }
}

@Composable
private fun Pill(content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit) {
    Row(
        Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0x2EFFFFFF))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        content = content,
    )
}

@Composable
private fun Sonar(delayMillis: Int) {
    val t = rememberInfiniteTransition(label = "sonar")
    val p by t.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            initialStartOffset = StartOffset(delayMillis),
        ),
        label = "sonarP",
    )
    val scale = 0.75f + p * 0.45f
    val alpha = (1f - p) * 0.6f
    Box(
        Modifier
            .size(150.dp)
            .scale(scale)
            .alpha(alpha)
            .border(2.dp, Color.White, CircleShape),
    )
}

private enum class PassPhase { LIVE, EXPIRED, DONE }

/** Half-transparent ring used for the rotating "C" arc (top + left visible). */
private val ArcBrush = Brush.sweepGradient(
    0f to Color.White,
    0.25f to Color.White,
    0.5f to Color.Transparent,
    0.75f to Color.Transparent,
    1f to Color.White,
)

private fun nowClock(): String =
    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
