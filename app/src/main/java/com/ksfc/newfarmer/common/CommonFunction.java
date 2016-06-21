package com.ksfc.newfarmer.common;

import android.app.Activity;
import android.os.Bundle;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.FloatingLayerActivity;
import com.ksfc.newfarmer.protocol.beans.PointResult;
import com.ksfc.newfarmer.utils.ActivityAnimationUtils;
import com.ksfc.newfarmer.utils.IntentUtil;

/**
 * Created by CAI on 2016/6/21.
 */
public class CommonFunction {

    /**
     * 展示签到成功页面
     */
    public static void showSuccess(BaseActivity activity, PointResult pointResult) {
        Bundle bundle = new Bundle();
        bundle.putString("activity", activity.getClass().getSimpleName());
        bundle.putSerializable("pointResult", pointResult);
        IntentUtil.activityForward(activity, FloatingLayerActivity.class, bundle, false);
        ActivityAnimationUtils.setActivityAnimation(activity, R.anim.animation_none, R.anim.animation_none);
    }

}
