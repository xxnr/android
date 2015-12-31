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
		/*
		 * if(!isFinish){
		 * ((Activity)activity).overridePendingTransition(R.anim.push_left_in,
		 * R.anim.push_left_out); return; }
		 */
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

	/*
	 * 启动一个服务
	 */
	public static void serviceForward(Context activity, Class clazz,
			Bundle bundle, boolean isFinish) {
		Intent intent = new Intent(activity, clazz);
		if (null != bundle)
			intent.putExtras(bundle);
		activity.startService(intent);
		Log.d("UpdateVersionActivity", "更新===服务启动22222222222222");
		if (isFinish && activity instanceof Activity)
			((Activity) activity).finish();
	}

}
