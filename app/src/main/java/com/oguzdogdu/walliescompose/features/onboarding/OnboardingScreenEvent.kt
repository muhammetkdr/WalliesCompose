package com.oguzdogdu.walliescompose.features.onboarding

import androidx.compose.runtime.Stable

@Stable
sealed class OnboardingScreenEvent {
    data class OnboardingFlowCompleted(val isCompleted: Boolean = false): OnboardingScreenEvent()
}