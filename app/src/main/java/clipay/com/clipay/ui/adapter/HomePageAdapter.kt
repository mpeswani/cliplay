package clipay.com.clipay.ui.adapter

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import clipay.com.clipay.R
import clipay.com.clipay.model.MultipleItem
import clipay.com.clipay.util.GradientFileParser
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import org.json.JSONException
import java.io.IOException
import java.util.*


class HomePageAdapter(list: List<MultipleItem>, context: Context, player: SimpleExoPlayer) : BaseMultiItemQuickAdapter<MultipleItem, BaseViewHolder>(list) {
    private val urls = arrayOf("https://cdn.pixabay.com/photo/2014/09/17/20/03/profile-449912_1280.jpg", "https://cdn.pixabay.com/photo/2015/01/07/15/51/woman-591576_1280.jpg", "https://cdn.pixabay.com/photo/2014/11/14/21/24/young-girl-531252_1280.jpg", "https://cdn.pixabay.com/photo/2017/04/03/10/42/woman-2197947_1280.jpg", "http://i.imgur.com/1ALnB2s.gif", "https://cdn.pixabay.com/photo/2015/03/17/14/05/sparkler-677774_1280.jpg", "https://cdn.pixabay.com/photo/2016/01/19/16/49/holding-hands-1149411_1280.jpg", "https://cdn.pixabay.com/photo/2018/01/25/14/12/nature-3106213_1280.jpg", "https://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_30mb.mp4")
    private var webGradients: List<Drawable>? = null
    private val random: Random
    private var count: Int = 0
    private val dataSourceFactory: DefaultDataSourceFactory
    private val player: SimpleExoPlayer

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

        random = Random()
        this.player = player
        val bandwidthMeterA = DefaultBandwidthMeter()
        dataSourceFactory = DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "clipay"), bandwidthMeterA)

    }

    override fun convert(viewHolder: BaseViewHolder, item: MultipleItem) {
        when (item.itemType) {
            MultipleItem.TEXT -> {
                val tv = viewHolder.getView<TextView>(R.id.inner_content)
                tv.background = webGradients!![viewHolder.getLayoutPosition()]
                tv.text = "Do not mind anything that anyone tells you about anyone else. Judge " + "everyone and everything for yourself."
            }
            MultipleItem.IMG -> {
                val url = urls[count % 7]
                if (count == 7) {
                    count = 0
                } else {
                    count++
                }
                Log.v("url", url)
                Glide.with(mContext).load(url)
                        .apply(RequestOptions.overrideOf(Resources.getSystem().displayMetrics
                                .widthPixels, mContext.resources.getDimensionPixelSize(R
                                .dimen._240sdp)))
                        .apply(RequestOptions.centerCropTransform()).transition(DrawableTransitionOptions.withCrossFade())
                        .into(viewHolder.getView<View>(R.id.inner_content) as PhotoView)
            }
            MultipleItem.VIDEO -> {
                val view = viewHolder.getView<SimpleExoPlayerView>(R.id.inner_content)
                val mp4VideoUri = Uri.parse(urls[urls.size - 1])
                val videoSource = ExtractorMediaSource(mp4VideoUri, dataSourceFactory, DefaultExtractorsFactory(), null, null)
                val loopingSource = LoopingMediaSource(videoSource)
                view.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL)
                view.player = player
                view.useController = true
                player.prepare(loopingSource)
                view.useController = false
                view.requestFocus()
                player.playWhenReady = true
            }

        }
        viewHolder.setText(R.id.time, random.nextInt(10).toString() + "hr")
                .setText(R.id.description, "Greyhound divisively hello coldly wonderfully " + "marginally far upon excluding.")
                .setText(R.id.fav_count, "99.0k")
                .setText(R.id.reply_count, "10k")
                .setText(R.id.share_count, "50k")
                .setText(R.id.user_name, "Kathryn Collins")
                .addOnClickListener(R.id.reply)
        Glide.with(mContext).load(URL).apply(RequestOptions.circleCropTransform())
                .into(viewHolder.getView<View>(R.id.image) as ImageView)
    }

    companion object {
        var URL = "https://cdn.pixabay" + ".com/photo/2016/04/10/21/34/woman-1320810_1280.jpg"
    }
}