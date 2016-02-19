package com.ksfc.newfarmer.fragment;

import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.ResponseResult;
import com.ksfc.newfarmer.protocol.beans.MyInviteResult;
import com.ksfc.newfarmer.protocol.beans.PersonalData;
import com.ksfc.newfarmer.protocol.beans.PersonalData.Data;
import com.ksfc.newfarmer.utils.Utils;
import com.ksfc.newfarmer.widget.dialog.CustomDialog;

import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyInviter extends BaseFragment implements OnClickListener {

	private EditText edit;//输入代表人的手机号
	private Button addInvite;//添加代表
	private String phoneNumber;//手机号
	private LinearLayout centerLin;//没有代表的布局
	private LinearLayout headLin;//有代表的布局
	private String nickname;//昵称
	private String phoneNum;//手机号
	private TextView nickname_tv;
	private TextView phoneNum_tv;
	private String userId;
	private Data user;
	private String user_phone;

	@Override
	public View InItView() {
		View view = inflater.inflate(R.layout.fragment_my_inviter, null);
		centerLin = (LinearLayout) view.findViewById(R.id.my_inviter_fragment);
		edit = (EditText) view.findViewById(R.id.my_inviter_fragment_edittext);
		addInvite = (Button) view.findViewById(R.id.add_inviter);
		addInvite.setOnClickListener(this);

		headLin = (LinearLayout) view
				.findViewById(R.id.my_inviter_fragment_lin);
		nickname_tv = (TextView) view.findViewById(R.id.my_inviter_nickname);
		phoneNum_tv = (TextView) view.findViewById(R.id.my_inviter_phone);

		headLin.setVisibility(View.GONE);

		RequestParams params = new RequestParams();
		userId = Store.User.queryMe().userid;
		params.put("locationUserId", userId);
		params.put("userId", userId);
		params.put("flags", "address");
		execApi(ApiType.PERSONAL_CENTER, params);
		showProgressDialog();
		return view;
	}

	@Override
	public void onClick(View v) {
		phoneNumber = edit.getText().toString();
		switch (v.getId()) {

		case R.id.add_inviter:

			if (TextUtils.isEmpty(phoneNumber)) {
				showToast("请输入一个手机号码");
				return;
			}

			if (!Utils.isMobileNum(phoneNumber)) {
				showToast("您输入 的手机号码格式不正确");
				return;
			}

			if (phoneNumber.equals(user_phone)) {
				showToast("不能设置自己为新农代表哦");
				return;
			}

			RequestParams params = new RequestParams();
			params.put("account", phoneNumber);
			execApi(ApiType.FIND_USER, params);

			break;
		}

	}


	@Override
	public void onResponsed(Request req) {
		disMissDialog();

		// TODO Auto-generated method stub
		if (req.getApi() == ApiType.GET_BINDINVITER) {
			MyInviteResult data = (MyInviteResult) req.getData();
			if (data.getStatus().equals("1000")) {
				showToast("绑定成功");
				centerLin.setVisibility(View.GONE);
				RequestParams params = new RequestParams();
				userId = Store.User.queryMe().userid;
				params.put("locationUserId", userId);
				params.put("userId", userId);
				params.put("flags", "address");
				execApi(ApiType.PERSONAL_CENTER, params);
			} else {
				showToast(data.getMessage());
			}
		} else if (req.getApi() == ApiType.PERSONAL_CENTER) {
			PersonalData data = (PersonalData) req.getData();
			user = data.datas;
			phoneNum = user.inviter;
			nickname = user.inviterName;
			user_phone = user.phone;

			if (TextUtils.isEmpty(phoneNum)) {
				headLin.setVisibility(View.GONE);
				centerLin.setVisibility(View.VISIBLE);
			} else {
				headLin.setVisibility(View.VISIBLE);
				if (!TextUtils.isEmpty(nickname)) {//如果有昵称 设置一些属性
					nickname_tv.setText(nickname);
					nickname_tv.setTextColor(Color.WHITE);
					nickname_tv.setBackgroundResource(R.drawable.login_roateup);
				}
				phoneNum_tv.setText(phoneNum);
			}
		} else if (req.getApi() == ApiType.FIND_USER) {
			ResponseResult data = (ResponseResult) req.getData();
			if (data.getStatus().equals("1000")) {
				CustomDialog.Builder builder = new CustomDialog.Builder(
						getActivity());
				builder.setMessage("代表人添加后不可修改,确定设置该用户为您的代表吗?")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
														int which) {
										RequestParams params = new RequestParams();
										String uid = Store.User.queryMe().userid;
										params.put("userId", uid);
										params.put("inviter", phoneNumber);
										execApi(ApiType.GET_BINDINVITER, params);
										dialog.dismiss();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
														int which) {
										dialog.dismiss();
									}
								});
				CustomDialog dialog = builder.create();
				dialog.show();
			} else {
				showToast("该手机号未注册，请确认后重新输入");
			}

		}
	}

}
