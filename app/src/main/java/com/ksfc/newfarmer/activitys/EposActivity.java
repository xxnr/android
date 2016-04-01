package com.ksfc.newfarmer.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.beans.MyOrderDetailResult;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.Utils;


/**
 * Created by HePeng on 2016/3/21.
 */
public class EposActivity extends BaseActivity {
    private TextView pay_price;
    private TextView RSC_companyName;
    private TextView RSC_Address;
    private TextView RSC_phone;
    private TextView pay_sure_tv;

    private LinearLayout state_info_ll;
    private RelativeLayout none_state_info_rel;
    private LinearLayout install_prompt;

    @Override
    public int getLayout() {
        return R.layout.epos_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {

        setTitle("全民付EPOS");
        initView();

    }

    private void initView() {

        pay_price = (TextView) findViewById(R.id.pay_price);
        RSC_companyName = (TextView) findViewById(R.id.RSC_companyName);
        RSC_Address = (TextView) findViewById(R.id.RSC_Address);
        RSC_phone = (TextView) findViewById(R.id.RSC_phone);
        pay_sure_tv = (TextView) findViewById(R.id.pay_sure_tv);
        state_info_ll = (LinearLayout) findViewById(R.id.state_info_ll);
        install_prompt=(LinearLayout) findViewById(R.id.install_prompt);

        none_state_info_rel = (RelativeLayout) findViewById(R.id.none_state_info_rel);
        state_info_ll.setVisibility(View.GONE);
        none_state_info_rel.setVisibility(View.GONE);

        setViewClick(R.id.pay_sure_tv);

        //是否安装插件
        if (!Utils.isPkgInstalled(this, "com.chinaums.mposplugin")) {
            pay_sure_tv.setText("立即支付");
            install_prompt.setVisibility(View.INVISIBLE);
        } else {
            pay_sure_tv.setText("安装插件");
            install_prompt.setVisibility(View.VISIBLE);
        }

        Bundle bundle = getIntent().getExtras();
        MyOrderDetailResult.Datas orderInfo = (MyOrderDetailResult.Datas) bundle.getSerializable("orderInfo");
        if (orderInfo != null && orderInfo.rows != null && orderInfo.rows.RSCInfo != null) {

            state_info_ll.setVisibility(View.VISIBLE);

            if (StringUtil.checkStr(orderInfo.rows.RSCInfo.companyName)) {
                RSC_companyName.setText(orderInfo.rows.RSCInfo.companyName);
            }

            if (StringUtil.checkStr(orderInfo.rows.RSCInfo.RSCAddress)) {
                RSC_Address.setText(orderInfo.rows.RSCInfo.RSCAddress);
            }

            if (StringUtil.checkStr(orderInfo.rows.RSCInfo.RSCPhone)) {
                RSC_phone.setText(orderInfo.rows.RSCInfo.RSCPhone);
            }

        } else {

            none_state_info_rel.setVisibility(View.VISIBLE);

        }

        String payPrice = bundle.getString("payPrice");

        if (StringUtil.checkStr(payPrice)) {
            pay_price.setText("¥" + payPrice);
        } else {
            pay_price.setText("");
        }


    }


    @Override
    public void OnViewClick(View v) {

        switch (v.getId()) {
            case R.id.pay_sure_tv:
                //是否安装插件
                if (Utils.isPkgInstalled(this, "com.chinaums.mposplugin")) {
                    showToast("Epos立即支付");
                } else {
//                    Utils.addApk(this, "mpospluginphone.apk");
                }
                break;

        }

    }

    @Override
    public void onResponsed(Request req) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        //是否安装插件
        if (Utils.isPkgInstalled(this, "com.chinaums.mposplugin")) {
            pay_sure_tv.setText("立即支付");
            install_prompt.setVisibility(View.INVISIBLE);
        } else {
            pay_sure_tv.setText("安装插件");
            install_prompt.setVisibility(View.VISIBLE);
        }
    }
}
