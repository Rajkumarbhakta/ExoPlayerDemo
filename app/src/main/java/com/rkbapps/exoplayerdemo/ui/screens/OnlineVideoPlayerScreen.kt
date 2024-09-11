package com.rkbapps.exoplayerdemo.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.rkbapps.exoplayerdemo.navigation.VideoUrl
import com.rkbapps.exoplayerdemo.ui.composable.VideoPlayer
import com.rkbapps.exoplayerdemo.ui.theme.surfaceContainerDark
import com.rkbapps.exoplayerdemo.viewmodels.OnlinePlayerViewModel

@Composable
fun OnlineVideoPlayerScreen(navController: NavHostController, videoUrl: VideoUrl) {
    val viewModel: OnlinePlayerViewModel = hiltViewModel()
    val videoTimer = rememberSaveable { viewModel.videoTimer }

    LaunchedEffect(Unit) {
        Log.d("URL", videoUrl.url)
        viewModel.playOnlineVideo(videoUrl.url)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.savePlaybackState()
        }
    }

    Scaffold(
        containerColor = surfaceContainerDark
    ) { paddingValues ->
        VideoPlayer(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            exoPlayer = viewModel.player,
            videoTittle = videoUrl.url,
            videoTimer = videoTimer,
        )
    }

}