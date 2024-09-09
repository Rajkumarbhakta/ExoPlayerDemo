package com.rkbapps.exoplayerdemo.util

import android.content.SharedPreferences
import javax.inject.Inject

class SharedPerfManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun writeString(key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun readString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

}