package net.yangentao.util.app;

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

    private static String imei;

    public static String imei() {
        if (imei == null) {
            TelephonyManager tm = (TelephonyManager) app
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getDeviceId();
        }
        return imei;
    }
}
