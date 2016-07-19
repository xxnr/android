package com.ksfc.newfarmer.protocol;

import com.google.gson.Gson;
import com.ksfc.newfarmer.protocol.config.HttpsConfig;
import com.ksfc.newfarmer.utils.RndLog;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 服务器接口
 *
 * @author wqz
 */
public final class ServerInterface {

    private static final String TAG = "ServerInterface";
    private static Gson mGson;
    private NetworkHelper mHelper;

    public ServerInterface() {
        mGson = new Gson();
        mHelper = NetworkHelper.getInstance();
    }

    public ResponseResult request(ApiType api, RequestParams params)
            throws NetworkException {

        Response response = getResponseByApi(api, params);

        if (response != null) {// response == null   可能是无网络引起
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    try {
                        String json = responseBody.string();
                        RndLog.d(TAG, "ResponseResult. json.Result = " + json);
                        return parseJson(json, getJsonClassByApi(api));
                    } catch (Exception e) {
                        throw new NetworkException(e);
                    }
                }
                return null;
            } else {
                RndLog.e(TAG, "HTTP CODE :" + response.code());
                throw new NetworkException(response.code(), response.message());
            }
        } else {
            // 执行过程中产生异常
            return new ResponseResult(Request.HTTP_ERROR, "");
        }
    }

    /**
     * 修改为根据result类型返回结果
     *
     * @param json
     * @param clazz
     * @return
     */
    public static ResponseResult parseJson(String json, Class<? extends ResponseResult> clazz) {

        ResponseResult res;
        try {
            res = mGson.fromJson(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            res = new ResponseResult(Request.PARSE_DATA_FAILED, "");
        }
        return res;
    }

    /**
     * 通过参数返回http响应
     *
     * @param api
     * @param params
     * @return
     */
    private Response getResponseByApi(ApiType api, RequestParams params) {
        //判断是否加https
        String url;
        if (HttpsConfig.httpsConfig().contains(api)) {
            url = api.getOpt().replaceFirst("http://", "https://");
        } else {
            url = api.getOpt();
        }
        params.put("user-agent", "Android-v2.3");
        try {
            // TODO 判断api类型
            return mHelper.getResponse(api, url, params);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Class<? extends ResponseResult> getJsonClassByApi(ApiType api) {
        // TODO 返回api对应的CLASS
        return api.getClazz();
    }

}
