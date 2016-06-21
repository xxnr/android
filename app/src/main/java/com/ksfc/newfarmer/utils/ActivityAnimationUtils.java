package com.ksfc.newfarmer.utils;

import android.app.Activity;

/**
 * Created by CAI on 2016/6/15.
 */
public class ActivityAnimationUtils {

    public static void setActivityAnimation(Activity activity,int enterAnim,int exitAnim){

        int version = Integer.valueOf(android.os.Build.VERSION.SDK);
        if (version > 5) {
            activity.overridePendingTransition(enterAnim,exitAnim);
        }
    }



}
