package com.fourshil.musicya.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.ui.dsp.DspScreen
import com.fourshil.musicya.ui.library.LibraryScreen
import com.fourshil.musicya.ui.library.LibraryViewModel
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
                onDspClick = { navController.navigate(Screen.Dsp.route) },
                onLibraryClick = { navController.navigate(Screen.Library.route) }
            )
        }
        composable(Screen.Dsp.route) {
            DspScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Library.route) {
            val viewModel = hiltViewModel<LibraryViewModel>()
            LibraryScreen(
                viewModel = viewModel,
                onSongClick = { song ->
                    viewModel.playSong(song)
                    navController.navigate(Screen.Player.route) {
                        popUpTo(Screen.Player.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
