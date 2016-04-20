package com.ksfc.newfarmer.utils.thrid;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.chinaums.mpos.service.IUmsMposService;


public class EposServiceManager {
	
	public IUmsMposService mUmsMposService;
	private static EposServiceManager instance;
	private String orderID;
	
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mUmsMposService = IUmsMposService.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name)	 {
			mUmsMposService = null;
		}

	};
		
	synchronized public static EposServiceManager getInstance() {
		if (instance == null) {
			instance = new EposServiceManager();
		}
		return instance;
	}
 
	
	public void setOrderID(String orderId){
		orderID=orderId;
	}
	
	public String getOrderId(){
		return instance.orderID;
	}

	public void bindMpospService(Context context) {
		// 绑定远程服务
		Intent intent = new Intent();
		intent.setClassName("com.chinaums.mposplugin",
				"com.chinaums.mpos.service.MposService");
		// 与服务端进行绑定，绑定成功会回调onServiceConnected里面
		context.bindService(intent, mConnection, Service.BIND_AUTO_CREATE);
	}

	public void unbindMposService(Context context)	 {
		context.unbindService(mConnection);
	}
}
