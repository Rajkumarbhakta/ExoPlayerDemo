package com.rkbapps.exoplayerdemo.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rkbapps.exoplayerdemo.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
):ViewModel() {

    fun savePathInSaveStateHandel(path:String){
        savedStateHandle.set(Constants.PATH,path)
    }

}