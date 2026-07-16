package net.wetheGoverned.ui.location

import android.annotation.SuppressLint
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

actual class LocationHelper() {
    // In Android, we'll need a way to provide context. 
    // For now, returning null to satisfy the interface until we add context injection.
    actual suspend fun getCurrentLocation(): Location? = null
    actual suspend fun getLastKnownLocation(): Location? = null
}
