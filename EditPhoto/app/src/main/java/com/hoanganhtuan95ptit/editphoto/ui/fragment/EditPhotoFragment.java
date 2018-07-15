package com.hoanganhtuan95ptit.editphoto.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hoanganhtuan95ptit.editphoto.R;
import com.hoanganhtuan95ptit.editphoto.StartSnapHelper;
import com.hoanganhtuan95ptit.editphoto.Utils;
import com.hoanganhtuan95ptit.editphoto.brightness.BrightnessFragment;
import com.hoanganhtuan95ptit.editphoto.brightness.OnBrightnessListener;
import com.hoanganhtuan95ptit.editphoto.contrast.ContrastFragment;
import com.hoanganhtuan95ptit.editphoto.contrast.OnContrastListener;
import com.hoanganhtuan95ptit.editphoto.crop.CropFragment;
import com.hoanganhtuan95ptit.editphoto.crop.OnCropListener;
import com.hoanganhtuan95ptit.editphoto.fillter.FilterFragment;
import com.hoanganhtuan95ptit.editphoto.fillter.OnFilterListener;
import com.hoanganhtuan95ptit.editphoto.model.EditType;
import com.hoanganhtuan95ptit.editphoto.rotate.OnRotateListener;
import com.hoanganhtuan95ptit.editphoto.rotate.RotateFragment;
import com.hoanganhtuan95ptit.editphoto.saturation.OnSaturationListener;
import com.hoanganhtuan95ptit.editphoto.saturation.SaturationFragment;
import com.hoanganhtuan95ptit.editphoto.ui.activity.EditPhotoActivity;
import com.hoanganhtuan95ptit.editphoto.ui.adapter.EditAdapter;
import com.wang.avi.AVLoadingIndicatorView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Hoang Anh Tuan on 11/23/2017.
 */

public class EditPhotoFragment extends Fragment implements
        EditAdapter.OnItemEditPhotoClickedListener,
        OnCropListener,
        OnFilterListener,
        OnRotateListener,
        OnSaturationListener,
        OnBrightnessListener,
        OnContrastListener {

    private static final String INPUT_URL = "inputUrl";

    private ImageView ivPhotoView;
    private AVLoadingIndicatorView ivLoading;
    private RecyclerView listEdit;

    public static EditPhotoFragment create(String inputUrl) {
        EditPhotoFragment fragment = new EditPhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INPUT_URL, inputUrl);
        fragment.setArguments(bundle);
        return fragment;
    }


    private String outputUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_photo, container, false);
        ivLoading = view.findViewById(R.id.ivLoading);
        ivPhotoView = view.findViewById(R.id.ivPhotoView);
        listEdit = view.findViewById(R.id.listEdit);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {

        EditAdapter editAdapter = new EditAdapter(getActivity());
        editAdapter.setOnItemEditPhotoClickedListener(this);
        listEdit.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        listEdit.setAdapter(editAdapter);
        new StartSnapHelper().attachToRecyclerView(listEdit);

        editAdapter.add(EditType.Crop);
        editAdapter.add(EditType.Filter);
        editAdapter.add(EditType.Rotate);
        editAdapter.add(EditType.Saturation);
        editAdapter.add(EditType.Brightness);
        editAdapter.add(EditType.Contrast);

        if (getArguments() != null) {

            outputUrl = getArguments().getString(INPUT_URL);
            showPhoto(outputUrl);
        }
    }


    private void showPhoto(String outputUrl) {
        this.outputUrl = outputUrl;
        Observable
                .just(outputUrl)
                .map(url -> getBitmap(url))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    public void onSubscribe(Disposable d) {
                        showLoading();
                    }

                    public void onNext(Bitmap bitmap) {
                        ivPhotoView.setImageBitmap(bitmap);
                    }

                    public void onError(Throwable e) {
                    }

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onItemEditPhotoClicked(EditType type) {
        switch (type) {
            case Crop:
                ((EditPhotoActivity) getActivity()).addFragmentToStack(CropFragment.create(outputUrl, this));
                break;
            case Filter:
                ((EditPhotoActivity) getActivity()).addFragmentToStack(FilterFragment.create(outputUrl, this));
                break;
            case Rotate:
                ((EditPhotoActivity) getActivity()).addFragmentToStack(RotateFragment.create(outputUrl, this));
                break;
            case Saturation:
                ((EditPhotoActivity) getActivity()).addFragmentToStack(SaturationFragment.create(outputUrl, this));
                break;
            case Brightness:
                ((EditPhotoActivity) getActivity()).addFragmentToStack(BrightnessFragment.create(outputUrl, this));
                break;
            case Contrast:
                ((EditPhotoActivity) getActivity()).addFragmentToStack(ContrastFragment.create(outputUrl, this));
                break;
        }
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
    public void onBrightnessPhotoCompleted(String s) {
        showPhoto(s);
    }

    @Override
    public void onContrastPhotoCompleted(String s) {
        showPhoto(s);
    }

    @Override
    public void onCropPhotoCompleted(String s) {
        showPhoto(s);
    }

    @Override
    public void onFilterPhotoCompleted(String s) {
        showPhoto(s);
    }

    @Override
    public void onRotatePhotoCompleted(String s) {
        showPhoto(s);
    }

    @Override
    public void onSaturationPhotoCompleted(String s) {
        showPhoto(s);
    }
}
