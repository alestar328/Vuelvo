package com.delta.vuelvo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.delta.vuelvo.data.local.entity.StampCardEntity
import com.delta.vuelvo.data.local.entity.VuelvoRewardEntity

/**
 * Al subir [version] hay que añadir su `Migration` en [VUELVO_MIGRATIONS]: la base de datos es la
 * única copia de las tarjetas del usuario y ya no se borra sola al actualizar la app.
 *
 * `exportSchema = true` deja el esquema versionado en `app/schemas/` para poder escribir y probar
 * esas migraciones contra el esquema real en lugar de a ojo.
 */
@Database(
    entities = [StampCardEntity::class, VuelvoRewardEntity::class],
    version = 6,
    exportSchema = true,
)
abstract class VuelvoDatabase : RoomDatabase() {
    abstract fun stampCardDao(): StampCardDao
    abstract fun rewardDao(): RewardDao

    companion object {
        const val NAME = "vuelvo.db"
    }
}
