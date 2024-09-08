package com.rkbapps.exoplayerdemo.models

import java.io.Serializable

data class Folders(
    val name: String,
    val location:StorageLocation,
    val files:MutableList<MediaVideos> = mutableListOf()
):Serializable


