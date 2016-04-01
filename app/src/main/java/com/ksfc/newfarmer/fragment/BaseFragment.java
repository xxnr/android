/**
 *
 */
package com.ksfc.newfarmer.fragment;

import java.util.HashMap;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.Push.UmengPush;
import com.ksfc.newfarmer.activitys.LoginActivity;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.OnApiDataReceivedCallback;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.LoginResult;
import com.ksfc.newfarmer.utils.Utils;
import com.ksfc.newfarmer.widget.dialog.CustomProgressDialog;
import com.ksfc.newfarmer.widget.dialog.CustomToast;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.BadTokenException;
import android.widget.Toast;

import net.yangentao.util.app.App;

/**
 * 项目名称：newFarmer63 类名称：BaseFragment 类描述： 创建人：王蕾 创建时间：2015-6-3 上午9:29:34 修改备注：
 */
public abstract class BaseFragment extends Fragment implements
        OnApiDataReceivedCallback, View.OnClickListener {

    public BaseActivity activity;
    public LayoutInflater inflater;
    private Dialog progressDialog;
    private HashMap<ApiType, Boolean> isReturnData = new HashMap<>();//是否请求超时（返回数据）
    public HashMap<ApiType, RequestParams> execApis = new HashMap<ApiType, RequestParams>();
    private CustomToast customToast;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

    }

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

    // 调用此方法 去登录页面
    public void tokenToLogin() {

        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            //解除推送alias
            UmengPush.removeAlias(getActivity(), userInfo.userid);
        }
        Store.User.removeMe();

        Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("isTokenError", true);
        getActivity().getApplicationContext().startActivity(intent);
    }

    @Override
    public void onResponse(Request req) {
        if (!isReturnData.get(req.getApi())) {
            if (req.getData().getStatus().equals("1401")) {
                req.showErrorMsg();
                tokenToLogin();
            } else {
                disMissDialog();
                if (req.isSuccess()) {
                    onResponsed(req);
                } else {
                    if (!(req.getApi() == ApiType.SURE_GET_GOODS
                    )) {
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
            progressDialog.dismiss();
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
        CustomToast.Builder builder = new CustomToast.Builder(getActivity());
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
            //设置刷新的文字
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    //更改标记不返回数据
                    isReturnData.put(api, true);
                }
            }, 20 * 1000);

        } catch (BadTokenException exception) {
            exception.printStackTrace();
        }
    }


}
