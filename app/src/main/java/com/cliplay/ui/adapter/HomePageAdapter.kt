package com.cliplay.ui.adapter

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cliplay.R
import com.cliplay.cache.VideoCacheManager
import com.cliplay.model.MultipleItem
import com.cliplay.util.DoubleTapDetector
import com.cliplay.util.DoubleTapImageView
import com.cliplay.util.GradientFileParser
import com.google.android.exoplayer2.ui.PlayerView
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import java.io.IOException
import java.util.*


class HomePageAdapter(list: List<MultipleItem>, context: Context) : BaseMultiItemQuickAdapter<MultipleItem, BaseViewHolder>(list) {
    private var webGradients: List<Drawable>? = null
    private val random: Random
    private var count: Int = 0
    private val cacheManager: VideoCacheManager = VideoCacheManager.getInstance()
    private val requestOptions: RequestOptions
    private var detector: DoubleTapDetector? = null
    private val circularProgressDrawable: CircularProgressDrawable
    fun setOnDoubleTabListener(detector: DoubleTapDetector) {
        this.detector = detector
    }

    init {
        addItemType(MultipleItem.TEXT, R.layout.main_page_cardview)
        addItemType(MultipleItem.IMG, R.layout.main_page_cardview_image)
        addItemType(MultipleItem.VIDEO, R.layout.main_page_cardview_video)
        try {
            webGradients = GradientFileParser.getGradientDrawables(context)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        requestOptions = RequestOptions()
        random = Random()
        circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
    }

    override fun convert(viewHolder: BaseViewHolder, item: MultipleItem) {
        when (item.itemType) {
            MultipleItem.TEXT -> {
                val tv = viewHolder.getView<TextView>(R.id.inner_content)
                tv.background = webGradients!![viewHolder.layoutPosition]
                tv.text = "Do not mind anything that anyone tells you about anyone else. Judge " + "everyone and everything for yourself."
            }
            MultipleItem.IMG -> {
                val url = item.content.id
                if (count == 7) {
                    count = 0
                } else {
                    count++
                }
                Log.v("url", url)
                val photo = viewHolder.getView<View>(R.id.inner_content) as DoubleTapImageView
                Glide.with(mContext).load(url)
                        .apply(RequestOptions.overrideOf(Resources.getSystem().displayMetrics
                                .widthPixels, mContext.resources.getDimensionPixelSize(R
                                .dimen._240sdp)))
                        .apply(RequestOptions.centerCropTransform().placeholder(circularProgressDrawable))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(photo)
                photo.setGestureDetector {
                    if (this.detector != null) {
                        this.detector!!.onDoubleTap(viewHolder.layoutPosition)
                    }
                }

            }
            MultipleItem.VIDEO -> {
                val view = viewHolder.getView<PlayerView>(R.id.inner_content)
                val bar = viewHolder.getView<ProgressBar>(R.id.video_progress)
                requestOptions.placeholder(R.drawable.no_video_img).error(R.drawable.no_video_img)
                Glide.with(mContext)
                        .load(item.content.id)
                        .apply(requestOptions)
                        .thumbnail(0.1f)
                        .into(viewHolder.getView(R.id.inner_content_image) as ImageView)
                view.requestFocus()
                Single.fromCallable {
                    val mp4VideoUri = Uri.parse(item.content.id)
                    val videoCache = cacheManager.get(viewHolder.layoutPosition)
                    if (videoCache == null) {
                        val videoCache2 = VideoCacheManager.VideoCache()
                        videoCache2.isPlaying = false
                        videoCache2.videoPlayPosition = 0
                        videoCache2.videoPosition = viewHolder.layoutPosition
                        videoCache2.uri = mp4VideoUri
                        cacheManager.put(position = viewHolder.layoutPosition, cache = videoCache2)
                    }
                }.subscribeOn(Schedulers.io())
                        .subscribe()
                bar.indeterminateDrawable.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        }
        viewHolder.setText(R.id.time, random.nextInt(10).toString() + "hr")
                .setText(R.id.description, "Greyhound divisively hello coldly wonderfully " + "marginally far upon excluding.")
                .setText(R.id.fav_count, "99.0k")
                .setText(R.id.reply_count, "10k")
                .setText(R.id.share_count, "50k")
                .setText(R.id.user_name, "Kathryn Collins")
                .addOnClickListener(R.id.reply)
                .addOnClickListener(R.id.more)
                .addOnClickListener(R.id.favourite)
                .addOnClickListener(R.id.share)
                .addOnClickListener(R.id.image)
                .addOnClickListener(R.id.user_name)
        Glide.with(mContext).load(URL).apply(RequestOptions.circleCropTransform())
                .into(viewHolder.getView<View>(R.id.image) as ImageView)
    }

    companion object {
        var URL = "https://cdn.pixabay" + ".com/photo/2016/04/10/21/34/woman-1320810_1280.jpg"
    }
}