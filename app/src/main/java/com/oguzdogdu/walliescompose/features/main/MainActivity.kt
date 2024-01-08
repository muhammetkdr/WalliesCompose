package com.oguzdogdu.walliescompose.features.main

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.oguzdogdu.walliescompose.WalliesApplication
import com.oguzdogdu.walliescompose.features.appstate.WalliesApp
import com.oguzdogdu.walliescompose.ui.theme.WalliesComposeTheme
import com.oguzdogdu.walliescompose.util.LocaleHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var application: WalliesApplication

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            viewModel.handleScreenEvents(MainScreenEvent.LanguageChanged)
            LaunchedEffect(application.theme.value) {
                viewModel.handleScreenEvents(MainScreenEvent.ThemeChanged)
            }
            LaunchedEffect(application.language.value) {
                viewModel.handleScreenEvents(MainScreenEvent.LanguageChanged)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    LocaleHelper(context = this@MainActivity).updateResourcesLegacy(application.language.value)
                } else {
                    LocaleHelper(context = this@MainActivity).updateResources(application.language.value)
                }
            }

            WalliesComposeTheme(
                appTheme = application.theme.value
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    WalliesApp(windowSizeClass = calculateWindowSizeClass(this))
                }
            }
        }
    }
}
