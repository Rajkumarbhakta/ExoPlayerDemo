package com.rkbapps.exoplayerdemo.repository

import android.annotation.SuppressLint
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.rkbapps.exoplayerdemo.models.Folders
import com.rkbapps.exoplayerdemo.models.MediaVideos
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


class HomeRepository @Inject constructor(
    @ApplicationContext val context:Context
) {

    private val _folderList: MutableStateFlow<MediaVideosUiState> = MutableStateFlow(MediaVideosUiState())
    val folderList: StateFlow<MediaVideosUiState> = _folderList

    @SuppressLint("Range", "Recycle")
    suspend fun fetchMediaFolders(){
        _folderList.emit(MediaVideosUiState(isLoading = true))
        try {
            val tempList = ArrayList<Folders>()
            val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            val cursor = context.contentResolver.query(uri,null,null,null,null)
            cursor?.let {
                if (cursor.moveToNext()){
                    do {
                        val id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                        val title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                        val displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))
                        val size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
                        val duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                        val path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                        val date = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))
                        val index = path.lastIndexOf("/")
                        val folderPath = path.substring(0,index)
                        val folderNameIndex = folderPath.lastIndexOf("/")
                        val folderName = folderPath.substring(folderNameIndex+1)
                        val folder = tempList.find { it.name == folderName }
                        if (folder!=null){
                            folder.files.add(
                                MediaVideos(id=id,title=title,displayName=displayName,size=size,
                                duration=duration,path=path,date=date,folderName=folderName)
                            )
                        }else{
                            tempList.add(Folders(name = folderName, files = mutableListOf(
                                MediaVideos(id=id,title=title,displayName=displayName,size=size,
                                duration=duration,path=path,date=date,folderName=folderName)
                            )))
                        }
                    }while (cursor.moveToNext())
                }
            }
            Log.d("VIDEOS","$tempList")
            _folderList.emit(MediaVideosUiState(folders = tempList))
        }catch (e:Exception){
            _folderList.emit(MediaVideosUiState(error = e.localizedMessage))
            e.localizedMessage
        }
    }

    fun searchFolder(query: String):List<Folders> {
        return _folderList.value.folders.filter { it.name.contains(query,ignoreCase = true) }?: emptyList()
    }

}
data class MediaVideosUiState(
    val isLoading:Boolean = false,
    val folders:List<Folders> = emptyList(),
    val error:String? = null
)