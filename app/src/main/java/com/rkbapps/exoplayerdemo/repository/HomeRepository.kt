package com.rkbapps.exoplayerdemo.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.rkbapps.exoplayerdemo.models.Folders
import com.rkbapps.exoplayerdemo.models.MediaVideos
import com.rkbapps.exoplayerdemo.models.StorageLocation
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class HomeRepository @Inject constructor(
    @ApplicationContext val context: Context
) {

    private val _folderList: MutableStateFlow<MediaVideosUiState> =
        MutableStateFlow(MediaVideosUiState())
    val folderList: StateFlow<MediaVideosUiState> = _folderList
    private var folders: List<Folders> = emptyList()
    private val internalStoragePath: String = Environment.getExternalStorageDirectory().absolutePath


    @SuppressLint("Range")
    suspend fun fetchMediaFolders() {
        _folderList.emit(MediaVideosUiState(isLoading = true))
        try {
            val tempList = ArrayList<Folders>()

            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DATE_ADDED
            )
            val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

            val selection = "${MediaStore.Video.Media.DURATION} >= ?"
            val selectionArgs =
                arrayOf(TimeUnit.MILLISECONDS.convert(10, TimeUnit.SECONDS).toString())

            val cursor =
                context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
            cursor?.use {
                if (cursor.moveToNext()) {
                    do {
                        val id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                        val title =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                        val displayName =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))
                        val size =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
                        val duration =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                        val path =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                        val date =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))

                        val index = path.lastIndexOf("/")
                        val folderPath = path.substring(0, index)
                        val folderNameIndex = folderPath.lastIndexOf("/")
                        val folderName = folderPath.substring(folderNameIndex + 1)
                        val folder = tempList.find { it.name == folderName }

                        val formatIndex = path.lastIndexOf(".")
                        val format = path.substring(formatIndex + 1)

                        val location =
                            if (isInternalStorage(path)) StorageLocation.INTERNAL else StorageLocation.EXTERNAL

                        Log.d("VIDEOS", "Folder Name : $folderName")
                        if (folder != null) {
                            folder.files.add(
                                MediaVideos(
                                    id = id, title = title, displayName = displayName,
                                    size = size, duration = duration, path = path, date = date,
                                    folderName = folderName, format = format, location = location
                                )
                            )
                        } else {
                            tempList.add(
                                Folders(
                                    name = folderName,
                                    files = mutableListOf(
                                        MediaVideos(
                                            id = id,
                                            title = title,
                                            displayName = displayName,
                                            size = size,
                                            duration = duration,
                                            path = path,
                                            date = date,
                                            folderName = folderName,
                                            format = format,
                                            location = location
                                        ),
                                    ),
                                    location = location,
                                )
                            )
                        }
                    } while (cursor.moveToNext())
                }
            }
            Log.d("VIDEOS", "${tempList.size}")
            folders = tempList
            _folderList.emit(MediaVideosUiState(folders = tempList))
        } catch (e: Exception) {
            _folderList.emit(MediaVideosUiState(error = e.localizedMessage))
            e.localizedMessage
        }
    }


    private fun isInternalStorage(path: String): Boolean {
        return path.startsWith(internalStoragePath)
    }

    private fun isExternalStorage(path: String): Boolean {
        val externalStoragePath = Environment.getExternalStorageDirectory().absolutePath
        return !path.startsWith(externalStoragePath)
    }

    suspend fun searchFolder(query: String) {
        if (query.isEmpty() || query.isBlank()) {
            _folderList.emit(MediaVideosUiState(folders = folders))
        } else {
            _folderList.emit(MediaVideosUiState(folders = folders.filter {
                it.name.contains(
                    query,
                    ignoreCase = true
                )
            }))
        }
    }
}

data class MediaVideosUiState(
    val isLoading: Boolean = false,
    val folders: List<Folders> = emptyList(),
    val error: String? = null
)