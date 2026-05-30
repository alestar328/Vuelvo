package com.delta.vuelvo.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.delta.vuelvo.ui.icons.VuelvoIcons
import com.delta.vuelvo.ui.theme.VuAccent
import com.delta.vuelvo.ui.theme.VuAccentDeep
import com.delta.vuelvo.ui.theme.VuAccentLight
import com.delta.vuelvo.ui.theme.VuAccentLine
import com.delta.vuelvo.ui.theme.VuAccentSoft
import com.delta.vuelvo.ui.theme.VuStampEmpty

private fun stampCols(max: Int) = when {
    max % 5 == 0 -> 5
    max % 6 == 0 -> 6
    max % 4 == 0 -> 4
    else -> 5
}

/** Grid of stamp circles. [popIndex] pops one freshly-added stamp in. */
@Composable
fun Stamps(
    count: Int,
    max: Int,
    modifier: Modifier = Modifier,
    size: Dp = 26.dp,
    gap: Dp = 9.dp,
    popIndex: Int = -1,
    accentEmpty: Boolean = false,
) {
    val cols = stampCols(max)
    val rows = (max + cols - 1) / cols
    Column(modifier, verticalArrangement = Arrangement.spacedBy(gap)) {
        for (r in 0 until rows) {
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                for (c in 0 until cols) {
                    val i = r * cols + c
                    if (i < max) Stamp(i, count, max, size, popIndex, accentEmpty)
                }
            }
        }
    }
}

@Composable
private fun Stamp(
    i: Int,
    count: Int,
    max: Int,
    size: Dp,
    popIndex: Int,
    accentEmpty: Boolean,
) {
    val filled = i < count
    val isReward = i == max - 1
    val pop = remember { Animatable(if (i == popIndex) 0f else 1f) }
    LaunchedEffect(popIndex) {
        if (i == popIndex) {
            pop.snapTo(0f)
            pop.animateTo(
                1f,
                animationSpec = keyframes {
                    durationMillis = 450
                    0f at 0
                    1.18f at 270
                    1f at 450
                },
            )
        }
    }

    val base = Modifier
        .size(size)
        .scale(pop.value)
        .clip(CircleShape)

    val bg =
        if (filled) {
            base.background(Brush.linearGradient(listOf(VuAccentLight, VuAccent, VuAccentDeep)))
        } else {
            base
                .background(if (accentEmpty) VuAccentSoft else Color.Transparent)
                .border(1.6.dp, VuStampEmpty, CircleShape)
        }

    Box(bg, contentAlignment = Alignment.Center) {
        when {
            filled -> Icon(VuelvoIcons.Check, null, Modifier.size(size * 0.5f), tint = Color.White)
            isReward -> Box(Modifier.size(size * 0.26f).clip(CircleShape).background(VuAccentLine))
        }
    }
}
