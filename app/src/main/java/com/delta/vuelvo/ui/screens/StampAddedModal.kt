package com.delta.vuelvo.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseIn
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.delta.vuelvo.data.vector
import com.delta.vuelvo.domain.ScanResult
import com.delta.vuelvo.ui.components.Stamps
import com.delta.vuelvo.ui.components.VuelvoMark
import com.delta.vuelvo.ui.icons.VuelvoIcons
import com.delta.vuelvo.ui.theme.VuAccentDeep
import kotlinx.coroutines.delay

private val ModalGradient = Brush.linearGradient(
    listOf(Color(0xFFA968FF), Color(0xFF9B5CFF), Color(0xFF7B3CE6)),
)

/**
 * Full-screen celebration shown after a scan completes a stamp card (`result.gotReward`).
 * Mirrors `vuelvo-stamp-modal.jsx` — also renders the plain "+1 sello" variant for reuse.
 */
@Composable
fun StampAddedModal(
    result: ScanResult,
    onClose: () -> Unit,
    onViewReward: () -> Unit,
) {
    val reward = result.gotReward
    val left = result.max - result.newCount
    val almost = !reward && left == 1

    Box(
        Modifier
            .fillMaxSize()
            .background(ModalGradient),
    ) {
        if (reward) Confetti()

        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 28.dp),
        ) {
            // top bar: brand + close
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
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

            // centerpiece
            Column(
                Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                IconDisc(result, reward)

                Text(
                    if (reward) "¡TARJETA COMPLETA!" else "SELLO AÑADIDO",
                    modifier = Modifier.padding(top = 22.dp),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.6.sp,
                    color = Color.White.copy(alpha = 0.85f),
                )
                Text(
                    if (reward) "¡Recompensa lista!" else "¡Suma y sigue!",
                    modifier = Modifier.padding(top = 6.dp),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.7).sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
                Text(
                    result.cardName,
                    modifier = Modifier.padding(top = 3.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.92f),
                )

                // stamp grid on a glass card
                Column(
                    Modifier
                        .padding(top = 24.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0x29FFFFFF))
                        .padding(horizontal = 22.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Stamps(
                        count = result.newCount,
                        max = result.max,
                        size = 26.dp,
                        gap = 10.dp,
                        popIndex = result.newCount - 1,
                    )
                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text(
                            "${result.newCount}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.4).sp,
                            color = Color.White,
                        )
                        Text(
                            "/${result.max}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.4).sp,
                            color = Color.White.copy(alpha = 0.65f),
                        )
                        Text(
                            "sellos",
                            modifier = Modifier.padding(start = 2.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f),
                        )
                    }
                }

                // status line / reward chip
                if (reward) {
                    Row(
                        Modifier
                            .padding(top = 18.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color.White)
                            .padding(horizontal = 18.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(VuelvoIcons.Gift, null, Modifier.size(18.dp), tint = VuAccentDeep)
                        Text(
                            result.reward ?: "",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = VuAccentDeep,
                        )
                    }
                } else {
                    Text(
                        if (almost) "¡Solo te falta 1 sello para tu recompensa!"
                        else "Te faltan $left sellos para tu recompensa",
                        modifier = Modifier.padding(top = 18.dp, start = 16.dp, end = 16.dp),
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 22.sp,
                        color = Color.White.copy(alpha = 0.95f),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            // footer actions
            Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
                if (reward) {
                    FooterButton("Ver mi recompensa", primary = true, onClick = onViewReward)
                    FooterButton("Seguir escaneando", primary = false, onClick = onClose)
                } else {
                    FooterButton("Hecho", primary = true, onClick = onClose)
                }
            }
        }
    }
}

@Composable
private fun FooterButton(label: String, primary: Boolean, onClick: () -> Unit) {
    Text(
        label,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (primary) Color.White else Color(0x29FFFFFF))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(vertical = if (primary) 17.dp else 14.dp),
        fontSize = if (primary) 16.5.sp else 15.sp,
        fontWeight = if (primary) FontWeight.ExtraBold else FontWeight.Bold,
        color = if (primary) VuAccentDeep else Color.White,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun IconDisc(result: ScanResult, reward: Boolean) {
    Box(
        Modifier.size(184.dp),
        contentAlignment = Alignment.Center,
    ) {
        Sonar(0)
        Sonar(700)
        Sonar(1400)

        val pop = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            pop.animateTo(1f, keyframes { durationMillis = 500; 0f at 0; 1.18f at 270; 1f at 500 })
        }
        Box(
            Modifier
                .size(122.dp)
                .scale(pop.value)
                .clip(CircleShape)
                .background(Color(0x29FFFFFF)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(result.icon.vector, null, Modifier.size(58.dp), tint = Color.White)
        }

        // +1 / check badge
        val badgePop = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            delay(150)
            badgePop.animateTo(1f, keyframes { durationMillis = 550; 0f at 0; 1.2f at 330; 1f at 550 })
        }
        Box(
            Modifier
                .offset(x = 28.dp, y = 40.dp)
                .scale(badgePop.value)
                .size(46.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            if (reward) {
                Icon(VuelvoIcons.Check, null, Modifier.size(24.dp), tint = VuAccentDeep)
            } else {
                Text("+1", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = VuAccentDeep)
            }
        }
    }
}

@Composable
private fun Sonar(delayMillis: Int) {
    val t = rememberInfiniteTransition(label = "sonar")
    val p by t.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = LinearEasing),
            initialStartOffset = StartOffset(delayMillis),
        ),
        label = "sonarP",
    )
    val scale = 0.6f + p * 0.6f
    val alpha = (1f - p) * 0.5f
    Box(
        Modifier
            .size(150.dp)
            .scale(scale)
            .alpha(alpha)
            .clip(CircleShape)
            .background(Color.Transparent)
            .border(2.dp, Color.White, CircleShape),
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
        List(30) {
            Piece(
                x = Math.random().toFloat(),
                delay = (Math.random() * 350).toInt(),
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
                    .offset(x = w * p.x, y = h * 0.32f + (h * 0.6f * anim.value))
                    .size(width = p.sizeDp.dp, height = (p.sizeDp * 1.4f).dp)
                    .alpha(1f - anim.value)
                    .clip(RoundedCornerShape(2.dp))
                    .background(p.color),
            )
        }
    }
}
