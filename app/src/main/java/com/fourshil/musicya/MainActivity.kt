package com.fourshil.musicya

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.fourshil.musicya.player.PlayerController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var playerController: PlayerController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set content to a placeholder or wait for Flutter integration
        // setContent { ... } was removed as Compose is no longer used.
    }
}
