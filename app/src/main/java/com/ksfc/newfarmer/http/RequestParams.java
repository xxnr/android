package com.ksfc.newfarmer.http;

import java.util.HashMap;

/**
 * http请求参数
 * 
 * @author wqz
 * 
 */
public final class RequestParams extends HashMap<String, String> {

	private static final long serialVersionUID = 3693700342564975575L;


	public RequestParams() {
		initParams();
	}

	private void initParams() {

	}

	/**
	 * put a int value
	 */
	public void put(String key, int val) {
		put(key, String.valueOf(val));
	}

	@Override
	public void clear() {
		super.clear();
		initParams();
	}


}
