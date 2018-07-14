package com.hoanganhtuan95ptit.editphoto.fillter.library.filter.BlurFilter;

import android.content.Context;

import com.hoanganhtuan95ptit.editphoto.fillter.library.filter.FilterGroup;
import com.hoanganhtuan95ptit.editphoto.fillter.library.filter.CameraFilter;

public class ImageFilterGaussianBlur extends FilterGroup<CameraFilter> {

    public ImageFilterGaussianBlur(Context context, float blur) {
        super();
        addFilter(new ImageFilterGaussianSingleBlur(context, blur, false));
        addFilter(new ImageFilterGaussianSingleBlur(context, blur, true));
    }
}
