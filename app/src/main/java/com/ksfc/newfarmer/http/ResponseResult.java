package com.ksfc.newfarmer.http;

import java.io.Serializable;


/**
 * 请求数据结果的基类
 * 
 * @author wqz
 * 
 */
public class ResponseResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private String code;

	private String message;

	public ResponseResult() {
	}

	public ResponseResult(String code, String info) {
		this.code = code;
		this.message = info;
	}

	public String getStatus() {
		return code;
	}

	public void setStatus(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
