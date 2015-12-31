package com.ksfc.newfarmer.widget;

/**
 * Created by CAI on 2015/11/27.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class VerticalScrollView extends ScrollView {
    private OnScrollToBottomListener onScrollToBottom;

    public VerticalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
                                  boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if(scrollY != 0 && null != onScrollToBottom){
            onScrollToBottom.onScrollBottomListener(clampedY);
        }
    }

    public void setOnScrollToBottomLintener(OnScrollToBottomListener listener){
        onScrollToBottom = listener;
    }


    public interface OnScrollToBottomListener{
        public void onScrollBottomListener(boolean isBottom);
    }



}
