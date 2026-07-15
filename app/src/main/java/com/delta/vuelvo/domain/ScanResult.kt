package com.delta.vuelvo.domain

import com.delta.vuelvo.data.CommerceIcon
import com.delta.vuelvo.data.VuelvoStorage

/** Outcome of applying a stamp, consumed by the scan UI to render success vs. reward. */
data class ScanResult(
    val gotReward: Boolean,
    val cardName: String,
    val reward: String?,
    val newCount: Int,
    val max: Int,
    val icon: CommerceIcon,
    /** Logo of the comercio, shown instead of [icon] when the tag included one. */
    val logoRef: String? = null,
) {
    val logoUrl: String? get() = logoRef?.let(VuelvoStorage::downloadUrl)
}
