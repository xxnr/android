package com.ksfc.newfarmer.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;

/**
 * Created by HePeng on 2016/2/24.
 */
public class PopWindowUtils {

    public static void setBackgroundBlack(View view, int what) {
        switch (what) {
            case 0:
                AlphaAnimation animation =new AlphaAnimation(0.0f,1.0f);
                animation.setDuration(150);
                view.setAnimation(animation);
                view.setVisibility(View.VISIBLE);
                break;
            case 1:
                view.setVisibility(View.GONE);
                break;
        }
    }
}
