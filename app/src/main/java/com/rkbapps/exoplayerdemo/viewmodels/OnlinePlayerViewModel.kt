package com.rkbapps.exoplayerdemo.viewmodels

import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.compose.runtime.mutableLongStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import com.google.gson.Gson
import com.rkbapps.exoplayerdemo.util.Constants
import com.rkbapps.exoplayerdemo.util.SharedPerfManager
import com.rkbapps.exoplayerdemo.util.findActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class OnlinePlayerViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val player : ExoPlayer,
    private val savedStateHandle: SavedStateHandle,
    private val sharedPerfManager: SharedPerfManager,
    private val gson: Gson
) :ViewModel() {

    companion object {
        private const val CURRENT_POSITION_KEY = "current_position"
    }

    val videoTimer = mutableLongStateOf(0L)

    init {
        player.prepare()
        val path = savedStateHandle.get<String>(Constants.PATH)
        val currentPosition = savedStateHandle.get<Long>(CURRENT_POSITION_KEY) ?: 0L

        player.seekTo(currentPosition)

    }

    val EXAMPLE_VIDEO_URI = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"


    fun playOnlineVideo(url:String,title:String = url){
        val mediaItem  = MediaItem.fromUri(EXAMPLE_VIDEO_URI)
        player.setMediaItem(mediaItem)
        player.playWhenReady = true
    }


    fun savePlaybackState() {
        savedStateHandle[CURRENT_POSITION_KEY] = player.currentPosition
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
        context.findActivity()?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}