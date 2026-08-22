package com.delta.vuelvo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.delta.vuelvo.data.local.entity.StampCardEntity
import com.delta.vuelvo.data.local.entity.VuelvoRewardEntity

@Database(
    entities = [StampCardEntity::class, VuelvoRewardEntity::class],
    version = 5,
    exportSchema = false,
)
abstract class VuelvoDatabase : RoomDatabase() {
    abstract fun stampCardDao(): StampCardDao
    abstract fun rewardDao(): RewardDao

    companion object {
        const val NAME = "vuelvo.db"

        /** Adds the nullable `businessCode` column — no existing row loses data. */
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE stamp_cards ADD COLUMN businessCode TEXT")
            }
        }
    }
}
