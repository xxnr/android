package com.ksfc.newfarmer.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.utils.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HePeng on 2015/12/29.
 */
public class AboutUsActivity extends BaseActivity {
    @BindView(R.id.about_us_version)
    TextView aboutUsVersion;

    public static final String ARG_PARAM1 = "param1";

    public static Intent getCallingIntent(Context context, String versionName) {
        Intent callingIntent = new Intent(context, AboutUsActivity.class);
        callingIntent.putExtra(ARG_PARAM1, versionName);
        return callingIntent;
    }

    @Override
    public int getLayout() {
        return R.layout.activity_about_us;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setTitle("关于我们");
        //设置版本
        String versionName = getIntent().getStringExtra(ARG_PARAM1);
        aboutUsVersion.setText(StringUtil.checkStr(versionName) ? "V" + versionName : "");
    }


    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {

    }
}
