package com.hoanganhtuan95ptit.editphoto.rotate;

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
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Hoang Anh Tuan on 11/22/2017.
 */

public class RotateFragment extends Fragment implements TwoLineSeekBar.OnSeekChangeListener, View.OnClickListener {

    private static final String INPUT_URL = "inputUrl";

    private RotateView rotateView;
    private AVLoadingIndicatorView ivLoading;
    private TextView tvProcess;
    private LinearLayout llProcess;
    private TwoLineSeekBar seekBar;
    private String inputUrl;
    private boolean start = true;

    public static RotateFragment create(String inputUrl, OnRotateListener onRotateListener) {
        RotateFragment fragment = new RotateFragment();
        fragment.setOnRotateListener(onRotateListener);
        Bundle bundle = new Bundle();
        bundle.putString(INPUT_URL, inputUrl);
        fragment.setArguments(bundle);
        return fragment;
    }

    private OnRotateListener onRotateListener;

    private void setOnRotateListener(OnRotateListener onRotateListener) {
        this.onRotateListener = onRotateListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rotate, container, false);
        mappingView(view);
        return view;
    }

    private void mappingView(View view) {
        rotateView = view.findViewById(R.id.rotateView);
        ivLoading = view.findViewById(R.id.ivLoading);
        tvProcess = view.findViewById(R.id.tvProcess);
        llProcess = view.findViewById(R.id.llProcess);
        seekBar = view.findViewById(R.id.seekBar);
        ImageView ivCancel = view.findViewById(R.id.ivCancel);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        ImageView ivCheck = view.findViewById(R.id.ivCheck);
        LinearLayout controller = view.findViewById(R.id.controller);
        RelativeLayout rootRotate = view.findViewById(R.id.rootRotate);

        ivCancel.setOnClickListener(this);
        ivCheck.setOnClickListener(this);
        rootRotate.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {

        seekBar.reset();
        seekBar.setSeekLength(0, 360, 0, 1);
        seekBar.setOnSeekChangeListener(this);
        seekBar.setValue(0);

        if (getArguments() != null) {
            inputUrl = getArguments().getString(INPUT_URL);
            showImage();
        }
    }

    private void showImage() {
        Observable.just(inputUrl)
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String url) throws Exception {
                        return getBitmap(url);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoading();
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        rotateView.setImageBitmap(bitmap);
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

    private void back() {
        getActivity().onBackPressed();
    }

    private void saveImage() {
        Observable.just(inputUrl)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String url) throws Exception {
                        return saveBitmap(url);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoading();
                    }

                    @Override
                    public void onNext(String url) {
                        if (onRotateListener != null)
                            onRotateListener.onRotatePhotoCompleted(url);
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

    private String saveBitmap(String inputUrl) {
        Bitmap bitmap = Utils.getBitmapSdcard(inputUrl);
        bitmap = Utils.rotateBitmap(bitmap, rotateView.getRotate());
        Utils.saveBitmap(inputUrl, bitmap);
        return inputUrl;
    }

    @Override
    public void onSeekChanged(float value, float step) {
        if (llProcess.getVisibility() != View.VISIBLE && !start)
            llProcess.setVisibility(View.VISIBLE);

        start = false;
        tvProcess.setText(Integer.toString((int) (value)));
        rotateView.setRotate((int) value);
    }

    @Override
    public void onSeekStopped(float value, float step) {
        if (llProcess.getVisibility() != View.GONE) llProcess.setVisibility(View.GONE);
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
