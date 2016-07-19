package com.ksfc.newfarmer.activitys;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.fragment.RscExchangeFragment;
import com.ksfc.newfarmer.fragment.RscGiftOrderListFragment;
import com.ksfc.newfarmer.fragment.RscOrderListFragment;
import com.ksfc.newfarmer.fragment.RscOrderFragment;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.PopWindowUtils;
import com.ksfc.newfarmer.utils.Utils;

import net.yangentao.util.msg.MsgCenter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HePeng on 2016/3/23.
 */
public class RSCOrderListActivity extends BaseActivity implements RscOrderListFragment.BgSwitch, RadioGroup.OnCheckedChangeListener, RscGiftOrderListFragment.BgSwitch {

    @BindView(R.id.pop_bg)
    RelativeLayout popBg;
    @BindView(R.id.rsc_order_list_radioGroup)
    RadioGroup rscOrderListRadioGroup;
    private FragmentManager fragmentManager;
    private RscExchangeFragment exchangeFragment;
    private RscOrderFragment orderFragment;


    @Override
    public int getLayout() {
        return R.layout.activity_rsc_order_list;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        ButterKnife.bind(this);


        //设置状态栏颜色状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Utils.setBarTint(this, R.color.green);
        }
        setViewClick(R.id.title_left_view);
        setViewClick(R.id.title_right_view);
        exchangeFragment = new RscExchangeFragment();
        orderFragment = new RscOrderFragment();
        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.rsc_order_list_fragment, exchangeFragment, "exChange")
                .add(R.id.rsc_order_list_fragment, orderFragment, "order")
                .commitAllowingStateLoss();

        rscOrderListRadioGroup.setOnCheckedChangeListener(this);
        rscOrderListRadioGroup.check(R.id.rsc_order_list_radioButton1);//默认选中叮单
    }

    //设置返回监听的，回传数据
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(RSCOrderListActivity.this, MainActivity.class);
            intent.putExtra("id",MainActivity.Tab.MINE);
            startActivity(intent);
            MsgCenter.fireNull(MsgID.MainActivity_select_tab,  MainActivity.Tab.MINE);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.title_right_view:
                if (rscOrderListRadioGroup.getCheckedRadioButtonId()==R.id.rsc_order_list_radioButton1){
                    IntentUtil.activityForward(RSCOrderListActivity.this, RscSearchOrderActivity.class, null, false);
                }else {
                    IntentUtil.activityForward(RSCOrderListActivity.this, RscSearchGiftOrderActivity.class, null, false);
                }
                int version = Integer.valueOf(Build.VERSION.SDK);
                if (version > 5) {
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                break;
            case R.id.title_left_view:
                Intent intent = new Intent(RSCOrderListActivity.this, MainActivity.class);
                intent.putExtra("id",MainActivity.Tab.MINE);
                startActivity(intent);
                MsgCenter.fireNull(MsgID.MainActivity_select_tab,  MainActivity.Tab.MINE);
                finish();
                break;
        }
    }

    @Override
    public void onResponsed(Request req) {

    }


    @Override
    public void backgroundSwitch(int bg) {
        PopWindowUtils.setBackgroundBlack(popBg, bg);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (checkedId) {
            case R.id.rsc_order_list_radioButton1:
                transaction.show(orderFragment);
                transaction.hide(exchangeFragment);
                break;
            case R.id.rsc_order_list_radioButton2:
                transaction.show(exchangeFragment);
                transaction.hide(orderFragment);
                break;
        }
        transaction.commitAllowingStateLoss();
    }
}
