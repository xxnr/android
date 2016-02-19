/**
 * 
 */
package com.ksfc.newfarmer.fragment;

import java.lang.reflect.Field;
import java.util.HashMap;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.activitys.LoginActivity;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.OnApiDataReceivedCallback;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.LoginResult;
import com.ksfc.newfarmer.widget.dialog.CustomProgressDialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.BadTokenException;
import net.yangentao.util.app.App;

/**
 * 项目名称：newFarmer63 类名称：BaseFragment 类描述： 创建人：王蕾 创建时间：2015-6-3 上午9:29:34 修改备注：
 */
public abstract class BaseFragment extends Fragment implements
		OnApiDataReceivedCallback {

	public BaseActivity activity;
	public LayoutInflater inflater;
	private Dialog progressDialog;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity = (BaseActivity) getActivity();
		this.inflater = inflater;
		return InItView();
	}

	/**
	 * 加载本地布局文件
	 * */
	public abstract View InItView();

	// 调用此方法 去登录页面
	public void tokenToLogin() {
		Store.User.removeMe();
		Intent intent = new Intent(getActivity().getApplicationContext(),
				LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		App.getApp().exit();
		getActivity().getApplicationContext().startActivity(intent);
	}

	@Override
	public void onResponse(Request req) {
		if (req.getData().getStatus().equals("1401")) {
			req.showErrorMsg();
			tokenToLogin();
		} else {
			disMissDialog();
			if (req.isSuccess()) {
				onResponsed(req);
			} else {

				req.showErrorMsg();
			}
		}

	}

	public abstract void onResponsed(Request req);

	/**
	 * 短时间显示Toast
	 * 
	 * @param info
	 *            显示的内容
	 */
	public void showToast(String info) {
		App.getApp().showToast(info);
	}

	/**
	 * 显示正在加载的进度条
	 * 
	 */
	public void showProgressDialog() {
		showProgressDialog("加载中...");
	}

	public void showProgressDialog(String msg) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		progressDialog = CustomProgressDialog
				.createLoadingDialog(activity, msg);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(true);
		try {
			progressDialog.show();
		} catch (BadTokenException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * 取消对话框显示
	 */
	public void disMissDialog() {
		try {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 执行网络请求
	 * 
	 * @param api
	 * 
	 * @param params
	 */
	public void execApi(ApiType api, RequestParams params) {
		execApis.put(api, params);
		Request req = new Request();
		req.setApi(api);
		if (params == null)
			params = new RequestParams();
		// 判断是不是通过验证
		if (params.containsKey("userId")) {
			LoginResult.UserInfo userInfo = Store.User.queryMe();
			if (userInfo!=null){
				params.put("token", userInfo.token);
			}

		}
		req.setParams(params);
		req.executeNetworkApi(this);
	}

	private boolean isShowDialog;
	HashMap<ApiType, RequestParams> execApis = new HashMap<ApiType, RequestParams>();

	@SuppressLint("NewApi")
	public void showDialog(final Context context, String message,
			String nbText, String pbText, final int flag) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context,
				AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("温馨提示")
				.setMessage(message)
				.setNegativeButton(nbText,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								isShowDialog = false;
								// 进行以下设置将不能关闭dialog
								try {
									Field field = dialog.getClass()
											.getSuperclass()
											.getDeclaredField("mShowing");
									field.setAccessible(true);
									field.set(dialog, true);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						})
				.setNeutralButton("重试", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						for (ApiType api : execApis.keySet()) {
							execApi(api, execApis.get(api));
							// RndLog.i("当前执行=======>" + api.toString());
						}
						isShowDialog = false;
						// 进行以下设置将不能关闭dialog
						try {
							Field field = dialog.getClass().getSuperclass()
									.getDeclaredField("mShowing");
							field.setAccessible(true);
							field.set(dialog, true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				})
				.setPositiveButton(pbText,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Intent intent = new Intent(
								// Settings.ACTION_SETTINGS);
								// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								// context.startActivity(intent);
								// 进行以下设置将不能关闭dialog
								try {
									Field field = dialog.getClass()
											.getSuperclass()
											.getDeclaredField("mShowing");
									field.setAccessible(true);
									field.set(dialog, false);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).create().show();
		isShowDialog = true;
	}

}
