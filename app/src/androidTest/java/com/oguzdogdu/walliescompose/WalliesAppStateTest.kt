//package com.oguzdogdu.walliescompose
//
//import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.remember
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.test.junit4.createComposeRule
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.ComposeNavigator
//import androidx.navigation.compose.composable
//import androidx.navigation.createGraph
//import androidx.navigation.testing.TestNavHostController
//import com.oguzdogdu.walliescompose.features.appstate.MainAppState
//import com.oguzdogdu.walliescompose.features.appstate.rememberMainAppState
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.collect
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.test.UnconfinedTestDispatcher
//import kotlinx.coroutines.test.runTest
//import org.junit.Assert.assertEquals
//import org.junit.Assert.assertTrue
//import org.junit.Rule
//import org.junit.Test
//
//class WalliesAppStateTest {
//    @get:Rule
//    val composeTestRule = createComposeRule()
//
//    private val networkMonitor = TestNetworkMonitor()
//
//    private lateinit var state: MainAppState
//
//    @Test
//    fun walliesAppState_currentDestination() = runTest {
//        var currentDestination: String? = null
//
//        composeTestRule.setContent {
//            val navController = rememberTestNavController()
//            state = remember(navController) {
//                MainAppState(
//                    coroutineScope = backgroundScope,
//                    networkMonitor = networkMonitor,
//                )
//            }
//
//            LaunchedEffect(Unit) {
//                navController.setCurrentDestination("b")
//            }
//        }
//
//        assertEquals("b", currentDestination)
//    }
//
//    @Test
//    fun walliesAppState_destinations() = runTest {
//        composeTestRule.setContent {
//            state = rememberMainAppState(
//                networkMonitor = networkMonitor,
//            )
//        }
//
//    }
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun stateIsOfflineWhenNetworkMonitorIsOffline() = runTest(UnconfinedTestDispatcher()) {
//        composeTestRule.setContent {
//            state = MainAppState(
//                coroutineScope = backgroundScope,
//                networkMonitor = networkMonitor,
//            )
//        }
//
//        backgroundScope.launch { state.isOffline.collect() }
//        networkMonitor.setConnected(false)
//        assertEquals(
//            true,
//            state.isOffline.value,
//        )
//    }
//}
//
//@Composable
//private fun rememberTestNavController(): TestNavHostController {
//    val context = LocalContext.current
//    return remember {
//        TestNavHostController(context).apply {
//            navigatorProvider.addNavigator(ComposeNavigator())
//            graph = createGraph(startDestination = "a") {
//                composable("a") { }
//                composable("b") { }
//                composable("c") { }
//            }
//        }
//    }
//}