package com.rkbapps.exoplayerdemo.viewmodels

import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.runtime.mutableLongStateOf
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
import com.google.gson.Gson
import com.rkbapps.exoplayerdemo.models.MediaVideos
import com.rkbapps.exoplayerdemo.util.Constants
import com.rkbapps.exoplayerdemo.util.SharedPerfManager
import com.rkbapps.exoplayerdemo.util.findActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class OfflineVideoPlayerViewModel  @Inject constructor(
    @ApplicationContext val context: Context,
    val player : ExoPlayer,
    private val savedStateHandle: SavedStateHandle,
    private val sharedPerfManager: SharedPerfManager,
    private val gson: Gson
):ViewModel(){

    companion object {
        private const val CURRENT_POSITION_KEY = "current_position"
        private const val CURRENT_VIDEO_INDEX_KEY = "current_video_index"
    }
    val videoTimer = mutableLongStateOf(0L)
    private var videos: List<MediaVideos> = emptyList()

    init {
        player.prepare()
        val path = savedStateHandle.get<String>(Constants.PATH)
        val currentPosition = savedStateHandle.get<Long>(CURRENT_POSITION_KEY) ?: 0L
        val currentVideoIndex = savedStateHandle.get<Int>(CURRENT_VIDEO_INDEX_KEY) ?: 0

        if (currentVideoIndex >= 0) {
            player.seekTo(currentVideoIndex, currentPosition)
        }
    }

    fun playOfflineVideo(path:String,title:String){
        val uri = Uri.parse(path)
        val mediaItem = MediaItem.Builder().setUri(uri).setMediaMetadata(MediaMetadata.Builder().setTitle(title).build()).build()
        player.setMediaItem(mediaItem)
        player.playWhenReady = true
    }


    fun saveLastPlayedVideo(video:MediaVideos){
        val lastPlayedVideo = gson.toJson(video)
        sharedPerfManager.writeString(Constants.LAST_PLAYED_VIDEO,lastPlayedVideo)
    }

    fun prepareAndPlayPlaylist(videos: List<MediaVideos>,video: MediaVideos){
        val startIndex = videos.indexOfFirst { it.path == video.path }
        if (startIndex == -1) return
        this.videos = videos
       videos.map {
            MediaItem.Builder()
                .setUri(Uri.parse(it.path))
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(it.title)
                        .build()
                )
                .build()
        }.also { mediaItems ->
            player.setMediaItems(mediaItems, startIndex,videoTimer.longValue)
            player.playWhenReady = true
        }
    }

    fun savePlaybackState() {
        savedStateHandle[CURRENT_POSITION_KEY] = player.currentPosition
        savedStateHandle[CURRENT_VIDEO_INDEX_KEY] = player.currentMediaItemIndex
    }


    fun playNextVideo() {
        if (player.hasNextMediaItem()) {
            player.seekToNext()
        } else {
            player.seekTo(0, 0L)
        }
    }

    fun playPreviousVideo() {
        if (player.hasPreviousMediaItem()) {
            player.seekToPrevious()
        } else {
            player.seekTo(videos.size - 1, 0L)
        }
    }


    override fun onCleared() {
        super.onCleared()
        player.release()
        context.findActivity()?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

}

