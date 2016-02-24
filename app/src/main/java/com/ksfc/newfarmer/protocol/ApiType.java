package com.ksfc.newfarmer.protocol;

import com.ksfc.newfarmer.protocol.beans.AboutUsResult;
import com.ksfc.newfarmer.protocol.beans.AddOrderResult;
import com.ksfc.newfarmer.protocol.beans.AddressList;
import com.ksfc.newfarmer.protocol.beans.AlipayResult;
import com.ksfc.newfarmer.protocol.beans.AttrSelectResult;
import com.ksfc.newfarmer.protocol.beans.BannerResult;
import com.ksfc.newfarmer.protocol.beans.BrandsResult;
import com.ksfc.newfarmer.protocol.beans.BrandsShaixuan;
import com.ksfc.newfarmer.protocol.beans.BuildingList;
import com.ksfc.newfarmer.protocol.beans.CameraResult;
import com.ksfc.newfarmer.protocol.beans.ChangeNum;
import com.ksfc.newfarmer.protocol.beans.CityList;
import com.ksfc.newfarmer.protocol.beans.ClassIDResult;
import com.ksfc.newfarmer.protocol.beans.CommentResult;
import com.ksfc.newfarmer.protocol.beans.ConsumerOrderResult;
import com.ksfc.newfarmer.protocol.beans.EvaluateList;
import com.ksfc.newfarmer.protocol.beans.FindPassResult;
import com.ksfc.newfarmer.protocol.beans.GetCodeResult;
import com.ksfc.newfarmer.protocol.beans.GetGoodsData;
import com.ksfc.newfarmer.protocol.beans.GetGoodsDetail;
import com.ksfc.newfarmer.protocol.beans.GetshopCart;
import com.ksfc.newfarmer.protocol.beans.HomeImageResult;
import com.ksfc.newfarmer.protocol.beans.HotLineResult;
import com.ksfc.newfarmer.protocol.beans.InformationResult;
import com.ksfc.newfarmer.protocol.beans.IntentionProductsResult;
import com.ksfc.newfarmer.protocol.beans.InviteeResult;
import com.ksfc.newfarmer.protocol.beans.IsPotentialCustomerResult;
import com.ksfc.newfarmer.protocol.beans.JifenData;
import com.ksfc.newfarmer.protocol.beans.LoginResult;
import com.ksfc.newfarmer.protocol.beans.MyInviteResult;
import com.ksfc.newfarmer.protocol.beans.MyOrderDetailResult;
import com.ksfc.newfarmer.protocol.beans.Payback;
import com.ksfc.newfarmer.protocol.beans.PersonalData;
import com.ksfc.newfarmer.protocol.beans.PointResult;
import com.ksfc.newfarmer.protocol.beans.PotentialCustomerDetailResult;
import com.ksfc.newfarmer.protocol.beans.PotentialListResult;
import com.ksfc.newfarmer.protocol.beans.ProFileResult;
import com.ksfc.newfarmer.protocol.beans.PublicKeyResult;
import com.ksfc.newfarmer.protocol.beans.QueueList;
import com.ksfc.newfarmer.protocol.beans.RemainGoodsAttr;
import com.ksfc.newfarmer.protocol.beans.SaveAdressList;
import com.ksfc.newfarmer.protocol.beans.SureOrderResult;
import com.ksfc.newfarmer.protocol.beans.TownList;
import com.ksfc.newfarmer.protocol.beans.UnipayResult;
import com.ksfc.newfarmer.protocol.beans.WaitingPay;
import com.ksfc.newfarmer.protocol.beans.addtoCart;
import com.ksfc.newfarmer.protocol.beans.saveAddress;

/**
 * API 请求的类型
 *
 * @author wqz
 */
public enum ApiType {

    /**
     * 获取用户白名单
     */
    GET_WRITE_LIST("/api/v2.0/user/isInWhiteList", ResponseResult.class),

    /**
     * 银联
     */
    GET_UNI("/unionpay", UnipayResult.class),
    /**
     * 支付宝
     */
    GET_ALI("/alipay", AlipayResult.class),
    /**
     * 汽车
     */
    GET_NYC("/api/v2.1/product/getProductsListPage", GetGoodsData.class),
    /**
     * 化肥
     */
    GET_HUAFEI("/api/v2.1/product/getProductsListPage", GetGoodsData.class),
    /**
     * 订单评价详情
     */
    COMMENT_DETAIL("/app/comment/commentDetails", CommentResult.class),
    /**
     * 订单评价
     */
    PINGJIA("/app/comment/addGoodsComment", ResponseResult.class),
    /**
     * 获取400热线电话
     */
    GET_PROFILE_LIST("/app/profile/getProfileList", HotLineResult.class),
    /**
     * 银行账户等信息接口
     */
    GET_PROFILE_BANKLIST("/app/profile/getProfileBankList", ProFileResult.class),
    /**
     * 确认订单
     */
    SURE_ORDER("/app/order/affirmOrder", SureOrderResult.class),
    /**
     * 选择期望送货日期接口
     */
    HOPE_TIME("/app/order/addDeliveryTime", ResponseResult.class),
    /**
     * 填写订单备注接口
     */
    ADD_REMARKS_BY_ORDERID("/app/order/addRemarksByOrderId",
            ResponseResult.class),
    /**
     * 取消订单
     */
    ABOUT_US("/app/profile/findAboutUs", AboutUsResult.class),
    /**
     * 取消订单
     */
    CANCEL_ORDER("/app/order/cancelOrder", ResponseResult.class),
    /**
     * 头像上传
     */
    UP_HEAD_IMG("/api/v2.0/user/uploadPortrait", CameraResult.class),
    /**
     * 获取首页广告
     */
    GETINDEXPIC("/home/index/getIndexPic", BannerResult.class),
    /**
     * 批量上传购物车数据
     */
    SHOPPING_UPLOADING("/app/shopCart/addToCartBatch", BannerResult.class),
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
    FIND_PASSWORD("/api/v2.0/user/resetpwd", FindPassResult.class),
    /**
     * 修改密码
     */
    CHANGE_PASSWORD("/api/v2.0/user/modifypwd", FindPassResult.class),
    /**
     * 短信验证
     */
    SEND_SMS(null, GetCodeResult.class),
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
     * 签到积分
     */
    MY_JIFEN("/api/v2.0/point/findPointList", JifenData.class),
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
    CHANGE_NUM("/api/v2.1/cart/changeNum", ChangeNum.class),
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
    ADDTOCART("/api/v2.1/cart/addToCart", addtoCart.class),
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
     *    选择配送地址接口
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
    GET_BINDINVITER("/api/v2.0/user/bindInviter", MyInviteResult.class),
    /**
     * 新农资讯
     */
    GET_INFORMATION("/api/v2.0/news", InformationResult.class),
    /**
     * 筛选相关 商品类型筛选
     */
    GET_ATTRIBUTENAME(null, BrandsShaixuan.class),
    /**
     * 筛选相关 筛选后的商品列表
     */
    GET_SHAIXUAN_LIST(null, GetGoodsData.class),
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
     * 用户报备 判断手机号是否能添加为潜在客户
     */
    IS_POTENTIAL_CUSTOMER("/api/v2.1/potentialCustomer/isAvailable", IsPotentialCustomerResult.class),
    /**
     * 用户报备 添加潜在客户
     */
    ADD_POTENTIAL_CUSTOMER("/api/v2.1/potentialCustomer/add", ResponseResult.class),
    /**
     * 用户报备 获取潜在用户列表
     */
    GET_POTENTIAL_CUSTOMER_LIST("/api/v2.1/potentialCustomer/query", PotentialListResult.class),
    /**
     * 用户报备 获取潜在客户详情
     */
    GET_POTENTIAL_CUSTOMER_DETAIL("/api/v2.1/potentialCustomer/get", PotentialCustomerDetailResult.class),

    TEST("", ResponseResult.class);
         private static String server_url = "http://api.xinxinnongren.com";
//    private static String server_url = "http://101.200.194.203";
//     private static String server_url = "http://192.168.1.15";


    public static final String url = server_url + "/";
    private String opt;
    private Class<? extends ResponseResult> clazz;
    private RequestMethod requestMethod = RequestMethod.POST;
    private int retryNumber = 1;

    public ApiType setOpt(String opt) {
        this.opt = opt;
        return this;
    }

    public ApiType setMethod(RequestMethod method) {
        requestMethod = method;
        return this;
    }

    private ApiType(String opt, Class<? extends ResponseResult> clazz) {
        this.opt = opt;
        this.clazz = clazz;
    }

    private ApiType(String opt, Class<? extends ResponseResult> clazz,
                    String server_url) {
        this.opt = opt;
        this.clazz = clazz;
        // this.server_url = server_url;
    }

    private ApiType(String opt, RequestMethod requestMethod) {
        this.opt = opt;
        this.requestMethod = requestMethod;
    }

    private ApiType(String opt, RequestMethod requestMethod, int retryNumber) {
        this.opt = opt;
        this.requestMethod = requestMethod;
        this.retryNumber = retryNumber;
    }

    public String getOpt() {
        return server_url + opt;
    }

    public Class<? extends ResponseResult> getClazz() {
        return clazz;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public int getRetryNumber() {
        return retryNumber;
    }

    public enum RequestMethod {
        POST("POST"), GET("GET"), FILE("FILE"), POSTJSON("POSTJSON");
        private String requestMethodName;

        RequestMethod(String requestMethodName) {
            this.requestMethodName = requestMethodName;
        }

        public String getRequestMethodName() {
            return requestMethodName;
        }
    }
}
