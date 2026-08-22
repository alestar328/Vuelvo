package com.delta.vuelvo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persisted loyalty card. Visual fields are stored as strings (hex colours and an
 * icon symbol name) so the data layer stays free of Compose types; the presentation
 * layer maps these into [androidx.compose.ui.graphics.Color] and [com.delta.vuelvo.data.CommerceIcon].
 */
@Entity(tableName = "stamp_cards")
data class StampCardEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val symbolName: String,
    val tileHex: String,
    val inkHex: String,
    val stamps: Int,
    val maxStamps: Int,
    val reward: String,
    /** Per-tag unique identifier from the scanned deep link; null for cards created before it existed. */
    val uuid: String? = null,
    /** Merchant's `businessCode` — key of its `businesses/{code}` Firestore record (see
     * [com.delta.vuelvo.data.BusinessStatusChecker]) and, since it superseded [id] as the primary match
     * key for an existing card, the field a rescan matches on first. Null for cards created before this
     * existed, or from a tag that still doesn't carry one. */
    val businessCode: String? = null,
    /** Firebase Storage object references for the logo/cover (see [com.delta.vuelvo.data.VuelvoStorage]);
     * null for cards created before this existed or whose tag didn't set one. */
    val logoRef: String? = null,
    val coverRef: String? = null,
) {
    val isReady: Boolean get() = stamps >= maxStamps
    val remaining: Int get() = maxStamps - stamps
    val progressFraction: Double get() = if (maxStamps == 0) 0.0 else stamps.toDouble() / maxStamps
}
