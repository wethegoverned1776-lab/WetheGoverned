package net.wetheGoverned

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Intent
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import net.wetheGoverned.data.P2PService
import javax.inject.Inject
import net.wetheGoverned.data.P2PSyncEngine

@HiltAndroidApp
class WeTheGovernedApplication : Application() {

    @Inject
    lateinit var p2pSyncEngine: P2PSyncEngine

    override fun onCreate() {
        super.onCreate()
        
        setupGlobalExceptionHandler()
        
        // Start the decentralized P2P server via a Foreground Service
        try {
            val intent = Intent(this, P2PService::class.java)
            startForegroundService(intent)
        } catch (e: Exception) {
            Log.e("WTGApp", "Failed to start P2P Service", e)
        }
    }

    private fun setupGlobalExceptionHandler() {
        val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("WTG_CRASH", "Uncaught exception on thread ${thread.name}", throwable)
            // Perform any emergency cleanup here
            oldHandler?.uncaughtException(thread, throwable)
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.i("WTGApp", "Trim memory level: $level")
        
        // Respond to memory pressure to avoid OOM on low-end devices
        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL,
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> {
                Log.w("WTGApp", "Critical memory pressure. Throttling P2P engine.")
                p2pSyncEngine.adjustPerformance(lowPowerMode = true)
            }
            ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> {
                // App went to background
            }
        }
    }
}
