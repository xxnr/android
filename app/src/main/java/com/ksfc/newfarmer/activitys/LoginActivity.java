/**
 *
 */
package com.ksfc.newfarmer.activitys;


import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.utils.thrid.UmengPush;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.LoginResult;
import com.ksfc.newfarmer.protocol.beans.PublicKeyResult;
import com.ksfc.newfarmer.utils.RSAUtil;
import com.ksfc.newfarmer.utils.SPUtils;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.ClearEditText;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import net.yangentao.util.PreferenceUtil;
import net.yangentao.util.app.App;
import net.yangentao.util.msg.MsgCenter;

/**
 * 项目名称：newFarmer 类名称：LoginActivity 类描述： 创建人：王蕾 创建时间：2015-5-28 上午11:05:33 修改备注：
 */
public class LoginActivity extends BaseActivity {
    private ClearEditText login_layout_phone, login_layoutpassword;
    private String phoneNumber;
    private String phonePassword;
    private int id; // 获取页面跳转过来的id,登陆之后跳转回到哪一页面
    private boolean isFromReg = false; // 是否从注册页跳转
    private String reg_phone;// 注册页注册的手机号
    private boolean isTokenError=false;

    @Override
    public int getLayout() {
        return R.layout.login_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        id = getIntent().getIntExtra("id", 1);
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
        login_layoutpassword = (ClearEditText) findViewById(R.id.login_layoutpassword);
        setViewClick(R.id.login_layout_complete);
        setViewClick(R.id.login_layoutforgetpassword);
        setViewClick(R.id.login_layoutReg);
        setTitle("登录");
        //注册完，登陆默认填入
        if (isFromReg && StringUtil.checkStr(reg_phone)) {
            login_layout_phone.setText(reg_phone);
            login_layout_phone.clearFocus();
            login_layoutpassword.requestFocus();
        } else {
            //获取上次保存再本地的手机号
            PreferenceUtil pu = new PreferenceUtil(this, "config");
            String lastPhoneNumber = pu.getString("lastPhoneNumber", "");
            if (StringUtil.checkStr(lastPhoneNumber)) {
                login_layout_phone.setText(lastPhoneNumber);
                login_layout_phone.clearFocus();
                login_layoutpassword.requestFocus();
            }
        }
        isTokenError = getIntent().getBooleanExtra("isTokenError",false);


        setLeftClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isTokenError) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    MsgCenter.fireNull(MsgID.MainActivity_select_tab, 4);
                    intent.putExtra("id", 4);
                    startActivity(intent);
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
                MsgCenter.fireNull(MsgID.MainActivity_select_tab, 4);
                intent.putExtra("id", 4);
                startActivity(intent);
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void OnViewClick(View v) {
        phoneNumber = login_layout_phone.getText().toString();
        phonePassword = login_layoutpassword.getText().toString();
        switch (v.getId()) {

            case R.id.login_layout_complete:

                if (TextUtils.isEmpty(phoneNumber)) {
                    showToast("请输入您的手机号码");
                    return;
                }

                if (!isMobileNum(phoneNumber)) {
                    showToast("您输入的手机号码格式不正确");
                    return;
                }

                if (TextUtils.isEmpty(phonePassword)) {
                    showToast("请输入密码");
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
            disMissDialog();
            final LoginResult login = (LoginResult) req.getData();
            if ("1000".equals(login.getStatus())) {

                login.datas.token = login.token;
                Store.User.saveMe(login.datas);
                saveMe(login.datas);
                String uid = login.datas.userid;
                App.getApp().setUid(uid);

                //保存到本地此购物车的Id
                SPUtils.put(LoginActivity.this, "shopCartId",
                        login.datas.cartId);
                showToast("登录成功");
                //保存到本地此用户的上次登陆的手机号
                PreferenceUtil pu = new PreferenceUtil(this, "config");
                pu.putString("lastPhoneNumber", login.datas.phone);

                // 发广播 让首页列表再次刷新
                MsgCenter.fireNull(MsgID.ISLOGIN);

                //注册推送alias
                UmengPush.addAlias(this, login.datas.userid);

                // 是否进入完善资料页
                if (login.datas.isUserInfoFullFilled) {
                    turntoMainActivityOrFinish(id);
                    disMissDialog();
                } else {
                    Intent intent = new Intent(this, ImprovePersonActivity.class);
                    startActivity(intent);
                    finish();
                }

            } else {
                showToast(login.getMessage());
            }
        }

    }


    public void turntoMainActivityOrFinish(int id) {
        if (id == 0) {
            disMissDialog();
            finish();
        } else {
            disMissDialog();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
            finish();
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
