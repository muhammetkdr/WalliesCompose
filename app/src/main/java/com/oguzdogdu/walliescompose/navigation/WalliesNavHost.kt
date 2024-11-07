package com.oguzdogdu.walliescompose.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.oguzdogdu.walliescompose.features.login.googlesignin.GoogleAuthUiClient

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WalliesNavHost(
    navController: NavHostController,
    googleAuthUiClient: GoogleAuthUiClient
) {
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = RootGraph,
        ) {
            navigationRootGraph(navHostController = navController)
            navigationAuthGraph(
                navHostController = navController,
                googleAuthUiClient = googleAuthUiClient
            )
            navigationBarGraph(
                navHostController = navController,
                scope = this@SharedTransitionLayout
            )
            navigationHomeGraph(
                navHostController = navController,
                sharedTransitionScope = this@SharedTransitionLayout
            )
        }
    }
}