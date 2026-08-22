package com.delta.vuelvo.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migraciones de [VuelvoDatabase]. **Cada subida de `version` necesita la suya**: sin ella Room
 * no puede abrir una base de datos antigua y la app se queda sin las tarjetas del usuario, que
 * sólo existen en este dispositivo.
 *
 * Todos los cambios hasta ahora han sido columnas nuevas y nullables, así que basta un
 * `ALTER TABLE ... ADD COLUMN`; los datos existentes se conservan intactos.
 */
internal val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // StampCardEntity.uuid — identificador por tag leído del deeplink.
        db.execSQL("ALTER TABLE `stamp_cards` ADD COLUMN `uuid` TEXT")
    }
}

internal val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // StampCardEntity.logoRef / coverRef — imágenes del comercio en Firebase Storage.
        db.execSQL("ALTER TABLE `stamp_cards` ADD COLUMN `logoRef` TEXT")
        db.execSQL("ALTER TABLE `stamp_cards` ADD COLUMN `coverRef` TEXT")
    }
}

internal val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // VuelvoRewardEntity.logoRef — el premio muestra el logo del comercio que lo emitió.
        db.execSQL("ALTER TABLE `rewards` ADD COLUMN `logoRef` TEXT")
    }
}

internal val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // StampCardEntity.businessCode — clave del registro "comercio activo" en Firestore; sustituye
        // a id como clave principal de matching al escanear (ver VuelvoRepository.applyStamp).
        db.execSQL("ALTER TABLE `stamp_cards` ADD COLUMN `businessCode` TEXT")
    }
}

/** Todas las migraciones conocidas, en orden. Añade la nueva aquí al subir `version`. */
internal val VUELVO_MIGRATIONS = arrayOf(
    MIGRATION_1_2,
    MIGRATION_2_3,
    MIGRATION_3_4,
    MIGRATION_4_5,
)
