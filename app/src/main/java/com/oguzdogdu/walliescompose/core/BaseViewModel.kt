package com.oguzdogdu.walliescompose.core

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Stable
abstract class BaseViewModel<State : ViewState, Event : ViewEvent, Effect : ViewEffect>(
    initialState: State,
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _effect = Channel<Effect>()
    val effect = _effect.receiveAsFlow()

    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    val currentState: State get() = _state.value


    fun <T> sendApiCall(
        request: suspend () -> Flow<T>,
        delay: Long = 0,
        onLoading: (Boolean) -> Unit = {},
        onSuccess: suspend (T) -> Unit,
        onError: (Throwable) -> Unit = {},
        onComplete: () -> Unit = {}
    ): Job = viewModelScope.launch {
        try {
            delay(delay)
            request()
                .onStart { onLoading(true) }
                .onEach {
                    onSuccess(it)
                }
                .catch { e ->
                    onLoading(false)
                    onError(e)
                }
                .onCompletion { e ->
                    onLoading(false)
                    e?.let { onError(it) }; onComplete()
                }
                .collect()
        } catch (e: Throwable) {
            onLoading(false)
            onError(e)
        }
    }

    fun setState(state: State) = _state.update { state }

    fun sendEffect(effect: Effect) = viewModelScope.launch { _effect.send(effect) }

    fun sendEvent(event: Event) = viewModelScope.launch {
        _eventChannel.send(event)
            .run { eventChannel.collect { handleEvents(it) } }
    }

    abstract fun handleEvents(event: Event)
}