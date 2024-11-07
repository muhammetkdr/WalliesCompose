package com.oguzdogdu.walliescompose.features.splash

import com.oguzdogdu.walliescompose.core.ViewEvent

sealed interface SplashScreenEvent : ViewEvent {
    data object CheckAuthState : SplashScreenEvent
}