package com.ksfc.newfarmer;

import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.utils.SPUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
                    startActivity(new Intent(SplashActivity.this,
                            GuideActivity.class));
                    SplashActivity.this.finish();
                    break;
                case 2:
                    startActivity(new Intent(SplashActivity.this,
                            MainActivity.class));
                    SplashActivity.this.finish();
                    break;
                case 3://新版本的第一次登陆
                    if (Store.User.queryMe() != null) {
                        Store.User.removeMe();
                        SPUtils.clear(getApplicationContext());
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        SplashActivity.this.finish();
                    }
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        PreferenceUtil pu = new PreferenceUtil();
        pu.init(this, "config");
        boolean isFirst = pu.getBool("first", true);
        boolean isFirst1 = pu.getBool("first1", true);//判断用户是否第一次登陆
        // 判断是否第一次启动
        if (isFirst) {
            handler.sendEmptyMessageDelayed(1, 3000);
        } else {
            if (isFirst1) {//如果是第一次启动新版本
                handler.sendEmptyMessageDelayed(3, 3000);
            } else {
                handler.sendEmptyMessageDelayed(2, 3000);
            }
        }
        pu.putBool("first", false);
        pu.putBool("first1", false);

    }


}
