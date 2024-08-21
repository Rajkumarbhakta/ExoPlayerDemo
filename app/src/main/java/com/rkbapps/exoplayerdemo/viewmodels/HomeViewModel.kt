package com.rkbapps.exoplayerdemo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkbapps.exoplayerdemo.models.Folders
import com.rkbapps.exoplayerdemo.repository.HomeRepository
import com.rkbapps.exoplayerdemo.repository.MediaVideosUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepository) :ViewModel() {


    val folderList: StateFlow<MediaVideosUiState> = repository.folderList


    init {
        viewModelScope.launch {
            repository.fetchMediaFolders()
        }
    }

    fun searchFolder(it: String):List<Folders> = repository.searchFolder(it)


}