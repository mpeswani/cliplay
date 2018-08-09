package com.cliplay.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * Created by Manohar Peswani on 09/08/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */
public class DoubleTapTextView extends AppCompatTextView {
    GestureDetector gestureDetector;
    DoubleTapDetector detector;

    public DoubleTapTextView(Context context) {
        super(context);
    }

    public void setGestureDetector(DoubleTapDetector detector) {
        this.detector = detector;
    }

    public DoubleTapTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // creating new gesture detector
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public DoubleTapTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    // skipping measure calculation and drawing

    // delegate the event to the gesture detector
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return gestureDetector.onTouchEvent(e);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
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
//            float x = e.getX();
//            float y = e.getY();
            return true;
        }
    }
}