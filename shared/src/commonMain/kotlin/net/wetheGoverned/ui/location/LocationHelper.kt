package net.wetheGoverned.ui.location

expect class LocationHelper() {
    suspend fun getCurrentLocation(): Location?
    suspend fun getLastKnownLocation(): Location?
}
