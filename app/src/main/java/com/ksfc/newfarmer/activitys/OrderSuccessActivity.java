package com.ksfc.newfarmer.activitys;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.RndApplication;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.utils.IntentUtil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class OrderSuccessActivity extends BaseActivity {
	private String orderId, paymentId;

	@Override
	public int getLayout() {
		// TODO Auto-generated method stub
		return R.layout.order_success_layout;
	}

	@Override
	public void OnActCreate(Bundle savedInstanceState) {
		quit();
		setTitle("订单成功");
		orderId = (String) getIntent().getSerializableExtra("orderId");
		paymentId = (String) getIntent().getSerializableExtra("orderNo");
		setViewClick(R.id.contact_tv);
		setViewClick(R.id.check_order_tv);
	}

	public void quit() {
		for (Activity activity : RndApplication.tempDestroyActivityList) {
			if (null != activity) {
				activity.finish();
			}
		}
		RndApplication.tempDestroyActivityList.clear();
	}

	@Override
	public void OnViewClick(View v) {
		switch (v.getId()) {
		case R.id.contact_tv:
			// IntentUtil.activityForward(OrderSuccessActivity.this,
			// ChatActivity.class, null, false);
			String strMobile = "400-056-0371";
			// 此处应该对电话号码进行验证。。
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ strMobile));
			this.startActivity(intent);
			break;
		case R.id.check_order_tv:
			intent = new Intent(OrderSuccessActivity.this,
					MyOrderDetailActivity.class);
			intent.putExtra("orderId", orderId);
			intent.putExtra("OrderNo", paymentId);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onResponsed(Request req) {
		// TODO Auto-generated method stub

	}

}
