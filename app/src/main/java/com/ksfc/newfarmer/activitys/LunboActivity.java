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
 * 项目名称：newFarmer 类名称：LunboActivity 类描述： 创建人：尚前琛 创建时间：2015-7-6 下午6:18:58 修改备注：
 */
public class LunboActivity extends BaseActivity {
	WebView lunbo_wv;
	private String url;

	@Override
	public int getLayout() {
		// TODO Auto-generated method stub
		return R.layout.lunbo_activity_lay;
	}

	@Override
	public void OnActCreate(Bundle savedInstanceState) {
		initView();
	}

	private void initView() {
		url = getIntent().getStringExtra("url");
		lunbo_wv = (WebView) findViewById(R.id.lunbo_wv);
		lunbo_wv.setBackgroundColor(0x00000000);
		lunbo_wv.loadData(url, "text/html;charset=utf-8", null);// 评论内容
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
