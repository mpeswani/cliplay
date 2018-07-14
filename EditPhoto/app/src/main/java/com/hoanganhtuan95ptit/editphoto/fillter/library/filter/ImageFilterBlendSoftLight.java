package com.hoanganhtuan95ptit.editphoto.fillter.library.filter;

import android.content.Context;
import android.opengl.GLES10;

import com.hoanganhtuan95ptit.editphoto.R;
import com.hoanganhtuan95ptit.editphoto.fillter.library.gles.GlUtil;

import androidx.annotation.DrawableRes;

public class ImageFilterBlendSoftLight extends CameraFilterBlendSoftLight {

    public ImageFilterBlendSoftLight(Context context, @DrawableRes int drawableId) {
        super(context, drawableId);
    }

    @Override public int getTextureTarget() {
        return GLES10.GL_TEXTURE_2D;
    }

    @Override protected int createProgram(Context applicationContext) {
        return GlUtil.createProgram(applicationContext, R.raw.vertex_shader_2d_two_input,
                R.raw.fragment_shader_2d_blend_soft_light);
    }
}