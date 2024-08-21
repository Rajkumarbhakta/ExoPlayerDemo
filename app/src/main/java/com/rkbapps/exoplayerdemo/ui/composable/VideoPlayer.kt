package com.rkbapps.exoplayerdemo.ui.composable

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.google.android.exoplayer2.Player.STATE_ENDED
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
) {
    val resizeMode = rememberSaveable { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }

    var shouldShowControls by rememberSaveable { mutableStateOf(false) }
    var isPlaying by rememberSaveable { mutableStateOf(exoPlayer.isPlaying) }
    var playbackState by remember { mutableIntStateOf(exoPlayer.playbackState) }
    val videoTimer = remember { mutableLongStateOf(0L) }
    val totalDuration = remember { mutableLongStateOf(0L) }
    val bufferedPercentage = remember { mutableIntStateOf(0) }



    //update video timer
    LaunchedEffect(exoPlayer) {
        if (isActive){
            while (true){
                if (exoPlayer.isPlaying){
                    videoTimer.longValue = exoPlayer.currentPosition
                }
                delay(1000)
            }
        }
    }

    //update buffer percentage
    LaunchedEffect(exoPlayer) {
        if (isActive){
            while (true){
                bufferedPercentage.intValue = exoPlayer.bufferedPercentage
                delay(1000)
            }
        }
    }

    //play from the already played position when configuration changes
    LaunchedEffect(Unit) {
        exoPlayer.seekTo(videoTimer.longValue)
        if (isPlaying){
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
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }


    Box(modifier = modifier) {
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
            update = {playerView ->
                playerView.resizeMode = resizeMode.intValue
            }

        )
        PlayerControl(
            modifier = Modifier.fillMaxSize(),
            isVisible = { shouldShowControls },
            isPlaying = { isPlaying },
            getTitle = { videoTittle },
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
            onFastForward = { exoPlayer.seekForward() },
            playbackState = { playbackState },
            bufferedPercentage = bufferedPercentage,
            videoTimer = videoTimer,
            totalDuration = totalDuration.longValue,
            resizeMode = resizeMode.intValue,
            onResizeModeChanged = {
                resizeMode.intValue = it
            }
        ) { position ->
                exoPlayer.seekTo(position.toLong()
            )
        }
    }
}