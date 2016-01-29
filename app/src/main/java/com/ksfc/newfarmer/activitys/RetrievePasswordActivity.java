package com.ksfc.newfarmer.activitys;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.ApiType.RequestMethod;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.FindPassResult;
import com.ksfc.newfarmer.protocol.beans.GetCodeResult;
import com.ksfc.newfarmer.protocol.beans.PublicKeyResult;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.RSAUtil;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import net.yangentao.util.app.App;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class RetrievePasswordActivity extends BaseActivity {
	private EditText backedit1, backyanzhengma, backnewpassword, confimPasword;
	private TextView backgetVerificationCode, backdengLubutton;
	private String mobile;
	private String password, smsCode;

	@Override
	public int getLayout() {
		return R.layout.retrievepassword_layout;
	}

	@Override
	public void OnActCreate(Bundle savedInstanceState) {
		backdengLubutton = (TextView) findViewById(R.id.backdengLubutton);
		backgetVerificationCode = (TextView) findViewById(R.id.backgetVerificationCode);
		backedit1 = (EditText) findViewById(R.id.backedit1);
		backyanzhengma = (EditText) findViewById(R.id.backyanzhengma);
		backnewpassword = (EditText) findViewById(R.id.backnewpassword);
		confimPasword = (EditText) findViewById(R.id.confimPasword);
		setViewClick(R.id.backgetVerificationCode);
		setViewClick(R.id.backdengLubutton);
		setTitle("忘记密码");
	}

	@Override
	public void OnViewClick(View v) {
		String phoneNumber = backedit1.getText().toString();
		password = confimPasword.getText().toString();
		smsCode = backyanzhengma.getText().toString();

		if (!isMobileValid(phoneNumber)) {
			return;
		}
		switch (v.getId()) {
		case R.id.backdengLubutton:
			if (!isMobileNum(backedit1.getText().toString())) {
				Toast.makeText(this, "手机号格式错误", Toast.LENGTH_SHORT).show();
				return;
			} else if (backedit1.getText().toString().isEmpty()) {
				Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
				return;
			} else if (backyanzhengma.getText().toString().isEmpty()) {
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
			params.put("account", mobile);
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
			FindPassResult res = (FindPassResult) req.getData();
			if ("1000".equals(res.getStatus())) {
				// 找回密码成功后 就跳到MainActivity
				showToast("修改密码成功");
				IntentUtil.activityForward(RetrievePasswordActivity.this,
						LoginActivity.class, null, true);
				App.getApp().quit();
			} else {
				showToast("修改密码失败");
			}
		} else if (req.getApi() == ApiType.SEND_SMS) {
			GetCodeResult res = (GetCodeResult) req.getData();
			if ("1000".equals(res.getStatus())) {
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
		} else {
			sendSMS();

		}

	}

	/**
	 * 发送验证码
	 */
	private void sendSMS() {
		showProgressDialog("正在获取验证码");
		RequestParams params = new RequestParams();
		execApi(ApiType.SEND_SMS.setMethod(RequestMethod.GET).setOpt(
				"/api/v2.0/sms" + "?tel=" + mobile + "&bizcode=resetpwd"),
				params);
	}

	/**
	 * 验证注册码
	 */
	private void checkCode() {
		if (!isMobileNum(backedit1.getText().toString())) {
			Toast.makeText(this, "请填写正确的手机号", Toast.LENGTH_SHORT).show();
			return;
		} else if (backedit1.getText().toString().isEmpty()) {
			Toast.makeText(this, "请填写手机号", Toast.LENGTH_SHORT).show();
			return;
		}
		mobile = backedit1.getText().toString();
		String checkcode = backyanzhengma.getText().toString();

		if (!isMobileValid(mobile))
			return;

		if (TextUtils.isEmpty(checkcode)) {
			showToast("验证码不能为空");
			return;
		}

		// RequestParams params = new RequestParams();
		// params.put("type",1);
		// params.put("mobile",mobile);
		// params.put("code",checkcode);
		//
		// execApi(ApiType.CHECK_SMS, params);
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
