package com.hoanganhtuan95ptit.editphoto.contrast;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;

import java.util.concurrent.TimeUnit;

import androidx.annotation.FloatRange;
import androidx.appcompat.widget.AppCompatImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
;

/**
 * Created by Hoang Anh Tuan on 12/12/2017.
 */

public class ContrastView extends AppCompatImageView {

    private float contrast;
    private PublishSubject<Float> subject;

    public ContrastView(Context context) {
        super(context);
        initView();
    }

    public ContrastView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ContrastView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        subject = PublishSubject.create();
        subject.debounce(0, TimeUnit.MILLISECONDS)
//                .filter(new Predicate<Float>() {
//                    @Override
//                    public boolean test(Float contrast) throws Exception {
//                        return true;
//                    }
//                })
                .distinctUntilChanged()
                .switchMap((Function<Float, ObservableSource<ColorMatrixColorFilter>>) value -> postContrast(value))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(colorMatrixColorFilter -> setColorFilter(colorMatrixColorFilter));
    }

    public float getContrast() {
        return contrast;
    }

    public void setContrast(@FloatRange(from = -180.0f, to = 180.0f) float contrast) {
        this.contrast = contrast / 180.0f + 1.0f;
        subject.onNext(this.contrast);
    }

    private Observable<ColorMatrixColorFilter> postContrast(float value) {
        return Observable.just(contrast(value));
    }

    private ColorMatrixColorFilter contrast(float value) {
        float scale = value;
        float[] array = new float[]{
                scale, 0, 0, 0, 0,
                0, scale, 0, 0, 0,
                0, 0, scale, 0, 0,
                0, 0, 0, 1, 0};
        ColorMatrix matrix = new ColorMatrix(array);
        return new ColorMatrixColorFilter(matrix);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
