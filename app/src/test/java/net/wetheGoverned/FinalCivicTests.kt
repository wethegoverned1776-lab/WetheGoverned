package net.wetheGoverned

import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import net.wetheGoverned.remote.api.WtgBackendApi
import net.wetheGoverned.repository.*
import net.wetheGoverned.session.SessionManager
import net.wetheGoverned.ui.HomeViewModel
import net.wetheGoverned.ui.OnboardingViewModel
import net.wetheGoverned.ui.OnboardingStep
import net.wetheGoverned.ui.TierVerificationViewModel
import net.wetheGoverned.ui.VerificationState
import net.wetheGoverned.core.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelSessionTest {
    private val pollRepository = mockk<PollRepository>(relaxed = true)
    private val residentRepository = mockk<ResidentRepository>(relaxed = true)
    private val sessionManager = mockk<SessionManager>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { sessionManager.currentSession } returns net.wetheGoverned.session.UserSession(
            pubKey = "abc",
            displayName = "Test User",
            districtId = "us-ga-05",
        )
        every { sessionManager.currentPubKey } returns "abc"
        every { pollRepository.observeDistrictPolls("us-ga-05") } returns flowOf(emptyList())
    }

    @After fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `HomeViewModel uses district from session`() = runTest {
        val vm = HomeViewModel(pollRepository, residentRepository, sessionManager)
        val state = vm.uiState.value
        assertEquals("us-ga-05", state.districtId)
        verify { pollRepository.observePollsByIds(listOf("us", "us-ga", "us-ga-05")) }
    }
}

class Secp256k1RoundTripTest {
    @Test
    fun `bech32 and secp256k1 round-trip for known key`() {
        val privKeyHex = "67dea2ed018072d675f5415ecfaed7d2597555e202d85b3d65ea4e58d2d92ffa"
        val nsec       = Bech32Codec.encodeNsec(privKeyHex)
        val decoded    = Bech32Codec.decodeNsec(nsec)
        assertEquals(privKeyHex, decoded)
        assertTrue(nsec.startsWith("nsec1"))
    }
}
