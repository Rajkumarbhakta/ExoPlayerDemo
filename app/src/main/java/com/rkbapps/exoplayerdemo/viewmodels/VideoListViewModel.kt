package com.rkbapps.exoplayerdemo.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.rkbapps.exoplayerdemo.models.MediaVideos
import com.rkbapps.exoplayerdemo.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    val gson: Gson
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


    private fun setVideoList(videos: List<MediaVideos>) {
        videoList = videos
    }

    suspend fun emitVideos(videos: String) {
        try {
            val actualVideoList = gson.fromJson(videos, Array<MediaVideos>::class.java).toList()
            delay(50)
            _videos.emit(actualVideoList)
            setVideoList(actualVideoList)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    fun savePathInSaveStateHandel(path: String) {
        savedStateHandle[Constants.PATH] = path
    }

}