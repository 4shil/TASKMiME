package com.fourshil.musicya.ui.navigation

sealed class Screen(val route: String) {
    data object Player : Screen("player")
    data object Library : Screen("library")
    data object Dsp : Screen("dsp")
}
