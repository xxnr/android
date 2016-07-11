package com.ksfc.newfarmer.http.RxApi;

import com.ksfc.newfarmer.http.beans.AttrSelectResult;
import com.ksfc.newfarmer.http.beans.GetGoodsData;
import com.ksfc.newfarmer.http.beans.MyOrderDetailResult;
import com.ksfc.newfarmer.http.beans.RscOrderDetailResult;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by CAI on 2016/6/30.
 */
public interface ApiService {

    /**
     * 商品列表
     */
    @Headers("Content-Type: application/json")
    @POST("/api/v2.1/product/getProductsListPage")
    Observable<GetGoodsData> GET_GOODS(
            @Body Map<String, Object> map);

    /**
     * 商品相关 获取商品属性列表
     */

    @GET("/api/v2.1/products/attributes")
    Observable<AttrSelectResult> GET_GOODS_ATTR(
            @Query("brand") String brand,
            @Query("category") String category);

    /**
     * 用户订单详情
     */
    @FormUrlEncoded
    @POST("/api/v2.0/order/getOrderDetails")
    Observable<MyOrderDetailResult> GET_ORDER_DETAILS(
            @Field("token") String token,
            @Field("orderId") String orderId);

    /**
     * RSC订单详情
     */
    @GET("/api/v2.2/RSC/orderDetail/")
    Observable<RscOrderDetailResult> GET_RSC_ORDER_Detail(
            @Query("token") String token,
            @Query("orderId") String orderId);


}
