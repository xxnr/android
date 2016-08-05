package com.ksfc.newfarmer.fragment;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver;

import com.ksfc.newfarmer.EventBaseFragment;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.event.BigImageEvent;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.utils.ScreenUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.squareup.picasso.Picasso;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by HePeng on 2016/1/8.
 */
public class BigImageFragment extends EventBaseFragment implements PhotoViewAttacher.OnPhotoTapListener {
    private PhotoView photoView;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private int position;

    private String originalUrl;
    private int top;
    private View rootView;

    public final int DURATION = 500;


    public static BigImageFragment newInstance(String originalUrl, int position) {
        BigImageFragment fragment = new BigImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, originalUrl);
        args.putInt(ARG_PARAM2, position);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            originalUrl = getArguments().getString(ARG_PARAM1);
            position = getArguments().getInt(ARG_PARAM2);
        }
    }


    @Override
    public View InItView() {
        View view = inflater.inflate(R.layout.activity_big_pic, null);
        photoView = (PhotoView) view.findViewById(R.id.photoView);
        rootView = view.findViewById(R.id.rootView);

        int windowsHeight = ScreenUtil.getWindowsHeight(activity);
        int screenWidth = ScreenUtil.getScreenWidth(activity);
        top = (windowsHeight - screenWidth) / 2;

        if (StringUtil.checkStr(originalUrl)) {
            Picasso.with(activity)
                    .load(MsgID.IP + originalUrl)
                    .skipMemoryCache()
                    .error(R.drawable.zhanweitu)
                    .config(Bitmap.Config.RGB_565)
                    .noFade()
                    .into(photoView);
        }

        photoView.setOnPhotoTapListener(this);
        photoView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                photoView.getViewTreeObserver().removeOnPreDrawListener(this);
                activityEnterAnim();
                return true;
            }
        });
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void activityExit(BigImageEvent event) {
        activityExitAnim();
        Observable
                .timer(DURATION, TimeUnit.MILLISECONDS)
                .compose(BigImageFragment.this.<Long>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        activity.finish();
                    }
                });
    }


    private void activityEnterAnim() {
        ObjectAnimator
                .ofFloat(rootView, "alpha", 0.0F, 1.0F)
                .setDuration(DURATION)
                .start();
        ObjectAnimator
                .ofFloat(photoView, "translationY", -top, 0.0f)
                .setDuration(DURATION)
                .start();
    }


    private void activityExitAnim() {
        ObjectAnimator
                .ofFloat(photoView, "translationY", 0.0f, -top)
                .setDuration(DURATION)
                .start();
    }

    @Override
    public void onResponsed(Request req) {

    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onPhotoTap(View view, float x, float y) {
        EventBus.getDefault().post(new BigImageEvent(position));
    }

    @Override
    public void onOutsidePhotoTap() {

    }

}
