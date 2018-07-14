package com.hoanganhtuan95ptit.editphoto.fillter.library.filter;

import android.content.Context;

import com.hoanganhtuan95ptit.editphoto.R;
import com.hoanganhtuan95ptit.editphoto.fillter.library.gles.GlUtil;

import androidx.annotation.DrawableRes;

public class CameraFilterBlendSoftLight extends CameraFilterBlend {

    public CameraFilterBlendSoftLight(Context context, @DrawableRes int drawableId) {
        super(context, drawableId);
    }

    @Override protected int createProgram(Context applicationContext) {

        return GlUtil.createProgram(applicationContext, R.raw.vertex_shader_two_input,
                R.raw.fragment_shader_ext_blend_soft_light);
    }
}