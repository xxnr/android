/**
 *
 */
package com.ksfc.newfarmer;

import java.util.HashMap;

import com.ksfc.newfarmer.common.FilterClassUtils;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.thrid.UmengPush;
import com.ksfc.newfarmer.activitys.LoginActivity;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.http.ApiType;
import com.ksfc.newfarmer.http.OnApiDataReceivedCallback;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.http.RequestParams;
import com.ksfc.newfarmer.http.beans.LoginResult;
import com.ksfc.newfarmer.utils.Utils;
import com.ksfc.newfarmer.widget.dialog.CustomProgressDialog;
import com.ksfc.newfarmer.widget.dialog.CustomToast;
import com.trello.rxlifecycle.components.support.RxFragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.BadTokenException;


/**
 * 项目名称：newFarmer63 类名称：BaseFragment 类描述： 创建人：王蕾 创建时间：2015-6-3 上午9:29:34 修改备注：
 */
public abstract class BaseFragment extends RxFragment implements
        OnApiDataReceivedCallback, View.OnClickListener {

    public BaseActivity activity;
    public LayoutInflater inflater;
    private Dialog progressDialog;
    private HashMap<ApiType, Boolean> isReturnData = new HashMap<>();//是否请求超时（返回数据）
    public HashMap<ApiType, RequestParams> execApis = new HashMap<>();
    private CustomToast customToast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (BaseActivity) getActivity();
        this.inflater = inflater;
        return InItView();
    }

    @Override
    public final void onClick(View v) {
        // 过滤要处理的控件  无法连续点击一个控件
        if (Utils.isFastClick()) {
            return;
        }
        OnViewClick(v);
    }

    /**
     * 控件的点击事件
     *
     * @param v
     */
    public abstract void OnViewClick(View v);

    /**
     * 加载本地布局文件
     */
    public abstract View InItView();


    @Override
    public void onResponse(Request req) {
        if (!isReturnData.get(req.getApi())) {
            disMissDialog();
            if (req.getData().getStatus().equals("1401")) {
                req.showErrorMsg();
                tokenToLogin();
            } else if (req.isSuccess()) {
                onResponsed(req);
            } else {
                if (!FilterClassUtils.getUnToastApis().contains(req.getApi())) {
                    if (req.getApi() == ApiType.RSC_ORDER_SELF_DELIVERY && req.getData().getStatus().equals("1429")) {
                        App.getApp().showToast("您输入错误次数较多，请1分钟后再操作");
                    } else if (req.getData().getStatus().equals("1403")) {
                        RndLog.d("onResponse", req.getData().getMessage());
                    } else {
                        req.showErrorMsg();
                    }
                }
            }
        } else {
            App.getApp().showToast("您的网络不太顺畅，重试或检查下网络吧~");
        }
    }

    public abstract void onResponsed(Request req);

    /**
     * 短时间显示Toast
     *
     * @param info 显示的内容
     */
    public void showToast(String info) {
        App.getApp().showToast(info);
    }

    /**
     * 显示正在加载的进度条
     */
    public void showProgressDialog() {
        showProgressDialog("加载中...");
    }

    public void showProgressDialog(String msg) {
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            progressDialog = null;
        }
        progressDialog = CustomProgressDialog
                .createLoadingDialog(activity, msg, Color.parseColor("#000000"));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        try {
            progressDialog.show();
        } catch (BadTokenException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 取消对话框显示
     */
    public void disMissDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示大号提示框
     */
    public void showCustomToast(String msg, int imgRes) {

        if (customToast != null) {
            customToast.cancel();
        }
        CustomToast.Builder builder = new CustomToast.Builder(activity);
        customToast = builder.setMessage(msg).setMessageImage(imgRes).create();
        customToast.show();

    }

    /*
     * 执行网络请求
     *
     * @param api
     *
     * @param params
     */
    public void execApi(final ApiType api, RequestParams params) {

        execApis.put(api, params);
        Request req = new Request();
        req.setApi(api);
        if (params == null)
            params = new RequestParams();
        // 判断是不是通过验证
        if (params.containsKey("userId")) {
            LoginResult.UserInfo userInfo = Store.User.queryMe();
            if (userInfo != null) {
                params.put("token", userInfo.token);
            }

        }
        req.setParams(params);
        req.executeNetworkApi(this);

        //加入请求队列
        isReturnData.put(api, false);
        try {
            //设置超时对话框关闭
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    try {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    //更改标记不返回数据
                    isReturnData.put(api, true);
                }

            }, 20 * 1000);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    // 调用此方法 去登录页面
    public void tokenToLogin() {

        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            //解除推送alias
            UmengPush.removeAlias(activity, userInfo.userid);
        }
        Store.User.removeMe();
        App.getApp().setUid("");
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("isTokenError", true);
        activity.startActivity(intent);
    }



}
