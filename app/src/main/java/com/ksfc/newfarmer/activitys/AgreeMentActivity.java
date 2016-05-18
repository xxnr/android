/**
 * 
 */
package com.ksfc.newfarmer.activitys;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.Request;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

/**
 * 项目名称：newFarmer63 类名称：AgreeMentActivity 类描述： 创建人：王蕾 创建时间：2015-6-3 下午8:15:18
 * 修改备注：
 */
public class AgreeMentActivity extends BaseActivity {

	@Override
	public int getLayout() {
		return R.layout.agreement_layout;

	}

	@Override
	public void OnActCreate(Bundle savedInstanceState) {
		setTitle("用户协议");
		initView();
	}

	private void initView() {
		WebView web = (WebView) findViewById(R.id.webView);
		web.loadUrl("file:///android_asset/yonghuxieyi.html");

	}

	@Override
	public void OnViewClick(View v) {

	}

	@Override
	public void onResponsed(Request req) {
		// TODO Auto-generated method stub

	}

}
