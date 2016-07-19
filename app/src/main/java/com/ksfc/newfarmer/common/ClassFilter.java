package com.ksfc.newfarmer.common;

import com.ksfc.newfarmer.activitys.FloatingLayerActivity;
import com.ksfc.newfarmer.activitys.GoodsListActivity;
import com.ksfc.newfarmer.activitys.HomepageActivity;
import com.ksfc.newfarmer.activitys.MineActivity;
import com.ksfc.newfarmer.activitys.NewFarmerInfomationActivity;
import com.ksfc.newfarmer.activitys.ShoppingCartActivity;
import com.ksfc.newfarmer.protocol.ApiType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CAI on 2016/6/3.
 */
public class ClassFilter {
    /**
     * 不设置状态栏颜色的activity
     *
     * @return
     */
    public static List<String> getunSetStatusBarClasses() {

        List<String> unSetStatusBar = new ArrayList<>();
        unSetStatusBar.add(HomepageActivity.class.getSimpleName());
        unSetStatusBar.add(MineActivity.class.getSimpleName());
        unSetStatusBar.add(ShoppingCartActivity.class.getSimpleName());
        unSetStatusBar.add(NewFarmerInfomationActivity.class.getSimpleName());
        unSetStatusBar.add(FloatingLayerActivity.class.getSimpleName());

        return unSetStatusBar;
    }

    /**
     * 超时不提示的activity
     *
     * @return
     */
    public static List<String> getTimeOutUnToastClasses() {

        List<String> unApiToastList = new ArrayList<>();
        unApiToastList.add(HomepageActivity.class.getSimpleName());
        unApiToastList.add(GoodsListActivity.class.getSimpleName());
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
        UnToastApis.add(ApiType.GET_INVITEE_ORDER_BY_NAME);
        UnToastApis.add(ApiType.GET_POTENTIAL_CUSTOMER_LIST_NEW);

        return UnToastApis;
    }

}
