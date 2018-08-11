/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cliplay

import androidx.multidex.MultiDexApplication
import com.cliplay.exo.DownloadTracker
import com.cliplay.networking.CliplayNetworking
import com.crashlytics.android.Crashlytics
import com.google.android.exoplayer2.BuildConfig
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper
import com.google.android.exoplayer2.offline.ProgressiveDownloadAction
import com.google.android.exoplayer2.source.dash.offline.DashDownloadAction
import com.google.android.exoplayer2.source.hls.offline.HlsDownloadAction
import com.google.android.exoplayer2.source.smoothstreaming.offline.SsDownloadAction
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Util
import io.fabric.sdk.android.Fabric
import java.io.File

/**
 * Placeholder application to facilitate overriding Application methods for debugging and testing.
 */
class Cliplay : MultiDexApplication() {
    private lateinit var userAgent: String
    private var downloadDirectory: File? = null
    private var downloadCache: Cache? = null
    private var downloadManager: DownloadManager? = null
    private var downloadTracker: DownloadTracker? = null

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        CliplayNetworking.init()
        userAgent = Util.getUserAgent(this, "ExoPlayerDemo")
    }

    /**
     * Returns a [DataSource.Factory].
     */
    fun buildDataSourceFactory(listener: TransferListener<in DataSource>?): DataSource.Factory {
        val upstreamFactory = DefaultDataSourceFactory(this, listener, buildHttpDataSourceFactory(listener))
        return buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache())
    }

    /**
     * Returns a [HttpDataSource.Factory].
     */
    fun buildHttpDataSourceFactory(
            listener: TransferListener<in DataSource>?): HttpDataSource.Factory {
        return DefaultHttpDataSourceFactory(userAgent, listener)
    }

    /**
     * Returns whether extension renderers should be used.
     */
    fun useExtensionRenderers(): Boolean {
        return "withExtensions" == BuildConfig.FLAVOR
    }

    fun getDownloadManager(): DownloadManager? {
        initDownloadManager()
        return downloadManager
    }

    fun getDownloadTracker(): DownloadTracker? {
        initDownloadManager()
        return downloadTracker
    }

    @Synchronized
    private fun initDownloadManager() {
        if (downloadManager ==
                /* eventListener= */ null) {
            val downloaderConstructorHelper = DownloaderConstructorHelper(
                    getDownloadCache(), buildHttpDataSourceFactory(/* listener= */null))
            downloadManager = DownloadManager(
                    downloaderConstructorHelper,
                    MAX_SIMULTANEOUS_DOWNLOADS,
                    DownloadManager.DEFAULT_MIN_RETRY_COUNT,
                    File(getDownloadDirectory(), DOWNLOAD_ACTION_FILE),
                    *DOWNLOAD_DESERIALIZERS)
            downloadTracker = DownloadTracker(
                    /* context= */ this,
                    buildDataSourceFactory(/* listener= */null),
                    File(getDownloadDirectory(), DOWNLOAD_TRACKER_ACTION_FILE),
                    DOWNLOAD_DESERIALIZERS)
            downloadManager!!.addListener(downloadTracker)
        }
    }

    @Synchronized
    private fun getDownloadCache(): Cache {
        if (downloadCache == null) {
            val downloadContentDirectory = File(getDownloadDirectory(),
                    DOWNLOAD_CONTENT_DIRECTORY)
            downloadCache = SimpleCache(downloadContentDirectory, NoOpCacheEvictor())
        }
        return downloadCache!!
    }

    private fun getDownloadDirectory(): File {
        if (downloadDirectory == null) {
            downloadDirectory = getExternalFilesDir(null)
            if (downloadDirectory == null) {
                downloadDirectory = filesDir
            }
        }
        return downloadDirectory!!
    }

    companion object {
        private const val DOWNLOAD_ACTION_FILE = "actions"
        private const val DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions"
        private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"
        private const val MAX_SIMULTANEOUS_DOWNLOADS = 2
        private val DOWNLOAD_DESERIALIZERS = arrayOf(DashDownloadAction.DESERIALIZER, HlsDownloadAction.DESERIALIZER, SsDownloadAction.DESERIALIZER, ProgressiveDownloadAction.DESERIALIZER)

        private fun buildReadOnlyCacheDataSource(
                upstreamFactory: DefaultDataSourceFactory, cache: Cache): CacheDataSourceFactory {
            return CacheDataSourceFactory(
                    cache,
                    upstreamFactory,
                    FileDataSourceFactory(),
                    CacheDataSinkFactory(cache, (2048 * 1000 * 100).toLong()),
                    CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null)
        }
    }
}
