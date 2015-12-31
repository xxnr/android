package com.ksfc.newfarmer.protocol;

import java.util.HashMap;

import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.beans.LoginResult.UserInfo;

import android.content.Context;
import net.yangentao.util.app.App;

/**
 * http请求参数
 * 
 * @author wqz
 * 
 */
public final class RequestParams extends HashMap<String, String> {

	private static final long serialVersionUID = 3693700342564975575L;

	private Context mContext;

	public Context getmContext() {

		return mContext;
	}

	public void setmContext(Context mContext) {

		this.mContext = mContext;
	}

	// ===========================================================
	// Constructors
	// ===========================================================
	public RequestParams(Context context) {

		if (mContext == null) {
			mContext = context;
		}
		initParams();
	}

	public RequestParams() {
		mContext = App.getApp();
		initParams();
	}

	private void initParams() {
		// TODO put一些必带的值
	}

	/**
	 * put文件信息
	 * 
	 * @param key
	 * @param path
	 */
	public void putFile(String key, String path) {
		put(NetworkHelper.Post_Entity_FILE_Data + key, path);
	}

	/**
	 * put a int value
	 * 
	 * @param key
	 * @param val
	 */
	public void put(String key, int val) {
		put(key, String.valueOf(val));
	}

	public void putDefault() {
		UserInfo me = Store.User.queryMe();
		if (me != null) {
			// put("userid", me.userid);
			// put("ticket", me.ticket);
		} else {
			// TODO 测试用
			// put("userid", 68);
			// put("ticket", "123456");
		}
	}

	public void removeDefaultParams() {
		// TODO remove init
	}

	@Override
	public void clear() {

		super.clear();
		initParams();
	}

	public interface ResultFormat {

		public static final String XML = "xml";

		public static final String JSON = "json";
	}

}
