package com.ksfc.newfarmer.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.db.XUtilsDb.XUtilsDbHelper;
import com.ksfc.newfarmer.http.ApiType;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.http.RequestParams;
import com.ksfc.newfarmer.http.beans.PotentialCustomerDetailResult;
import com.ksfc.newfarmer.http.beans.PotentialListResult;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.Utils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import com.ksfc.newfarmer.App;
import net.yangentao.util.msg.MsgCenter;

/**
 * Created by HePeng on 2016/2/2.
 */
public class CustomerDetailActivity extends BaseActivity {
    private TextView name, phone, choice_city_text, choice_town_text, intent_product_text;
    private TextView sex;
    private String _id;
    private ImageView phone_tv_icon;
    private TextView choice_remark_text;

    @Override
    public int getLayout() {
        return R.layout.activity_potential_detail;
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
        //点击拨打电话
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

            case R.id.phone_lin:
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

                        DbUtils dbUtils = XUtilsDbHelper.getInstance(CustomerDetailActivity.this, App.getApp().getUid());
                        try {
                            PotentialListResult.PotentialCustomersEntity customersEntity =
                                    dbUtils.findById(PotentialListResult.PotentialCustomersEntity.class, _id);

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
                                dbUtils.saveOrUpdate(customersEntity);
                                MsgCenter.fireNull(MsgID.change_potential_success, "change");
                            }

                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }


                }


            }
        }


    }
}
