package net.wetheGoverned.ui.location

data class Location(val latitude: Double, val longitude: Double)

expect class LocationHelper {
    suspend fun getCurrentLocation(): Location?
    suspend fun getLastKnownLocation(): Location?
}
