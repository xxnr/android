package com.ksfc.newfarmer.activitys;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.Request;

import android.os.Bundle;
import android.view.View;

public class QiandaoActivity extends BaseActivity {

	@Override
	public int getLayout() {
		return R.layout.qiandao_layout;
	}

	@Override
	public void OnActCreate(Bundle savedInstanceState) {
		initView();
	}

	private void initView() {
		setViewClick(R.id.qiandao_sure_tv);
	}



	@Override
	public void OnViewClick(View v) {
		switch (v.getId()) {
		case R.id.qiandao_sure_tv:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onResponsed(Request req) {


	}

}
