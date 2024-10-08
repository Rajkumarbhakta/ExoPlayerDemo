package com.rkbapps.exoplayerdemo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rkbapps.exoplayerdemo.models.Videos
import com.rkbapps.exoplayerdemo.repository.HomeRepository
import com.rkbapps.exoplayerdemo.repository.MediaVideosUiState
import com.rkbapps.exoplayerdemo.util.Constants
import com.rkbapps.exoplayerdemo.util.SharedPerfManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val sharedPerfManager: SharedPerfManager,
    val gson: Gson
) : ViewModel() {


    val folderList: StateFlow<MediaVideosUiState> = repository.folderList

    init {
        viewModelScope.launch {
            delay(50)
            repository.fetchMediaFolders()
        }
    }

    fun fetchVideos() {
        viewModelScope.launch {
            repository.fetchMediaFolders()
        }
    }

    fun readLastPlayedVideo(): Videos? {
        return try {
            val data = sharedPerfManager.readString(Constants.LAST_PLAYED_VIDEO)
            data?.let {
                val video = gson.fromJson(it, Videos::class.java)
                video
            }
        } catch (e: Exception) {
            null
        }
    }


    fun searchFolder(it: String) {
        viewModelScope.launch {
            repository.searchFolder(it)
        }
    }


}