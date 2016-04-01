package com.ksfc.newfarmer.activitys;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.ApiType.RequestMethod;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.PublicKeyResult;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.RSAUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.ClearEditText;

import net.yangentao.util.app.App;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class RetrievePasswordActivity extends BaseActivity {
    private ClearEditText backedit1, backnewpassword, confimPasword;
    private EditText backyanzhengma;
    private TextView backgetVerificationCode;
    private String mobile;
    private String password, smsCode;

    @Override
    public int getLayout() {
        return R.layout.retrievepassword_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("忘记密码");
        backgetVerificationCode = (TextView) findViewById(R.id.backgetVerificationCode);
        backedit1 = (ClearEditText) findViewById(R.id.backedit1);
        backyanzhengma = (EditText) findViewById(R.id.backyanzhengma);
        backnewpassword = (ClearEditText) findViewById(R.id.backnewpassword);
        confimPasword = (ClearEditText) findViewById(R.id.confimPasword);
        setViewClick(R.id.backgetVerificationCode);
        setViewClick(R.id.backdengLubutton);
    }

    @Override
    public void OnViewClick(View v) {
        password = confimPasword.getText().toString();
        smsCode = backyanzhengma.getText().toString();

        switch (v.getId()) {
            case R.id.backdengLubutton:
                if (!StringUtil.checkStr(backedit1.getText().toString())) {
                    Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!isMobileNum(backedit1.getText().toString())) {
                    Toast.makeText(this, "手机号格式错误", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!StringUtil.checkStr(backyanzhengma.getText().toString())) {
                    Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
                    return;
                } else if (backnewpassword.getText().toString().isEmpty()) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                } else if (confimPasword.getText().toString().isEmpty()) {
                    Toast.makeText(this, "请输入确认密码", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!password.equals(backnewpassword.getText().toString())) {
                    showToast("密码不一致");
                    return;
                } else if (backnewpassword.getText().toString().length() > 20) {
                    showToast("密码长度不能大于20位");
                    return;
                }
                showProgressDialog();
                execApi(ApiType.GET_PUBLIC_KEY, new RequestParams());
                break;
            case R.id.backgetVerificationCode:
                // 获取验证码
                getCode();
                break;
            default:
                break;
        }

    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (req.getApi() == ApiType.GET_PUBLIC_KEY) {
            PublicKeyResult res = (PublicKeyResult) req.getData();
            RequestParams params = new RequestParams();
            String phone = backedit1.getText().toString().trim();
            params.put("account", phone);
            params.put("smsCode", smsCode);
            try {
                params.put(
                        "newPwd",
                        RSAUtil.encryptByPublicKey(password,
                                RSAUtil.generatePublicKey(res.public_key)));
                execApi(ApiType.FIND_PASSWORD, params);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (req.getApi() == ApiType.FIND_PASSWORD) {
            if ("1000".equals(req.getData().getStatus())) {
                // 找回密码成功后 就跳到MainActivity
                showToast("修改密码成功");
                IntentUtil.activityForward(RetrievePasswordActivity.this,
                        LoginActivity.class, null, true);
                App.getApp().quit();
            } else {
                showToast("修改密码失败");
            }
        } else if (req.getApi() == ApiType.SEND_SMS) {
            if ("1000".equals(req.getData().getStatus())) {
                showToast("成功获取短信，请注意查收");
                final MyCount mc = new MyCount(60000, 1000);
                mc.start();
            }
        }
    }

    /**
     * 获取验证码
     */
    private void getCode() {

        mobile = backedit1.getText().toString();
        if (!isMobileValid(mobile)) {
            return;
        }
        sendSMS();
    }

    /**
     * 发送验证码
     */
    private void sendSMS() {
        showProgressDialog("正在获取验证码");
        RequestParams params = new RequestParams();
        params.put("tel", mobile);
        params.put("bizcode", "resetpwd");
        execApi(ApiType.SEND_SMS.setMethod(RequestMethod.GET), params);
    }


    /* 定义一个倒计时的内部类 */
    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {

            backgetVerificationCode.setText("重新获取");
            backgetVerificationCode.setClickable(true);

        }

        @Override
        public void onTick(long millisUntilFinished) {
            backgetVerificationCode.setClickable(false);
            backgetVerificationCode.setText("(" + millisUntilFinished / 1000
                    + ")秒后重试");
        }
    }

}
