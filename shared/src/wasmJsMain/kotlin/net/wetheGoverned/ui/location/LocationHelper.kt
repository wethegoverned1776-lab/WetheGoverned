package net.wetheGoverned.ui.location

actual class LocationHelper {
    actual suspend fun getCurrentLocation(): Location? {
        // Basic stub for Wasm - could use browser geolocation API
        return Location(32.137, -81.24) // Default to Palm Coast region for demo
    }
    actual suspend fun getLastKnownLocation(): Location? {
        return Location(32.137, -81.24)
    }
}
