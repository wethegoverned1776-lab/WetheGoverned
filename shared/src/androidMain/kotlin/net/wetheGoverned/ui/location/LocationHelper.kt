package net.wetheGoverned.ui.location

import android.content.Context
import net.wetheGoverned.data.MeshDiscoveryManager

actual class LocationHelper(private val context: Context) {
    actual suspend fun getCurrentLocation(): Location? = null
    actual suspend fun getLastKnownLocation(): Location? = null
}
