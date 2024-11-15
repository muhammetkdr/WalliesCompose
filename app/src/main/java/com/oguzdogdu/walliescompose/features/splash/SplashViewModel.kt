package com.oguzdogdu.walliescompose.features.splash

import androidx.lifecycle.viewModelScope
import com.oguzdogdu.walliescompose.core.BaseViewModel
import com.oguzdogdu.walliescompose.core.ViewEffect
import com.oguzdogdu.walliescompose.data.repository.AppSettingsRepositoryImpl.Companion.ONBOARDING
import com.oguzdogdu.walliescompose.domain.repository.AppSettingsRepository
import com.oguzdogdu.walliescompose.domain.repository.UserAuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authenticationRepository: UserAuthenticationRepository,
    private val appSettingsRepository: AppSettingsRepository
) : BaseViewModel<SplashScreenState,SplashScreenEvent,ViewEffect>(SplashScreenState()) {

    private val visibilityOfOnboarding: StateFlow<Boolean> =
        appSettingsRepository.getOnboardingShow(ONBOARDING).map { it }
            .stateIn(viewModelScope, started = SharingStarted.Eagerly, false)

    override fun handleEvents(event: SplashScreenEvent) {
        when(event) {
            SplashScreenEvent.CheckAuthState -> checkSignIn()
        }
    }

    private fun checkSignIn() {
            sendApiCall(
                request = {
                    authenticationRepository.isUserAuthenticatedInFirebase()
                },
                delay = 2000,
                onLoading = {
                    setState(currentState.copy(loading = it))
                },
                onSuccess = {
                    if (!visibilityOfOnboarding.value) {
                        setState(currentState.copy(goToOnboarding = true, loading = false))
                    } else {
                        setState(currentState.copy(goToOnboarding = false, loading = false))
                        setState(currentState.copy(userSignedIn = it, loading = false))
                    }
                }
            )
        }
    }
