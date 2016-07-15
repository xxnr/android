package com.ksfc.newfarmer;

import com.ksfc.newfarmer.http.ApiType;

/**
 * 广播ID
 *
 * @author Bruce.Wang
 */
public interface MsgID {

    String IP = ApiType.url;
    String ISLOGIN = "islogin";// 用户登陆了
    String UPDATE_USER = "update";// 用户信息更新
    String CLEAR_USER = "clear";// 用户信息删除
    String UPDATE_USER_TYPE = "update_userType";//用户更改了类型 或者类型下的认证状态
    String rsc_swipe_reFlash = "rsc_swipe_reFlash";//rsc订单列表刷新
    String swipe_reFlash = "swipe_reFlash";//rsc订单列表刷新
    String MainActivity_select_tab = "MainActivity_select_tab";//MainActivity应选中的tab
    String MSG_Change_ADDRESS = "MSG.ADDRESS.CALL.BACK";//用户的收货地址更改
    String add_potential_success = "add_potential_success";//添加潜在客户成功
    String change_potential_success = "change_potential_success";//更新了潜在客户成功
    String MyaccountActivityFinish = "MyaccountActivityFinish";//修改密码后通知“我的”页面关闭
    String Pay_success = "Pay_success";//支付成功通知各个页面
    String Rsc_order_Change = "Rsc_order_Change";//RSC订单在详情改变时列表页刷新
    String order_Change = "order_Change";//用户订单在详情改变时列表页刷新
    String PAY_PRICE = "PAY_PRICE";//银联的回调通知支付页Activity
    String Integral_Guide_Change = "Integral_Guide_change";//浮层引导页切换通知
    String IS_Signed = "IS_Signed";//用户签到通知
    String gift_swipe_reFlash = "gift_swipe_reFlash";//gift订单列表刷新
    String rsc_gift_swipe_reFlash = "rsc_gift_swipe_reFlash";//Rsc_gift订单列表刷新

}
