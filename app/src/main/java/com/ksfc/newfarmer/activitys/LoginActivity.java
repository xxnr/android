/**
 *
 */
package com.ksfc.newfarmer.activitys;


import com.ksfc.newfarmer.App;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.event.IsLoginEvent;
import com.ksfc.newfarmer.event.MainTabSelectEvent;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.beans.LoginResult;
import com.ksfc.newfarmer.beans.PublicKeyResult;
import com.ksfc.newfarmer.common.UmengPushHelper;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;

import com.ksfc.newfarmer.utils.RSAUtil;
import com.ksfc.newfarmer.utils.SPUtils;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.ClearEditText;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import net.yangentao.util.PreferenceUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * 项目名称：newFarmer 类名称：LoginActivity 类描述： 创建人：王蕾 创建时间：2015-5-28 上午11:05:33 修改备注：
 */
public class LoginActivity extends BaseActivity {
    private ClearEditText login_layout_phone, login_layout_password;
    private String phoneNumber;
    private String phonePassword;
    private boolean isFromReg = false; // 是否从注册页跳转
    private String reg_phone;// 注册页注册的手机号
    private boolean isTokenError = false;

    @Override
    public int getLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        //接收注册的号码
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isFromReg = bundle.getBoolean("from_reg", false);
            reg_phone = bundle.getString("reg_phone");
        }

        initView();
    }


    public void initView() {

        login_layout_phone = (ClearEditText) findViewById(R.id.login_layout_phone);
        login_layout_password = (ClearEditText) findViewById(R.id.login_layoutpassword);
        setViewClick(R.id.login_layout_complete);
        setViewClick(R.id.login_layoutforgetpassword);
        setViewClick(R.id.login_layoutReg);
        setTitle("登录");

        //注册完，登陆默认填入
        if (isFromReg && StringUtil.checkStr(reg_phone)) {
            login_layout_phone.setText(reg_phone);
            login_layout_phone.clearFocus();
            login_layout_password.requestFocus();
        } else {
            //获取上次保存再本地的手机号
            PreferenceUtil pu = new PreferenceUtil(this, App.SPNAME);
            String lastPhoneNumber = pu.getString("lastPhoneNumber", "");
            if (StringUtil.checkStr(lastPhoneNumber)) {
                login_layout_phone.setText(lastPhoneNumber);
                login_layout_phone.clearFocus();
                login_layout_password.requestFocus();
            }
        }
        isTokenError = getIntent().getBooleanExtra("isTokenError", false);

        setLeftClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isTokenError) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("id",MainActivity.Tab.MINE);
                    startActivity(intent);
                    EventBus.getDefault().post(new MainTabSelectEvent(MainActivity.Tab.MINE));
                }
                finish();
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (isTokenError) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("id",MainActivity.Tab.MINE);
                startActivity(intent);
                EventBus.getDefault().post(new MainTabSelectEvent(MainActivity.Tab.MINE));
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void OnViewClick(View v) {

        switch (v.getId()) {
            case R.id.login_layout_complete:
                phoneNumber = login_layout_phone.getText().toString();
                phonePassword = login_layout_password.getText().toString();
                if (!StringUtil.checkStr(phoneNumber)) {
                    showToast(getString(R.string.please_input_phone));
                    return;
                }
                if (!isMobileNum(phoneNumber)) {
                    showToast(getString(R.string.please_input_right_phone));
                    return;
                }
                if (!StringUtil.checkStr(phonePassword)) {
                    showToast(getString(R.string.input_password));
                    return;
                }
                showProgressDialog();
                execApi(ApiType.GET_PUBLIC_KEY, new RequestParams());
                break;
            case R.id.login_layoutforgetpassword:
                startActivity(RetrievePasswordActivity.class);
                break;
            case R.id.login_layoutReg:
                startActivity(RegisterActivity.class);
                break;

            default:
                break;
        }
    }


    @Override
    public void onResponsed(Request req) {

        if (req.getApi() == ApiType.GET_PUBLIC_KEY) {
            PublicKeyResult result = (PublicKeyResult) req.getData();
            RequestParams params = new RequestParams();
            params.put("account", phoneNumber);
            try {
                params.put(
                        "password",
                        RSAUtil.encryptByPublicKey(phonePassword,
                                RSAUtil.generatePublicKey(result.public_key)));
                execApi(ApiType.LOGIN, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (req.getApi() == ApiType.LOGIN) {
            final LoginResult login = (LoginResult) req.getData();
            if (login.datas != null) {
                login.datas.token = login.token;
                Store.User.saveMe(login.datas);
                saveMe(login.datas);
                String uid = login.datas.userid;
                App.getApp().setUid(uid);
                //保存到本地此购物车的Id
                SPUtils.put(LoginActivity.this, "shopCartId",
                        login.datas.cartId);
                //保存到本地此用户的上次登陆的手机号
                PreferenceUtil pu = new PreferenceUtil(this, App.SPNAME);
                pu.putString("lastPhoneNumber", login.datas.phone);
                //登陆msg
                EventBus.getDefault().post(new IsLoginEvent());
                //注册推送alias
                UmengPushHelper.addAlias(this, login.datas.userid);
                // 是否进入完善资料页
                if (!login.datas.isUserInfoFullFilled) {
                    Intent intent = new Intent(this, ImprovePersonActivity.class);
                    startActivity(intent);
                }
                showToast("登录成功");
                //登录成功跳转
                if (isTokenError) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("id",MainActivity.Tab.MINE);
                    startActivity(intent);
                    EventBus.getDefault().post(new MainTabSelectEvent(MainActivity.Tab.MINE));
                }
                finish();
            }

        }

    }


    //存储个人信息部分到本地
    public void saveMe(LoginResult.UserInfo user) {
        LoginResult.UserInfo me = Store.User.queryMe();
        if (me != null) {
            if (user.userAddress != null) {
                String province = "";
                String city = "";
                String county = "";
                String town = "";
                String address = "";
                if (user.userAddress.province != null) {
                    province = user.userAddress.province.name;
                    me.provinceid = user.userAddress.province.id;
                }
                if (user.userAddress.city != null) {
                    city = user.userAddress.city.name;
                    me.cityid = user.userAddress.city.id;
                }
                if (user.userAddress.county != null) {
                    county = user.userAddress.county.name;
                    me.countyid = user.userAddress.county.id;
                }
                if (user.userAddress.town != null) {
                    town = user.userAddress.town.name;
                    me.townid = user.userAddress.town.id;
                }
                address = StringUtil.checkBufferStr
                        (province, city, county, "");
                me.addressCity = address;
                me.addressTown = town;
            }
            me.sex = user.sex;
            me.name = user.name;
            me.userType = user.userType;
            me.userTypeInName = user.userTypeInName;
            me.isXXNRAgent = user.isXXNRAgent;
            Store.User.saveMe(me);
        }

    }
}