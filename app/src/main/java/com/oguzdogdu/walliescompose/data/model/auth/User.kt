package com.oguzdogdu.walliescompose.data.model.auth

import androidx.compose.runtime.Immutable

@Immutable
data class User(
    val name: String?,
    val surname: String?,
    val email: String?,
    val image: String?,
    val favorites: List<HashMap<String,String>>?,
    val bio : String?,
    val location: String?,
    val stepName: String? = null,
    val allStepsCompleted: Boolean = false
)

fun User.toUserDomain() =
    com.oguzdogdu.walliescompose.domain.model.auth.User(
        name = name,
        surname = surname,
        email = email,
        image = image,
        favorites = favorites,
        bio = bio,
        location = location,
        stepName = stepName,
        allStepsCompleted = allStepsCompleted
    )