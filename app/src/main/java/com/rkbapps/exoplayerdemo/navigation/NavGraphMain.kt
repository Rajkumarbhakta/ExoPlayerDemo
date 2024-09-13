package com.rkbapps.exoplayerdemo.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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


@Composable
fun NavGraphMain(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Splash,
        enterTransition = { slideInHorizontally(initialOffsetX = { it/2 }) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { it/2 }) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it/2 }) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { it/2 }) }
    ) {
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