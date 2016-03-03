package com.ksfc.newfarmer.activitys;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.FindPassResult;
import com.ksfc.newfarmer.protocol.beans.PublicKeyResult;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.RSAUtil;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;


@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class ChangePasswordActivity extends BaseActivity {
	private EditText old_pass, new_pass, confirm_pass;//旧密码 新密码 确认新密码 的编辑框
	private String old_pwd;
	private String password;
	private String confirm;

	@Override
	public int getLayout() {
		return R.layout.changepassword_layout;
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
			if (TextUtils.isEmpty(old_pwd)) {
				showToast("请填写原密码");
				return;
			} else if (TextUtils.isEmpty(password)) {
				showToast("请填写新密码");
				return;
			} else if (TextUtils.isEmpty(confirm)) {
				showToast("请填写确认密码");
				return;
			} else if (!password.equals(confirm)) {
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
			if (isLogin()){
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
			FindPassResult res = (FindPassResult) req.getData();
			if ("1000".equals(res.getStatus())) {
				// 找回密码成功后 就跳到MainActivity
				showToast("修改密码成功");
				exitLogin();
				IntentUtil.activityForward(ChangePasswordActivity.this,
						LoginActivity.class, null, true);
			} else {
				showToast("修改密码失败");
			}
		}
	}

}
