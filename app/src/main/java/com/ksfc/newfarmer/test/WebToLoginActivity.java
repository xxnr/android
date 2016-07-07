package com.ksfc.newfarmer.test;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.http.Request;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by CAI on 2016/7/5.
 */
public class WebToLoginActivity extends BaseActivity {
    @BindView(R.id.webView)
    WebView webView;

    @Override
    public int getLayout() {
        return R.layout.activity_web_view;
    }

    @SuppressLint({"AddJavascriptInterface", "JavascriptInterface"})
    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("测试向h5传token");
        ButterKnife.bind(this);
        // 允许运行js脚本
        webView.getSettings().setJavaScriptEnabled(true);
        // 如果web内出现链接 依旧由当前webVIew加载
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.addJavascriptInterface(new JavaScriptObject(WebToLoginActivity.this,webView),"mObj");
        //载入js
        webView.loadUrl("file:///android_asset/testPass.html");




    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {

    }


}
