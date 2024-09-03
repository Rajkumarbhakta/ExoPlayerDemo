package com.rkbapps.exoplayerdemo.ui.screens

import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.rkbapps.exoplayerdemo.models.MediaVideos
import com.rkbapps.exoplayerdemo.ui.composable.VideoPlayer
import com.rkbapps.exoplayerdemo.ui.theme.surfaceContainerDark
import com.rkbapps.exoplayerdemo.viewmodels.OfflineVideoPlayerViewModel

class OfflineVideoPlayerScreen(private val video: MediaVideos,
                               private val videoList: List<MediaVideos> = emptyList(),
                               private val index:Int = 0
) : Screen {

    @OptIn(UnstableApi::class)
    @Composable
    override fun Content() {
        val viewModel: OfflineVideoPlayerViewModel = hiltViewModel()
        val videoTimer = rememberSaveable { viewModel.videoTimer }

        LaunchedEffect(Unit) {
            if (videoList.isEmpty()){
                viewModel.playOfflineVideo(video.path, video.title)
            }else{
                viewModel.prepareAndPlayPlaylist(videoList,video)
            }
            viewModel.saveLastPlayedVideo(video)
        }

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
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                exoPlayer = viewModel.player,
                videoTittle = video.title,
                videoTimer = videoTimer,
                onPreviousClicked = { viewModel.playPreviousVideo() },
                onNextClicked = { viewModel.playNextVideo() },
            )
        }
    }





}