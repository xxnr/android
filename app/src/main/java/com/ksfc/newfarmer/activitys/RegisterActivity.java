/**
 *
 */
package com.ksfc.newfarmer.activitys;


import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.ApiType.RequestMethod;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.LoginResult;
import com.ksfc.newfarmer.protocol.beans.PublicKeyResult;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.RSAUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.ClearEditText;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 项目名称：newFarmer 类名称：RegisterActivity 类描述： 创建人：王蕾 创建时间：2015-5-28 上午11:43:11
 * 修改备注：
 */
public class RegisterActivity extends BaseActivity {
    private ClearEditText backedit1, backnewpassword, confimPasword;
    private EditText backyanzhengma;
    private TextView backgetVerificationCode, register_layoutxieyi;
    private String mobile;
    private String phoneNumber, password, smsCode;
    private CheckBox checkBox;

    @Override
    public int getLayout() {
        // TODO Auto-generated method stub
        return R.layout.register_layout;

    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {

        setTitle("注册");

        backgetVerificationCode = (TextView) findViewById(R.id.backgetVerificationCode);
        register_layoutxieyi = (TextView) findViewById(R.id.register_layoutxieyi);
        backedit1 = (ClearEditText) findViewById(R.id.backedit1);
        backyanzhengma = (EditText) findViewById(R.id.backyanzhengma);
        backnewpassword = (ClearEditText) findViewById(R.id.backnewpassword);
        confimPasword = (ClearEditText) findViewById(R.id.confimPasword);
        checkBox = (CheckBox) findViewById(R.id.check_box);
        setViewClick(R.id.backgetVerificationCode);
        setViewClick(R.id.backdengLubutton);
        setViewClick(R.id.register_layoutxieyi);
        setViewClick(R.id.reg_dengLubutton);
        register_layoutxieyi.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        setLeftClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.putExtra("id", 4);
                startActivity(intent);
                finish();
            }
        });

    }


    @Override
    public void OnViewClick(View v) {
        phoneNumber = backedit1.getText().toString();
        password = confimPasword.getText().toString();
        smsCode = backyanzhengma.getText().toString();
        switch (v.getId()) {
            case R.id.backdengLubutton:

                if (!StringUtil.checkStr(phoneNumber)) {
                    showToast("请输入手机号");
                    return ;
                }
                if (!isMobileNum(phoneNumber)){
                    showToast("手机号格式错误");
                    return;
                }

                if (!checkBox.isChecked()) {
                    showToast("您需要同意注册协议才可继续注册哦~");
                    return;
                }

                if (smsCode.isEmpty()) {
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
                // app/user/register
                // account:登录账号
                // password:登录密码
                // smsCode:短信验证码
                showProgressDialog("正在注册中...");
                execApi(ApiType.GET_PUBLIC_KEY, new RequestParams());
                break;
            case R.id.backgetVerificationCode:
                // 获取验证码
                getCode();
                break;
            case R.id.register_layoutxieyi:
                startActivity(AgreeMentActivity.class);
                break;
            case R.id.reg_dengLubutton:
                startActivity(LoginActivity.class);
                break;

            default:
                break;
        }

    }

    /**
     * 获取验证码
     */
    private void getCode() {
        mobile = backedit1.getText().toString().trim();
        if (mobile.isEmpty()) {
            showToast("请输入手机号");
            return;
        }
        if (!isMobileNum(mobile)) {
            showToast("手机号格式错误");
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
        params.put("bizcode", "register");
        execApi(ApiType.SEND_SMS.setMethod(RequestMethod.GET), params);
    }

    /* 定义一个倒计时的内部类 */
    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            backgetVerificationCode.setText("重新获取验证码");
            backgetVerificationCode.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            backgetVerificationCode.setClickable(false);
            backgetVerificationCode.setText("(" + millisUntilFinished / 1000
                    + ")秒后重试");
        }
    }


    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (req.getApi() == ApiType.GET_PUBLIC_KEY) {
            PublicKeyResult res = (PublicKeyResult) req.getData();
            RequestParams params = new RequestParams();
            params.put("account", phoneNumber);
            params.put("smsCode", smsCode);
            try {
                params.put(
                        "password",
                        RSAUtil.encryptByPublicKey(password,
                                RSAUtil.generatePublicKey(res.public_key)));
                execApi(ApiType.REGISTER, params);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (req.getApi() == ApiType.SEND_SMS) {
            if ("1000".equals(req.getData().getStatus())) {
                showToast("成功获取短信，请注意查收");
                MyCount mc = new MyCount(60000, 1000);
                mc.start();
            }
        } else if (req.getApi() == ApiType.REGISTER) {
            LoginResult res = (LoginResult) req.getData();
            if ("1000".equals(res.getStatus())) {
                showToast("注册成功");
                Bundle bundle = new Bundle();
                bundle.putBoolean("from_reg", true);
                bundle.putString("reg_phone", phoneNumber);
                IntentUtil.activityForward(this, LoginActivity.class, bundle,
                        true);
                finish();
            } else {
                showToast(res.getMessage());
            }
        }
    }

}
