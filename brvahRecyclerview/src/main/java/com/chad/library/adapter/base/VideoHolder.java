package com.chad.library.adapter.base;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by tuanha00 on 1/23/2018.
 */

public abstract class VideoHolder extends RecyclerView.ViewHolder {

    public VideoHolder(View itemView) {
        super(itemView);
    }

    public abstract View getVideoLayout();

    public abstract void playVideo();

    public abstract void stopVideo();

}