package com.rkbapps.exoplayerdemo.module

import android.content.Context
import android.content.SharedPreferences
import com.rkbapps.exoplayerdemo.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedPerfModule {

    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences {
        val sharedPref =
            context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        return sharedPref
    }

}