package com.rkbapps.exoplayerdemo.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.google.gson.Gson
import com.rkbapps.exoplayerdemo.models.Videos
import com.rkbapps.exoplayerdemo.navigation.VideoListing
import com.rkbapps.exoplayerdemo.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    val gson: Gson
) : ViewModel() {

    private val _videos = MutableStateFlow<List<Videos>>(emptyList())
    val videos: StateFlow<List<Videos>> = _videos

    private var videoList: List<Videos> = emptyList()


    init {
        viewModelScope.launch {
            emitVideos()
        }
    }


    suspend fun searchVideos(query: String) {
        if (query.isEmpty() || query.isBlank()) {
            _videos.emit(videoList)
        } else {
            val folders = videoList.filter { it.displayName.contains(query, ignoreCase = true) }
            _videos.emit(folders)
        }
    }


    private fun setVideoList(videos: List<Videos>) {
        videoList = videos
    }

    private suspend fun emitVideos() {
        try {
            val videos = savedStateHandle.toRoute<VideoListing>()
            val actualVideoList = gson.fromJson(videos.videos, Array<Videos>::class.java).toList()
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