package com.ksfc.newfarmer.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by HePeng on 2016/3/7.
 */
public class ShowHideUtils {

    //浮现出来的动画
    public static void showFadeOut(View view) {

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        view.setAnimation(animation);

    }

    //隐藏进去的动画
    public static void hideFadeIn(View view) {

        Animation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(500);
        view.setAnimation(animation);

    }


    //浮现出来的动画
    public static void showRightFadeOut(View view) {

        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(300);
        view.setAnimation(animation);

    }

    //隐藏进去的动画
    public static void hideLeftFadeIn(View view) {

        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(300);
        view.setAnimation(animation);

    }


    //浮现出来的动画
    public static void showBottomFadeOut(View view) {

        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,
                0,
                Animation.RELATIVE_TO_SELF,
                0,
                Animation.RELATIVE_TO_SELF,
                -1,
                Animation.RELATIVE_TO_SELF,
                0);
        animation.setDuration(300);
        view.setAnimation(animation);
    }

    //隐藏进去的动画
    public static void hideTopFadeIn(View view) {

        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,
                0,
                Animation.RELATIVE_TO_SELF,
                0,
                Animation.RELATIVE_TO_SELF,
                0,
                Animation.RELATIVE_TO_SELF,
                -1);

    }


}
