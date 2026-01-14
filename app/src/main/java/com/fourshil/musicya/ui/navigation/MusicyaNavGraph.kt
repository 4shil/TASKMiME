package com.fourshil.musicya.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fourshil.musicya.ui.dsp.DspScreen
import com.fourshil.musicya.ui.player.PlayerScreen

@Composable
fun MusicyaNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Player.route
    ) {
        composable(Screen.Player.route) {
            PlayerScreen(
                onDspClick = { navController.navigate(Screen.Dsp.route) }
            )
        }
        composable(Screen.Dsp.route) {
            DspScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
