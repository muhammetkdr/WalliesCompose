package com.oguzdogdu.walliescompose.features.authenticateduser.changenameandsurname

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.oguzdogdu.walliescompose.navigation.Screens

fun NavController.navigateToChangeNameAndASurnameScreen(name:String?, surname: String?, bio: String?) {
    navigate(route = Screens.ChangeNameAndSurnameScreenRoute(name = name, surname = surname, bio = bio))
}

fun NavGraphBuilder.changeNameAndSurnameScreen(
    navigateBack: () -> Unit,
) {
    composable<Screens.ChangeNameAndSurnameScreenRoute> {
        ChangeNameAndSurnameScreenRoute(navigateBack = {
            navigateBack.invoke()
        })
    }
}