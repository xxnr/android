package com.ksfc.newfarmer.activitys;

import java.security.acl.Group;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.fragment.InviteFriendsList;
import com.ksfc.newfarmer.fragment.MyInviter;
import com.ksfc.newfarmer.protocol.Request;

public class NewFramerInvite extends BaseActivity implements
		OnCheckedChangeListener {
	private RadioGroup radioGroup;
	private InviteFriendsList friendsList;
	private FragmentManager fragmentManager;
	private MyInviter myInviter;

	@Override
	public int getLayout() {
		// TODO Auto-generated method stub
		return R.layout.invite_layout;
	}

	@Override
	public void OnActCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setTitle("新农代表");
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup_invite);
		friendsList = new InviteFriendsList();
		myInviter = new MyInviter();
		fragmentManager = getSupportFragmentManager();
		FragmentTransaction tanTransaction = fragmentManager.beginTransaction();
		tanTransaction.add(R.id.newframentfragment, friendsList);
		tanTransaction.commit();
		// 设置默认选中
		radioGroup.check(radioGroup.getChildAt(0).getId());
		radioGroup.setOnCheckedChangeListener(this);
	}

	@Override
	public void OnViewClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResponsed(Request req) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub

		fragmentManager = getSupportFragmentManager();
		FragmentTransaction tanTransaction = fragmentManager.beginTransaction();
		if (checkedId == group.getChildAt(0).getId()) {
			tanTransaction.replace(R.id.newframentfragment, friendsList);
		} else {
			tanTransaction.replace(R.id.newframentfragment, myInviter);
		}
		tanTransaction.commit();
	}

}
