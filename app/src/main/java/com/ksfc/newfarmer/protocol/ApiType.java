package com.ksfc.newfarmer.protocol;

import com.ksfc.newfarmer.beans.AddGiftOrderResult;
import com.ksfc.newfarmer.beans.AddOrderResult;
import com.ksfc.newfarmer.beans.AddressList;
import com.ksfc.newfarmer.beans.AlipayResult;
import com.ksfc.newfarmer.beans.AppUpgrade;
import com.ksfc.newfarmer.beans.AttrSelectResult;
import com.ksfc.newfarmer.beans.BrandsResult;
import com.ksfc.newfarmer.beans.BuildingList;
import com.ksfc.newfarmer.beans.CameraResult;
import com.ksfc.newfarmer.beans.CampaignDetailResult;
import com.ksfc.newfarmer.beans.CampaignListResult;
import com.ksfc.newfarmer.beans.CityList;
import com.ksfc.newfarmer.beans.ClassIDResult;
import com.ksfc.newfarmer.beans.ConsigneeResult;
import com.ksfc.newfarmer.beans.ConsumerOrderResult;
import com.ksfc.newfarmer.beans.CustomerIsLatestResult;
import com.ksfc.newfarmer.beans.DeliveriesResult;
import com.ksfc.newfarmer.beans.DeliveryCodeResult;
import com.ksfc.newfarmer.beans.EposPayResult;
import com.ksfc.newfarmer.beans.EvaluateList;
import com.ksfc.newfarmer.beans.GetGoodsData;
import com.ksfc.newfarmer.beans.GetGoodsDetail;
import com.ksfc.newfarmer.beans.GetshopCart;
import com.ksfc.newfarmer.beans.GiftCategoriesResult;
import com.ksfc.newfarmer.beans.GiftDetailResult;
import com.ksfc.newfarmer.beans.GiftListResult;
import com.ksfc.newfarmer.beans.GiftOrderListResult;
import com.ksfc.newfarmer.beans.HomeImageResult;
import com.ksfc.newfarmer.beans.InformationResult;
import com.ksfc.newfarmer.beans.IntegralGetResult;
import com.ksfc.newfarmer.beans.IntentProductsResult;
import com.ksfc.newfarmer.beans.IntentionProductsResult;
import com.ksfc.newfarmer.beans.IsPotentialCustomerResult;
import com.ksfc.newfarmer.beans.LoginResult;
import com.ksfc.newfarmer.beans.MinPayPriceResult;
import com.ksfc.newfarmer.beans.MyInviterResult;
import com.ksfc.newfarmer.beans.MyOrderDetailResult;
import com.ksfc.newfarmer.beans.NominatedInviterResult;
import com.ksfc.newfarmer.beans.OfflinePayWayResult;
import com.ksfc.newfarmer.beans.Payback;
import com.ksfc.newfarmer.beans.PersonalData;
import com.ksfc.newfarmer.beans.PointLogsResult;
import com.ksfc.newfarmer.beans.PointResult;
import com.ksfc.newfarmer.beans.PotentialCustomerDetailResult;
import com.ksfc.newfarmer.beans.PublicKeyResult;
import com.ksfc.newfarmer.beans.QueueList;
import com.ksfc.newfarmer.beans.RSCInfoResult;
import com.ksfc.newfarmer.beans.RSCAddressListResult;
import com.ksfc.newfarmer.beans.RSCStateInfoResult;
import com.ksfc.newfarmer.beans.RemainGoodsAttr;
import com.ksfc.newfarmer.beans.RscGiftOrderListResult;
import com.ksfc.newfarmer.beans.RscOrderDetailResult;
import com.ksfc.newfarmer.beans.RscOrderResult;
import com.ksfc.newfarmer.beans.SaveAdressList;
import com.ksfc.newfarmer.beans.ShareAddPointsResult;
import com.ksfc.newfarmer.beans.SmsResult;
import com.ksfc.newfarmer.beans.TownList;
import com.ksfc.newfarmer.beans.UnionPayResponse;
import com.ksfc.newfarmer.beans.WaitingPay;
import com.ksfc.newfarmer.beans.saveAddress;
import com.ksfc.newfarmer.beans.dbbeans.InviteeResult;
import com.ksfc.newfarmer.beans.dbbeans.PotentialListResult;

/**
 * API 请求的类型
 *
 * @author wqz
 */
public enum ApiType {

    /**
     * 获取用户白名单
     */
    fGET_WRITE_LIST("/api/v2.0/user/isInWhiteList", ResponseResult.class),

    /**
     * 银联
     */
    GET_UNI("/unionpay", UnionPayResponse.class),
    /**
     * 支付宝
     */
    GET_ALI("/alipay", AlipayResult.class),
    /**
     * 化肥
     */
    GET_GOODS("/api/v2.1/product/getProductsListPage", GetGoodsData.class),
    /**
     * 头像上传
     */
    UP_HEAD_IMG("/api/v2.0/user/uploadPortrait", CameraResult.class),

    /**
     * 首页轮播图
     */
    GETHOMEPIC("/api/v2.0/ad/getAdList", HomeImageResult.class),
    /**
     * 首页签到
     */
    SIGN_IN_POINT("/api/v2.0/user/sign", PointResult.class),
    /**
     * 找回密码
     */
    FIND_PASSWORD("/api/v2.0/user/resetpwd", ResponseResult.class),
    /**
     * 修改密码
     */
    CHANGE_PASSWORD("/api/v2.0/user/modifypwd", ResponseResult.class),
    /**
     * 短信验证
     */
    SEND_SMS("/api/v2.3/sms", SmsResult.class),
    /**
     * 刷新图形验证码
     */
    REFRESH_SMS_CODE("/api/v2.3/captcha", SmsResult.class),

    /**
     * 注册
     */
    REGISTER("/api/v2.0/user/register", LoginResult.class),
    /**
     * 登陆GF
     */
    LOGIN("/api/v2.0/user/login", LoginResult.class),
    /**
     * 商品详情
     */
    GET_GOOD_DETAIL("/api/v2.0/product/getAppProductDetails", GetGoodsDetail.class),
    /**
     * 根据所选SKU属性获取价格区间和剩余可选SKU
     */
    GET_GOOD_ATTR("/api/v2.1/SKU/attributes_and_price/query", RemainGoodsAttr.class),
    /**
     * 获取购物车信息
     */
    GET_SHOPCART_LIST("/api/v2.1/cart/getShoppingCart", GetshopCart.class),
    /**
     * 获取本地购物车信息
     */
    GET_LOCAL_SHOPCART_LIST("/api/v2.1/cart/getShoppingCartOffline",
            GetshopCart.class),
    /**
     * 省列表接口
     */
    FINDAREALIST("/api/v2.0/area/getAreaList", CityList.class),
    /**
     * 对应城市列表接口
     */
    QUERYBYAREAID("/api/v2.0/businessDistrict/getBusinessByAreaId",
            QueueList.class),
    /**
     * 对应县区列表接口
     */
    QUERYBYBUSINESSID("/api/v2.0/build/getBuildByBusiness", BuildingList.class),
    /**
     * 对应乡镇列表接口
     */
    QUERYTOWNID("/api/v2.0/area/getAreaTown", TownList.class),


    /**
     * 保存用户地址
     */
    SAVEORUPDATE("/app/buildUser/saveAddress", SaveAdressList.class),
    /**
     * 我的评价
     */
    MY_EVALUATE("/app/comment/MyJudgeList", EvaluateList.class),
    /**
     * 订单详情
     */
    GET_ORDER_DETAILS("/api/v2.0/order/getOrderDetails",
            MyOrderDetailResult.class),
    /**
     * 确认收货接口
     */
    GET_ORDER_CONFIRM("/api/v2.0/order/confirmeOrder", ResponseResult.class),
    /**
     * 我的订单列表
     */
    GETORDERLIST("/api/v2.0/order/getAppOrderList", WaitingPay.class),
    /**
     * 购物车修改数目
     */
    CHANGE_NUM("/api/v2.1/cart/changeNum", ResponseResult.class),
    /**
     * 修改个人信息
     */
    UPDATE_MYUSER("/app/user/updateMyUser", ResponseResult.class),
    /**
     * 保存个人信息
     */
    SAVE_MYUSER("/api/v2.0/user/modify", ResponseResult.class),
    /**
     * 首页添加到购物车
     */
    ADDTOCART("/api/v2.1/cart/addToCart", ResponseResult.class),
    /**
     * 生成订单
     */
    ADD_ORDER("/api/v2.1/order/addOrder", AddOrderResult.class),
    /**
     * 获取个人信息
     */
    PERSONAL_CENTER("/api/v2.0/user/get", PersonalData.class),
    /**
     * 增加地址
     */
    SAVE_ADDRESS("/api/v2.0/user/saveUserAddress", saveAddress.class),
    /**
     * 获取地址列表
     */
    ADDRESS_LIST("/api/v2.0/user/getUserAddressList", AddressList.class),
    /**
     * 地址列表中删除地址
     */
    DELETE_ADDRESS("/api/v2.0/user/deleteUserAddress", ResponseResult.class),
    /**
     * 地址列表中修改地址
     */
    UPDATE_ADDRESS("/api/v2.0/user/updateUserAddress", ResponseResult.class),
    /**
     * 选择配送地址接口
     */
    SELECT_ADDRESS("/app/order/addBuildingUserId", ResponseResult.class),
    /**
     * 版本更新接口
     */
    LATEST_VERSION("/app/version/latestVersion", ResponseResult.class),
    /**
     * 用户相关 支付完成回调接口
     */
    PAY_BACK("/app/order/payNotify", Payback.class),
    /**
     * 用户相关 获取密码加密公钥
     */
    GET_PUBLIC_KEY("/api/v2.0/user/getpubkey", PublicKeyResult.class),
    /**
     * 用户相关 邀请好友列表
     */
    GET_INVITEE("/api/v2.0/user/getInvitee", InviteeResult.class),
    /**
     * 用户相关 我的邀请人
     */
    GET_BINDINVITER("/api/v2.0/user/bindInviter", ResponseResult.class),
    /**
     * 新农资讯
     */
    GET_INFORMATION("/api/v2.0/news", InformationResult.class),
    /**
     * 商品相关 化肥或者汽车的classID
     */
    GET_CLASSID("/api/v2.0/products/categories", ClassIDResult.class),
    /**
     * 订单相关 更新订单支付方式
     */
    GET_UPDATPAYWAY("/api/v2.0/order/updateOrderPaytype", ResponseResult.class),
    /**
     * 用户相关 查找有无此用户
     */
    FIND_USER("/api/v2.0/user/findAccount/", ResponseResult.class),
    /**
     * 用户相关 选择用户类型
     */
    USER_TYPE("/api/v2.0/usertypes", ResponseResult.class),
    /**
     * 订单相关 客户订单
     */
    GET_INVITEE_ORDERS("/api/v2.0/user/getInviteeOrders", ConsumerOrderResult.class),
    /**
     * 商品相关 获取商品属性列表
     */
    GET_GOODS_ATTR("/api/v2.1/products/attributes", AttrSelectResult.class),

    /**
     * 筛选相关 获取品牌列表
     */
    GET_BRANDS_LIST("/api/v2.1/brands", BrandsResult.class),

    /**
     * 用户报备 获取意向商品列表
     */
    GET_PURPOSE_GOODS_LIST("/api/v2.1/intentionProducts", IntentionProductsResult.class),
    /**
     * 用户报备 获取意向商品列表
     */
    GET_INTENT_PRODUCTS("/api/v2.3/intentionProducts", IntentProductsResult.class),

    /**
     * 用户报备 判断手机号是否能添加为潜在客户
     */
    IS_POTENTIAL_CUSTOMER("/api/v2.1/potentialCustomer/isAvailable", IsPotentialCustomerResult.class),
    /**
     * 用户报备 添加潜在客户
     */
    ADD_POTENTIAL_CUSTOMER("/api/v2.1/potentialCustomer/add", ResponseResult.class),
    /**
     * 用户报备 获取潜在客户详情
     */
    GET_POTENTIAL_CUSTOMER_DETAIL("/api/v2.1/potentialCustomer/get", PotentialCustomerDetailResult.class),
    /**
     * 用户相关 获取推荐的新农代表:
     */
    GET_RECOMMEND_INVITER("/api/v2.1/user/getNominatedInviter", NominatedInviterResult.class),

    /**
     * 用户相关 我的客户:
     */
    GET_MY_INVITER("/api/v2.0/user/getInviter", MyInviterResult.class),
    /**
     * 订单相关 获取最小支付金额:
     */
    GET_MIN_PAY_PRICE("/api/v2.0/getMinPayPrice/", MinPayPriceResult.class),
    /**
     * 县级经销商 认证县级经销商:
     */
    ADD_RSC_INFO("/api/v2.2/RSC/info/fill", ResponseResult.class),

    /**
     * 县级经销商 查看认证信息:
     */
    GET_RSC_INFO("/api/v2.2/RSC/info/get", RSCInfoResult.class),

    /**
     * 订单配送 获取配送方式:
     */
    GET_DELIVERIES("/api/v2.2/cart/getDeliveries", DeliveriesResult.class),


    /**
     * 订单配送 获取自提网点:
     */
    GET_RSC_STATE_INFO("/api/v2.2/RSC", RSCStateInfoResult.class),

    /**
     * 订单配送 获取自提地区（省）:
     */
    GET_RSC_ADDRESS_PROVINCE("/api/v2.2/RSC/address/province", RSCAddressListResult.class),

    /**
     * 订单配送 获取自提地区（市）:
     */
    GET_RSC_ADDRESS_CITY("/api/v2.2/RSC/address/city", RSCAddressListResult.class),
    /**
     * 订单配送 获取自提地区（县）:
     */
    GET_RSC_ADDRESS_COUNTY("/api/v2.2/RSC/address/county", RSCAddressListResult.class),

    /**
     * 订单配送 获取自提地区（区镇）:
     */
    GET_RSC_ADDRESS_TOWN("/api/v2.2/RSC/address/town", RSCAddressListResult.class),

    /**
     * 用户选择 自提方式时保存收货人信息:
     */
    SAVE_CONSIGNEE_INFO("/api/v2.2/user/saveConsignees", ResponseResult.class),

    /**
     * 用户选择自提方式时 获取收货人列表信息:
     */
    GET_CONSIGNEE_INFO("/api/v2.2/user/queryConsignees", ConsigneeResult.class),

    /**
     * 订单相关 线下支付:
     */
    OFFLINE_PAY("/offlinepay", ResponseResult.class),

    /**
     * 订单相关 Epos支付:
     */
    EPOS_PAY("/EPOSpay", EposPayResult.class),

    /**
     * 订单相关 线下支付方式:
     */
    GET_OFFLINE_PAY_WAY("/api/v2.2/getOfflinePayType", OfflinePayWayResult.class),

    /**
     * 订单相关 获取自提码:
     */
    GET_DELIVERY_CODE("/api/v2.2/order/getDeliveryCode", DeliveryCodeResult.class),

    /**
     * 订单相关 确认收货:
     */
    SURE_GET_GOODS("/api/v2.2/order/confirmSKUReceived", ResponseResult.class),

    /**
     * RSC 订单列表:
     */
    GET_RSC_ORDER_LIST("/api/v2.2/RSC/orders", RscOrderResult.class),
    /**
     * RSC 订单详情:
     */
    GET_RSC_ORDER_Detail("/api/v2.2/RSC/orderDetail/", RscOrderDetailResult.class),

    /**
     * RSC 审核付款:
     */
    CONFIRM_OFFLINE_PAY("/api/v2.2/RSC/confirmOfflinePay", ResponseResult.class),

    /**
     * RSC 网点发货:
     */
    RSC_ORDER_DELIVERING("/api/v2.2/RSC/order/deliverStatus/delivering", ResponseResult.class),
    /**
     * RSC 网点自提:
     */
    RSC_ORDER_SELF_DELIVERY("/api/v2.2/RSC/order/selfDelivery", ResponseResult.class),

    /**
     * 用户报备: 列表是否需要更新今天剩余报备人数
     */
    GET_POTENTIAL_CUSTOMER_ISLATEST("/api/v2.1/potentialCustomer/isLatest", CustomerIsLatestResult.class),

    /**
     * 用户报备：按姓名pinyin排序获取全部报备用户列表
     */
    GET_POTENTIAL_CUSTOMER_LIST_NEW("/api/v2.1/potentialCustomer/queryAllOrderbyName", PotentialListResult.class),


    /**
     * 用户相关：获取新农代表的客户列表，按拼音字母排序:
     */
    GET_INVITEE_ORDER_BY_NAME("/api/v2.0/user/getInviteeOrderbyName", InviteeResult.class),

    /**
     * 版本升级：获取最新版本和升级地址:
     */
    APP_UP_GRADE("/api/v2.1/AppUpgrade", AppUpgrade.class),
    /**
     * 积分商城：获得积分:
     */
    GET_INTEGRAL("/api/v2.3/rewardshop/get", IntegralGetResult.class),

    /**
     * 积分商城：礼品分类:
     */
    GET_GIFT_CATEGORIES("/api/v2.3/rewardshop/gifts/categories", GiftCategoriesResult.class),
    /**
     * 积分商城：礼品列表:
     */
    GET_GIFT_LIST("/api/v2.3/rewardshop/gifts", GiftListResult.class),

    /**
     * 积分商城：礼品详情:
     */
    GET_GIFT_DETAIL("/api/v2.3/rewardshop/gifts/getGiftDetail", GiftDetailResult.class),

    /**
     * 积分商城：我的积分记录:
     */
    GET_POINTS_LOGS("/api/v2.3/rewardshop/pointslogs", PointLogsResult.class),
    /**
     * 积分商城：提交兑换:
     */
    ADD_GIFT_ORDER("/api/v2.3/rewardshop/addGiftOrder", AddGiftOrderResult.class),

    /**
     * 积分商城：礼品订单列表:
     */
    GET_GIFT_ORDER_LIST("/api/v2.3/rewardshop/getGiftOrderList", GiftOrderListResult.class),
    /**
     * Rsc订单：兑换订单列表:
     */
    GET_RSC_GIFT_ORDER_LIST("/api/v2.3/RSC/rewardshop/getGiftOrderList", RscGiftOrderListResult.class),
    /**
     * Rsc订单：兑换自提:
     */
    GET_RSC_GIFT_ORDER_SELF_DELIVERY("/api/v2.3/RSC/rewardshop/order/selfDelivery", ResponseResult.class),

    /**
     * 活动：获取所有上线活动:
     */
    GET_CAMPAIGNS("/api/v2.3/campaigns", CampaignListResult.class),

    /**
     * 活动：获取app分享详情:
     */
    GET_APP_SHARE_INFO("/api/v2.3/campaign/app_share_info", CampaignDetailResult.class),

    /**
     * 分享：用户分享加积分:
     */
    SHARE_ADD_POINTS("/api/v2.3/user/shareAddPoints", ShareAddPointsResult.class),

    /**
     * Host:
     */
    TEST("", ResponseResult.class);

//        private static String server_url = "http://api.xinxinnongren.com";
    private static String server_url = "http://ppe.xinxinnongren.com";
//    private static String server_url = "http://192.168.0.6:8070";
//    private static String server_url = "http://192.168.1.21:8070";


    public static final String url = server_url;
    private String path;
    private Class<? extends ResponseResult> clazz;
    private RequestMethod requestMethod = RequestMethod.POST;


    ApiType(String path, Class<? extends ResponseResult> clazz) {
        this.path = path;
        this.clazz = clazz;
    }

    public ApiType setMethod(RequestMethod method) {
        requestMethod = method;
        return this;
    }

    public String getPath() {
        return path;
    }

    public String getOpt() {
        return server_url + path;
    }

    public Class<? extends ResponseResult> getClazz() {
        return clazz;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public enum RequestMethod {
        POST, GET, PUT, POSTJSON
    }
}
