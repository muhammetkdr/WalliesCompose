package com.oguzdogdu.walliescompose.features.authenticateduser

import android.net.Uri
import androidx.compose.runtime.Stable
import com.oguzdogdu.walliescompose.features.authenticateduser.component.StepName

@Stable
data class UserInfoState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val name: String? = null,
    val surname: String? = null,
    val email: String? = null,
    val profileImage: String? = null,
    val isAuthenticatedWithFirebase: Boolean = false,
    val isAuthenticatedWithGoogle: Boolean = false,
    val favorites: List<HashMap<String, String>>? = emptyList(),
    val photoUri: Uri? = null,
    val bio : String? = null,
    val location: String? = null,
    val stepName: String? = StepName.SURNAME.stepName,
    val allStepsCompleted: Boolean = false
)
