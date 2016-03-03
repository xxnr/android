package net.yangentao.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

import java.net.HttpURLConnection;
import java.net.URL;

public class NetUtil {

    public static ConnectivityManager getConnectivityManager(Context c) {
        return (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private static WifiLock wifilock = null;

    /**
     * 使用Wifi连接, wifi锁
     */
    public static void startUseWifi(Context c) {
        WifiManager wifimanager = (WifiManager) c
                .getSystemService(Context.WIFI_SERVICE);
        if (wifimanager != null) {
            wifilock = wifimanager.createWifiLock(WifiManager.WIFI_MODE_FULL,
                    "meq");
            if (wifilock != null) {
                wifilock.acquire();
            }
        }
    }

    /**
     * 释放wifi锁
     */
    public static void stopUseWifi() {
        if (wifilock != null && wifilock.isHeld()) {
            wifilock.release();
        }
        wifilock = null;
    }

    /**
     * 是否存在已经连接上的网络, 不论是wifi/cmwap/cmnet还是其他
     *
     * @return
     */
    public static boolean isConnected(Context c) {
        NetworkInfo ni = getConnectivityManager(c).getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    /**
     * 　　* 判断链接是否有效
     * 　　* 输入链接
     * 　　* 返回true或者false
     */
    public static boolean isValid(String strLink) {
        URL url;
        try {
            url = new URL(strLink);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            String strMessage = connection.getResponseMessage();
            if (strMessage.compareTo("Not Found") == 0) {
                return false;
            }
            connection.disconnect();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
