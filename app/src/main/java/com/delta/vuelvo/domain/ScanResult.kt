package com.delta.vuelvo.domain

import com.delta.vuelvo.data.CommerceIcon

/** Outcome of applying a stamp, consumed by the scan UI to render success vs. reward. */
data class ScanResult(
    val gotReward: Boolean,
    val cardName: String,
    val reward: String?,
    val newCount: Int,
    val max: Int,
    val icon: CommerceIcon,
)
