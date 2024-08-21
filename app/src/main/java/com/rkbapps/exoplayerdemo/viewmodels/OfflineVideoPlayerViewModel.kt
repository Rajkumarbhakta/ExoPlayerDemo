package com.rkbapps.exoplayerdemo.viewmodels

import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.annotation.OptIn
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.fromUri
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.rkbapps.exoplayerdemo.util.Constants
import com.rkbapps.exoplayerdemo.util.findActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class OfflineVideoPlayerViewModel  @Inject constructor(
    @ApplicationContext val context: Context,
    val player : ExoPlayer,
    savedStateHandle: SavedStateHandle,
):ViewModel(){

    init {
        player.prepare()
        val path = savedStateHandle.get<String>(Constants.PATH)
    }

    fun playOfflineVideo(path:String,title:String){
        val uri = Uri.parse(path)
        val mediaItem = MediaItem.Builder().setUri(uri).setMediaMetadata(MediaMetadata.Builder().setTitle(title).build()).build()
        player.setMediaItem(mediaItem)
        player.playWhenReady = true
    }


    override fun onCleared() {
        super.onCleared()
        player.release()
        context.findActivity()?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

}

