package com.ksfc.newfarmer.activitys;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MainActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.FindPassResult;
import com.ksfc.newfarmer.protocol.beans.GetCodeResult;
import com.ksfc.newfarmer.protocol.beans.PublicKeyResult;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.RSAUtil;

import android.annotation.TargetApi;
import android.content.Intent;
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
public class ChangePasswordActivity extends BaseActivity {
	private EditText backedit1, backnewpassword, confimPasword;
	private TextView backdengLubutton;
	private String oldpwd;
	private String password;
	private String confim;

	@Override
	public int getLayout() {
		// TODO Auto-generated method stub
		return R.layout.changepassword_layout;
	}

	@Override
	public void OnActCreate(Bundle savedInstanceState) {
		backdengLubutton = (TextView) findViewById(R.id.backdengLubutton);
		backedit1 = (EditText) findViewById(R.id.backedit1);
		backnewpassword = (EditText) findViewById(R.id.backnewpassword);
		confimPasword = (EditText) findViewById(R.id.confimPasword);
		setViewClick(R.id.backdengLubutton);
		setTitle("修改密码");
		setLeftClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	public void OnViewClick(View v) {
		if (v.getId() == R.id.backdengLubutton) {
			oldpwd = backedit1.getText().toString();
			password = backnewpassword.getText().toString();
			confim = confimPasword.getText().toString();
			if (TextUtils.isEmpty(oldpwd)) {
				showToast("请填写原密码");
				return;
			} else if (TextUtils.isEmpty(password)) {
				showToast("请填写新密码");
				return;
			} else if (TextUtils.isEmpty(confim)) {
				showToast("请填写确认密码");
				return;
			} else if (!password.equals(confim)) {
				showToast("密码不一致");
				return;
			} else if (password.length() > 20) {
				showToast("密码长度不能大于20位");
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
			params.put("userId", Store.User.queryMe().userid);
			try {
				params.put(
						"newPwd",
						RSAUtil.encryptByPublicKey(password,
								RSAUtil.generatePublicKey(res.public_key)));
				params.put(
						"oldPwd",
						RSAUtil.encryptByPublicKey(oldpwd,
								RSAUtil.generatePublicKey(res.public_key)));
				execApi(ApiType.CHANGE_PASSWORD, params);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else if (req.getApi() == ApiType.CHANGE_PASSWORD) {
			FindPassResult res = (FindPassResult) req.getData();
			if ("1000".equals(res.getStatus())) {
				// 找回密码成功后 就跳到MainActivity
				showToast("修改密码成功");
				Store.User.removeMe();
				IntentUtil.activityForward(ChangePasswordActivity.this,
						LoginActivity.class, null, true);
				App.getApp().quit();

			} else {
				showToast("修改密码失败");
			}
		}
	}

}
