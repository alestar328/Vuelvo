package com.delta.vuelvo.di

import android.content.Context
import androidx.room.Room
import com.delta.vuelvo.data.local.RewardDao
import com.delta.vuelvo.data.local.StampCardDao
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

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VuelvoDatabase =
        Room.databaseBuilder(context, VuelvoDatabase::class.java, VuelvoDatabase.NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideStampCardDao(db: VuelvoDatabase): StampCardDao = db.stampCardDao()

    @Provides
    fun provideRewardDao(db: VuelvoDatabase): RewardDao = db.rewardDao()
}
