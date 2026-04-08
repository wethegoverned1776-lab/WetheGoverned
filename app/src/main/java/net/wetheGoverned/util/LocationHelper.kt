package net.wetheGoverned.ui.location

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun getCurrentLocation(): Location? = null
    suspend fun getLastKnownLocation(): Location? = null
}

data class Location(val latitude: Double, val longitude: Double)
