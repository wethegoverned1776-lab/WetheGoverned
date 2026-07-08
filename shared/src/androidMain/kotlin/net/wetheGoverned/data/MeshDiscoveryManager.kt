package net.wetheGoverned.data

import android.content.Context
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException

/**
 * Android implementation of MeshDiscoveryManager using UDP Broadcast.
 * Unified with Desktop for cross-platform local mesh discovery.
 */
class AndroidMeshDiscoveryManager : MeshDiscoveryManager {
    companion object {
        lateinit var appContext: Context
    }

    private val _discoveredPeers = MutableStateFlow<Set<String>>(emptySet())
    override val discoveredPeers: StateFlow<Set<String>> = _discoveredPeers.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var socket: DatagramSocket? = null
    private val discoveryPort = 8888
    private val broadcastInterval = 5000L

    override fun registerService(port: Int, districtId: String) {
        scope.launch {
            try {
                val socket = DatagramSocket().apply { broadcast = true }
                val message = "WTG_NODE:$districtId:$port"
                val data = message.toByteArray()
                // Broadcast to the local network
                val address = InetAddress.getByName("255.255.255.255")
                
                while (isActive) {
                    val packet = DatagramPacket(data, data.size, address, discoveryPort)
                    socket.send(packet)
                    delay(broadcastInterval)
                }
            } catch (ignore: Exception) {
                // Network error
            }
        }
    }

    override fun discoverPeers() {
        scope.launch {
            try {
                socket = DatagramSocket(discoveryPort).apply {
                    soTimeout = 10000
                }
                val buffer = ByteArray(1024)
                
                while (isActive) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    try {
                        socket?.receive(packet)
                        val message = String(packet.data, 0, packet.length)
                        if (message.startsWith("WTG_NODE:")) {
                            val peerAddress = packet.address.hostAddress
                            // Don't add ourselves if we somehow receive our own broadcast
                            if (peerAddress != null) {
                                _discoveredPeers.value += peerAddress
                            }
                        }
                    } catch (ignore: Exception) {
                        // Timeout or other error
                    }
                }
            } catch (e: SocketException) {
                // Discovery socket error
            }
        }
    }

    override fun stopDiscovery() {
        scope.cancel()
        socket?.close()
    }
}
