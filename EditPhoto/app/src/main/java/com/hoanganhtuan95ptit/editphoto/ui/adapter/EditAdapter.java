package com.hoanganhtuan95ptit.editphoto.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hoanganhtuan95ptit.editphoto.R;
import com.hoanganhtuan95ptit.editphoto.model.EditType;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Hoang Anh Tuan on 11/29/2017.
 */

public class EditAdapter extends BaseAdapter<EditType> {

    private OnItemEditPhotoClickedListener onItemEditPhotoClickedListener;

    public EditAdapter(Activity activity) {
        super(activity);
    }

    public void setOnItemEditPhotoClickedListener(OnItemEditPhotoClickedListener onItemEditPhotoClickedListener) {
        this.onItemEditPhotoClickedListener = onItemEditPhotoClickedListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_edit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        final EditType type = list.get(position);

        Glide.with(activity)
                .load(type.VALUE)
                .into(viewHolder.iv);
        viewHolder.tv.setText(type.name());

        viewHolder.item.setOnClickListener(view -> onItemEditPhotoClickedListener.onItemEditPhotoClicked(type));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tv;
        LinearLayout item;

        ViewHolder(View view) {
            super(view);
            iv = view.findViewById(R.id.iv);
            tv = view.findViewById(R.id.tv);
            item = view.findViewById(R.id.item);
        }
    }

    public interface OnItemEditPhotoClickedListener{
        void onItemEditPhotoClicked(EditType type);
    }
}
