package com.rkbapps.exoplayerdemo.models

import com.rkbapps.exoplayerdemo.util.Constants
import java.io.Serializable

@kotlinx.serialization.Serializable
data class Folders(
    val id: String = Constants.generateUniqueKey(),
    val name: String,
    val location: String,
    val files: MutableList<MediaVideos> = mutableListOf()
) : Serializable


