package clipay.com.clipay.ui.activity

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import clipay.com.clipay.R
import clipay.com.clipay.ui.adapter.QuickAdapter
import clipay.com.clipay.util.SlidingImageAdapter
import com.bumptech.glide.Glide
import com.mukesh.image_processing.ImageProcessingConstants
import com.mukesh.image_processing.ImageProcessor
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_pix_editor.*


class PixEditorActivity : AppCompatActivity() {
    private val subject = PublishSubject.create<Bitmap>()
    private val disposable = CompositeDisposable();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pix_editor)
        val list = intent.getStringArrayListExtra("image_results")
        viewPager.adapter = SlidingImageAdapter(this, list)
        val listOfBitmap = ArrayList<Bitmap>()
        val adapter = QuickAdapter(listOfBitmap)
        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

            }

        })

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        getBitmap(list[0])
        disposable.addAll(subject.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<Bitmap>() {
                    override fun onError(e: Throwable) {
                    }

                    override fun onNext(bitmap: Bitmap) {
                        adapter.addData(bitmap)
                    }


                    override fun onComplete() {
                    }
                }))
        adapter.setOnItemClickListener { a, view, position ->
            viewPager.getChildAt(0).findViewById<ImageView>(R.id.image)
                    .setImageBitmap(adapter.getItem(position))

        }
    }


    private fun getBitmap(url: String) {
        Single.fromCallable { Glide.with(this).asBitmap().load(url).submit().get() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Bitmap> {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(bitmap: Bitmap) {
                        getBitMaps(bitmap)
                    }

                    override fun onError(e: Throwable) {}
                })
    }

    private fun sub(single: Single<Bitmap>) {
        single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Bitmap> {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(bitmap: Bitmap) {
                        subject.onNext(bitmap)
                    }

                    override fun onError(e: Throwable) {}
                })
    }

    private fun getBitMaps(bm: Bitmap) {
        val mImageProcessor = ImageProcessor()
        sub(Single.fromCallable { mImageProcessor.doGreyScale(bm) })
        sub(Single.fromCallable { mImageProcessor.doHighlightImage(bm, 15, Color.RED) })
        sub(Single.fromCallable { mImageProcessor.doInvert(bm) })
        sub(Single.fromCallable { mImageProcessor.doGamma(bm, 0.6, 0.6, 0.6) })
        sub(Single.fromCallable { mImageProcessor.doGamma(bm, 1.8, 1.8, 1.8) })
        sub(Single.fromCallable { mImageProcessor.doColorFilter(bm, 1.0, 0.0, 0.0) })
        sub(Single.fromCallable { mImageProcessor.doColorFilter(bm, 0.0, 1.0, 0.0) })
        sub(Single.fromCallable { mImageProcessor.doColorFilter(bm, 0.0, 0.0, 1.0) })
        sub(Single.fromCallable { mImageProcessor.doColorFilter(bm, 0.5, 0.5, 0.5) })
        sub(Single.fromCallable { mImageProcessor.doColorFilter(bm, 1.5, 1.5, 1.5) })
        sub(Single.fromCallable { mImageProcessor.createSepiaToningEffect(bm, 150, 0.7, 0.3, 0.12) })
        sub(Single.fromCallable { mImageProcessor.createSepiaToningEffect(bm, 150, 0.8, 0.2, 0.0) })
        sub(Single.fromCallable { mImageProcessor.createSepiaToningEffect(bm, 150, 0.12, 0.7, 0.3) })
        sub(Single.fromCallable { mImageProcessor.createSepiaToningEffect(bm, 150, 0.12, 0.3, 0.7) })
        sub(Single.fromCallable { mImageProcessor.decreaseColorDepth(bm, 32) })
        sub(Single.fromCallable { mImageProcessor.decreaseColorDepth(bm, 64) })
        sub(Single.fromCallable { mImageProcessor.decreaseColorDepth(bm, 128) })
        sub(Single.fromCallable { mImageProcessor.createContrast(bm, 50.0) })
        sub(Single.fromCallable { mImageProcessor.createContrast(bm, 100.0) })
        sub(Single.fromCallable { mImageProcessor.rotate(bm, 40F) })
        sub(Single.fromCallable { mImageProcessor.rotate(bm, 340F) })
        sub(Single.fromCallable { mImageProcessor.doBrightness(bm, -60) })
        sub(Single.fromCallable { mImageProcessor.doBrightness(bm, 30) })
        sub(Single.fromCallable { mImageProcessor.doBrightness(bm, 80) })
        sub(Single.fromCallable { mImageProcessor.applyGaussianBlur(bm) })
        sub(Single.fromCallable { mImageProcessor.createShadow(bm) })
        sub(Single.fromCallable { mImageProcessor.sharpen(bm, 11.0) })
        sub(Single.fromCallable { mImageProcessor.applyMeanRemoval(bm) })
        sub(Single.fromCallable { mImageProcessor.smooth(bm, 100.0) })
        sub(Single.fromCallable { mImageProcessor.emboss(bm) })
        sub(Single.fromCallable { mImageProcessor.engrave(bm) })
        sub(Single.fromCallable { mImageProcessor.boost(bm, ImageProcessingConstants.RED, 1.5) })
        sub(Single.fromCallable { mImageProcessor.boost(bm, ImageProcessingConstants.GREEN, 0.5) })
        sub(Single.fromCallable { mImageProcessor.boost(bm, ImageProcessingConstants.BLUE, 0.67) })
        sub(Single.fromCallable { mImageProcessor.roundCorner(bm, 45.0) })
        sub(Single.fromCallable { mImageProcessor.flip(bm, ImageProcessingConstants.FLIP_VERTICAL) })
        sub(Single.fromCallable { mImageProcessor.tintImage(bm, 50) })
        sub(Single.fromCallable { mImageProcessor.replaceColor(bm, Color.BLACK, Color.BLUE) })
        sub(Single.fromCallable { mImageProcessor.applyFleaEffect(bm) })
        sub(Single.fromCallable { mImageProcessor.applyBlackFilter(bm) })
        sub(Single.fromCallable { mImageProcessor.applySnowEffect(bm) })
        sub(Single.fromCallable { mImageProcessor.applyShadingFilter(bm, Color.MAGENTA) })
        sub(Single.fromCallable { mImageProcessor.applyShadingFilter(bm, Color.BLUE) })
        sub(Single.fromCallable { mImageProcessor.applySaturationFilter(bm, 1) })
        sub(Single.fromCallable { mImageProcessor.applySaturationFilter(bm, 5) })
        sub(Single.fromCallable { mImageProcessor.applyHueFilter(bm, 1) })
        sub(Single.fromCallable { mImageProcessor.applyHueFilter(bm, 5) })
        sub(Single.fromCallable { mImageProcessor.applyReflection(bm) })
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }
}
