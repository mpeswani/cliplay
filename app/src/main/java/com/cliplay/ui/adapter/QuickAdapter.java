package com.cliplay.ui.adapter;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cliplay.R;

import java.util.List;
;

public class QuickAdapter extends BaseQuickAdapter<Bitmap, BaseViewHolder> {
    public QuickAdapter(List<Bitmap> bitmaps) {
        super(R.layout.filter_image, bitmaps);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, Bitmap item) {
        Glide.with(mContext).load(item).into((ImageView) viewHolder
                .getView(R.id.image));
    }

    @Override
    public Bitmap getItem(int position) {
        return mData.get(position);
    }
}