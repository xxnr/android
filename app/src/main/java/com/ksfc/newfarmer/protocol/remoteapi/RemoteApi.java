package com.ksfc.newfarmer.protocol.remoteapi;

import android.provider.Settings;

import com.google.gson.Gson;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.BaseFragment;
import com.ksfc.newfarmer.beans.LoginResult;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.Utils;
import com.umeng.message.PushAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CAI on 2016/6/21.
 */
public class RemoteApi {
    /**
     * app是否需要升级
     *
     * @param activity
     */
    public static void appIsNeedUpdate(BaseActivity activity) {

        RequestParams params = new RequestParams();
        String versionInfo = Utils.getVersionInfo(activity);
        PushAgent pushAgent = PushAgent.getInstance(activity);
        String device_token = pushAgent.getRegistrationId();
        if (StringUtil.checkStr(versionInfo)) {
            params.put("version", versionInfo);
        }
        if (StringUtil.checkStr(device_token)) {
            params.put("device_token", device_token);
        }
        String m_szAndroidID = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (StringUtil.checkStr(m_szAndroidID)) {
            params.put("device_id", m_szAndroidID);
        } else {
            params.put("device_id", device_token);
        }
        params.put("user_agent", "android");
        activity.execApi(ApiType.APP_UP_GRADE, params);
    }

    /**
     * 获取首页banner图
     */
    public static void getBanner(BaseActivity activity) {
        // 获取首页轮播图
        RequestParams params = new RequestParams();
        activity.execApi(ApiType.GETHOMEPIC, params);
    }

    /**
     * 获取首页分类id
     */
    public static void getClassId(BaseActivity activity) {
        RequestParams params = new RequestParams();
        activity.execApi(ApiType.GET_CLASSID.setMethod(ApiType.RequestMethod.GET), params);
    }

    /**
     * 获得用户积分
     */
    public static void getIntegral(BaseActivity activity) {
        RequestParams params = new RequestParams();
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            params.put("userId", userInfo.userid);
        }
        activity.execApi(ApiType.GET_INTEGRAL.setMethod(ApiType.RequestMethod.GET), params);
    }

    /**
     * 用户签到
     */
    public static void sign(BaseActivity activity) {
        if (activity.isLogin()) {
            activity.showProgressDialog("请稍后...");
            RequestParams params = new RequestParams();
            params.put("userId", Store.User.queryMe().userid);
            activity.execApi(ApiType.SIGN_IN_POINT, params);
        } else {
            activity.showToast("您还未登录哦，请登录后签到");
        }
    }

    /**
     * 获得礼品详情
     *
     * @param id 礼品id
     */
    public static void getGiftDetail(BaseActivity activity, String id) {
        if (StringUtil.checkStr(id)) {
            activity.showProgressDialog();
            RequestParams params = new RequestParams();
            params.put("id", id);
            activity.execApi(ApiType.GET_GIFT_DETAIL.setMethod(ApiType.RequestMethod.GET), params);
        }
    }

    /**
     * 获取历史联系人
     */
    public static void getConsignees(BaseActivity activity) {
        RequestParams params = new RequestParams();
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            params.put("userId", userInfo.userid);
        }
        activity.execApi(ApiType.GET_CONSIGNEE_INFO.setMethod(ApiType.RequestMethod.GET), params);
    }


    /**
     * 提交礼品订单
     *
     * @param activity
     * @param deliveryType
     * @param RSC_Id
     * @param consigneeName
     * @param consigneePhone
     * @param giftId
     */
    public static void addGiftOrder(BaseActivity activity, int deliveryType, String RSC_Id, String consigneeName, String consigneePhone, String giftId) {

        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("deliveryType", deliveryType);
        map.put("RSCId", RSC_Id);
        map.put("consigneeName", consigneeName);
        map.put("consigneePhone", consigneePhone);
        map.put("giftId", giftId);
        if (activity.isLogin()) {
            map.put("token", Store.User.queryMe().token);
        }
        String jsonString = gson.toJson(map);
        RequestParams params = new RequestParams();
        params.put("JSON", jsonString);
        activity.execApi(ApiType.ADD_GIFT_ORDER.setMethod(ApiType.RequestMethod.POSTJSON), params);
    }

    /**
     * 获取礼品分类
     */
    public static void getGiftCategories(BaseActivity activity) {
        activity.execApi(ApiType.GET_GIFT_CATEGORIES.setMethod(ApiType.RequestMethod.GET), new RequestParams());
    }

    /**
     * 请求gift列表
     */
    public static void getGiftList(BaseActivity activity, String categoriesBean_id) {
        RequestParams params = new RequestParams();
        params.put("category", categoriesBean_id);
        activity.execApi(ApiType.GET_GIFT_LIST.setMethod(ApiType.RequestMethod.GET), params);
    }


    /**
     * 请求gift订单列表
     *
     * @param type 请求订单列表类型 1 未完成 2 已完成
     * @param page 请求第几页
     */
    public static void getGiftOrderList(BaseFragment fragment, int type, int page) {
        RequestParams params = new RequestParams();
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            params.put("userId", userInfo.userid);
        }
        params.put("type", type);
        params.put("page", page);
        fragment.execApi(ApiType.GET_GIFT_ORDER_LIST.setMethod(ApiType.RequestMethod.GET), params);
    }


    /**
     * 获取积分列表
     */
    public static void getPointsLogs(BaseActivity activity, int page) {
        RequestParams params = new RequestParams();
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            params.put("userId", userInfo.userid);
        }
        params.put("page", page);
        activity.execApi(ApiType.GET_POINTS_LOGS.setMethod(ApiType.RequestMethod.GET), params);
    }


    /**
     * 请求Rsc gift订单列表
     *
     * @param search 请求订单列表类型 1 未完成 2 已完成
     * @param page 请求第几页
     */
    public static void getRscGiftOrderList(BaseActivity activity, String search, int page) {
        RequestParams params = new RequestParams();
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            params.put("userId", userInfo.userid);
        }
        params.put("search", search);
        params.put("page", page);
        activity.execApi(ApiType.GET_RSC_GIFT_ORDER_LIST.setMethod(ApiType.RequestMethod.GET), params);
    }

    public static void getRscGiftOrderList(BaseFragment fragment, int type, int page) {
        RequestParams params = new RequestParams();
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            params.put("userId", userInfo.userid);
        }
        params.put("type", type);
        params.put("page", page);
        fragment.execApi(ApiType.GET_RSC_GIFT_ORDER_LIST.setMethod(ApiType.RequestMethod.GET), params);
    }

    /**
     * 请求Rsc 用户自提
     *
     * @param orderId  订单号
     * @param code  自提码
     */
    public static void rscSelfDelivery(BaseFragment fragment, String orderId, String code) {
        RequestParams params = new RequestParams();
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            params.put("userId", userInfo.userid);
        }
        params.put("orderId", orderId);
        params.put("code", code);
        fragment.execApi(ApiType.GET_RSC_GIFT_ORDER_SELF_DELIVERY, params);
    }

    public static void rscSelfDelivery(BaseActivity activity, String orderId, String code) {
        RequestParams params = new RequestParams();
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            params.put("userId", userInfo.userid);
        }
        params.put("orderId", orderId);
        params.put("code", code);
        activity.execApi(ApiType.GET_RSC_GIFT_ORDER_SELF_DELIVERY, params);
    }




}
