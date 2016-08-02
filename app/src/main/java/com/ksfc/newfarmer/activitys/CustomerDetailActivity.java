package com.ksfc.newfarmer.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.db.DBManager;
import com.ksfc.newfarmer.event.ChangePotentialEvent;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.beans.PotentialCustomerDetailResult;
import com.ksfc.newfarmer.beans.dbbeans.PotentialCustomersEntity;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.Utils;



import org.greenrobot.eventbus.EventBus;

import greendao.DaoSession;

/**
 * Created by HePeng on 2016/2/2.
 */
public class CustomerDetailActivity extends BaseActivity {
    private TextView name, phone, choice_city_text, choice_town_text, intent_product_text;
    private TextView sex;
    private String _id;
    private ImageView phone_tv_icon;
    private TextView choice_remark_text;


    public static final String ARG_PARAM1 = "param1";

    public static Intent getCallingIntent(Context context, String _id) {
        Intent callingIntent = new Intent(context, CustomerDetailActivity.class);
        callingIntent.putExtra(ARG_PARAM1, _id);
        return callingIntent;
    }



    @Override
    public int getLayout() {
        return R.layout.activity_potential_detail;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        initView();
        setTitle("客户详情");
        _id = getIntent().getStringExtra(ARG_PARAM1);
        showProgressDialog();
        getData();
    }

    private void getData() {
        RequestParams params = new RequestParams();
        if (isLogin()) {
            params.put("userId", Store.User.queryMe().userid);
            params.put("&_id", _id);
            execApi(ApiType.GET_POTENTIAL_CUSTOMER_DETAIL
                    .setMethod(ApiType.RequestMethod.GET), params);
        }
    }

    private void initView() {
        name = (TextView) findViewById(R.id.name_tv);
        phone = (TextView) findViewById(R.id.phone_tv);
        setViewClick(R.id.phone_lin);
        sex = (TextView) findViewById(R.id.choice_sex_text);
        phone_tv_icon = (ImageView) findViewById(R.id.phone_tv_icon);
        choice_city_text = (TextView) findViewById(R.id.choice_city_text);
        choice_town_text = (TextView) findViewById(R.id.choice_town_text);
        intent_product_text = (TextView) findViewById(R.id.choice_type_text); //意向商品
        choice_remark_text = (TextView) findViewById(R.id.choice_remark_text); //备注
    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.phone_lin:            //点击拨打电话
                if (isMobileNum(phone.getText().toString().trim())) {
                    Utils.dial(this, phone.getText().toString().trim());
                }
                break;
        }
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
                        phone_tv_icon.setVisibility(View.VISIBLE);
                    }
                    if (data.potentialCustomer.sex) {
                        sex.setText("女");
                    } else {
                        sex.setText("男");
                    }

                    if (StringUtil.checkStr(data.potentialCustomer.remarks)) {
                        choice_remark_text.setText(data.potentialCustomer.remarks);
                    }

                    if (data.potentialCustomer.buyIntentions != null
                            && !data.potentialCustomer.buyIntentions.isEmpty()) {

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

                    //如果有变化更新客户的数据库
                    if (data.potentialCustomer != null) {

                        DaoSession writableDaoSession = DBManager.getInstance(CustomerDetailActivity.this).getWritableDaoSession();
                        PotentialCustomersEntity customersEntity =
                                writableDaoSession.load(PotentialCustomersEntity.class, _id);

                        if (!(customersEntity.name.equals(data.potentialCustomer.name)
                                && customersEntity.isRegistered == data.potentialCustomer.isRegistered
                                && customersEntity.namePinyin.equals(data.potentialCustomer.namePinyin)
                                && customersEntity.nameInitial.equals(data.potentialCustomer.nameInitial)
                                && customersEntity.phone.equals(data.potentialCustomer.phone)
                                && customersEntity.sex == data.potentialCustomer.sex
                                && customersEntity.nameInitialType == data.potentialCustomer.nameInitialType
                        )) {


                            customersEntity.name = data.potentialCustomer.name;
                            customersEntity.isRegistered = data.potentialCustomer.isRegistered;
                            customersEntity.namePinyin = data.potentialCustomer.namePinyin;
                            customersEntity.nameInitial = data.potentialCustomer.nameInitial;
                            customersEntity.phone = data.potentialCustomer.phone;
                            customersEntity.sex = data.potentialCustomer.sex;
                            customersEntity.nameInitialType = data.potentialCustomer.nameInitialType;
                            writableDaoSession.update(customersEntity);
                            EventBus.getDefault().post(new ChangePotentialEvent());
                        }

                    }
                }
            }
        }
    }

}
