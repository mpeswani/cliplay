package com.hoanganhtuan95ptit.editphoto.contrast;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hoanganhtuan95ptit.editphoto.R;
import com.hoanganhtuan95ptit.library.TwoLineSeekBar;
import com.wang.avi.AVLoadingIndicatorView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Hoang Anh Tuan on 12/12/2017.
 */

public class ContrastFragment extends Fragment implements View.OnClickListener, TwoLineSeekBar.OnSeekChangeListener {

    private static final String INPUT_URL = "inputUrl";

    private ContrastView contrastView;
    private AVLoadingIndicatorView ivLoading;
    private TextView tvProcess;
    private LinearLayout llProcess;
    private TwoLineSeekBar seekBar;
    private String inputUrl;
    private boolean start = true;

    public static ContrastFragment create(String inputUrl, OnContrastListener onContrastListener) {
        ContrastFragment fragment = new ContrastFragment();
        fragment.setOnContrastListener(onContrastListener);
        Bundle bundle = new Bundle();
        bundle.putString(INPUT_URL, inputUrl);
        fragment.setArguments(bundle);
        return fragment;
    }

    @SuppressLint("ValidFragment")
    private ContrastFragment() {
    }

    private OnContrastListener onContrastListener;

    private void setOnContrastListener(OnContrastListener onContrastListener) {
        this.onContrastListener = onContrastListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contrast, container, false);
        mappingView(view);
        return view;
    }

    private void mappingView(View view) {
        contrastView = view.findViewById(R.id.contrastView);
        ivLoading = view.findViewById(R.id.ivLoading);
        tvProcess = view.findViewById(R.id.tvProcess);
        llProcess = view.findViewById(R.id.llProcess);
        seekBar = view.findViewById(R.id.seekBar);
        ImageView ivCancel = view.findViewById(R.id.ivCancel);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        ImageView ivCheck = view.findViewById(R.id.ivCheck);
        RelativeLayout rootBrightness = view.findViewById(R.id.rootBrightness);

        ivCancel.setOnClickListener(this);
        ivCheck.setOnClickListener(this);
        rootBrightness.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {

        seekBar.reset();
        seekBar.setSeekLength(-1000, 1000, 0, 1f);
        seekBar.setOnSeekChangeListener(this);
        seekBar.setValue(0);

        if (getArguments() != null) {
            inputUrl = getArguments().getString(INPUT_URL);
            showImage();
        }
    }

    private void showImage() {
        Observable.just(inputUrl)
                .map(url -> getBitmap(url))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoading();
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        contrastView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("eror", e.toString());
                    }

                    @Override
                    public void onComplete() {
                        hideLoading();
                    }
                });
    }

    private Bitmap getBitmap(String inputUrl) {
        Bitmap bitmap = Utils.getBitmapSdcard(inputUrl);
        bitmap = Utils.scaleDown(bitmap);
        return bitmap;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSeekChanged(float value, float step) {
        if (llProcess.getVisibility() != View.VISIBLE && !start)
            llProcess.setVisibility(View.VISIBLE);

        start = false;
        tvProcess.setText(Float.toString(value / 10f));
        contrastView.setContrast(value / 10f);
    }

    @Override
    public void onSeekStopped(float value, float step) {
        if (llProcess.getVisibility() != View.GONE) llProcess.setVisibility(View.GONE);
    }

    private void back() {
        getActivity().onBackPressed();
    }

    private void saveImage() {
        Observable.just(inputUrl)
                .map(url -> saveBitmap(url))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoading();
                    }

                    @Override
                    public void onNext(String url) {
                        if (onContrastListener != null)
                            onContrastListener.onContrastPhotoCompleted(url);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        back();
                    }
                });
    }

    private String saveBitmap(String url) {
        Bitmap bitmap = Utils.getBitmapSdcard(url);
        bitmap = Utils.contrastBitmap(bitmap, contrastView.getContrast());
        Utils.saveBitmap(url, bitmap);
        if (bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
        return url;
    }

    private void hideLoading() {
        if (ivLoading != null)
            ivLoading.smoothToHide();
    }

    private void showLoading() {
        if (ivLoading != null)
            ivLoading.smoothToShow();
    }

    @Override
    public void onClick(View view) {
        if (ivLoading.isShown()) return;
        if (view.getId() == R.id.ivCancel) {
            back();
        } else if (view.getId() == R.id.ivCheck) {
            saveImage();
        }
    }
}
