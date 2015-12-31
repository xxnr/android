package com.ksfc.newfarmer.activitys;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.Request;

@SuppressLint("SetJavaScriptEnabled")
public class ArticleActivity extends BaseActivity {

	@Override
	public int getLayout() {
		// TODO Auto-generated method stub
		return R.layout.article_layout;
	}

	@Override
	public void OnActCreate(Bundle savedInstanceState) {
		setTitle("资讯详情");

		String url = getIntent().getStringExtra("articleUrl");
		WebView web = (WebView) findViewById(R.id.webView);

		web.loadUrl(url);
		web.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		// 允许运行js脚本
		web.getSettings().setJavaScriptEnabled(true);
	}

	@Override
	public void OnViewClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResponsed(Request req) {
		// TODO Auto-generated method stub

	}

}
