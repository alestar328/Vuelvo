package com.delta.vuelvo

import android.app.Application
import com.delta.vuelvo.data.repository.VuelvoRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class VuelvoApplication : Application() {

    @Inject lateinit var repository: VuelvoRepository

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // Seed the initial catalogue on first launch.
        appScope.launch { repository.seedIfNeeded() }
    }
}
