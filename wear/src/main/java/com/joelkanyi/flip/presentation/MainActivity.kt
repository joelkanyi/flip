package com.joelkanyi.flip.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.joelkanyi.flip.audio.SoundManager
import com.joelkanyi.flip.data.repository.FlipRepositoryImpl
import com.joelkanyi.flip.haptic.HapticManager
import com.joelkanyi.flip.presentation.theme.FlipTheme
import com.joelkanyi.flip.presentation.viewmodel.CoinFlipViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            FlipApp()
        }
    }
}

@Composable
fun FlipApp() {
    val context = LocalContext.current
    val navController = rememberSwipeDismissableNavController()

    val viewModel = remember {
        val repository = FlipRepositoryImpl(context)
        val hapticManager = HapticManager(context)
        val soundManager = SoundManager(context)
        CoinFlipViewModel(repository, hapticManager, soundManager)
    }

    FlipTheme {
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = "flip"
        ) {
            composable("flip") {
                FlipScreen(
                    viewModel = viewModel,
                    onNavigateToStats = {
                        navController.navigate("stats")
                    }
                )
            }
            composable("stats") {
                StatsScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
