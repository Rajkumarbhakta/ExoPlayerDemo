package com.rkbapps.exoplayerdemo.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rkbapps.exoplayerdemo.models.MediaVideos
import com.rkbapps.exoplayerdemo.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _videos = MutableStateFlow<List<MediaVideos>>(emptyList())
    val videos: StateFlow<List<MediaVideos>> = _videos

    private var videoList: List<MediaVideos> = emptyList()


    suspend fun searchVideos(query: String) {
        if (query.isEmpty() || query.isBlank()) {
            _videos.emit(videoList)
        } else {
            val folders = videoList.filter { it.displayName.contains(query, ignoreCase = true) }
            _videos.emit(folders)
        }
    }


    fun setVideoList(videos: List<MediaVideos>) {
        videoList = videos
    }

    suspend fun emitVideos(videos: List<MediaVideos>) {
        _videos.emit(videos)
    }


    fun savePathInSaveStateHandel(path: String) {
        savedStateHandle[Constants.PATH] = path
    }

}