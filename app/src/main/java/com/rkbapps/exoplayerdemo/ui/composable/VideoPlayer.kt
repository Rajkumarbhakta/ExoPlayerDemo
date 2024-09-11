package com.rkbapps.exoplayerdemo.ui.composable

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.rkbapps.exoplayerdemo.ui.composable.controls.PlayerControl
import com.rkbapps.exoplayerdemo.util.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    exoPlayer: ExoPlayer,
    videoTittle: String,
    videoTimer: MutableLongState,
    onPreviousClicked: (() -> Unit)? = null,
    onNextClicked: (() -> Unit)? = null,
    navigateBack:()->Unit
) {
    val resizeMode = rememberSaveable { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }
    var shouldShowControls by rememberSaveable { mutableStateOf(false) }
    var isPlaying by rememberSaveable { mutableStateOf(exoPlayer.isPlaying) }
    var playbackState by remember { mutableIntStateOf(exoPlayer.playbackState) }
    val totalDuration = remember { mutableLongStateOf(0L) }
    val bufferedPercentage = remember { mutableIntStateOf(0) }


    val error = rememberSaveable { mutableStateOf<String?>(null) }
    val isErrorDialogVisible = rememberSaveable { mutableStateOf(false) }

    //update video timer
    LaunchedEffect(exoPlayer) {
        if (isActive) {
            while (true) {
                if (exoPlayer.isPlaying) {
                    videoTimer.longValue = exoPlayer.currentPosition
                }
                delay(1000)
            }
        }
    }

    //update buffer percentage
    LaunchedEffect(exoPlayer) {
        if (isActive) {
            while (true) {
                bufferedPercentage.intValue = exoPlayer.bufferedPercentage
                delay(1000)
            }
        }
    }

    //play from the already played position when configuration changes
    LaunchedEffect(Unit) {
        exoPlayer.seekTo(videoTimer.longValue)
        if (isPlaying) {
            exoPlayer.playWhenReady = true
        }
    }

    //hide controls after 5 seconds
    LaunchedEffect(key1 = shouldShowControls) {
        if (shouldShowControls) {
            delay(Constants.PLAYER_CONTROLS_VISIBILITY)
            shouldShowControls = false
        }
    }


    //add listener to exoPlayer
    DisposableEffect(true) {
        val listener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                isPlaying = player.isPlaying
                totalDuration.longValue = player.duration
                videoTimer.longValue = player.currentPosition
                bufferedPercentage.intValue = player.bufferedPercentage
                playbackState = player.playbackState
            }

            override fun onPlayerError(e: PlaybackException) {
                error.value = e.localizedMessage
                isErrorDialogVisible.value = true
                super.onPlayerError(e)
            }

            override fun onPlayerErrorChanged(e: PlaybackException?) {
                error.value = e?.localizedMessage
                isErrorDialogVisible.value = true
                super.onPlayerErrorChanged(e)
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }


    Box(modifier = modifier) {


        if (isErrorDialogVisible.value) {
            ErrorDialog(
                msg = error.value,
                onDismiss = { /*TODO*/ }) {
                isErrorDialogVisible.value = false
                navigateBack()
            }
        }


        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    layoutParams =
                        FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    useController = false
                    this.resizeMode = resizeMode.intValue
                    videoSurfaceView
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    shouldShowControls = !shouldShowControls
                },
            update = { playerView ->
                playerView.resizeMode = resizeMode.intValue
            }

        )
        PlayerControl(
            modifier = Modifier.fillMaxSize(),
            isVisible = { shouldShowControls },
            isPlaying = { isPlaying },
            title = videoTittle,
            onRewind = { exoPlayer.seekBack() },
            onPlay = {
                when {
                    exoPlayer.isPlaying -> {
                        exoPlayer.pause()
                    }

                    exoPlayer.isPlaying.not() && playbackState == STATE_ENDED -> {
                        exoPlayer.seekTo(0, 0)
                        exoPlayer.playWhenReady = true
                    }

                    else -> {
                        exoPlayer.play()
                    }
                }
                isPlaying = isPlaying.not()
            },
            onPrevious = onPreviousClicked,
            onNext = onNextClicked,
            onFastForward = { exoPlayer.seekForward() },
            playbackState = { playbackState },
            bufferedPercentage = bufferedPercentage,
            videoTimer = videoTimer,
            totalDuration = totalDuration.longValue,
            resizeMode = resizeMode.intValue,
            onResizeModeChanged = {
                resizeMode.intValue = it
            },
            onNavigateBack = navigateBack,
        ) { position ->
            exoPlayer.seekTo(
                position.toLong()
            )
        }
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