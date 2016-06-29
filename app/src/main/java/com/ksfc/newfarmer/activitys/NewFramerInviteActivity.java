package com.ksfc.newfarmer.activitys;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.fragment.InviteFriendsListFragment;
import com.ksfc.newfarmer.fragment.MyInviterFragment;
import com.ksfc.newfarmer.fragment.PotentialCustomer;
import com.ksfc.newfarmer.http.ApiType;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.http.RequestParams;
import com.ksfc.newfarmer.http.beans.CustomerIsLatestResult;
import com.ksfc.newfarmer.http.beans.LoginResult;
import com.ksfc.newfarmer.http.beans.PersonalData;
import com.ksfc.newfarmer.utils.IntentUtil;

import net.yangentao.util.PreferenceUtil;


public class NewFramerInviteActivity extends BaseActivity implements
        OnCheckedChangeListener {
    private InviteFriendsListFragment friendsList;
    private FragmentManager fragmentManager;
    private MyInviterFragment myInviter;
    private PotentialCustomer potentialCustomer;
    private boolean isXXNRAgent = false;
    private RelativeLayout customer_bg;

    private int potentialCount = 0;//0需要展示提示bg 1不需要
    private RadioButton radioButton3;
    private RadioGroup radioGroup;

    @Override
    public int getLayout() {
        // TODO Auto-generated method stub
        return R.layout.activity_invite;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        setTitle("新农代表");

        if (isLogin()) {
            isXXNRAgent = Store.User.queryMe().isXXNRAgent;
        }
        initView();
        //判断经纪人下的好友数量 如果数量为0 展示引导页
        if (isXXNRAgent) {
            getIsLatest(0);
        }

        showRightImage();
        setRightImage(R.drawable.search_icon);
        setRightViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.activityForward(NewFramerInviteActivity.this, InviterSearchActivity.class, null, false);
                int version = Integer.valueOf(android.os.Build.VERSION.SDK);
                if (version > 5) {
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });

        getUser();


    }


    /**
     * 获取个人信息
     */
    private void getUser() {
        RequestParams params = new RequestParams();
        if (isLogin()) {
            params.put("userId", Store.User.queryMe().userid);
        }
        params.put("flags", "address");
        execApi(ApiType.PERSONAL_CENTER, params);
    }


    //获取客户信息
    private void getIsLatest(int count) {
        RequestParams params = new RequestParams();
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            params.put("userId", userInfo.userid);
            params.put("count", count);
            execApi(ApiType.GET_POTENTIAL_CUSTOMER_ISLATEST.setMethod(ApiType.RequestMethod.GET)
                    , params);
        }
    }

    //设置返回监听的，回传数据
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (customer_bg.getVisibility() == View.VISIBLE) {
                customer_bg.setVisibility(View.GONE);
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        // TODO Auto-generated method stub

        customer_bg = ((RelativeLayout) findViewById(R.id.customer_reg_bg));

        friendsList = new InviteFriendsListFragment();
        myInviter = new MyInviterFragment();
        potentialCustomer = new PotentialCustomer();

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction tanTransaction = fragmentManager.beginTransaction();
        tanTransaction.add(R.id.newframentfragment, friendsList);
        tanTransaction.commitAllowingStateLoss();

        radioButton3 = (RadioButton) findViewById(R.id.radio_button3);//客户登记按钮

        if (isXXNRAgent) {
            radioButton3.setVisibility(View.VISIBLE); //如果是新农经纪人
        } else {
            radioButton3.setVisibility(View.GONE);
        }
        // 设置默认选中
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup_invite);
        radioGroup.setOnCheckedChangeListener(this);
        radioGroup.check(R.id.radio_button1);
        setViewClick(R.id.customer_reg_bg_button);
    }

    @Override
    public void OnViewClick(View v) {
        // TODO Auto-generated method stub

        switch (v.getId()) {
            case R.id.customer_reg_bg_button:
                customer_bg.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_POTENTIAL_CUSTOMER_ISLATEST) {
            if (req.getData().getStatus().equals("1000")) {
                CustomerIsLatestResult data = (CustomerIsLatestResult) req.getData();
                if (data.needUpdate == 1) {
                    potentialCount = 1;
                } else if (data.needUpdate == 0) {
                    potentialCount = 0;
                }
            }
        } else if (req.getApi() == ApiType.PERSONAL_CENTER) {

            PersonalData data = (PersonalData) req.getData();
            PersonalData.Data user = data.datas;
            if (user != null) {
                isXXNRAgent = user.isXXNRAgent;
            }

            if (isXXNRAgent) {
                //如果是新农经纪人
                radioButton3.setVisibility(View.VISIBLE);
            } else {
                radioButton3.setVisibility(View.GONE);
                if (radioButton3.isChecked()){
                    radioGroup.check(R.id.radio_button1);
                }

            }

        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // TODO Auto-generated method stub

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction tanTransaction = fragmentManager.beginTransaction();
        if (checkedId == R.id.radio_button1) {
            tanTransaction.show(friendsList);
            tanTransaction.hide(myInviter);
            tanTransaction.hide(potentialCustomer);
        } else if (checkedId == R.id.radio_button2) {
            if (!fragmentManager.getFragments().contains(myInviter)) {
                tanTransaction.add(R.id.newframentfragment, myInviter);
            }
            tanTransaction.show(myInviter);
            tanTransaction.hide(friendsList);
            tanTransaction.hide(potentialCustomer);
        } else if (checkedId == R.id.radio_button3) {
            //是否是第一次到客户登记tab 如果是的话展示引导
            PreferenceUtil pu = new PreferenceUtil(this, "config");
            boolean isFirst = pu.getBool("first3", true);
            if (isFirst && potentialCount == 0) {
                customer_bg.setVisibility(View.VISIBLE);
            }
            pu.putBool("first3", false);

            if (!fragmentManager.getFragments().contains(potentialCustomer)) {
                tanTransaction.add(R.id.newframentfragment, potentialCustomer);
            }
            tanTransaction.show(potentialCustomer);
            tanTransaction.hide(myInviter);
            tanTransaction.hide(friendsList);

        }
        tanTransaction.commitAllowingStateLoss();
    }


}
