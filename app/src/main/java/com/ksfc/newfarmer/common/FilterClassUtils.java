package com.ksfc.newfarmer.common;

import com.ksfc.newfarmer.http.ApiType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CAI on 2016/6/3.
 */
public class FilterClassUtils {
    /**
     * 不设置状态栏颜色的activity
     *
     * @return
     */
    public static List<String> getunSetStatusBarClasses() {

        List<String> unSetStatusBar = new ArrayList<>();
        unSetStatusBar.add("HomepageActivity");
        unSetStatusBar.add("MineActivity");
        unSetStatusBar.add("ShoppingCartActivity");
        unSetStatusBar.add("NewFarmerInfomationActivity");
        unSetStatusBar.add("QiandaoActivity");
        unSetStatusBar.add("FloatingLayerActivity");

        return unSetStatusBar;
    }

    /**
     * 超时不提示的activity
     *
     * @return
     */
    public static List<String> getTimeOutUnToastClasses() {

        List<String> unApiToastList = new ArrayList<>();
        unApiToastList.add("HomepageActivity");
        unApiToastList.add("GoodsListActivity");
        return unApiToastList;
    }

    /**
     * 错误不提示的api
     *
     * @return
     */
    public static List<ApiType> getUnToastApis() {
        List<ApiType> UnToastApis = new ArrayList<>();
        UnToastApis.add(ApiType.GET_MIN_PAY_PRICE);
        UnToastApis.add(ApiType.SAVE_CONSIGNEE_INFO);
        UnToastApis.add(ApiType.SURE_GET_GOODS);
        UnToastApis.add(ApiType.GET_RECOMMEND_INVITER);

        return UnToastApis;
    }

}
