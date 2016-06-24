/**
 *
 */
package com.ksfc.newfarmer.activitys;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.Request;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名称：newFarmer63 类名称：AgreeMentActivity 类描述： 创建人：王蕾 创建时间：2015-6-3 下午8:15:18
 * 修改备注：
 */
public class AgreeMentActivity extends BaseActivity {

    @BindView(R.id.webView)
    WebView webView;

    @Override
    public int getLayout() {
        return R.layout.web_view_layout;

    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setTitle("用户协议");
        webView.loadUrl("file:///android_asset/yonghuxieyi.html");
    }


    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {
        // TODO Auto-generated method stub

    }


}
