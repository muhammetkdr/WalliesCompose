package com.oguzdogdu.walliescompose.features.authenticateduser.changenameandsurname

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.oguzdogdu.walliescompose.R
import com.oguzdogdu.walliescompose.core.BaseViewModel
import com.oguzdogdu.walliescompose.domain.repository.UserAuthenticationRepository
import com.oguzdogdu.walliescompose.domain.wrapper.onFailure
import com.oguzdogdu.walliescompose.domain.wrapper.onSuccess
import com.oguzdogdu.walliescompose.features.appstate.Duration
import com.oguzdogdu.walliescompose.features.appstate.MessageContent
import com.oguzdogdu.walliescompose.features.appstate.MessageType
import com.oguzdogdu.walliescompose.features.appstate.SnackbarModel
import com.oguzdogdu.walliescompose.features.authenticateduser.component.StepName
import com.oguzdogdu.walliescompose.navigation.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPersonalInfoViewModel @Inject constructor(
    private val authenticationRepository: UserAuthenticationRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<EditPersonalInfoState, EditPersonalInfoEvent, EditPersonalInfoEffect>(
    EditPersonalInfoState()
) {
    private val nameFlow: MutableStateFlow<String?> =
        MutableStateFlow(savedStateHandle.toRoute<Screens.ChangeNameAndSurnameScreenRoute>().name)
    private val surnameFlow: MutableStateFlow<String?> =
        MutableStateFlow(savedStateHandle.toRoute<Screens.ChangeNameAndSurnameScreenRoute>().surname)
    private val bioFlow:MutableStateFlow<String?> =
        MutableStateFlow(savedStateHandle.toRoute<Screens.ChangeNameAndSurnameScreenRoute>().bio)
    private val locationFlow: MutableStateFlow<String?> =
        MutableStateFlow(savedStateHandle.toRoute<Screens.ChangeNameAndSurnameScreenRoute>().location)
    val currentStep: StateFlow<String?> = authenticationRepository.userVerificationStep().map { it }
        .stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5000), null)
    val allStepsCompleted: StateFlow<Boolean> =
        authenticationRepository.userVerificationStepsCompleted().map { it }
            .stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5000), false)
    val initialData: StateFlow<EditPersonalInfoState> = combine(
        nameFlow,
        surnameFlow,
        bioFlow,
        locationFlow
    ) { name, surname, bio, location ->
        _userState.updateAndGet {
            it.copy(name = name, surname = surname, bio =  bio, location = location)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EditPersonalInfoState(
            name = nameFlow.value,
            surname = surnameFlow.value,
            bio = bioFlow.value,
            location = locationFlow.value
        )
    )

    private val _userState: MutableStateFlow<EditPersonalInfoState> = MutableStateFlow(
        EditPersonalInfoState()
    )
    val userState = _userState.asStateFlow()

    override fun handleEvents(event: EditPersonalInfoEvent) {
        when(event) {
            is EditPersonalInfoEvent.ChangedUserPersonalInfos -> {
                changeUserNameAndSurname(event.name,event.surname)
                addOrChangeBio(event.bio)
                addOrChangeLocation(event.location)
                adjustStep(surname = event.surname, bio = event.bio, location = event.location)
            }
        }
    }

    private fun changeUserNameAndSurname(name: String?, surname: String?) {
        viewModelScope.launch {
                when {
                    (name?.isEmpty() == true || surname?.isEmpty() == true) ->{
                        sendEffect(
                            EditPersonalInfoEffect.ShowSnackbar(
                                SnackbarModel(
                                    type = MessageType.ERROR,
                                    drawableRes = R.drawable.ic_cancel,
                                    message = MessageContent.PlainString("Name or surname field cannot be left blank"),
                                    duration = Duration.SHORT
                                )
                            )
                        )
                    }
                    else -> {
                        val changeName = authenticationRepository.changeUsername(name)
                        val changeSurName = authenticationRepository.changeSurname(surname)
                        if (!nameFlow.value.equals(name) || !surnameFlow.value.equals(surname)) {
                            combine(changeName, changeSurName) { username, userSurname ->
                                username.onSuccess { name ->
                                    sendEffect(
                                        EditPersonalInfoEffect.ShowSnackbar(
                                            SnackbarModel(
                                                type = MessageType.SUCCESS,
                                                drawableRes = R.drawable.ic_completed,
                                                message = MessageContent.ResourceString(R.string.user_name_surname_update),
                                                duration = Duration.SHORT
                                            )
                                        )
                                    )
                                    setState(currentState.copy(name = name))
                                }
                                username.onFailure { error ->
                                    sendEffect(
                                        EditPersonalInfoEffect.ShowSnackbar(
                                            SnackbarModel(
                                                type = MessageType.ERROR,
                                                drawableRes = R.drawable.ic_cancel,
                                                message = MessageContent.PlainString(error),
                                                duration = Duration.SHORT
                                            )
                                        )
                                    )
                                }
                                userSurname.onSuccess { surname ->
                                    setState(currentState.copy(surname = surname))
                                }
                                userSurname.onFailure { error ->
                                    sendEffect(
                                        EditPersonalInfoEffect.ShowSnackbar(
                                            SnackbarModel(
                                                type = MessageType.ERROR,
                                                drawableRes = R.drawable.ic_cancel,
                                                message = MessageContent.PlainString(error),
                                                duration = Duration.SHORT
                                            )
                                        )
                                    )
                                }
                            }.collect()
                        }
                }
            }
        }
    }
    private fun addOrChangeBio(bio:String?) {
        viewModelScope.launch {
            if (!bioFlow.value.contentEquals(bio)){
                authenticationRepository.changeBio(bio).collect { state ->
                    state.onFailure { error ->
                        sendEffect(
                            EditPersonalInfoEffect.ShowSnackbar(
                                SnackbarModel(
                                    type = MessageType.ERROR,
                                    drawableRes = R.drawable.ic_cancel,
                                    message = MessageContent.PlainString(error),
                                    duration = Duration.SHORT
                                )
                            )
                        )
                    }
                }
            }
        }
    }
    private fun addOrChangeLocation(location: String?) {
        viewModelScope.launch {
            if (!locationFlow.value.equals(location)) {
                authenticationRepository.changeLocation(location).collect { state ->
                    state.onFailure { error ->
                        sendEffect(
                            EditPersonalInfoEffect.ShowSnackbar(
                                SnackbarModel(
                                    type = MessageType.ERROR,
                                    drawableRes = R.drawable.ic_cancel,
                                    message = MessageContent.PlainString(error),
                                    duration = Duration.SHORT
                                )
                            )
                        )
                    }
                    state.onSuccess {
                        authenticationRepository.changeVerificationStep(StepName.LOCATION.stepName)
                    }
                }
            }
        }
    }

    private fun adjustStep(surname: String?, bio: String?, location: String?) {
        viewModelScope.launch {
            if (surname?.isNotEmpty() == true && !surname.contentEquals(surnameFlow.value)) {
                authenticationRepository.changeVerificationStep(StepName.SURNAME.stepName)
            }
            if (bio?.isNotEmpty() == true && !bio.contentEquals(bioFlow.value)) {
                authenticationRepository.changeVerificationStep(StepName.BIO.stepName)
            }
            if (location?.isNotEmpty() == true && !location.contentEquals(locationFlow.value)) {
                authenticationRepository.changeVerificationStep(StepName.LOCATION.stepName)
            }
        }
    }
}
