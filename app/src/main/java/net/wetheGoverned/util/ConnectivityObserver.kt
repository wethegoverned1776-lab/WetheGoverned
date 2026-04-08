package net.wetheGoverned.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

// ─────────────────────────────────────────────────────────────────────────────
// ConnectivityObserver
//
// `open` so tests can provide fake implementations.
//
// Two constructors:
//   Primary (@Inject) — used by Hilt in production; requires Context.
//   Protected (context = null) — used by FakeConnectivityObserver in tests;
//     skips ConnectivityManager initialization entirely.
// ─────────────────────────────────────────────────────────────────────────────

@Singleton
open class ConnectivityObserver @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    /**
     * Protected secondary constructor for test doubles.
     * Passes a null context sentinel; real ConnectivityManager calls are
     * never made because subclasses override `isConnected` and `isCurrentlyConnected`.
     */
    protected constructor() : this(
        // Suppress Hilt's requirement for a real Context in tests.
        // This constructor is only reachable from FakeConnectivityObserver,
        // which overrides everything that touches the ConnectivityManager.
        context = object : android.content.ContextWrapper(null) {},
    )

    private val connectivityManager: ConnectivityManager? by lazy {
        runCatching {
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        }.getOrNull()
    }

    /**
     * Emits `true` when internet is available, `false` when lost.
     * Emits the current state immediately on collection.
     */
    open val isConnected: Flow<Boolean> = callbackFlow {
        trySend(isCurrentlyConnected())

        val cm = connectivityManager ?: run { close(); return@callbackFlow }

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network)  { trySend(true) }
            override fun onLost(network: Network)       { trySend(isCurrentlyConnected()) }
            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                val ok = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                         caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                trySend(ok)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()

        cm.registerNetworkCallback(request, callback)
        awaitClose { cm.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()

    open fun isCurrentlyConnected(): Boolean {
        val cm      = connectivityManager ?: return false
        val network = cm.activeNetwork ?: return false
        val caps    = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// FakeConnectivityObserver  — test double, no Android framework dependencies
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Lightweight test double for ConnectivityObserver.
 *
 * Uses the protected no-arg constructor so no real Context or ConnectivityManager
 * is created. All methods are overridden to return the fixed [connected] value.
 *
 * Usage in Compose tests:
 *   composeRule.setContent {
 *       WtgNavHost(
 *           sessionManager       = sessionManager,
 *           connectivityObserver = FakeConnectivityObserver(connected = false),
 *       )
 *   }
 *   composeRule.onNodeWithText("You're offline.").assertIsDisplayed()
 */
class FakeConnectivityObserver(private val connected: Boolean = true) : ConnectivityObserver() {
    override val isConnected: Flow<Boolean> = flowOf(connected)
    override fun isCurrentlyConnected(): Boolean = connected
}
