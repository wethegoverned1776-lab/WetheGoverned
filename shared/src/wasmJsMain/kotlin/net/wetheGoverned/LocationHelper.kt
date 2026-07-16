package net.wetheGoverned

import net.wetheGoverned.model.Location

actual class LocationHelper actual constructor() {
    actual suspend fun getCurrentLocation(): Location? {
        return Location(32.137, -81.24)
    }
    actual suspend fun getLastKnownLocation(): Location? {
        return Location(32.137, -81.24)
    }
}
