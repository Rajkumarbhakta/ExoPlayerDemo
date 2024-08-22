package com.rkbapps.exoplayerdemo.module

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cronet.CronetDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.net.cronet.okhttptransport.CronetCallFactory
import com.rkbapps.exoplayerdemo.util.Constants.PLAYER_SEEK_BACK_INCREMENT
import com.rkbapps.exoplayerdemo.util.Constants.PLAYER_SEEK_FORWARD_INCREMENT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import org.chromium.net.CronetEngine
import java.util.concurrent.Executors

@Module
@InstallIn(ViewModelComponent::class)
object Module {

    @OptIn(UnstableApi::class)
    @Provides
    @ViewModelScoped
    fun providePlayer(@ApplicationContext context: Context): ExoPlayer {

        val cronetEngine: CronetEngine = CronetEngine.Builder(context.applicationContext)
            .enableHttp2(true)
            .enableQuic(true)
            .enableBrotli(true)
            .enableHttpCache(CronetEngine.Builder.HTTP_CACHE_IN_MEMORY, 1024L * 1024L) // 1MiB
            .build()
        val cronetDataSourceFactory = CronetDataSource.Factory(cronetEngine, Executors.newCachedThreadPool())
        val dataSourceFactory = DefaultDataSource.
                Factory(context, cronetDataSourceFactory)


        return ExoPlayer
            .Builder(context.applicationContext)
            .setSeekBackIncrementMs(PLAYER_SEEK_BACK_INCREMENT)
            .setSeekForwardIncrementMs(PLAYER_SEEK_FORWARD_INCREMENT)
            .setUsePlatformDiagnostics(false)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .setHandleAudioBecomingNoisy(true)
            .setLoadControl(getLoadControl())
            .build()
    }

    @OptIn(UnstableApi::class)
    fun getLoadControl(): LoadControl {
        return DefaultLoadControl.Builder()
            // cache the last three minutes
            .setBackBuffer(1000 * 60 * 3, true)
            .setBufferDurationsMs(32 * 1024 , 64 * 1024,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
            )
            .build()

    }

    @Provides
    @ViewModelScoped
    fun provideGson(): Gson{
        return GsonBuilder().create()
    }




}

