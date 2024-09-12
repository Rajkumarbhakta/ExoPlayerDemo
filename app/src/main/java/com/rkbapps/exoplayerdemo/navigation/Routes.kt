package com.rkbapps.exoplayerdemo.navigation

import kotlinx.serialization.Serializable

@Serializable
object Splash

@Serializable
object Home

@Serializable
data class VideoUrl(val url: String)

@Serializable
data class OfflinePlayer(val video: String, val videoList: String)

@Serializable
data class VideoListing(val name: String, val videos: String)

