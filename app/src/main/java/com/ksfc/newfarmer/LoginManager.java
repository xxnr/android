package com.ksfc.newfarmer;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;

/**
 * 
 * 项目名称：QianXihe 类名称：LoginManager 类描述： 环信登录管理类,本地登录或第三方登录成功后，调用此类的登录方法 创建人：范东
 * 创建时间：2015年6月28日 上午9:23:35 修改备注：
 */
public class LoginManager {
	private static Context lgcontext;
	private static String uid;
	private static String pwd;

	private boolean isOK = false;// 登录是否成功
	private Timer timer = new Timer();// 定时器
	private int i = 0; // 设置网络超时时间为10s

	private static LoginManager instance;

	private LoginManager() {

	}

	public static LoginManager getInstance(Context context, String userid,
			String password) {
		lgcontext = context;
		uid = userid;
		pwd = password;
		if (instance == null) {
			instance = new LoginManager();
		}
		return instance;
	}


}
