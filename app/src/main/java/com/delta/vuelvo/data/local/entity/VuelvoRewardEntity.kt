package com.delta.vuelvo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persisted reward. [cardId] references [StampCardEntity.id] as a plain string foreign
 * key (no formal Room relation, per the data model).
 */
@Entity(tableName = "rewards")
data class VuelvoRewardEntity(
    @PrimaryKey val id: String,
    val cardId: String,
    val name: String,
    val commerce: String,
    val symbolName: String,
    val tileHex: String,
    val inkHex: String,
    val isAvailable: Boolean,
    val dateLabel: String,
    val createdAt: Long,
)
