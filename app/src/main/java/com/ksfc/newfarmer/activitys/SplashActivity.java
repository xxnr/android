package com.ksfc.newfarmer.activitys;

import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.SPUtils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import net.yangentao.util.PreferenceUtil;

/**
 * 项目名称：QianXihe 类名称：SplashActivity 类描述： 启动页 创建人：Jimes 创建时间：2015-5-14 上午11:38:21
 * 修改备注：
 */
public class SplashActivity extends Activity {

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    IntentUtil.activityForward(SplashActivity.this, GuideActivity.class, null, true);
                    break;
                case 2:
                    IntentUtil.activityForward(SplashActivity.this, MainActivity.class, null, true);
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 屏幕竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_welcome);
        PreferenceUtil pu = new PreferenceUtil(this, "config");
        boolean isFirst = pu.getBool("first", true); //判断用户是否是第一次打开app
        // 判断是否第一次启动
        if (isFirst) {
            handler.sendEmptyMessageDelayed(1, 3000);
        } else {
            handler.sendEmptyMessageDelayed(2, 3000);
        }
        pu.putBool("first", false);

    }


}
