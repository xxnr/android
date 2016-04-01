package com.ksfc.newfarmer.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.PotentialCustomerDetailResult;
import com.ksfc.newfarmer.utils.StringUtil;

/**
 * Created by HePeng on 2016/2/2.
 */
public class PotentialCustomerDetailActivity extends BaseActivity {
    private TextView name, phone, choice_city_text, choice_town_text, intent_product_text;
    private TextView sex;
    private String _id;

    @Override
    public int getLayout() {
        return R.layout.potential_detail_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        initView();
        setTitle("客户详情");
        _id = getIntent().getStringExtra("_id");
        showProgressDialog();
        getData();
    }

    private void getData() {
        RequestParams params = new RequestParams();
        if (isLogin()){
            params.put("userId", Store.User.queryMe().userid);
            params.put("&_id",_id);
            execApi(ApiType.GET_POTENTIAL_CUSTOMER_DETAIL
                    .setMethod(ApiType.RequestMethod.GET), params);
        }
    }

    private void initView() {
        name = (TextView) findViewById(R.id.name_tv);
        phone = (TextView) findViewById(R.id.phone_tv);
        sex = (TextView) findViewById(R.id.choice_sex_text);
        choice_city_text = (TextView) findViewById(R.id.choice_city_text);
        choice_town_text = (TextView) findViewById(R.id.choice_town_text);
        intent_product_text = (TextView) findViewById(R.id.choice_type_text); //意向商品
    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (req.getApi() == ApiType.GET_POTENTIAL_CUSTOMER_DETAIL) {
            PotentialCustomerDetailResult data = (PotentialCustomerDetailResult) req.getData();
            if (data.getStatus().equals("1000")) {
                if (data.potentialCustomer != null) {
                    if (StringUtil.checkStr(data.potentialCustomer.name)) {
                        name.setText(data.potentialCustomer.name);
                    }
                    if (StringUtil.checkStr(data.potentialCustomer.phone)) {
                        phone.setText(data.potentialCustomer.phone);
                    }
                    if (data.potentialCustomer.sex) {
                        sex.setText("女");
                    } else {
                        sex.setText("男");
                    }

                    if (data.potentialCustomer.buyIntentions != null && !data.potentialCustomer.buyIntentions.isEmpty()) {

                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < data.potentialCustomer.buyIntentions.size(); i++) {
                            if (StringUtil.checkStr(data.potentialCustomer.buyIntentions.get(i).name)) {
                                builder.append(data.potentialCustomer.buyIntentions.get(i).name).append("；");
                            }
                        }

                        if (StringUtil.checkStr(builder.toString().trim())) {
                            String substring = builder.toString().substring(0, builder.toString().length() - 1);
                            intent_product_text.setText(substring);
                        }

                    }

                    if (data.potentialCustomer.address != null) {
                        StringBuilder builder = new StringBuilder();
                        PotentialCustomerDetailResult.PotentialCustomerEntity.AddressEntity address = data.potentialCustomer.address;
                        if (address.province != null) {
                            if (StringUtil.checkStr(address.province.name))
                                builder.append(address.province.name).append(" ");
                        }
                        if (address.city != null) {
                            if (StringUtil.checkStr(address.city.name))
                                builder.append(address.city.name).append(" ");
                        }
                        if (address.county != null) {
                            if (StringUtil.checkStr(address.county.name))
                                builder.append(address.county.name);
                        }
                        choice_city_text.setText(builder);
                        if (address.town != null) {
                            if (StringUtil.checkStr(address.town.name))
                                choice_town_text.setText(address.town.name);
                        }
                    }

                }


            }
        }


    }
}
