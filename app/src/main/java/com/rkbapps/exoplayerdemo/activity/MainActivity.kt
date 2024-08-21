package com.rkbapps.exoplayerdemo.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.multidex.MultiDex
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.rkbapps.exoplayerdemo.ui.screens.SplashScreen
import com.rkbapps.exoplayerdemo.ui.theme.ExoPlayerDemoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MultiDex.install(this)
        setContent {
            ExoPlayerDemoTheme {
                Navigator(screen = SplashScreen()){
                    SlideTransition(navigator = it)
                }

            }
        }
    }
}

