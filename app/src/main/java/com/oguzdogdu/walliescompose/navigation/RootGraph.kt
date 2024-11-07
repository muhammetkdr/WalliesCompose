package com.oguzdogdu.walliescompose.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.oguzdogdu.walliescompose.features.onboarding.onboardingScreen
import com.oguzdogdu.walliescompose.features.splash.splashScreen
import kotlinx.serialization.Serializable

@Serializable
object RootGraph

fun NavGraphBuilder.navigationRootGraph(
    navHostController: NavHostController,
) {
    navigation<RootGraph>(startDestination = Screens.SplashScreenRoute) {
        splashScreen(goToLoginFlow = {
            navHostController.navigate(AuthGraph) {
                popUpTo(Screens.SplashScreenRoute) { inclusive = true }
            }
        }, goToContentScreen = {
            navHostController.navigate(NavigationBarGraph) {
                popUpTo(Screens.SplashScreenRoute) { inclusive = true }
            }
        }, goToOnboarding = {
            navHostController.navigate(Screens.OnboardingScreenNavigationRoute) {
                popUpTo(Screens.SplashScreenRoute) { inclusive = true }
            }
        })

        onboardingScreen(goToLoginFlow = {
            navHostController.navigate(AuthGraph) {
                popUpTo(Screens.OnboardingScreenNavigationRoute) { inclusive = true }
            }
        }, goToContentScreen = {
            navHostController.navigate(NavigationBarGraph) {
                popUpTo(Screens.OnboardingScreenNavigationRoute) { inclusive = true }
            }
        })
    }
}
