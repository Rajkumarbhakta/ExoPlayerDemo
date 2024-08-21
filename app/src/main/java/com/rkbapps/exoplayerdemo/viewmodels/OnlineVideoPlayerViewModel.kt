package com.rkbapps.exoplayerdemo.viewmodels

import android.content.Context
import android.content.pm.ActivityInfo
import androidx.annotation.OptIn
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem.fromUri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.rkbapps.exoplayerdemo.util.Constants
import com.rkbapps.exoplayerdemo.util.findActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class OnlineVideoPlayerViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val player : ExoPlayer,
    savedStateHandle: SavedStateHandle,
) :ViewModel() {

    val AUDIO = "https://rr1---sn-i5uif5t-jj0l.googlevideo.com/videoplayback?expire=1723813138&ei=svi-ZpCSCp-W4t4PuIuNmQo&ip=203.163.250.63&id=o-ABOm0xhk6NKMADI3QemwHOVrkDlwwmxbIIW55Cq_h1VC&itag=139&source=youtube&requiressl=yes&xpc=EgVo2aDSNQ%3D%3D&mh=jP&mm=31%2C29&mn=sn-i5uif5t-jj0l%2Csn-cvh76nl7&ms=au%2Crdu&mv=m&mvi=1&pl=25&initcwndbps=810000&vprv=1&svpuc=1&mime=audio%2Fmp4&rqh=1&gir=yes&clen=1114056&dur=182.508&lmt=1721945181272293&mt=1723791200&fvip=3&keepalive=yes&c=IOS&txp=5532434&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cxpc%2Cvprv%2Csvpuc%2Cmime%2Crqh%2Cgir%2Cclen%2Cdur%2Clmt&sig=AJfQdSswRAIgJijwxJdDP1MpaqjZjm8YLl8ksCwnmpfd0S6eiL-TrlUCIC1bZWtmkSagpA7uxmD2ZYEc3DXuwO99h1XMh8XW0VI2&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AGtxev0wRQIhAOcQL74pXwOIbsOE3YFaSKj5Km6dnC2o4hJmEJF5BdjbAiAQkbG5YjpVFxIDo5YJRAhFfz6E0NeaaVfziBGxEmwUnw%3D%3D"
    val VIDEO = "https://rr1---sn-i5uif5t-jj0l.googlevideo.com/videoplayback?expire=1723813171&ei=0_i-ZvbMJ9yfjuMP09Ly-AQ&ip=203.163.250.63&id=o-ACtIfUf-S1CxIzsWkeZ0udmLOVRscZ1phBt1zdDTx1Bl&itag=135&source=youtube&requiressl=yes&xpc=EgVo2aDSNQ%3D%3D&mh=jP&mm=31%2C29&mn=sn-i5uif5t-jj0l%2Csn-cvh7knsz&ms=au%2Crdu&mv=m&mvi=1&pl=25&initcwndbps=810000&vprv=1&svpuc=1&mime=video%2Fmp4&rqh=1&gir=yes&clen=18366264&dur=182.374&lmt=1721947082549010&mt=1723791200&fvip=1&keepalive=yes&c=IOS&txp=5532434&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cxpc%2Cvprv%2Csvpuc%2Cmime%2Crqh%2Cgir%2Cclen%2Cdur%2Clmt&sig=AJfQdSswRAIgJ-hbmaYXiv6rmA54RszI7alzqDuGiKNVROrS4ImupGECIEn2oDGd4YkkZevHvps6FSW0vCIh9KNUQ7pXiVTIaVqR&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AGtxev0wRQIhAIHG_1LKJjdSAtHiowLXTQL40Uw9qS4nkDMNuftTsTiCAiAw6jFFaQTaEuF_6BDn8W6rHh27Ug-SCZB4Mb-5rHbsAQ%3D%3D"

    private var originalOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


    init {
        player.prepare()
        originalOrientation = context.resources.configuration.orientation
    }


    @OptIn(UnstableApi::class)
    fun onPlay(videoUri:String = VIDEO,audioUri:String = AUDIO){
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
        val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
            fromUri(videoUri)
        )
        val audioSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
            fromUri(audioUri)
        )
        val mergeSource: MediaSource = MergingMediaSource(audioSource,videoSource)
        player.setMediaSource(mergeSource)
        player.playWhenReady = true
    }


    override fun onCleared() {
        super.onCleared()
        player.release()
        context.findActivity()?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }


}