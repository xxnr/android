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
import com.ksfc.newfarmer.fragment.InviteFriendsList;
import com.ksfc.newfarmer.fragment.MyInviter;
import com.ksfc.newfarmer.fragment.PotentialCustomer;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.PotentialListResult;

import net.yangentao.util.PreferenceUtil;

public class NewFramerInvite extends BaseActivity implements
        OnCheckedChangeListener {
    private RadioGroup radioGroup;
    private InviteFriendsList friendsList;
    private FragmentManager fragmentManager;
    private MyInviter myInviter;
    private PotentialCustomer potentialCustomer;
    private boolean isXXNRAgent=false;
    private RelativeLayout customer_bg;

    private int potentialCount=0;

    @Override
    public int getLayout() {
        // TODO Auto-generated method stub
        return R.layout.invite_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        setTitle("新农代表");
        if (isLogin()){
            isXXNRAgent = Store.User.queryMe().isXXNRAgent;
        }
        initView();
        //判断经纪人下的好友数量 如果数量为0 展示引导页
        if (isXXNRAgent){
            getData();
        }


    }

    private void getData() {
        RequestParams params = new RequestParams();
        if (isLogin()){
            execApi(ApiType.GET_POTENTIAL_CUSTOMER_LIST.setMethod(ApiType.RequestMethod.GET)
                    .setOpt("/api/v2.1/potentialCustomer/query" + "?token=" + Store.User.queryMe().token + "&page=" + 1), params);
        }
    }

    //设置返回监听的，回传数据
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (customer_bg.getVisibility()==View.VISIBLE){
                customer_bg.setVisibility(View.GONE);
            }else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        // TODO Auto-generated method stub

        customer_bg = ((RelativeLayout) findViewById(R.id.customer_reg_bg));
        customer_bg.setOnClickListener(this);

        friendsList = new InviteFriendsList();
        myInviter = new MyInviter();
        potentialCustomer = new PotentialCustomer();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction tanTransaction = fragmentManager.beginTransaction();
        tanTransaction.add(R.id.newframentfragment, friendsList);
        tanTransaction.commit();

        RadioButton radioButton = (RadioButton) findViewById(R.id.radio_button3);//客户登记按钮
        if (isXXNRAgent) {
            //如果是新农经纪人
            radioButton.setVisibility(View.VISIBLE);
        } else {
            radioButton.setVisibility(View.GONE);
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
        if (req.getApi() == ApiType.GET_POTENTIAL_CUSTOMER_LIST) {
            PotentialListResult data = (PotentialListResult) req.getData();
            if (data.getStatus().equals("1000")) {
                potentialCount=data.count;
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
            if (!fragmentManager.getFragments().contains(myInviter)){
                tanTransaction.add(R.id.newframentfragment, myInviter);
            }
            tanTransaction.show(myInviter);
            tanTransaction.hide(friendsList);
            tanTransaction.hide(potentialCustomer);
        } else if (checkedId == R.id.radio_button3) {
            //是否是第一次到客户登记tab 如果是的话展示引导

            PreferenceUtil pu = new PreferenceUtil();
            pu.init(this, "config");
            boolean isFirst = pu.getBool("first3", true);
            if (isFirst&&potentialCount==0) {
                customer_bg.setVisibility(View.VISIBLE);
            }
            pu.putBool("first3", false);

            if (!fragmentManager.getFragments().contains(potentialCustomer)){
                tanTransaction.add(R.id.newframentfragment, potentialCustomer);
            }
            tanTransaction.show(potentialCustomer);
            tanTransaction.hide(myInviter);
            tanTransaction.hide(friendsList);

        }
        tanTransaction.commit();
    }


}
