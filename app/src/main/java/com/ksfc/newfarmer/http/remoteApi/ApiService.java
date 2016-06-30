package com.ksfc.newfarmer.http.remoteApi;

import com.ksfc.newfarmer.http.ResponseResult;
import com.ksfc.newfarmer.http.beans.AlipayResult;
import com.ksfc.newfarmer.http.beans.CameraResult;
import com.ksfc.newfarmer.http.beans.GetGoodsData;
import com.ksfc.newfarmer.http.beans.HomeImageResult;
import com.ksfc.newfarmer.http.beans.PointResult;
import com.ksfc.newfarmer.http.beans.UnionPayResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by CAI on 2016/6/30.
 */
public interface ApiService {
    /**
     * 获取用户白名单
     */
    @POST("/api/v2.0/user/isInWhiteList")
    Observable<ResponseResult> GET_WRITE_LIST(@Field("token") String token);

    /**
     * 银联
     */
    @FormUrlEncoded
    @POST("/unionpay")
    Observable<UnionPayResponse> GET_UNI(@FieldMap Map<String, String> options);

    /**
     * 支付宝
     */
    @FormUrlEncoded
    @POST("/alipay")
    Observable<AlipayResult> GET_ALI(@FieldMap Map<String, String> options);

    /**
     * 商品列表
     */
    @Headers("Content-Type: application/json")
    @POST("/api/v2.1/product/getProductsListPage")
    Observable<GetGoodsData> GET_GOODS(@Body Map<String,Object> map);

    /**
     * 头像上传
     */
    @Multipart
    @POST("/api/v2.0/user/uploadPortrait")
    Observable<CameraResult> UP_HEAD_IMG(@Part MultipartBody.Part file);

    /**
     * 首页轮播图
     */
    @GET("/api/v2.0/ad/getAdList")
    Observable<HomeImageResult> GETHOMEPIC();

    /**
     * 首页签到
     */
    @FormUrlEncoded
    @POST("/api/v2.0/user/sign")
    Observable<PointResult> SIGN_IN_POINT(@Field("token") String token);

    /**
     * 找回密码
     */
    @FormUrlEncoded
    @POST("/api/v2.0/user/resetpwd")
    Observable<ResponseResult> FIND_PASSWORD(@FieldMap Map<String, String> options);

    /**
     * 修改密码
     */
    @FormUrlEncoded
    @POST("/api/v2.0/user/modifypwd")
    Observable<ResponseResult> CHANGE_PASSWORD(@FieldMap Map<String, String> options);



}
