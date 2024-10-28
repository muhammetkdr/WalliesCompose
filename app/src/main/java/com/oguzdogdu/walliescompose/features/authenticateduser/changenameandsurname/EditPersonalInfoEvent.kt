package com.oguzdogdu.walliescompose.features.authenticateduser.changenameandsurname

import com.oguzdogdu.walliescompose.core.ViewEvent

sealed class EditPersonalInfoEvent : ViewEvent {
    data class ChangedUserNameAndSurname(val name: String?,val surname: String?) : EditPersonalInfoEvent()
    data class ChangedUserBio(val bio: String?) : EditPersonalInfoEvent()
    data class ChangedUserLocation(val location: String?) : EditPersonalInfoEvent()
}
