package clipay.com.clipay.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.blankj.utilcode.util.BarUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class ScrollingFABBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
    private int toolbarHeight;

    public ScrollingFABBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.toolbarHeight = BarUtils.getActionBarHeight();
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull FloatingActionButton fab, @NonNull View
            dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull FloatingActionButton fab,
                                          @NonNull View dependency) {
        if (dependency instanceof AppBarLayout) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab
                    .getLayoutParams();
            int fabBottomMargin = lp.bottomMargin;
            int distanceToScroll = fab.getHeight() + fabBottomMargin;
            float ratio = dependency.getY() / (float) toolbarHeight;
            fab.setTranslationY(-distanceToScroll * ratio);
        }
        return true;
    }
}