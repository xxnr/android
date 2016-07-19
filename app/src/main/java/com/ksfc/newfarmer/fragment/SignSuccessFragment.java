package com.ksfc.newfarmer.fragment;


import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseFragment;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.beans.PointResult;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by CAI on 2016/6/14.
 */
public class SignSuccessFragment extends BaseFragment {

    @BindView(R.id.sign_success_times_tv)
    TextView signSuccessTimesTv;
    @BindView(R.id.sign_success_count_tv)
    TextView signSuccessCountTv;
    @BindView(R.id.sign_success_gif_img)
    ImageView signSuccessGifImg;

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public View InItView() {
        View view = inflater.inflate(R.layout.fragment_sign_success, null);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            PointResult pointResult = (PointResult) bundle.getSerializable("pointResult");
            if (pointResult != null) {
                signSuccessTimesTv.setText(String.valueOf(pointResult.consecutiveTimes));
                signSuccessCountTv.setText(String.valueOf(pointResult.pointAdded));
            }
            // 获取AnimationDrawable对象
            final AnimationDrawable animationDrawable = (AnimationDrawable) signSuccessGifImg.getBackground();
            //开始或者继续动画播放
            animationDrawable.setOneShot(true);
            animationDrawable.start();
            //播放完成后关闭页面
            Observable.timer(2, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(this.<Long>bindToLifecycle())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            if (animationDrawable.isRunning()) {
                                animationDrawable.stop();
                                activity.finish();
                            }
                        }
                    });
        }

        return view;
    }

    @Override
    public void onResponsed(Request req) {

    }
}

