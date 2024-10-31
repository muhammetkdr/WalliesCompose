package com.oguzdogdu.walliescompose.features.authenticateduser.changenameandsurname

import com.oguzdogdu.walliescompose.core.ViewEvent

sealed class EditPersonalInfoEvent : ViewEvent {
    data class ChangedUserPersonalInfos(
        val name: String?,
        val surname: String?,
        val bio: String?,
        val location: String?
    ) : EditPersonalInfoEvent()
}
