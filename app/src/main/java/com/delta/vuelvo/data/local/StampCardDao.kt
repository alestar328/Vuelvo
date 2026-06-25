package com.delta.vuelvo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.delta.vuelvo.data.local.entity.StampCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StampCardDao {

    @Query("SELECT * FROM stamp_cards")
    fun observeAll(): Flow<List<StampCardEntity>>

    @Query("SELECT * FROM stamp_cards WHERE id = :id")
    fun observeById(id: String): Flow<StampCardEntity?>

    @Query("SELECT * FROM stamp_cards WHERE id = :id")
    suspend fun findById(id: String): StampCardEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(cards: List<StampCardEntity>)

    @Upsert
    suspend fun upsert(card: StampCardEntity)

    @Query("DELETE FROM stamp_cards WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM stamp_cards")
    suspend fun count(): Int
}
