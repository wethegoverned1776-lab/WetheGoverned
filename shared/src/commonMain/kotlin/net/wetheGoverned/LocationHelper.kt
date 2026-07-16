package net.wetheGoverned

import net.wetheGoverned.model.Location

expect class LocationHelper() {
    suspend fun getCurrentLocation(): Location?
    suspend fun getLastKnownLocation(): Location?
}
