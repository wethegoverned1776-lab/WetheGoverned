package net.wetheGoverned.ui.location

actual class LocationHelper {
    actual suspend fun getCurrentLocation(): Location? = null
    actual suspend fun getLastKnownLocation(): Location? = null
}
