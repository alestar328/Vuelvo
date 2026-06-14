package com.delta.vuelvo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.delta.vuelvo.data.local.entity.VuelvoRewardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardDao {

    @Query("SELECT * FROM rewards ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<VuelvoRewardEntity>>

    @Query("SELECT * FROM rewards WHERE id = :id")
    suspend fun findById(id: String): VuelvoRewardEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(rewards: List<VuelvoRewardEntity>)

    @Upsert
    suspend fun upsert(reward: VuelvoRewardEntity)
}
