package com.oguzdogdu.walliescompose.features.login

import androidx.compose.runtime.Stable
import com.oguzdogdu.walliescompose.core.ViewEvent

@Stable
sealed class LoginScreenEvent : ViewEvent {
    data class GoogleButton(val idToken: String?) : LoginScreenEvent()
}
