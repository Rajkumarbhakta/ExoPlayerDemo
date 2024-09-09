package com.rkbapps.exoplayerdemo.util

import java.util.Locale

object Constants {
    const val PATH = "path"
    const val DESCRIPTION_LINES = 4
    const val PLAYER_CONTROLS_VISIBILITY = 5 * 1000L //5 seconds
    const val PLAYER_SEEK_BACK_INCREMENT = 5 * 1000L //5 seconds
    const val PLAYER_SEEK_FORWARD_INCREMENT = 10 * 1000L

    const val LAST_PLAYED_VIDEO = "last_played_video"

    fun convertTime(millisecond: String): String {
        return try {
            val mSecond = millisecond.toLong()
            val totalSeconds = mSecond / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60

            if (hours == 0L) {
                String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            } else {
                String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
            }
        } catch (e: Exception) {
            millisecond
        }
    }

    fun formatSize(byteString: String): String {
        return try {
            // Convert string to long
            val bytes = byteString.toLong()
            val kilobytes = bytes / 1024.0
            val megabytes = kilobytes / 1024.0
            val gigabytes = megabytes / 1024.0

            when {
                gigabytes >= 1 -> String.format(Locale.getDefault(), "%.2f GB", gigabytes)
                megabytes >= 1 -> String.format(Locale.getDefault(), "%.2f MB", megabytes)
                kilobytes >= 1 -> String.format(Locale.getDefault(), "%.2f KB", kilobytes)
                else -> String.format(Locale.getDefault(), "%d Bytes", bytes)
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            "$byteString Bytes"
        }
    }

}

