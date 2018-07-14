package com.hoanganhtuan95ptit.editphoto.fillter.library.filter.BlurFilter;

import android.content.Context;

import com.hoanganhtuan95ptit.editphoto.fillter.library.filter.FilterGroup;
import com.hoanganhtuan95ptit.editphoto.fillter.library.filter.CameraFilter;

public class CameraFilterGaussianBlur extends FilterGroup<CameraFilter> {

    public CameraFilterGaussianBlur(Context context, float blur) {
        super();
        addFilter(new CameraFilterGaussianSingleBlur(context, blur, false));
        addFilter(new CameraFilterGaussianSingleBlur(context, blur, true));
    }
}
