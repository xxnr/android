package com.ksfc.newfarmer.Push;

import com.ksfc.newfarmer.RndApplication;

import android.content.Context;
import android.telephony.TelephonyManager;

public class App {
	private static RndApplication app;

	public static void setApp(RndApplication application) {
		app = application;
	}

	public static RndApplication getApp() {
		return app;
	}

}
