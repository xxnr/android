package com.ksfc.newfarmer.test;

import android.content.Intent;
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
public class WebActivity extends BaseActivity {
    @BindView(R.id.webView)
    WebView webView;

    @Override
    public int getLayout() {
        return R.layout.activity_web_view;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setTitle("新农资讯");
        WebView web = (WebView) findViewById(R.id.webView);
        // 允许运行js脚本
        web.getSettings().setJavaScriptEnabled(true);
        // 如果web内出现链接 依旧由当前webVIew加载
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        if (getIntent() != null) {
                Uri uri = Uri.parse(getIntent().getDataString());
                if (uri != null) {
                    String id = uri.getQueryParameter("id");
                    web.loadUrl("http://api.xinxinnongren.com/news/"+id);
                }
        }

        setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(WebToLoginActivity.class);
            }
        });
    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {

    }

}
