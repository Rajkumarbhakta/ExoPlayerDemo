package com.rkbapps.exoplayerdemo.ui.composable.controls

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.Window
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.IntState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.LongState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import com.rkbapps.exoplayerdemo.R
import com.rkbapps.exoplayerdemo.util.Constants
import com.rkbapps.exoplayerdemo.util.findActivity
import com.rkbapps.exoplayerdemo.util.hideSystemBars
import com.rkbapps.exoplayerdemo.util.showSystemBars

@OptIn(UnstableApi::class)
@Composable
fun BottomControl(
    modifier: Modifier = Modifier,
    bufferPercent: IntState,
    isPlaying:Boolean=false,
    time: LongState,
    totalTime: Long,
    resizeMode: Int,
    onPlay:()->Unit={},
    onPrevious:()->Unit={},
    onNext:()->Unit={},
    onResizeModeChanged: (Int) -> Unit,
    onSeekChanged: (newValue: Float) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val activity = remember { context.findActivity() }
    val window = remember { activity?.window }

    val timer = remember(time.longValue) { time.longValue }

    val orientationIcon = rememberSaveable {
        mutableIntStateOf(R.drawable.maximise)
    }
    val resizeModes = remember{
        mutableStateListOf(
            AspectRatioFrameLayout.RESIZE_MODE_FIT,
            AspectRatioFrameLayout.RESIZE_MODE_FILL,
            AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        )
    }

    val currentResizeMode = rememberSaveable {
        mutableStateOf("Fit")
    }


    LaunchedEffect(resizeMode) {
        currentResizeMode.value = when(resizeMode) {
            AspectRatioFrameLayout.RESIZE_MODE_FIT -> "Fit"
            AspectRatioFrameLayout.RESIZE_MODE_FILL -> "Fill"
            AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> "Zoom"
            else -> "Fit"
        }
    }



    LaunchedEffect(configuration.orientation) {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            orientationIcon.intValue = R.drawable.minimize
            window?.let {
                hideSystemBars(it)
            }
        } else {
            orientationIcon.intValue = R.drawable.maximise
            window?.let {
                showSystemBars(it)
            }
        }
    }


    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = Constants.convertTime(timer.toString()),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(5.dp))
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Slider(
                    value = bufferPercent.intValue.toFloat(),
                    onValueChange = {},
                    enabled = false,
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        disabledThumbColor = Color.Transparent,
                        disabledActiveTrackColor = Color.White
                    ),
                )
                Slider(
                    value = timer.toFloat(), onValueChange = {
                        onSeekChanged.invoke(it)
                    }, valueRange = 0f..totalTime.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                    )
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = Constants.convertTime(totalTime.toString()),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.width(5.dp))
        }

        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center){
                Row {
                    IconButton(onClick = { onPrevious() }) {
                        Icon(painter = painterResource(id = R.drawable.play_next),
                            contentDescription = "play previous",
                            modifier= Modifier.rotate(180f).size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    IconButton(onClick = { onPlay() }) {
                        Icon(painter = if (isPlaying) painterResource(id = R.drawable.pause) else painterResource(id = R.drawable.play),
                            contentDescription = "play/pause",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    IconButton(onClick = { onNext() }) {
                        Icon(painter = painterResource(id = R.drawable.play_next),
                            contentDescription = "play next",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            Row {
                TextButton(onClick = {
                    if (resizeMode == AspectRatioFrameLayout.RESIZE_MODE_FIT)  {
                        onResizeModeChanged(AspectRatioFrameLayout.RESIZE_MODE_FILL)
                    }
                    if (resizeMode == AspectRatioFrameLayout.RESIZE_MODE_FILL) {
                        onResizeModeChanged(AspectRatioFrameLayout.RESIZE_MODE_ZOOM)
                    }
                    if (resizeMode == AspectRatioFrameLayout.RESIZE_MODE_ZOOM) {
                        onResizeModeChanged(AspectRatioFrameLayout.RESIZE_MODE_FIT)
                    }
                }) {
                    Text(text = currentResizeMode.value, style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface)
                }
                Spacer(modifier = Modifier.width(5.dp))
                IconButton(onClick = {
                    if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    } else {
                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    }
                }) {
                    Icon(painter =  painterResource(id = R.drawable.orientation_change), contentDescription = "minimize/maximize")
                }
            }

        }
    }



}

