package com.ksfc.newfarmer.widget;

/**
 * Created by HePeng on 2015/11/27.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class VerticalScrollView extends ScrollView {

    private int mLastY = 0;

    public VerticalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int y = (int) ev.getY();
        View childView = getChildAt(0);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mLastY-y> 0&&childView != null && childView.getMeasuredHeight() <= getScrollY() + getHeight()) {
                    return false;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }



}
