package com.delta.vuelvo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.delta.vuelvo.data.local.entity.StampCardEntity
import com.delta.vuelvo.data.local.entity.VuelvoRewardEntity

@Database(
    entities = [StampCardEntity::class, VuelvoRewardEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class VuelvoDatabase : RoomDatabase() {
    abstract fun stampCardDao(): StampCardDao
    abstract fun rewardDao(): RewardDao

    companion object {
        const val NAME = "vuelvo.db"
    }
}
