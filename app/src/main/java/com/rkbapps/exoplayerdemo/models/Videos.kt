package com.rkbapps.exoplayerdemo.models

import java.io.Serializable

@kotlinx.serialization.Serializable
data class Videos(
    val id: String,
    val title: String,
    val displayName: String,
    val size: String,
    val duration: String,
    val path: String,
    val date: String,
    val folderName: String,
    val format: String,
    val location: String,
) : Serializable

