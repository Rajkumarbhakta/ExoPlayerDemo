package com.rkbapps.exoplayerdemo.ui.composable.controls

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.IntState
import androidx.compose.runtime.LongState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun PlayerControl(
    modifier: Modifier = Modifier,
    isVisible: () -> Boolean,
    isPlaying: () -> Boolean,
    title: String,
    onRewind: () -> Unit,
    onPlay: () -> Unit,
    onFastForward: () -> Unit,
    playbackState: () -> Int,
    bufferedPercentage: IntState,
    videoTimer: LongState,
    totalDuration: Long,
    resizeMode: Int,
    onPrevious: (() -> Unit)? = {},
    onNext: (() -> Unit)? = {},
    onResizeModeChanged: (Int) -> Unit,
    onSeekChanged: (newValue: Float) -> Unit,

    ) {

    val context = LocalContext.current

    val visible = remember(isVisible()) { isVisible() }
    val playing = remember(isPlaying()) { isPlaying() }

    val duration = remember(totalDuration) { totalDuration.coerceAtLeast(0) }

    val timer = remember(videoTimer) { videoTimer }

    val videoTitle = remember(title) { title }

    val buffer = remember(bufferedPercentage) { bufferedPercentage }

    val playerState = remember(playbackState()) {
        playbackState()
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {

            TopController(
                modifier = Modifier
                    .background(color = Color.Black.copy(alpha = 0.6f))
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(),
                title = videoTitle
            )

            CenterController(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                isPlaying = playing,
                onRewind = onRewind, onPlay = onPlay, onFastForward = onFastForward
            )

            BottomControl(
                modifier = Modifier
                    .background(color = Color.Black.copy(alpha = 0.6f))
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(8.dp),
                bufferPercent = buffer, time = timer, totalTime = duration,
                resizeMode = resizeMode,
                isPlaying = playing,
                onResizeModeChanged = onResizeModeChanged,
                onSeekChanged = onSeekChanged,
                onPlay = onPlay,
                onNext = onNext,
                onPrevious = onPrevious

            )
        }
    }
}