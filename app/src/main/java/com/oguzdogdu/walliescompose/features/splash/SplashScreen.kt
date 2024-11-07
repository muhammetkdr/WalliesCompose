package com.oguzdogdu.walliescompose.features.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oguzdogdu.walliescompose.R

@Composable
fun SplashScreenRoute(
    viewModel: SplashViewModel = hiltViewModel(),
    goToLoginFlow: () -> Unit,
    goToContentScreen: () -> Unit,
    goToOnboarding: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.sendEvent(SplashScreenEvent.CheckAuthState)
    }
    if (state.loading) {
        SplashScreenContent()
    }
    LaunchedEffect(state) {
        if (!state.loading) {
            when {
                state.goToOnboarding -> goToOnboarding.invoke()
                state.userSignedIn -> goToContentScreen.invoke()
                else -> goToLoginFlow.invoke()
            }
        }
    }
}

@Composable
fun SplashScreenContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Icon(
            painter = painterResource(id = R.drawable.logo), contentDescription = stringResource(
                id = R.string.app_logo
            ), tint = Color.Unspecified, modifier = modifier.clip(RoundedCornerShape(64.dp))
        )
    }
}