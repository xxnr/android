package com.ksfc.newfarmer;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ksfc.newfarmer.activitys.LoginActivity;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.OnApiDataReceivedCallback;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.LoginResult.UserInfo;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.thrid.UmengPush;
import com.ksfc.newfarmer.utils.Utils;
import com.ksfc.newfarmer.widget.dialog.CustomProgressDialog;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.SPUtils;
import com.ksfc.newfarmer.widget.dialog.CustomToast;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import net.yangentao.util.app.App;

public abstract class BaseActivity extends FragmentActivity implements
        OnClickListener, OnApiDataReceivedCallback {


    public String TAG = this.getClass().getSimpleName();

    private boolean titleLoaded = false; // 标题是否加载成功
    protected View titleLeftView;
    protected View titleRightView;
    private View titleView;
    private TextView tv_title;
    private TextView tvTitleRight;
    private ImageView ivTitleRight;
    private Dialog progressDialog;

    private HashMap<ApiType, Boolean> isReturnData = new HashMap<>();//是否请求超时（返回数据）
    private CustomToast customToast;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //防止fragment中找不到getActivity Null空指针
        if (savedInstanceState != null) {
            String FRAGMENTS_TAG = "android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);
        }
        setFullScreen(false);
        RndApplication.unDestroyActivityList.add(this);
        // 屏幕竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (getLayout() != 0) {
            setContentView(getLayout());
        }
        loadTitle();
        OnActCreate(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    // -------------------------------------------------------------
    // 重写方法区

    /**
     * 返回本界面的布局文件
     *
     * @return
     */
    public abstract int getLayout();

    /**
     * 子类OnCreate方法
     *
     * @param savedInstanceState
     */
    public abstract void OnActCreate(Bundle savedInstanceState);

    /**
     * 控件的点击事件
     *
     * @param v
     */
    public abstract void OnViewClick(View v);

    /**
     * 网络返回数据回调方法 此方法只处理成功的请求, 不需要弹出错误信息和取消对话框显示,如果需要对错误的请求做单独处理,请重写
     * {@link #onResponsedError}方法
     *
     * @param req
     */
    public abstract void onResponsed(Request req);

    /**
     * 当网络请求数据失败时执行此方法
     *
     * @param req
     */
    public void onResponsedError(Request req) {
        // empty
    }

    // -------------------------------------------------------------
    // 父类方法区

    /**
     * 加载标题
     */
    private void loadTitle() {
        titleLeftView = findViewById(R.id.ll_title_left_view);
        titleRightView = findViewById(R.id.ll_title_right_view);
        titleView = findViewById(R.id.titleview);
        tv_title = (TextView) findViewById(R.id.title_name_text);
        tvTitleRight = (TextView) findViewById(R.id.title_right_text);
        ivTitleRight = (ImageView) findViewById(R.id.title_right_img);
        if (titleView != null) {
            titleLeftView.setOnClickListener(this);
            titleLoaded = true;
            RndLog.i(TAG, "titleView loaded.");
        }

    }


    /**
     * 判断当前用户是否登录
     *
     * @return
     */
    public boolean isLogin() {
        UserInfo me = Store.User.queryMe();
        // 判断本地登录
        return me != null && StringUtil.checkStr(me.token);
    }

    @Override
    public final void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.ll_title_left_view:
                // 左上角返回按钮
                finish();
                break;
            default:
                break;
        }
        OnViewClick(v);
    }

    @Override
    public final void onResponse(Request req) {
        if (!isReturnData.get(req.getApi())) {
            //token异常登出
            if (req.getData().getStatus().equals("1401")) {
                req.showErrorMsg();
                tokenToLogin();
            } else {
                if (req.isSuccess()) {
                    disMissDialog();
                    onResponsed(req);
                } else {
                    disMissDialog();
                    //这些Api不返回前台用户error msg
                    if (!(req.getApi() == ApiType.GET_MIN_PAY_PRICE
                            || req.getApi() == ApiType.SAVE_CONSIGNEE_INFO
                            || req.getApi() == ApiType.SURE_GET_GOODS
                    )) {
                        req.showErrorMsg();
                    }
                }
            }
        } else {
            //请求时间过长提示
            if (!getClass().getName().equals(
                    "com.ksfc.newfarmer.activitys.HomepageActivity")) {
                App.getApp().showToast("您的网络不太顺畅，重试或检查下网络吧~");
            }
            onResponsedError(req);
        }
    }

    // -------------------------------------------------------------
    // 公共方法区

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        if (titleLoaded) {
            tv_title.setText(title);
        }
    }

    /**
     * 隐藏标题
     */
    public void hideTitle() {
        if (titleLoaded) {
            titleView.setVisibility(View.GONE);
        }
    }

    /**
     * 隐藏左控件
     */
    public void hideLeft() {
        if (titleLoaded) {
            titleLeftView.setVisibility(View.GONE);
        }
    }


    /**
     * 隐藏右控件
     */
    public void hideRight() {
        if (titleLoaded) {
            tvTitleRight.setVisibility(View.GONE);
        }
    }


    /*
     * 显示有图片
     */
    public void showRightImage() {

        if (titleLoaded) {
            ivTitleRight.setVisibility(View.VISIBLE);
        }
    }

    /*
     * 设置右图片
     */
    @SuppressLint("NewApi")
    public void setRightImage(int bg) {
        if (titleLoaded) {
            ivTitleRight.setImageResource(bg);
        }
    }

    /*
     * 设置右图片的监听事件
     */
    public void setRightViewListener(OnClickListener listener) {
        if (titleLoaded) {
            titleRightView.setOnClickListener(listener);
        }
    }

    /*
     * 得到右图片组件
     */
    public ImageView getRightImageView() {
        return ivTitleRight;
    }

    /*
     * 设置右文本的监听事件
     */
    public void setRightTextViewListener(OnClickListener listener) {
        if (titleLoaded) {
            tvTitleRight.setOnClickListener(listener);
        }
    }

    /*
     * 设置右文本
     */
    public void showRightTextView() {
        if (titleLoaded) {
            tvTitleRight.setVisibility(View.VISIBLE);
        }
    }

    /*
     * 设置右文本显示
     */
    public void setRightTextView(String str) {
        if (titleLoaded) {
            tvTitleRight.setText(str);
        }
    }

    /**
     * 设置左控件的点击事件
     */
    public void setLeftClickListener(OnClickListener listener) {
        if (titleLoaded) {
            titleLeftView.setOnClickListener(listener);
        }
    }

    /**
     * 给控件设置监听
     *
     * @param resId
     * @param listener
     */
    public View setViewClick(int resId, OnClickListener listener) {
        View view = findViewById(resId);
        if (view != null) {
            view.setOnClickListener(listener);
        }
        return view;
    }

    /**
     * 给控件设置监听
     *
     * @param resId
     */
    public View setViewClick(int resId) {
        return setViewClick(resId, this);
    }

    /**
     * 跳转一个界面不传递数据
     *
     * @param clazz
     */
    public void startActivity(Class<? extends BaseActivity> clazz) {
        Intent intent = new Intent();
        intent.setClass(this, clazz);
        startActivity(intent);
    }


    /**
     * 执行网络请求
     *
     * @param api
     * @param params
     */
    public void execApi(final ApiType api, RequestParams params) {

        // 判断是不是通过验证
        if (params.containsKey("userId")) {
            if (Store.User.queryMe() != null) {
                params.put("token", Store.User.queryMe().token);
            }
        }
        final Request req = new Request();
        req.setApi(api);
        req.setParams(params);
        req.executeNetworkApi(this);

        try {
            //加入请求队列
            isReturnData.put(api, false);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } catch (IllegalArgumentException exception) {
                        exception.printStackTrace();
                    }
                    //更改标记不返回数据
                    isReturnData.put(api, true);
                }
            }, 20 * 1000);

        } catch (Exception exception) {
            exception.printStackTrace();
        }


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
        progressDialog = CustomProgressDialog.createLoadingDialog(this, msg, Color.parseColor("#000000"));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.show();

    }

    /**
     * 显示大号提示框
     */
    public void showCustomToast(String msg, int imgRes) {

        if (customToast != null) {
            customToast.cancel();
        }
        CustomToast.Builder builder = new CustomToast.Builder(this);
        customToast = builder.setMessage(msg).setMessageImage(imgRes).create();
        customToast.show();

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
     * 是否全屏和显示标题，true为全屏和无标题，false为无标题，请在setContentView()方法前调用
     *
     * @param fullScreen
     */
    public void setFullScreen(boolean fullScreen) {
        if (fullScreen) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

    }

    /**
     * 短时间显示Toast
     *
     * @param info 显示的内容
     */
    public void showToast(String info) {
        App.getApp().showToast(info);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RndApplication.unDestroyActivityList.remove(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // umeng
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    // 判断是否是手机号
    public boolean isMobileNum(String mobiles) {
        Pattern p = Pattern
                .compile("^[1]([0-8]{1}[0-9]{1}|59|58|88|89)[0-9]{8}");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }


    //退出登录时调用
    public void exitLogin() {

        UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            //解除推送alias
            UmengPush.removeAlias(this, userInfo.userid);
        }
        Store.User.removeMe();
        SPUtils.clear(getApplicationContext());
    }

    // 调用此方法 去登录页面
    public void tokenToLogin() {
        exitLogin();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("isTokenError", true);
        getApplicationContext().startActivity(intent);
    }

}
