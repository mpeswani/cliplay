package com.cliplay.ui.adapter

import android.view.View
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cliplay.R


/**
 * Created by Manohar Peswani on 15/07/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */

class TextEditorFunAdapter(dataSize: List<Int>) :
        BaseQuickAdapter<Int, BaseViewHolder>(R.layout.editors_option_item, dataSize) {

    override fun convert(helper: BaseViewHolder, item: Int) {
        Glide.with(mContext).load(item)
                .apply(RequestOptions.circleCropTransform())
                .into(helper.getView<View>(R.id.circle_editor) as ImageView)
    }
}
