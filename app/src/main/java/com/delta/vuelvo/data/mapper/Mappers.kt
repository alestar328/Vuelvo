package com.delta.vuelvo.data.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.delta.vuelvo.data.CommerceIcon
import com.delta.vuelvo.data.Reward
import com.delta.vuelvo.data.RewardStatus
import com.delta.vuelvo.data.StampCard
import com.delta.vuelvo.data.local.entity.StampCardEntity
import com.delta.vuelvo.data.local.entity.VuelvoRewardEntity

/** "#RRGGBB" → Color, tolerant of missing '#' and bad input (falls back to the accent tint). */
fun String.toColorOrAccent(): Color = try {
    val clean = removePrefix("#")
    Color(("FF" + clean.takeLast(6)).toLong(16))
} catch (_: Exception) {
    Color(0xFFEDE7FB)
}

/** Color → "#RRGGBB" (alpha dropped; the design palette is fully opaque). */
fun Color.toHex(): String = "#%06X".format(0xFFFFFF and toArgb())

fun String.toCommerceIcon(): CommerceIcon =
    runCatching { CommerceIcon.valueOf(this) }.getOrDefault(CommerceIcon.COFFEE)

// --- StampCard -------------------------------------------------------------

fun StampCardEntity.toUi(): StampCard = StampCard(
    id = id,
    name = name,
    category = category,
    icon = symbolName.toCommerceIcon(),
    tile = tileHex.toColorOrAccent(),
    ink = inkHex.toColorOrAccent(),
    stamps = stamps,
    max = maxStamps,
    reward = reward,
)

fun StampCard.toEntity(): StampCardEntity = StampCardEntity(
    id = id,
    name = name,
    category = category,
    symbolName = icon.name,
    tileHex = tile.toHex(),
    inkHex = ink.toHex(),
    stamps = stamps,
    maxStamps = max,
    reward = reward,
)

// --- Reward ----------------------------------------------------------------

fun VuelvoRewardEntity.toUi(): Reward = Reward(
    id = id,
    cardId = cardId,
    name = name,
    commerce = commerce,
    icon = symbolName.toCommerceIcon(),
    tile = tileHex.toColorOrAccent(),
    ink = inkHex.toColorOrAccent(),
    status = if (isAvailable) RewardStatus.AVAILABLE else RewardStatus.REDEEMED,
    date = dateLabel,
)

fun Reward.toEntity(createdAt: Long): VuelvoRewardEntity = VuelvoRewardEntity(
    id = id,
    cardId = cardId,
    name = name,
    commerce = commerce,
    symbolName = icon.name,
    tileHex = tile.toHex(),
    inkHex = ink.toHex(),
    isAvailable = status == RewardStatus.AVAILABLE,
    dateLabel = date,
    createdAt = createdAt,
)
