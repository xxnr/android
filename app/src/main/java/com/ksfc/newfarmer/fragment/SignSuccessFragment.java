package com.ksfc.newfarmer.fragment;


import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.beans.PointResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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
    private Unbinder unbinder;

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public View InItView() {
        View view = inflater.inflate(R.layout.sign_success_fragment_layout, null);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
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
        signSuccessGifImg.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (animationDrawable.isRunning()) {
                    animationDrawable.stop();
                    activity.finish();
                }
            }
        }, 2000);

        return view;
    }

    @Override
    public void onResponsed(Request req) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

