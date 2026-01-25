package com.fourshil.musicya

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fourshil.musicya.player.PlayerController
import com.fourshil.musicya.ui.MusicyaApp
import com.fourshil.musicya.ui.theme.MusicyaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var playerController: PlayerController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Connect player controller when app starts
        playerController.connect()
        
        setContent {
            MusicyaTheme {
                MusicyaApp()
            }
        }
    }
}
