package com.vpaliy.loginconcept;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.mukesh.countrypicker.CountryPicker;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class LoginActivity extends AppCompatActivity {
    protected List<ImageView> sharedElements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        if (preferences.getBoolean("login", false)) {
            try {
                startActivity(new Intent(this, Class.forName("clipay.com.clipay.ui" +
                        ".activity.HomePage")));
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return;
        }
        setContentView(R.layout.activity_login);
        CountryPicker countryPicker =
                new CountryPicker.Builder().with(this)
                        .listener(country ->
                                Toast.makeText(LoginActivity.this, country.getDialCode(), Toast
                                        .LENGTH_SHORT)
                                        .show()).build();
        countryPicker.showDialog(getSupportFragmentManager());
        sharedElements = new ArrayList<>();
        ImageView logo = findViewById(R.id.logo);
        ImageView first = findViewById(R.id.first);
        ImageView second = findViewById(R.id.second);
        ImageView last = findViewById(R.id.last);
        sharedElements.add(logo);
        sharedElements.add(first);
        sharedElements.add(second);
        sharedElements.add(last);
        final AnimatedViewPager pager = findViewById(R.id.pager);
        final ImageView background = findViewById(R.id.scrolling_background);
        int[] screenSize = screenSize();
        for (ImageView element : sharedElements) {
            @ColorRes int color = element.getId() != R.id.logo ? R.color.white_transparent : R
                    .color.color_logo_log_in;
            DrawableCompat.setTint(element.getDrawable(), ContextCompat.getColor(this, color));
        }
        //load a very big image and resize it, so it fits our needs
        Glide.with(this).asBitmap()
                .load(R.drawable.table)
                .apply(new RequestOptions().override(screenSize[0] * 2, screenSize[1]))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap>
                            target, DataSource dataSource, boolean isFirstResource) {
                        background.setImageBitmap(resource);
                        background.post(() -> {
                            //we need to scroll to the very left edge of the image
                            //fire the scale animation
                            background.scrollTo(-background.getWidth() / 2, 0);
                            ObjectAnimator xAnimator = ObjectAnimator.ofFloat(background, View
                                    .SCALE_X, 4f, background.getScaleX());
                            ObjectAnimator yAnimator = ObjectAnimator.ofFloat(background, View
                                    .SCALE_Y, 4f, background.getScaleY());
                            AnimatorSet set = new AnimatorSet();
                            set.playTogether(xAnimator, yAnimator);
                            set.setDuration(getResources().getInteger(R.integer.duration));
                            set.start();
                        });
                        pager.post(() -> {
                            AuthAdapter adapter = new AuthAdapter(getSupportFragmentManager(),
                                    pager, background, sharedElements);
                            pager.setAdapter(adapter);
                        });
                        return true;
                    }
                })
                .into(new ImageViewTarget<Bitmap>(background) {
                    @Override
                    protected void setResource(Bitmap resource) {
                    }
                });
        findViewById(R.id.first).setOnClickListener(view -> {
            try {
                preferences.edit().putBoolean("login", true).apply();
                startActivity(new Intent(this, Class.forName("clipay.com.clipay.ui" +
                        ".activity.HomePage")));
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    private int[] screenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new int[]{size.x, size.y};
    }
}
