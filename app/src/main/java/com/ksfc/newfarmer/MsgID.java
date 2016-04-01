package com.ksfc.newfarmer;

import com.ksfc.newfarmer.protocol.ApiType;

/**
 * 广播ID
 * 
 * @author Bruce.Wang
 * 
 */
public interface MsgID {

	String IP = ApiType.url;
	String IPUphead = ApiType.url + "/app/res/show/";
	String EXIT = "app.exit";// 程序退出
	String LOGIN = "user.login";

	String CHANGE = "discount.change";// 选择的商品发生改变时
	String GOODS_CHANGE = "ordering.change";// 选择的商品发生改变时

	String NO_BILL = "billinfo.noBill";// 不需要发票
	String NEED_BILL = "billinfo.needBill";// 需要发票
	String ADD_BILL = "billinfo.addBill";// 添加发票

	String ORDERING_HANDONG = "ordering.huadong";
	String ISLOGIN = "islogin";// 用户登陆了
	String LOGININ = "login in";// 用户登陆了
	String UPDATE_USER = "update";// 用户登陆了
	String CLEAR_USER = "clear";// 用户登陆了

    String UPDATE_USER_TYPE="update_userType";//用户更改了类型 或者类型下的认证状态
}
