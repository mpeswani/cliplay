package com.cliplay.views

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.VideoHolder
import com.cliplay.Clipay
import com.cliplay.R
import com.cliplay.cache.VideoCacheManager
import com.cliplay.model.MultipleItem
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.offline.FilteringManifestParser
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser
import com.google.android.exoplayer2.source.dash.manifest.RepresentationKey
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser
import com.google.android.exoplayer2.source.hls.playlist.RenditionKey
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser
import com.google.android.exoplayer2.source.smoothstreaming.manifest.StreamKey
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class AutoPlayVideoRecyclerView : RecyclerView {

    private var subject: PublishSubject<Int>? = null
    var handingVideoHolder: BaseViewHolder? = null
    private var handingPosition = 0
    private var newPosition = -1
    private var heightScreen: Int = 0
    private val cacheManager: VideoCacheManager = VideoCacheManager.getInstance()
    private var dataSourceFactory: DefaultDataSourceFactory
    private var mExoPlayer: SimpleExoPlayer
    private val mediaDataSourceFactory: DataSource.Factory
    private val BANDWIDTH_METER = DefaultBandwidthMeter()

    private var listener: Player.EventListener = object : Player.EventListener {
        override fun onSeekProcessed() {
        }

        override fun onPositionDiscontinuity(reason: Int) {
            Log.v(TAG, "Listener-onPositionDiscontinuity...$reason")
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
            Log.v(TAG, "Listener-onTracksChanged...")
        }

        override fun onLoadingChanged(isLoading: Boolean) {
            Log.v(TAG, "Listener-onLoadingChanged...isLoading:$isLoading")
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            if (handingVideoHolder == null) return
            val bar = handingVideoHolder!!.getView<ProgressBar>(R.id.video_progress)
            val imageView = handingVideoHolder!!.getView<AppCompatImageView>(R.id.inner_content_image)
            val imageButton = handingVideoHolder!!.getView<ImageView>(R.id.exo_play)
            imageButton.setOnClickListener {
                playOrStop()
            }
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                    bar.visibility = View.VISIBLE
                    if (getPlayer().currentPosition > 100) {
                        imageView.visibility = View.GONE
                    } else {
                        imageView.visibility = View.VISIBLE
                    }
                }
                Player.STATE_READY -> {
                    bar.visibility = View.GONE
                    imageView.visibility = View.GONE
                }
                Player.STATE_IDLE -> {
                    bar.visibility = View.GONE
                    imageView.visibility = View.GONE
                }
            }
            Log.v(TAG, "Listener-onPlayerStateChanged...$playbackState")
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            Log.v(TAG, "Listener-onRepeatModeChanged...")
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            error.printStackTrace()
            Log.v(TAG, "Listener-onPlayerError...")
            getPlayer().stop()
//            mExoPlayer.prepare(loopingSource);
//            mExoPlayer.setPlayWhenReady(true);
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            Log.v(TAG, "Listener-onPlaybackParametersChanged...")
        }
    }

    fun playOrStop() {
        if (getPlayer().playWhenReady && getPlayer().playbackState == Player.STATE_READY) {
            if (stop()) {
                val view = handingVideoHolder!!.getView(R.id.exo_play_1) as ImageView
                cacheManager.get(handingVideoHolder!!.layoutPosition)!!.isStoppedExplicitly = true
                view.visibility = View.VISIBLE
            }
        } else {
            if (handingVideoHolder != null) {
                cacheManager.get(handingVideoHolder!!.layoutPosition)!!.isStoppedExplicitly = false
            }
            if (play()) {
                val view = handingVideoHolder!!.getView(R.id.exo_play_1) as ImageView
                view.visibility = View.GONE
            }
        }
    }

    private fun getPlayer(): ExoPlayer {
        return mExoPlayer
    }

    private val viewHolderCenterScreen: BaseViewHolder?
        get() {
            val limitPosition = limitPositionInScreen
            val min = limitPosition[0]
            val max = limitPosition[1]
            var viewHolderMax: BaseViewHolder? = null
            var percentMax = 0f
            for (i in min..max) {
                val viewHolder = findViewHolderForAdapterPosition(i) as? BaseViewHolder ?: continue
                val percentViewHolder = getPercentViewHolderInScreen(viewHolder as VideoHolder)
                if (percentViewHolder > percentMax && percentViewHolder >= 50) {
                    percentMax = percentViewHolder
                    viewHolderMax = viewHolder
                    newPosition = i
                }
            }
            return viewHolderMax
        }

    private val limitPositionInScreen: IntArray
        get() {
            val findFirstVisibleItemPosition = (layoutManager as LinearLayoutManager)
                    .findFirstVisibleItemPosition()
            val findFirstCompletelyVisibleItemPosition = (layoutManager as LinearLayoutManager)
                    .findFirstCompletelyVisibleItemPosition()
            val findLastVisibleItemPosition = (layoutManager as LinearLayoutManager)
                    .findLastVisibleItemPosition()
            val findLastCompletelyVisibleItemPosition = (layoutManager as LinearLayoutManager)
                    .findLastCompletelyVisibleItemPosition()
            val min = Math.min(Math.min(findFirstVisibleItemPosition,
                    findFirstCompletelyVisibleItemPosition),
                    Math.min(findLastVisibleItemPosition, findLastCompletelyVisibleItemPosition))
            val max = Math.max(Math.max(findFirstVisibleItemPosition,
                    findFirstCompletelyVisibleItemPosition),
                    Math.max(findLastVisibleItemPosition, findLastCompletelyVisibleItemPosition))
            return intArrayOf(min, max)
        }

    fun playWhenReady(boolean: Boolean) {
        if (handingVideoHolder == null) return
        if (boolean) {
            play()
        } else {
            stop()
        }
    }

    fun stopPlayer() {
        mExoPlayer.stop()
    }

    fun releasePlayer() {
        mExoPlayer.release()
        disposable?.dispose()
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    private var disposable: Disposable?

    init {
        heightScreen = getHeightScreen()
        subject = createSubject()
        disposable = subject!!.debounce(300, TimeUnit.MILLISECONDS)
                .filter { true }
                .switchMap { Observable.just(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { this.playVideo(it) }
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                checkPositionHandingViewHolder()
                subject!!.onNext(dy)
            }
        })
        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
        mExoPlayer.addListener(listener)
        val bandwidthMeterA = DefaultBandwidthMeter()
        mediaDataSourceFactory = buildDataSourceFactory(true)
        dataSourceFactory = DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "clipay"), bandwidthMeterA)
        mExoPlayer.videoScalingMode.times(1)
    }

    private fun buildDataSourceFactory(useBandwidthMeter: Boolean): DataSource.Factory {
        return (context.applicationContext as Clipay)
                .buildDataSourceFactory(if (useBandwidthMeter) BANDWIDTH_METER else null)
    }

    private fun checkPositionHandingViewHolder() {
        if (handingVideoHolder == null) return
        Observable.just(handingVideoHolder!!)
                .map { this.getPercentViewHolderInScreen(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Float> {
                    override fun onSubscribe(@NonNull d: Disposable) {}

                    override fun onNext(@NonNull aFloat: Float) {
                        if (aFloat < 50 && handingVideoHolder != null) {
                            handingVideoHolder!!.stopVideo()
                            stop()
                            Log.e("play", "===>+ video stop onNext" + handingVideoHolder!!
                                    .layoutPosition)
                            handingVideoHolder = null
                            handingPosition = -1
                        }
                    }

                    override fun onError(@NonNull e: Throwable) {}

                    override fun onComplete() {}
                })
    }

    private fun getHeightScreen(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

    private fun createSubject(): PublishSubject<Int> {
        return PublishSubject.create()
    }

    private fun playVideo(value: Int) {
        Observable.just(value)
                .map<BaseViewHolder> { aFloat ->
                    val videoHolder = viewHolderCenterScreen ?: return@map null

                    if (videoHolder.itemViewType == MultipleItem.TEXT || videoHolder
                                    .itemViewType == MultipleItem.IMG) {
                        return@map null
                    }
                    if (videoHolder == handingVideoHolder && handingPosition == newPosition)
                        return@map null
                    handingPosition = newPosition
                    return@map videoHolder
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<BaseViewHolder> {
                    override fun onSubscribe(@NonNull d: Disposable) {}

                    override fun onNext(@NonNull videoHolder: BaseViewHolder) {
                        if (handingVideoHolder != null) {
                            handingVideoHolder!!.stopVideo()
                            stop()
                            Log.e("play", "===>+ video stop" + handingVideoHolder!!
                                    .layoutPosition)
                        }
                        videoHolder.playVideo()
                        handingVideoHolder = videoHolder
                        Log.e("play", "===>+ video play" + handingVideoHolder!!.layoutPosition)
                        play()
                    }

                    override fun onError(@NonNull e: Throwable) {}

                    override fun onComplete() {}
                })
    }

    fun play(): Boolean {
//        synchronized(mExoPlayer) {
        if (handingVideoHolder == null) return false
        val position = handingVideoHolder!!.layoutPosition
        val cache = cacheManager.get(position) ?: return false
        if (cache.isStoppedExplicitly) return false
        val player = (handingVideoHolder!!.getView(R.id.inner_content)) as PlayerView
        Log.v("player", "====>" + player.id + "===>" + player.tag)
        player.player = mExoPlayer
        (player.player as SimpleExoPlayer).addVideoListener(object : VideoListener {
            override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int,
                                            pixelWidthHeightRatio: Float) {
                if (handingVideoHolder == null) return
                val params = player.layoutParams
                val deviceWidth = Resources.getSystem().displayMetrics.widthPixels
                params.height = ((height / width.toFloat()) * deviceWidth).toInt()
                params.width = deviceWidth

//                handingVideoHolder!!.getView<ProgressBar>(R.id.video_progress).layoutParams = layoutParams
//                handingVideoHolder!!.getView<ProgressBar>(R.id.video_progress).layoutParams = layoutParams
            }

            override fun onRenderedFirstFrame() {
            }
        })
        player.setShutterBackgroundColor(Color.TRANSPARENT)
        val mediaSource = buildMediaSource(cache.uri!!, "")
        (player.player as SimpleExoPlayer).prepare(mediaSource, false, false)
        player.player.repeatMode = Player.REPEAT_MODE_ONE
        player.player.playWhenReady = true
        if (cache.videoPlayPosition > 10) {
            player.player.seekTo(cache.videoPlayPosition)
        }
        return true
//        }
    }

    fun stop(): Boolean {
        if (handingVideoHolder == null) return false
        val position = handingVideoHolder!!.layoutPosition
        val player = (handingVideoHolder!!.getView(R.id.inner_content)) as PlayerView
        if (player.player == null) return false
        val cache = cacheManager.get(position) ?: return false
        player.player.playWhenReady = false
        cache.videoPlayPosition = player.player.currentPosition
        player.player = null
//        val imageView = handingVideoHolder!!.getView<AppCompatImageView>(R.id.inner_content_image)
//        imageView.visibility = View.VISIBLE

//          val myPlayer = player.player as SimpleExoPlayer
        return true
//        }
    }

    private fun getOfflineStreamKeys(uri: Uri): List<*> {
        return (context.applicationContext as Clipay).downloadTracker.getOfflineStreamKeys<Any>(uri)
    }

    private fun getPercentViewHolderInScreen(viewHolder: VideoHolder?): Float {
        if (viewHolder == null) return 0f
        val view = viewHolder.videoLayout
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val viewHeight = view.height
        val viewFromY = location[1]
        val viewToY = location[1] + viewHeight
        if (viewFromY >= 0 && viewToY <= heightScreen) return 100f
        if (viewFromY < 0 && viewToY > heightScreen) return 100f
        return if (viewFromY < 0) (viewToY - -viewFromY).toFloat() / viewHeight * 100 else (heightScreen - viewFromY).toFloat() / viewHeight * 100
    }

    private fun buildMediaSource(uri: Uri, @Nullable overrideExtension: String): MediaSource {
        @C.ContentType val type = Util.inferContentType(uri, overrideExtension)
        when (type) {
            C.TYPE_DASH -> return DashMediaSource.Factory(
                    DefaultDashChunkSource.Factory(mediaDataSourceFactory),
                    buildDataSourceFactory(false))
                    .setManifestParser(
                            FilteringManifestParser(
                                    DashManifestParser(), getOfflineStreamKeys(uri) as List<RepresentationKey>))
                    .createMediaSource(uri)
            C.TYPE_SS -> return SsMediaSource.Factory(
                    DefaultSsChunkSource.Factory(mediaDataSourceFactory),
                    buildDataSourceFactory(false))
                    .setManifestParser(
                            FilteringManifestParser(
                                    SsManifestParser(), getOfflineStreamKeys(uri) as (List<StreamKey>)))
                    .createMediaSource(uri)
            C.TYPE_HLS -> return HlsMediaSource.Factory(mediaDataSourceFactory)
                    .setPlaylistParser(
                            FilteringManifestParser(
                                    HlsPlaylistParser(), getOfflineStreamKeys(uri) as List<RenditionKey>))
                    .createMediaSource(uri)
            C.TYPE_OTHER -> return ExtractorMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri)
            else -> {
                throw IllegalStateException("Unsupported type: $type")
            }
        }
    }

    companion object {
        private val TAG = "AutoPlayVideoRecyclerVi"
    }
}