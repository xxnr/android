package com.ksfc.newfarmer.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ksfc.newfarmer.R;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by CAI on 2016/6/24.
 */
public class PtrHeaderView extends FrameLayout implements PtrUIHandler {
    // 下拉刷新文字
    private TextView tvHeadTitle;
    // 下拉图标
    private ImageView ivWindmill;
    private RotateAnimation animation;


    public PtrHeaderView(Context context) {
        this(context, null);
    }

    public PtrHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public PtrHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        /**
         * 头部
         */
        ViewGroup headView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.ptr_header_layout, this, true);
        ivWindmill = (ImageView) headView.findViewById(R.id.iv_windmill);
        tvHeadTitle = (TextView) headView.findViewById(R.id.tv_head_title);
        tvHeadTitle.setText("下拉刷新");
        animation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(100);
        animation.setDuration(800);
    }


    @Override
    public void onUIReset(PtrFrameLayout frame) {
        tvHeadTitle.setText("下拉刷新");
        ivWindmill.setAnimation(animation);
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        tvHeadTitle.setText("下拉刷新");
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        tvHeadTitle.setText("正在载入...");
        ivWindmill.startAnimation(animation);
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        ivWindmill.clearAnimation();
        animation.cancel();
        tvHeadTitle.setText("刷新完成");
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        final int mOffsetToRefresh = frame.getOffsetToRefresh();
        final int currentPos = ptrIndicator.getCurrentPosY();
        final int lastPos = ptrIndicator.getLastPosY();

        if (currentPos < mOffsetToRefresh && lastPos >= mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                tvHeadTitle.setText("下拉刷新");
            }
        } else if (currentPos > mOffsetToRefresh && lastPos <= mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                tvHeadTitle.setText("放开刷新");
            }
        }

    }


}
