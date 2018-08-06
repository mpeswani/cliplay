package clipay.com.clipay.ui.fragment.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import clipay.com.clipay.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView


/**
 * Created by Manohar Peswani on 05/08/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */

class ImageAdapter internal constructor(internal var context: Context) : PagerAdapter() {

    private val images = arrayOf("https://images.unsplash.com/photo-1516726817505-f5ed825624d8?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=e04ffeb2255b63153da60f5916295753&auto=format&fit=crop&w=934&q=80",
            "https://images.unsplash.com/photo-1456885284447-7dd4bb8720bf?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=d6bdc66b5572ed855aa631c7002de5a3&auto=format&fit=crop&w=934&q=80",
            "https://images.unsplash.com/photo-1514315384763-ba401779410f?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=eb9a2cf082cb3b043ed447ed40a2a0d1&auto=format&fit=crop&w=930&q=80",
            "https://images.unsplash.com/photo-1516522973472-f009f23bba59?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=99763e974065d33dade207c2dcf24d3f&auto=format&fit=crop&w=934&q=80",
            "https://images.unsplash.com/photo-1504703395950-b89145a5425b?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=d702cb99ca804bffcfa8820c46483264&auto=format&fit=crop&w=951&q=80",
            "https://images.unsplash.com/photo-1513619371302-207c0b203098?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=95f2ea48be8e574a5e1dbe14b7271de1&auto=format&fit=crop&w=975&q=80",
            "https://images.unsplash.com/photo-1495366691023-cc4eadcc2d7e?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=fe4a823b6c32e6f9002f005d174a106b&auto=format&fit=crop&w=934&q=80",
            "https://images.unsplash.com/photo-1508529031-bae5f83226a8?ixlib=rb-0.3.5&s=4e26c450e68001cdcf059437801b5575&auto=format&fit=crop&w=934&q=80",
            "https://images.unsplash.com/photo-1482555670981-4de159d8553b?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=fd9c7783d5a84ff9bbc48abf5d2bc7ce&auto=format&fit=crop&w=934&q=80",
            "https://images.unsplash.com/photo-1512220188689-9eeca2ef886d?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=f6cdbfc118dc813121fe7b445f89e68f&auto=format&fit=crop&w=988&q=80",
            "https://images.unsplash.com/photo-1512719593792-766dc402338f?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=4f9ac42676332de2d62ff4b5d297e4b9&auto=format&fit=crop&w=934&q=80")


    private var mLayoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj as PhotoView
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = mLayoutInflater.inflate(R.layout.imageview_viewpager, container, false)
        val imageView = itemView.findViewById(R.id.profileImage2) as ImageView
        Glide.with(context).load(images[position]).apply(RequestOptions.centerCropTransform()).into(imageView)
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as PhotoView)
    }
}