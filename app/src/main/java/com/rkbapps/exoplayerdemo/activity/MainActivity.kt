package com.rkbapps.exoplayerdemo.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.multidex.MultiDex
import androidx.navigation.compose.rememberNavController
import com.rkbapps.exoplayerdemo.navigation.NavGraphMain
import com.rkbapps.exoplayerdemo.ui.theme.ExoPlayerDemoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MultiDex.install(this)
        setContent {
            val navController = rememberNavController()
            ExoPlayerDemoTheme {
                NavGraphMain(navController = navController)
            }
        }
    }
}

