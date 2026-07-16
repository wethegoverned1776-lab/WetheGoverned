package net.wetheGoverned.ui.location

actual class LocationHelper actual constructor() {
    actual suspend fun getCurrentLocation(): Location? = null
    actual suspend fun getLastKnownLocation(): Location? = null
}
