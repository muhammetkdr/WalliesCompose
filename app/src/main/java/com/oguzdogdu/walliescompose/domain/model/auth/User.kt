package com.oguzdogdu.walliescompose.domain.model.auth

import androidx.compose.runtime.Immutable
import com.oguzdogdu.walliescompose.features.authenticateduser.component.StepName
import com.oguzdogdu.walliescompose.features.authenticateduser.component.StepState

@Immutable
data class User(
    val name: String? = null,
    val surname: String? = null,
    val email: String? = null,
    val image: String? = null,
    val favorites: List<HashMap<String,String>>? = emptyList(),
    val bio: String? = null,
    val location: String? = null,
    val stepName: String? = StepName.SURNAME.stepName,
    val allStepsCompleted: Boolean = false
)
