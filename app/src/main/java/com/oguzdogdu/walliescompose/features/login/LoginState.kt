package com.oguzdogdu.walliescompose.features.login

import androidx.compose.runtime.Stable
import com.oguzdogdu.walliescompose.core.ViewState

@Stable
data class LoginState(
    val loading: Boolean = false,
    val errorMessage: String = "",
    val userSignedIn: Boolean = false
) : ViewState

