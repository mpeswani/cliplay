package com.cliplay.cache

import android.net.Uri
import java.util.concurrent.ConcurrentHashMap


/**
 * Created by Manohar Peswani on 20/07/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */

class VideoCacheManager {

    companion object {

        @Volatile
        private var INSTANCE: VideoCacheManager? = null

        fun getInstance(): VideoCacheManager =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: VideoCacheManager().also { INSTANCE = it }
                }

    }

    class VideoCache {
        var videoPosition: Int = 0
        var videoPlayPosition: Long = 0
        var isPlaying: Boolean = false
        var uri: Uri? = null
        var isStoppedExplicitly: Boolean = false
    }

    private val cacheVideo: ConcurrentHashMap<Int, VideoCache> = ConcurrentHashMap()

    fun put(position: Int, cache: VideoCache) {
        cacheVideo[position] = cache
    }

    fun get(position: Int): VideoCache? {
        return cacheVideo[position]
    }
}