package com.delta.vuelvo.di

import android.content.Context
import androidx.room.Room
import com.delta.vuelvo.data.local.RewardDao
import com.delta.vuelvo.data.local.StampCardDao
import com.delta.vuelvo.data.local.VUELVO_MIGRATIONS
import com.delta.vuelvo.data.local.VuelvoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Las tarjetas del usuario sólo viven aquí, así que la base de datos **nunca** debe borrarse
     * al actualizar la app: en vez de `fallbackToDestructiveMigration()` se declaran migraciones
     * explícitas (ver [VUELVO_MIGRATIONS]). Si falta una, la app falla al abrir en desarrollo —
     * un fallo ruidoso es preferible a perder los sellos del usuario en silencio.
     *
     * Sólo se permite el borrado en *downgrade* (instalar una versión anterior a la que ya se
     * tenía), un caso que no ocurre por Play Store y para el que no hay migración posible.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VuelvoDatabase =
        Room.databaseBuilder(context, VuelvoDatabase::class.java, VuelvoDatabase.NAME)
            .addMigrations(*VUELVO_MIGRATIONS)
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()

    @Provides
    fun provideStampCardDao(db: VuelvoDatabase): StampCardDao = db.stampCardDao()

    @Provides
    fun provideRewardDao(db: VuelvoDatabase): RewardDao = db.rewardDao()
}
