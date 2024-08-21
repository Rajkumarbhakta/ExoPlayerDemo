package com.rkbapps.exoplayerdemo.ui.screens

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import cafe.adriel.voyager.core.screen.Screen
import com.rkbapps.exoplayerdemo.models.MediaVideos
import com.rkbapps.exoplayerdemo.ui.composable.VideoPlayer
import com.rkbapps.exoplayerdemo.viewmodels.OfflineVideoPlayerViewModel

class OfflineVideoPlayerScreen(private val video: MediaVideos) : Screen {
    @OptIn(UnstableApi::class)
    @Composable
    override fun Content() {
        val viewModel: OfflineVideoPlayerViewModel = hiltViewModel()


        LaunchedEffect(Unit) {
            viewModel.playOfflineVideo(video.path, video.title)
        }

        val lifecycle = LocalLifecycleOwner.current.lifecycle

        Scaffold() { paddingValues ->
            VideoPlayer(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                exoPlayer = viewModel.player,
                videoTittle = video.title,
            )
        }
    }
}