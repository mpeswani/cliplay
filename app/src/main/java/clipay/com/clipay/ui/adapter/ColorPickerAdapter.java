package clipay.com.clipay.ui.adapter;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import clipay.com.clipay.R;

/**
 * Created by Manohar Peswani on 04/07/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */
public class ColorPickerAdapter extends BaseQuickAdapter<Drawable, BaseViewHolder> {
    public ColorPickerAdapter(List<Drawable> dataSize) {
        super(R.layout.colors, dataSize);
    }

    @Override
    public Drawable getItem(int position) {
        return mData.get(position);
    }

    @Override
    protected void convert(BaseViewHolder helper, Drawable item) {
        Glide.with(mContext).load(item).apply(RequestOptions.circleCropTransform()).into
                ((ImageView) helper.getView(R.id.circle_colors));
//        helper.getView(R.id.circle_colors).setBackground(item);
    }
}
