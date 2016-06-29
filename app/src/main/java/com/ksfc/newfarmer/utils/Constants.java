package com.ksfc.newfarmer.utils;

import com.ksfc.newfarmer.activitys.HomepageActivity;
import com.ksfc.newfarmer.activitys.MineActivity;
import com.ksfc.newfarmer.activitys.NewFarmerInfomationActivity;
import com.ksfc.newfarmer.activitys.ShoppingCartActivity;

/**
 * 常量类
 *
 * @author Bruce.Wang
 */
public interface Constants {

    String TAB_LIST[] = {"主页", "资讯", "购物车", "我的"};

    Class mHomeTabClassArray[] = {HomepageActivity.class,
            NewFarmerInfomationActivity.class, ShoppingCartActivity.class,
            MineActivity.class};

}
