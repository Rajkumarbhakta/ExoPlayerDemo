package com.rkbapps.exoplayerdemo.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.rkbapps.exoplayerdemo.models.MediaVideos
import com.rkbapps.exoplayerdemo.navigation.OfflinePlayer
import com.rkbapps.exoplayerdemo.ui.composable.VideoPlayer
import com.rkbapps.exoplayerdemo.ui.theme.surfaceContainerDark
import com.rkbapps.exoplayerdemo.viewmodels.OfflineVideoPlayerViewModel

@Composable
fun OfflineVideoPlayerScreen(viewModel: OfflineVideoPlayerViewModel = hiltViewModel(),navController: NavHostController, offlineVideo: OfflinePlayer) {
    val videoTimer = rememberSaveable { viewModel.videoTimer }
    val videoTittle = remember { viewModel.videoTittle }



    DisposableEffect(Unit) {
        onDispose {
            viewModel.savePlaybackState()
        }
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    Scaffold(
        containerColor = surfaceContainerDark
    ) { paddingValues ->
        VideoPlayer(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            exoPlayer = viewModel.player,
            videoTittle = videoTittle.value,
            videoTimer = videoTimer,
            onPreviousClicked = { viewModel.playPreviousVideo() },
            onNextClicked = { viewModel.playNextVideo() },
        ){
            navController.navigateUp()
        }
    }
}