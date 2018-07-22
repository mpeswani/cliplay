package clipay.com.clipay.util;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageView;

public class DoubleTapImageView extends AppCompatImageView {
    GestureDetector gestureDetector;
    DoubleTapDetector detector;

    public  interface DoubleTapDetector {
        void onDoubleTap(int imagePosition);
    }

    public void setGestureDetector(DoubleTapDetector detector) {
        this.detector = detector;
    }

    public DoubleTapImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // creating new gesture detector
        gestureDetector = new GestureDetector(context, new GestureListener());
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
            float x = e.getX();
            float y = e.getY();
            Log.d("Double Tap", "Tapped at: (" + x + "," + y + ")");
            return true;
        }
    }
}