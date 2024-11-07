package com.oguzdogdu.walliescompose.features.onboarding

import androidx.glance.Visibility
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oguzdogdu.walliescompose.data.repository.AppSettingsRepositoryImpl.Companion.ONBOARDING
import com.oguzdogdu.walliescompose.domain.repository.AppSettingsRepository
import com.oguzdogdu.walliescompose.domain.repository.UserAuthenticationRepository
import com.oguzdogdu.walliescompose.features.splash.SplashScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingScreenViewModel @Inject constructor(
    private val authenticationRepository: UserAuthenticationRepository,
    private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {

    private val _onboardingState: MutableStateFlow<OnboardingScreenState> = MutableStateFlow(
        OnboardingScreenState()
    )
    val onboardingState = _onboardingState.asStateFlow()

    fun handleUIEvent(event: OnboardingScreenEvent) {
        when (event) {
            is OnboardingScreenEvent.OnboardingFlowCompleted ->
                adjustOnboardingVisibility(event.isCompleted)
        }
    }

    private fun checkSignIn() {
        viewModelScope.launch {
            val authState = authenticationRepository.isUserAuthenticatedInFirebase().single()
            _onboardingState.update {
                it.copy(isSigned = authState)
            }
        }
    }

    private fun adjustOnboardingVisibility(visibility: Boolean) {
        viewModelScope.launch {
            async { appSettingsRepository.putOnboardingShow(key = ONBOARDING, value = visibility) }.await()
            checkSignIn()
        }
    }
}