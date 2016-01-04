/**
 *
 */
package com.ksfc.newfarmer.activitys;

import java.util.Timer;
import java.util.TimerTask;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MainActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.ApiType.RequestMethod;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.GetCodeResult;
import com.ksfc.newfarmer.protocol.beans.LoginResult;
import com.ksfc.newfarmer.protocol.beans.PublicKeyResult;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.RSAUtil;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 项目名称：newFarmer 类名称：RegisterActivity 类描述： 创建人：王蕾 创建时间：2015-5-28 上午11:43:11
 * 修改备注：
 */
public class RegisterActivity extends BaseActivity {
    private EditText backedit1, backyanzhengma, backnewpassword, confimPasword;
    private TextView backgetVerificationCode,
            register_layoutxieyi;
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
        backgetVerificationCode = (TextView) findViewById(R.id.backgetVerificationCode);
        register_layoutxieyi = (TextView) findViewById(R.id.register_layoutxieyi);
        backedit1 = (EditText) findViewById(R.id.backedit1);
        backyanzhengma = (EditText) findViewById(R.id.backyanzhengma);
        backnewpassword = (EditText) findViewById(R.id.backnewpassword);
        confimPasword = (EditText) findViewById(R.id.confimPasword);
        checkBox = (CheckBox) findViewById(R.id.check_box);
        setViewClick(R.id.backgetVerificationCode);
        setViewClick(R.id.backdengLubutton);
        setTitle("注册");
        register_layoutxieyi.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        setViewClick(R.id.register_layoutxieyi);
        setViewClick(R.id.reg_dengLubutton);
    }

    private String uid;
    private String pwd;

    @Override
    public void OnViewClick(View v) {
        phoneNumber = backedit1.getText().toString();
        password = confimPasword.getText().toString();
        smsCode = backyanzhengma.getText().toString();
        switch (v.getId()) {
            case R.id.backdengLubutton:

                if (!isMobileValid(phoneNumber)) {
                    return;
                }
                if (!checkBox.isChecked()) {
                    showToast("您需要同意注册协议才可继续注册哦~");
                    return;
                }

                if (smsCode.isEmpty()) {
                    Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } else if (backnewpassword.getText().toString().isEmpty()) {
                    Toast.makeText(this, "请填写密码", Toast.LENGTH_SHORT).show();
                    return;
                } else if (confimPasword.getText().toString().isEmpty()) {
                    Toast.makeText(this, "请填写确认密码", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!password.equals(backnewpassword.getText().toString())) {
                    showToast("密码不一致");
                    return;
                } else if (backnewpassword.getText().toString().length() > 20) {
                    showToast("密码长度不能大于20位");
                    return;
                }
                uid = backedit1.getText().toString();
                pwd = backnewpassword.getText().toString();
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
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
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
            showToast("请填写手机号");
            return;
        }
        if (!isMobileNum(mobile)) {
            showToast("请填写正确的手机号");
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
        // params.put("tel", mobile);
        // params.put("bizcode", "register");
        // execApi(ApiType.SEND_SMS.setMethod(RequestMethod.GET), params);

        execApi(ApiType.SEND_SMS.setMethod(RequestMethod.GET).setOpt(
                        "/api/v2.0/sms" + "?tel=" + mobile + "&bizcode=register"),
                params);
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

    private boolean isOk;
    private int i;

    @Override
    public void onResponsed(Request req) {
        i = 0;
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
            GetCodeResult res = (GetCodeResult) req.getData();
            if ("1000".equals(res.getStatus())) {
                showToast("发送验证码成功");
                MyCount mc = new MyCount(60000, 1000);
                mc.start();
            }
        } else if (req.getApi() == ApiType.REGISTER) {
            LoginResult res = (LoginResult) req.getData();
            if ("1000".equals(res.getStatus())) {
                // 保存
                // 本地登录成功
                // HXLogin.getInstance().login(RegisterActivity.this, uid, pwd);
                //
                // TimerTask task = new TimerTask(){
                // @Override
                // public void run() {
                // isOk = DemoHXSDKHelper.getInstance().isLogined();
                // i++;
                // }
                //
                // };
                // Timer timer = new Timer();
                // timer.schedule(task, 0, 1000);
                //
                // while(true){
                // if(isOk){
                // IntentUtil.activityForward(this, MainActivity.class, null,
                // true);
                // finish();
                // showToast("注册成功");
                // break;
                // }
                // if(i >= 10){
                // disMissDialog();
                // showToast("网络超时");
                // break;
                // }
                // }
                showToast("注册成功");
                IntentUtil.activityForward(this, LoginActivity.class, null,
                        true);
                finish();

            } else {
                showToast(res.getMessage());
            }
        }
    }

}
