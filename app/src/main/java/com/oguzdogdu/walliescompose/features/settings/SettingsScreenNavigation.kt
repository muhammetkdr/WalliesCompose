package com.oguzdogdu.walliescompose.features.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val SettingsScreenNavigationRoute = "settings_screen_route"

fun NavController.navigateToSettingsScreen(
    navOptions: NavOptions? = null,
) {
    this.navigate(SettingsScreenNavigationRoute, navOptions)
}

fun NavGraphBuilder.settingsScreen() {
    composable(
        SettingsScreenNavigationRoute
    ) {
        SettingsScreenRoute()
    }
}