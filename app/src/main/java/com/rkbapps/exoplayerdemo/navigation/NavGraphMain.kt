package com.rkbapps.exoplayerdemo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.rkbapps.exoplayerdemo.ui.screens.HomeScreen
import com.rkbapps.exoplayerdemo.ui.screens.OfflineVideoPlayerScreen
import com.rkbapps.exoplayerdemo.ui.screens.OnlineVideoPlayerScreen
import com.rkbapps.exoplayerdemo.ui.screens.SplashScreen
import com.rkbapps.exoplayerdemo.ui.screens.VideoListScreen
import kotlinx.serialization.Serializable


@Serializable
object Splash

@Serializable
object Home

@Serializable
data class VideoUrl(val url: String)

@Serializable
data class OfflinePlayer(val video: String, val videoList: String)

@Serializable
data class VideoListing(val name: String, val videos: String)


@Composable
fun NavGraphMain(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Splash) {
        composable<Splash> {
            SplashScreen(navController = navController)
        }
        composable<Home> {
            HomeScreen(navController = navController)
        }
        composable<VideoListing> {
            VideoListScreen(navController = navController, folder = it.toRoute())
        }
        composable<VideoUrl> {
            OnlineVideoPlayerScreen(navController = navController, videoUrl = it.toRoute())
        }
        composable<OfflinePlayer> {
            OfflineVideoPlayerScreen(navController = navController, offlineVideo = it.toRoute())
        }
    }
}