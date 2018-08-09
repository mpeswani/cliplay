package com.cliplay.ui.adapter

import android.os.Build
import android.text.Html
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cliplay.R
import com.cliplay.model.Comment


/**
 * Created by Manohar Peswani on 15/07/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */
class CommentsAdapter(dataSize: List<Comment>) :
        BaseQuickAdapter<Comment, BaseViewHolder>(R.layout.comments_adapter, dataSize) {

    override fun getItem(position: Int): Comment? {
        return mData[position]
    }

    override fun convert(helper: BaseViewHolder, item: Comment) {
        Glide.with(mContext).load(item.userImage).apply(RequestOptions.circleCropTransform())
                .into(helper.getView<View>(R.id.imageView3) as ImageView)
        val comment = "<font color='#ED6565'>" + item.userName + "</font> <font color='black'>" + item.comment + "</font>"
        val spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(comment, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(comment)
        }
        helper.setText(R.id.time, "9h")
                .setText(R.id.likes, "99.1k")
                .setText(R.id.comment_text, spanned)
    }
}
