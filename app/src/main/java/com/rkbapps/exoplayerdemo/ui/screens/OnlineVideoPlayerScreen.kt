package com.rkbapps.exoplayerdemo.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.core.screen.Screen
import com.rkbapps.exoplayerdemo.ui.composable.VideoPlayer
import com.rkbapps.exoplayerdemo.ui.theme.surfaceContainerDark
import com.rkbapps.exoplayerdemo.viewmodels.OnlinePlayerViewModel

class OnlineVideoPlayerScreen(private val url: String) : Screen {
    @Composable
    override fun Content() {

        val viewModel: OnlinePlayerViewModel = hiltViewModel()
        val videoTimer = rememberSaveable { viewModel.videoTimer }

        val error = rememberSaveable { mutableStateOf<String?>(null) }
        val isErrorDialogVisible = rememberSaveable {
            mutableStateOf(false)
        }

        LaunchedEffect(Unit) {
            Log.d("URL", url)
            viewModel.playOnlineVideo(url)
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
                videoTittle = url,
                videoTimer = videoTimer,
            )
        }


    }

    @Composable
    fun ErrorDialog(
        modifier: Modifier = Modifier,
        msg: String? = null,
        onDismiss: () -> Unit,
        onConfirm: () -> Unit
    ) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = { onDismiss.invoke() },
            title = { Text(text = "Error") },
            text = { Text(text = msg ?: "Something went wrong") },
            confirmButton = {
                TextButton(onClick = { onConfirm.invoke() }) {
                    Text(text = "Ok")
                }
            })
    }

}