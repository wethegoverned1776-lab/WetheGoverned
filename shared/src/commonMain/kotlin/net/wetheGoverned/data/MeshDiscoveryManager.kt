package net.wetheGoverned.data

import kotlinx.coroutines.flow.StateFlow

/**
 * Platform-independent mesh discovery interface.
 */
interface MeshDiscoveryManager {
    val discoveredPeers: StateFlow<Set<String>>
    fun registerService(port: Int, districtId: String)
    fun discoverPeers()
    fun stopDiscovery()
}
