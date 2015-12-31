package com.ksfc.newfarmer.utils;

import com.ksfc.newfarmer.activitys.HomepageActivity;
import com.ksfc.newfarmer.activitys.MineActivity;
import com.ksfc.newfarmer.activitys.NewFarmerInfomation;
import com.ksfc.newfarmer.activitys.ShoppingCartActivity1;

/**
 * 常量类
 * 
 * @author Bruce.Wang
 * 
 */
public interface Constants {

	public static String TAB_LIST[] = { "主页", "资讯", "购物车", "我的" };

	public static Class mHomeTabClassArray[] = { HomepageActivity.class,
			NewFarmerInfomation.class, ShoppingCartActivity1.class,
			MineActivity.class };

}
