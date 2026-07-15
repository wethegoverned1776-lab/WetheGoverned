package net.wetheGoverned.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import net.wetheGoverned.repository.*
import net.wetheGoverned.session.SessionManager
import net.wetheGoverned.ui.HomeViewModel
import net.wetheGoverned.ui.PollViewModel
import net.wetheGoverned.ui.ResidentProfileViewModel

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideHomeViewModel(
        pollRepository: PollRepository,
        residentRepository: ResidentRepository,
        sessionManager: SessionManager,
        relayManager: net.wetheGoverned.data.NostrRelayManager
    ): HomeViewModel = HomeViewModel(pollRepository, residentRepository, sessionManager, relayManager)

    @Provides
    @ViewModelScoped
    fun providePollViewModel(
        pollRepository: PollRepository,
        sessionManager: SessionManager
    ): PollViewModel = PollViewModel(pollRepository, sessionManager)

    @Provides
    @ViewModelScoped
    fun provideResidentProfileViewModel(
        residentRepository: ResidentRepository,
        accountRepository: AccountRepository,
        sessionManager: SessionManager,
        requestRepository: VerificationRequestRepository,
    ): ResidentProfileViewModel = ResidentProfileViewModel(residentRepository, accountRepository, sessionManager, requestRepository)
}
