package com.ksfc.newfarmer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/*
 * 页面的跳转，Intent常量的设置
 */
public class IntentUtil {

	/*
	 * 无回调的
	 */
	public static void activityForward(Context activity, Class clazz,
			Bundle bundle, boolean isFinish) {
		Intent intent = new Intent(activity, clazz);
		if (null != bundle)
			intent.putExtras(bundle);
		activity.startActivity(intent);
		if (isFinish && activity instanceof Activity)
			((Activity) activity).finish();
	}

	/*
	 * 可回调的
	 */
	public static void startActivityForResult(Activity activity, Class clazz,
			int requestCode, Bundle bundle) {
		Intent intent = new Intent(activity, clazz);
		if (null != bundle) {
			intent.putExtras(bundle);
			activity.startActivityForResult(intent, requestCode);
		} else {
			activity.startActivityForResult(intent, requestCode);
		}
	}



}
