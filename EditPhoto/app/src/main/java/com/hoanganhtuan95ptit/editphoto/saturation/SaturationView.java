package com.hoanganhtuan95ptit.editphoto.saturation;

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

public class SaturationView extends AppCompatImageView {

    private float saturation;
    private PublishSubject<Float> subject;

    public SaturationView(Context context) {
        super(context);
        initView();
    }

    public SaturationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SaturationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        subject = PublishSubject.create();
        subject.debounce(0, TimeUnit.MILLISECONDS)
//                .filter(new Predicate<Float>() {
//                    @Override
//                    public boolean test(Float brightness) throws Exception {
//                        return true;
//                    }
//                })
                .distinctUntilChanged()
                .switchMap((Function<Float, ObservableSource<ColorMatrixColorFilter>>) value -> postBrightness(value))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(colorMatrixColorFilter -> setColorFilter(colorMatrixColorFilter));
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(@FloatRange(from = 0, to = 100) float saturation) {
        this.saturation = saturation / 100f;
        subject.onNext(this.saturation);
    }

    private Observable<ColorMatrixColorFilter> postBrightness(float value) {
        return Observable.just(brightness(value));
    }

    private ColorMatrixColorFilter brightness(float value) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(value);
        return new ColorMatrixColorFilter(colorMatrix);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
