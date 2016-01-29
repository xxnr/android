/**
 *
 */
package com.ksfc.newfarmer.activitys;


import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MainActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.db.dao.ShoppingDao;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.ResponseResult;
import com.ksfc.newfarmer.protocol.beans.LoginResult;
import com.ksfc.newfarmer.protocol.beans.PublicKeyResult;
import com.ksfc.newfarmer.utils.RSAUtil;
import com.ksfc.newfarmer.utils.SPUtils;
import com.ksfc.newfarmer.utils.StringUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import net.yangentao.util.PreferenceUtil;
import net.yangentao.util.app.App;
import net.yangentao.util.msg.MsgCenter;

/**
 * 项目名称：newFarmer 类名称：LoginActivity 类描述： 创建人：王蕾 创建时间：2015-5-28 上午11:05:33 修改备注：
 */
public class LoginActivity extends BaseActivity {
    private EditText login_layout_phone, login_layoutpassword;
    private String phoneNumber;
    private String phonePassword;
    private int id; // 获取页面跳转过来的id,登陆之后跳转回到哪一页面

    @Override
    public int getLayout() {
        return R.layout.login_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        id = getIntent().getIntExtra("id", 1);
        login_layout_phone = (EditText) findViewById(R.id.login_layout_phone);
        login_layoutpassword = (EditText) findViewById(R.id.login_layoutpassword);
        setViewClick(R.id.login_layout_complete);
        setViewClick(R.id.login_layoutforgetpassword);
        setViewClick(R.id.login_layoutReg);
        setTitle("登录");

        //获取上次保存再本地的手机号
        PreferenceUtil pu = new PreferenceUtil();
        pu.init(this, "config");
        String lastPhoneNumber = pu.getString("lastPhoneNumber", "");
        if (!StringUtil.empty(lastPhoneNumber)) {
            login_layout_phone.setText(lastPhoneNumber);
            login_layout_phone.clearFocus();
            login_layoutpassword.requestFocus();
        }

        setLeftClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (id == 0) {
                    disMissDialog();
                    finish();
                } else {
                    disMissDialog();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                    finish();
                }
            }
        });

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
                    showToast("您输入 的手机号码格式不正确");
                    return;
                }

                if (TextUtils.isEmpty(phonePassword)) {
                    showToast("请输入密码");
                    return;
                }
                showProgressDialog("正在登录...");
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
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
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
            LoginResult login = (LoginResult) req.getData();
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
                PreferenceUtil pu = new PreferenceUtil();
                pu.init(this, "config");
                pu.putString("lastPhoneNumber", login.datas.phone);
                // 发广播 让首页列表再次刷新
                MsgCenter.fireNull(MsgID.ISLOGIN, "islogin");
                // turntoMainActivityOrFinish(id);
                // if (id == 3) { // 如果是从提交订单页过来的，则需要上传购物车数据，并清空本地数据库数据
                // RequestParams params = new RequestParams();
                // params.put("locationUserId", uid);
                // params.put("goodsDatas", baoWei);
                // params.put("userId", uid);
                // execApi(ApiType.SHOPPING_UPLOADING, params);
                //
                // } else { // 正常登录逻辑
                // // TimerTask task = new TimerTask(){
                // // @Override
                // // public void run() {
                // // isOk = DemoHXSDKHelper.getInstance().isLogined();
                // // i++;
                // // }
                // //
                // // };
                // // Timer timer = new Timer();
                // // timer.schedule(task, 0, 1000);
                // //
                // // while(true){
                // // if(isOk){
                // // turntoMainActivityOrFinish(id);
                // // disMissDialog();
                // // break;
                // // }
                // // if(i >= 10){
                // // disMissDialog();
                // // showToast("网络超时");
                // // break;
                // // }
                // // }
                //
                // }


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
        } else if (req.getApi() == ApiType.SHOPPING_UPLOADING) {
            ResponseResult data = req.getData();
            if ("1000".equals(data.getStatus())) { // 上传成功，清空数据库

                ShoppingDao dao = new ShoppingDao(LoginActivity.this);
//				if (dao.deleteAllShopping()) {
//					// 从结算中登录成功后清空购物车表，然后可以在这里做一些逻辑处理
//				}
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
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
        Store.User.saveMe(me);
    }
}
