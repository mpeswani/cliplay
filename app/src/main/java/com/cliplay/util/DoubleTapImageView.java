package com.cliplay.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageView;

public class DoubleTapImageView extends AppCompatImageView {
    GestureDetector gestureDetector;
    DoubleTapDetector detector;

    public DoubleTapImageView(Context context) {
        super(context);
    }

    public void setGestureDetector(DoubleTapDetector detector) {
        this.detector = detector;
    }

    public DoubleTapImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // creating new gesture detector
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public DoubleTapImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    // skipping measure calculation and drawing

    // delegate the event to the gesture detector
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return gestureDetector.onTouchEvent(e);
    }

     class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (detector != null) {
                detector.onDoubleTap(-1);
            }
            float x = e.getX();
            float y = e.getY();
            return true;
        }
    }
}