package com.oguzdogdu.walliescompose.features.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OnboardingScreenRoute(
    viewModel: OnboardingScreenViewModel = hiltViewModel(),
    goToLoginFlow: () -> Unit,
    goToContentScreen: () -> Unit
) {
    val state by viewModel.onboardingState.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        when (state.isSigned) {
            false -> goToLoginFlow.invoke()
            true -> goToContentScreen.invoke()
            null -> return@LaunchedEffect
        }
    }

    OnboardingScreenContent(onSkipClicked = {
        viewModel.handleUIEvent(
            OnboardingScreenEvent.OnboardingFlowCompleted(
                true
            )
        )
    },
        onGetStartedClicked = {
            viewModel.handleUIEvent(
                OnboardingScreenEvent.OnboardingFlowCompleted(
                    true
                )
            )
        })
}

@Composable
fun OnboardingScreenContent(onSkipClicked: () -> Unit, onGetStartedClicked: () -> Unit) {
    OnboardingScreen(onSkipClicked = onSkipClicked, onGetStartedClicked = onGetStartedClicked)
}