package net.wetheGoverned

import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import net.wetheGoverned.model.*
import net.wetheGoverned.remote.backend.WtgBackendApi
import net.wetheGoverned.repository.*
import net.wetheGoverned.session.SessionManager
import net.wetheGoverned.ui.HomeViewModel
import net.wetheGoverned.ui.onboarding.OnboardingViewModel
import net.wetheGoverned.ui.onboarding.OnboardingStep
import net.wetheGoverned.ui.TierVerificationViewModel
import net.wetheGoverned.ui.VerificationState
import net.wetheGoverned.core.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelSessionTest {
    private val pollRepository = mockk<PollRepository>(relaxed = true)
    private val sessionManager = mockk<SessionManager>(relaxed = true)

    @Before fun setUp() {
        every { sessionManager.currentSession } returns net.wetheGoverned.session.UserSession("abc", "us-ga-05")
        every { sessionManager.currentPubKey } returns "abc"
        every { pollRepository.observeDistrictPolls("us-ga-05") } returns flowOf(emptyList())
    }

    @Test
    fun `HomeViewModel uses district from session`() = runTest {
        val vm = HomeViewModel(pollRepository, sessionManager)
        val state = vm.uiState.value
        assertEquals("us-ga-05", state.districtId)
        verify { pollRepository.observeDistrictPolls("us-ga-05") }
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
