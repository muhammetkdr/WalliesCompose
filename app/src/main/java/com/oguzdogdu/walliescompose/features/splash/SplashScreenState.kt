package com.oguzdogdu.walliescompose.features.splash

import com.oguzdogdu.walliescompose.core.ViewState

data class SplashScreenState(
    val userSignedIn: Boolean = false,
    val goToOnboarding: Boolean = false,
    val loading: Boolean = true
) : ViewState
