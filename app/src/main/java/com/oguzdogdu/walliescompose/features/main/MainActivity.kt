package com.oguzdogdu.walliescompose.features.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.google.android.gms.auth.api.identity.Identity
import com.oguzdogdu.walliescompose.WalliesApplication
import com.oguzdogdu.walliescompose.features.appstate.WalliesApp
import com.oguzdogdu.walliescompose.features.login.googlesignin.GoogleAuthUiClient
import com.oguzdogdu.walliescompose.ui.theme.WalliesComposeTheme
import com.oguzdogdu.walliescompose.util.LocaleHelper
import com.oguzdogdu.walliescompose.util.NetworkMonitor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var application: WalliesApplication

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    private val viewModel: MainViewModel by viewModels()

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
                viewModel.handleScreenEvents(MainScreenEvent.CheckUserAuthState)
                viewModel.handleScreenEvents(MainScreenEvent.ThemeChanged)
                viewModel.handleScreenEvents(MainScreenEvent.LanguageChanged)

            }

            LaunchedEffect(application.language.value) {
                viewModel.handleScreenEvents(MainScreenEvent.LanguageChanged)
                LocaleHelper(context = this@MainActivity).updateResourcesLegacy(application.language.value)
            }
            InitUiWithTheme(application.theme.value, networkMonitor, googleAuthUiClient)
        }
    }
}

@Composable
fun InitUiWithTheme(
    theme: String, networkMonitor: NetworkMonitor, googleAuthUiClient: GoogleAuthUiClient
) {
    var currentTheme by remember(theme) { mutableStateOf(theme) }
    LaunchedEffect(theme) {
        currentTheme = theme
    }

    WalliesComposeTheme(
        appTheme = currentTheme
    ) {
        WalliesApp(
            networkMonitor = networkMonitor, googleAuthUiClient = googleAuthUiClient
        )
    }
}


