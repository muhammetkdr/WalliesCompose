package com.oguzdogdu.walliescompose.features.onboarding

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.oguzdogdu.walliescompose.navigation.Screens

fun NavController.navigateToOnboardingScreen() = navigate(Screens.OnboardingScreenNavigationRoute)

fun NavGraphBuilder.onboardingScreen(
    goToLoginFlow: () -> Unit,
    goToContentScreen: () -> Unit
) {
    composable<Screens.OnboardingScreenNavigationRoute> {
        OnboardingScreenRoute(
            goToLoginFlow = {
            goToLoginFlow.invoke()
        }, goToContentScreen = {
            goToContentScreen.invoke()
        })
    }
}