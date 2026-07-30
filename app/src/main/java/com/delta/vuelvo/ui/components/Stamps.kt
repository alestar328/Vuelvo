package com.delta.vuelvo.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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

/** A single row never goes past this; more than 7 circles in a line reads as a strip, not a card. */
private const val MAX_STAMP_COLS = 7

/**
 * Columns for a fixed-size stamp: as many as fit in [available] at the requested [size],
 * then evened out over the resulting rows so the grid spreads across the width instead of
 * hugging the left edge and no row is left nearly empty.
 */
private fun stampCols(max: Int, available: Dp, size: Dp, gap: Dp): Int {
    val perRow = (((available + gap).value / (size + gap).value).toInt())
        .coerceIn(1, minOf(max, MAX_STAMP_COLS))
    val rows = (max + perRow - 1) / perRow
    return (max + rows - 1) / rows
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
    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        val cols =
            if (constraints.hasBoundedWidth) stampCols(max, maxWidth, size, gap)
            else minOf(max, MAX_STAMP_COLS)
        val rows = (max + cols - 1) / cols
        // Given a fixed width (caller passed fillMaxWidth) the columns spread over it instead of
        // packing at the left edge. The stamp keeps its size; only the gap opens up, and not past
        // 1.5x the stamp so a short row still reads as one group instead of scattered dots.
        val colGap =
            if (constraints.hasFixedWidth && cols > 1) {
                ((maxWidth - size * cols) / (cols - 1)).coerceIn(gap, maxOf(size * 1.5f, gap))
            } else {
                gap
            }
        val gridWidth = size * cols + colGap * (cols - 1)
        Column(verticalArrangement = Arrangement.spacedBy(gap)) {
            for (r in 0 until rows) {
                Row(
                    Modifier.width(gridWidth),
                    horizontalArrangement = Arrangement.spacedBy(colGap, Alignment.CenterHorizontally),
                ) {
                    for (c in 0 until cols) {
                        val i = r * cols + c
                        if (i < max) Stamp(i, count, max, size, popIndex, accentEmpty)
                    }
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
