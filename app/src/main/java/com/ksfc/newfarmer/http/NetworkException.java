package com.ksfc.newfarmer.http;

public class NetworkException extends Exception {

	private static final long serialVersionUID = 8740567601464736005L;
	public static final int UNKNOW_STATUS = -1;
	private int mNetworkStatus = UNKNOW_STATUS;

	public NetworkException(Exception e) {
		super(e);
	}

	public NetworkException(int httpStatus, String message) {
		super(message);
		mNetworkStatus = httpStatus;
	}

	public int getNetworkStatus() {
		return mNetworkStatus;
	}
}
