package com.ksfc.newfarmer.activitys;

import com.ksfc.newfarmer.App;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;

import com.ksfc.newfarmer.event.ChangePassWordEvent;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.beans.PublicKeyResult;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.RSAUtil;
import com.ksfc.newfarmer.utils.StringUtil;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


import org.greenrobot.eventbus.EventBus;


@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class ChangePasswordActivity extends BaseActivity {
    private EditText old_pass, new_pass, confirm_pass;//旧密码 新密码 确认新密码 的编辑框
    private String old_pwd;
    private String password;
    private String confirm;

    @Override
    public int getLayout() {
        return R.layout.activity_change_password;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        old_pass = (EditText) findViewById(R.id.backedit1);
        new_pass = (EditText) findViewById(R.id.backnewpassword);
        confirm_pass = (EditText) findViewById(R.id.confimPasword);
        setViewClick(R.id.backdengLubutton);
        setTitle("修改密码");
    }

    @Override
    public void OnViewClick(View v) {
        if (v.getId() == R.id.backdengLubutton) {
            old_pwd = old_pass.getText().toString();
            password = new_pass.getText().toString();
            confirm = confirm_pass.getText().toString();
            if (StringUtil.empty(old_pwd)) {
                showToast(getString(R.string.input_old_password));
                return;
            } else if (StringUtil.empty(password)) {
                showToast(getString(R.string.newPasswork));
                return;
            } else if (StringUtil.empty(confirm)) {
                showToast(getString(R.string.input_confirm_password));
                return;
            } else if (!password.equals(confirm)) {
                showToast(getString(R.string.password_not_fit));
                return;
            } else if (password.length()<6){
                showToast(getString(R.string.password_lt_6));
                return;
            } else if (password.length() > 20) {
                showToast(getString(R.string.password_gt_20));
                return;
            }
            showProgressDialog();
            execApi(ApiType.GET_PUBLIC_KEY, new RequestParams());
        }
    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (req.getApi() == ApiType.GET_PUBLIC_KEY) {
            PublicKeyResult res = (PublicKeyResult) req.getData();
            RequestParams params = new RequestParams();
            if (isLogin()) {
                params.put("userId", Store.User.queryMe().userid);
            }
            try {
                params.put(
                        "newPwd",
                        RSAUtil.encryptByPublicKey(password,
                                RSAUtil.generatePublicKey(res.public_key)));
                params.put(
                        "oldPwd",
                        RSAUtil.encryptByPublicKey(old_pwd,
                                RSAUtil.generatePublicKey(res.public_key)));
                execApi(ApiType.CHANGE_PASSWORD, params);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        } else if (req.getApi() == ApiType.CHANGE_PASSWORD) {
            if ("1000".equals(req.getData().getStatus())) {
                // 找回密码成功后 就跳到MainActivity
                showToast("修改密码成功");
                App.loginOut();
                //通知 "我"activity 结束
                EventBus.getDefault().post(new ChangePassWordEvent());
                IntentUtil.activityForward(ChangePasswordActivity.this,
                        LoginActivity.class, null, true);
            }
        }
    }

}