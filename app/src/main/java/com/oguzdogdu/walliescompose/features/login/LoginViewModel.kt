package com.oguzdogdu.walliescompose.features.login

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.oguzdogdu.walliescompose.core.BaseViewModel
import com.oguzdogdu.walliescompose.core.ViewEffect
import com.oguzdogdu.walliescompose.domain.repository.UserAuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticationRepository: UserAuthenticationRepository
) : BaseViewModel<LoginState, LoginScreenEvent, ViewEffect>(LoginState()) {

    override fun handleEvents(event: LoginScreenEvent) {
        when (event) {
            is LoginScreenEvent.GoogleButton -> {
                signInWithGoogle(idToken = event.idToken)
            }
        }
    }

    private fun signInWithGoogle(idToken: String?) {
        viewModelScope.launch {
            sendApiCall(request = {
                authenticationRepository.signInWithGoogle(idToken)
            }, onSuccess = {
                setState(currentState.copy(loading = true))
                delay(2500)
                setState(currentState.copy(userSignedIn = it.user != null))
                setState(currentState.copy(loading = false))
            }, onError = {
                setState(currentState.copy(errorMessage = it.message.toString()))
            })
        }
    }
}