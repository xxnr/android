package com.ksfc.newfarmer.protocol;

import android.text.TextUtils;
import android.widget.Toast;

import com.ksfc.newfarmer.utils.RndLog;

import com.ksfc.newfarmer.App;


/**
 * 网络请求对象
 * 
 * @author Bruce.Wang
 * 
 */
public class Request {

	public final static String HTTP_ERROR = "http_error";
	public final static String NO_NETWORK_ERROR = "no_network";
	public final static String PARSE_DATA_FAILED = "parse_data_failed";

	public final static String TAG="Request";

	private String errorMsg;
	private ApiType api;
	private boolean success;
	private RequestParams params;
	private ResponseResult data;
	private OnApiDataReceivedCallback callback;

	/**
	 * 得到想要的数据
	 * 
	 * @return
	 */
	public ResponseResult getData() {
		return data;
	}

	public void setData(ResponseResult data) {
		this.data = data;
		dealData();
	}

	/**
	 * 对数据做处理,得到想要的信息
	 */
	private void dealData() {
		RndLog.d(TAG,"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$" + data.getStatus());
		if ("1000".equals(data.getStatus())) {
			errorMsg = "";
		} else if (HTTP_ERROR.equals(data.getStatus())) {
			// 连接服务器失败
			errorMsg = "连接服务器失败";
		} else if (NO_NETWORK_ERROR.equals(data.getStatus())) {
			errorMsg = "设备未连网";
		} else if (PARSE_DATA_FAILED.equals(data.getStatus())) {
			errorMsg = "解析数据失败";
		} else {
			errorMsg = data.getMessage();
		}

		// 设置任务是否成功
		if ("1000".equals(data.getStatus())) {
			setSuccess(true);
		}

	}

	/**
	 * 提示错误信息
	 */
	public void showErrorMsg() {
		if (!TextUtils.isEmpty(errorMsg)) {
			if (errorMsg.equals("解析数据失败") /* || errorMsg.equals("未查询到数据") */
					|| errorMsg.equals("连接服务器失败")) {
			} else {
				Toast.makeText(App.getApp(), errorMsg, Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public RequestParams getParams() {
		return params;
	}

	/**
	 * 设置请求参数
	 */
	public void setParams(RequestParams params) {
		this.params = params;
	}

	public ApiType getApi() {
		return api;
	}

	public void setApi(ApiType api) {
		this.api = api;
	}

	/**
	 * 得到错误原因
	 * 
	 * @return
	 */
	private String getErrorMsg() {
		return errorMsg == null ? "" : errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	/**
	 * 调用接口,扩充接口在{ApiType}枚举中添加实例
	 * 
	 * api
	 *      接口类型
	 * params
	 *      请求参数
	 *  listener
	 *     回调方法
	 */
	public void executeNetworkApi(final OnApiDataReceivedCallback Callback) {

		this.callback = Callback;
		if (api == null || params == null) {
			errorMsg = "参数为空";
			// 防止在处理返回数据时发生异常崩溃
			try {
				Callback.onResponse(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		NetworkDataService.getNetworkDataService().callServerInterface(this);
	}

	/**
	 * 完成请求
	 */
	public void done() {
		callback.onResponse(this);
	}

}
