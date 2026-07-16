package net.wetheGoverned

import net.wetheGoverned.model.Location

actual class LocationHelper actual constructor() {
    actual suspend fun getCurrentLocation(): Location? = null
    actual suspend fun getLastKnownLocation(): Location? = null
}
