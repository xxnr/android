/**
 * 
 */
package com.ksfc.newfarmer.utils;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

/**
 * 项目名称：newFarmer63 类名称：NetUtils 类描述： 创建人：王蕾 创建时间：2015-7-4 上午6:37:49 修改备注：
 */
public class NetUtils extends BaseActivity {

	private static NetUtils instance;

	public NetUtils() {
	}

	public static NetUtils getInstance() {
		if (instance == null) {
			instance = new NetUtils();
		}
		return instance;
	}

	int tag;

	public void execApi(Context context, int tag, int page) {
		this.tag = tag;
		switch (tag) {
		case 1:

			RequestParams params = new RequestParams();
			// params.put("userId", Store.User.queryMe().id);
			// params.put("locationUserId", Store.User.queryMe().id);
			params.put("userId", "6785231633eeb459e286719e8aa489b9");
			params.put("locationUserId", "6785231633eeb459e286719e8aa489b9");
			params.put("page", page);
			execApi(ApiType.GETORDERLIST, params);

			break;
		case 2:

			break;
		case 3:

			break;
		case 4:

			break;
		case 5:

			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ksfc.newfarmer.BaseActivity#getLayout()
	 */
	@Override
	public int getLayout() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ksfc.newfarmer.BaseActivity#OnActCreate(android.os.Bundle)
	 */
	@Override
	public void OnActCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ksfc.newfarmer.BaseActivity#OnViewClick(android.view.View)
	 */
	@Override
	public void OnViewClick(View v) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ksfc.newfarmer.BaseActivity#onResponsed(com.ksfc.newfarmer.protocol
	 * .Request)
	 */
	@Override
	public void onResponsed(Request req) {
		// TODO Auto-generated method stub

	}

}
