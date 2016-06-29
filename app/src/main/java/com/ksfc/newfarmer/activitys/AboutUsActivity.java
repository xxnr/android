package com.ksfc.newfarmer.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.utils.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HePeng on 2015/12/29.
 */
public class AboutUsActivity extends BaseActivity {
    @BindView(R.id.about_us_version)
    TextView aboutUsVersion;

    @Override
    public int getLayout() {
        return R.layout.activity_about_us;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setTitle("关于我们");
        //设置版本
        Bundle bundle = getIntent().getExtras();
        String versionName = null;
        if (bundle != null) {
            versionName = bundle.getString("versionName");
        }
        aboutUsVersion.setText(StringUtil.checkStr(versionName) ? "V" + versionName : "");
    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {

    }
}
