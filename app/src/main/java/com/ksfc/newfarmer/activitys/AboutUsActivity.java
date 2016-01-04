package com.ksfc.newfarmer.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.StringUtil;

/**
 * Created by CAI on 2015/12/29.
 */
public class AboutUsActivity extends BaseActivity {
    @Override
    public int getLayout() {
        return R.layout.about_us_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("关于我们");
        findViewById(R.id.titleview).setBackgroundColor(getResources().getColor(R.color.half_green));//设置title背景
        //设置版本
        TextView version = (TextView) findViewById(R.id.about_us_version);
        Bundle bundle = getIntent().getExtras();
        String versionName = bundle.getString("versionName");
        if (StringUtil.checkStr(versionName)) {
            RndLog.d(TAG, versionName);
            version.setText("V" + versionName);
        }
    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {

    }
}
