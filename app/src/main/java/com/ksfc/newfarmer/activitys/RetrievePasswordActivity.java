package com.ksfc.newfarmer.activitys;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.beans.PublicKeyResult;
import com.ksfc.newfarmer.beans.SmsResult;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.RSAUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.ClearEditText;
import com.ksfc.newfarmer.widget.dialog.CustomDialogForSms;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class RetrievePasswordActivity extends BaseActivity {
    private ClearEditText backedit1, backnewpassword, confimPasword;
    private EditText backyanzhengma;
    private TextView backgetVerificationCode;
    private String mobile;
    private String password, smsCode;
    private CustomDialogForSms dialog;

    @Override
    public int getLayout() {
        return R.layout.activity_retrievepassword;
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
                    showToast(getString(R.string.please_input_phone));
                    return;
                } else if (!isMobileNum(backedit1.getText().toString())) {
                    showToast(getString(R.string.please_input_right_phone));
                    return;
                } else if (!StringUtil.checkStr(backyanzhengma.getText().toString())) {
                    showToast(getString(R.string.input_sms_code));
                    return;
                } else if (backnewpassword.getText().toString().isEmpty()) {
                    showToast(getString(R.string.input_password));
                    return;
                } else if (confimPasword.getText().toString().isEmpty()) {
                    showToast(getString(R.string.input_confirm_password));
                    return;
                } else if (!password.equals(backnewpassword.getText().toString())) {
                    showToast(getString(R.string.password_not_fit));
                    return;
                } else if (backnewpassword.getText().toString().length() < 6) {
                    showToast(getString(R.string.password_lt_6));
                    return;
                } else if (backnewpassword.getText().toString().length() > 20) {
                    showToast(getString(R.string.password_gt_20));
                    return;
                }
                showProgressDialog();
                execApi(ApiType.GET_PUBLIC_KEY, new RequestParams());
                break;
            case R.id.backgetVerificationCode:
                // 获取验证码
                getCode();
                break;
            case R.id.sms_auth_code_iv:
            case R.id.sms_auth_code_refresh_iv:
                try {
                    CustomDialogForSms.sms_auth_code_iv.setEnabled(false);
                    CustomDialogForSms.sms_auth_code_refresh_iv.setEnabled(false);
                    CustomDialogForSms.sms_auth_code_iv.setImageResource(0);
                    reFreshCode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                showToast("找回密码成功");
                IntentUtil.activityForward(RetrievePasswordActivity.this,
                        LoginActivity.class, null, true);
            }
        } else if (req.getApi() == ApiType.SEND_SMS) {
            if (req.getData().getStatus().equals("1000")) {
                SmsResult smsResult = (SmsResult) req.getData();
                if (StringUtil.checkStr(smsResult.captcha)) {
                    if (dialog == null) {
                        CustomDialogForSms.Builder builder = new CustomDialogForSms.Builder(
                                RetrievePasswordActivity.this);
                        builder.setMessage("安全验证")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String trim = CustomDialogForSms.editText.getText().toString().trim();
                                        if (StringUtil.checkStr(trim)) {
                                            CustomDialogForSms.code_error.setVisibility(View.INVISIBLE);
                                            sendSMS(trim);
                                        } else {
                                            CustomDialogForSms.code_error.setText("请输入图形验证码");
                                            CustomDialogForSms.code_error.setVisibility(View.VISIBLE);
                                        }
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false);
                        CustomDialogForSms.sms_auth_code_iv.setOnClickListener(this);
                        CustomDialogForSms.sms_auth_code_refresh_iv.setOnClickListener(this);

                    }
                    CustomDialogForSms.code_error.setVisibility(View.INVISIBLE);
                    CustomDialogForSms.editText.setText("");

                    if (StringUtil.checkStr(smsResult.getMessage())) {
                        CustomDialogForSms.code_error.setVisibility(View.VISIBLE);
                        CustomDialogForSms.code_error.setText(smsResult.getMessage());
                    }

                    try {
                        if (!dialog.isShowing()) {
                            dialog.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Picasso.with(RetrievePasswordActivity.this)
                            .load(smsResult.captcha)
                            .noFade()
                            .skipMemoryCache()
                            .config(Bitmap.Config.RGB_565)
                            .error(R.drawable.code_load_failed)
                            .into(CustomDialogForSms.sms_auth_code_iv);
                } else {
                    try {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    showToast("验证码发送成功，请注意查收");
                    MyCount mc = new MyCount(60000, 1000);
                    mc.start();
                }
            } else {
                if (StringUtil.checkStr(req.getData().getMessage())) {
                    try {
                        if (dialog != null && dialog.isShowing()) {
                            CustomDialogForSms.code_error.setVisibility(View.VISIBLE);
                            CustomDialogForSms.code_error.setText(req.getData().getMessage());
                        } else {
                            showToast(req.getData().getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 获取验证码
     */
    private void getCode() {

        mobile = backedit1.getText().toString();
        if (!StringUtil.checkStr(mobile)) {
            showToast(getString(R.string.please_input_phone));
            return;
        }
        if (!isMobileNum(mobile)) {
            showToast(getString(R.string.please_input_right_phone));

            return;
        }
        sendSMS();
    }

    /**
     * 发送验证码
     */
    private void sendSMS() {
        sendSMS(null);
    }

    /**
     * 发送验证码
     */
    private void sendSMS(String authCode) {
        showProgressDialog();
        RequestParams params = new RequestParams();
        params.put("tel", mobile);
        params.put("bizcode", "resetpwd");
        if (StringUtil.checkStr(authCode)) {
            params.put("authCode", authCode);
        }
        execApi(ApiType.SEND_SMS, params);
    }

    /**
     * 刷新图形验证码
     */
    private void reFreshCode() {

        Picasso.with(RetrievePasswordActivity.this)
                .load(ApiType.REFRESH_SMS_CODE.getOpt() + "?tel=" + mobile + "&bizcode=resetpwd")
                .skipMemoryCache()
                .config(Bitmap.Config.RGB_565)
                .noFade()
                .error(R.drawable.code_load_failed)
                .into(CustomDialogForSms.sms_auth_code_iv, new Callback() {
                    @Override
                    public void onSuccess() {
                        CustomDialogForSms.sms_auth_code_iv.setEnabled(true);
                        CustomDialogForSms.sms_auth_code_refresh_iv.setEnabled(true);
                    }

                    @Override
                    public void onError() {
                        CustomDialogForSms.sms_auth_code_iv.setEnabled(true);
                        CustomDialogForSms.sms_auth_code_refresh_iv.setEnabled(true);
                    }
                });
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
            backgetVerificationCode.setText("(" + millisUntilFinished / 1000 + ")秒后重试");
        }
    }

}