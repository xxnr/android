package com.ksfc.newfarmer.protocol;


/**
 * 接口数据的回调
 * 
 * @author Bruce.Wang
 */
public interface OnApiDataReceivedCallback {

	/**
	 * 响应
	 * 
	 * @param req
	 */
	void onResponse(Request req);

}
