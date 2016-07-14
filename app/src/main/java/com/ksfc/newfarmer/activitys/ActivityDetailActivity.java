package com.ksfc.newfarmer.activitys;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.jsinterface.JavaScriptObject;

import net.yangentao.util.NetUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by CAI on 2016/6/24.
 */
public class ActivityDetailActivity extends BaseActivity {

    @BindView(R.id.webView)
    WebView webView;

    @Override
    public int getLayout() {
        return R.layout.activity_web_view;
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setTitle("");
        // 允许运行js脚本
        webView.getSettings().setJavaScriptEnabled(true);
        // 如果web内出现链接 依旧由当前webVIew加载
        webView.addJavascriptInterface(new JavaScriptObject(this), "jsObj");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ActivityDetailActivity.this.setTitle(view.getTitle());
            }
        });
        if (NetUtil.isConnected(this)) {
            webView.loadUrl("http://192.168.1.11:8070/campaigns/events/rewardShopLaunch");
        } else {
            showToast("网络未连接");
        }
    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {

    }

}
