package com.ksfc.newfarmer.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.IsPotentialCustomerResult;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.MaxLengthWatcher;
import com.ksfc.newfarmer.utils.StringUtil;

import net.yangentao.util.msg.MsgCenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HePeng on 2015/12/10.
 */
public class AddPotentialActivity extends BaseActivity {
    private EditText name;
    private final int cityRequestCode = 1;//省市区
    private final int townRequestCode = 2;//乡镇

    private TextView choice_city_text;
    private TextView choice_town_text;
    private TextView intent_product_text;

    private String queueid = "";
    private String buildid = "";
    private String cityareaid;
    private String townid;


    private String city;
    private String town;


    private Map<String, String> map = new HashMap<>();
    private CheckBox boy_box;
    private CheckBox girl_box;
    private EditText phone;
    private TextView phone_error;
    private LinearLayout phone_error_ll;
    private TextView dividing_line;
    private List<String> productIdList;
    //以下是_id
    private String cityareaid2;
    private String queueid2;
    private String buildid2;
    private String townid2;
    private EditText add_potential_remark;


    @Override
    public int getLayout() {
        return R.layout.add_potential_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("添加潜在客户");
        intView();
    }


    private void intView() {
        name = (EditText) findViewById(R.id.name_tv);
        //限制字数
        name.addTextChangedListener(new MaxLengthWatcher(12, name, AddPotentialActivity.this));
        phone = (EditText) findViewById(R.id.phone_tv);
        phone_error = (TextView) findViewById(R.id.phone_error);
        phone_error_ll = (LinearLayout) findViewById(R.id.phone_error_ll);//手机号能添加为潜在用户的提示框
        dividing_line = (TextView) findViewById(R.id.dividing_line);//分割线
        choice_city_text = (TextView) findViewById(R.id.choice_city_text);
        choice_town_text = (TextView) findViewById(R.id.choice_town_text);
        intent_product_text = (TextView) findViewById(R.id.choice_type_text); //意向商品
        add_potential_remark = (EditText) findViewById(R.id.add_potential_remark);//备注

        boy_box = (CheckBox) findViewById(R.id.btn_check_item_item);
        girl_box = (CheckBox) findViewById(R.id.btn_check_item_item1);
        boy_box.setChecked(true);//默认选中性别 男

        //输到11位 自动 判断该客户的登记状态和注册状态
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isMobileNum(s.toString())) {
                    RequestParams params = new RequestParams();
                    if (isLogin()) {
                        params.put("userId", Store.User.queryMe().userid);
                        params.put("phone", s.toString().trim());
                        execApi(ApiType.IS_POTENTIAL_CUSTOMER
                                .setMethod(ApiType.RequestMethod.GET), params);
                    }
                } else {
                    if (s.length() == 11) {
                        showToast("请输入正确的手机号");
                    } else {
                        phone_error_ll.setVisibility(View.GONE);
                        dividing_line.setVisibility(View.GONE);
                    }
                }

            }
        });

        boy_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    girl_box.setChecked(false);
                } else {
                    girl_box.setChecked(true);
                }
            }
        });

        girl_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    boy_box.setChecked(false);
                } else {
                    boy_box.setChecked(true);
                }
            }
        });


        setViewClick(R.id.choice_city_layout);
        setViewClick(R.id.choice_town_layout);
        setViewClick(R.id.choose_type_ll);
        setViewClick(R.id.choice_compelet);
    }


    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        if (arg1 == RESULT_OK) {
            switch (arg0) {
                case cityRequestCode:
                    city = arg2.getExtras().getString("city");
                    cityareaid = arg2.getExtras().getString("cityareaid");
                    queueid = arg2.getExtras().getString("queueid");
                    buildid = arg2.getExtras().getString("buildid");

                    cityareaid2 = arg2.getExtras().getString("cityareaid2");
                    queueid2 = arg2.getExtras().getString("queueid2");
                    buildid2 = arg2.getExtras().getString("buildid2");

                    choice_city_text.setText(city);
                    choice_town_text.setText("");

                    townid = "";
                    town = "";

                    //预加载乡镇
                    RequestParams params = new RequestParams();
                    params.put("countyId", buildid);
                    params.put("cityId", queueid);
                    execApi(ApiType.QUERYTOWNID, params);
                    showProgressDialog();

                    break;
                case townRequestCode:
                    town = arg2.getExtras().getString("town");
                    townid = arg2.getExtras().getString("townid");
                    townid2 = arg2.getExtras().getString("townid2");
                    choice_town_text.setText(town);
                    break;
                default:
                    break;
            }
        }

        if (arg1 == 0x14) {
            productIdList = (List<String>) arg2.getSerializableExtra("productIdList");
            intent_product_text.setText(arg2.getStringExtra("str"));
        }
    }


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.choice_city_layout:
                Bundle bundle1 = new Bundle();
                bundle1.putInt("tag", 0);
                bundle1.putBoolean("isUse_Id", true);
                IntentUtil.startActivityForResult(this, SelectAddressActivity.class,
                        cityRequestCode, bundle1);
                break;
            case R.id.choice_town_layout:
                if (StringUtil.checkStr(choice_city_text.getText().toString().trim())) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("tag", 1);
                    bundle.putString("queueid", queueid);
                    bundle.putString("buildid", buildid);
                    IntentUtil.startActivityForResult(this, SelectAddressActivity.class,
                            townRequestCode, bundle);
                } else {
                    showToast("请先选择地区");
                }
                break;
            case R.id.choose_type_ll:
                IntentUtil.startActivityForResult(this, SelectIntentProductActivity.class,
                        1000, null);
                break;
            case R.id.choice_compelet:
                if (StringUtil.checkStr(name.getText().toString().trim())
                        && phone_error_ll.getVisibility() == View.GONE
                        && isMobileNum(phone.getText().toString().trim())
                        && StringUtil.checkStr(choice_city_text.getText().toString().trim())
                        && StringUtil.checkStr(choice_town_text.getText().toString().trim())
                        && StringUtil.checkStr(intent_product_text.getText().toString().trim())) {
                    saveInfo();
                } else {
                    if (phone_error_ll.getVisibility() == View.VISIBLE) {
                        showToast("请修改填写的手机号");
                    } else {
                        showToast("请完善信息");
                    }

                }
                break;

        }
    }

    private void saveInfo() {
        Map<String, Object> map1 = new HashMap<>();
        //省 市 县 镇
        Map<String, String> map = new HashMap<>();
        map.put("province", cityareaid2);
        map.put("city", queueid2);
        map.put("county", buildid2);
        map.put("town", townid2);
        map1.put("address", map);
        //姓名 手机号
        map1.put("name", name.getText().toString().trim());
        map1.put("phone", phone.getText().toString().trim());
        //姓别
        if (boy_box.isChecked()) {
            map1.put("sex", 0);
        }
        if (girl_box.isChecked()) {
            map1.put("sex", 1);
        }
        String trim = add_potential_remark.getText().toString().trim();
        if (StringUtil.checkStr(trim)) {
            map1.put("remarks", trim);
        }
        map1.put("buyIntentions", productIdList);
        if (isLogin()) {
            map1.put("token", Store.User.queryMe().token);
        }
        Gson gson = new Gson();
        String toJson = gson.toJson(map1);
        RequestParams params = new RequestParams();
        params.put("JSON", toJson);
        execApi(ApiType.ADD_POTENTIAL_CUSTOMER.setMethod(ApiType.RequestMethod.POSTJSON), params);

    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (req.getApi() == ApiType.IS_POTENTIAL_CUSTOMER) {
            IsPotentialCustomerResult data = (IsPotentialCustomerResult) req.getData();
            if (data.getStatus().equals("1000")) {
                if (data.available) {
                    dividing_line.setVisibility(View.GONE);
                    phone_error_ll.setVisibility(View.GONE);
                } else {
                    dividing_line.setVisibility(View.VISIBLE);
                    phone_error_ll.setVisibility(View.VISIBLE);
                    if (StringUtil.checkStr(data.getMessage())) {
                        phone_error.setText(data.getMessage());
                    }
                }

            }
        } else if (req.getApi() == ApiType.ADD_POTENTIAL_CUSTOMER) {
            if (req.getData().getStatus().equals("1000")) {
                showToast("添加成功");
                MsgCenter.fireNull(MsgID.add_potential_success, "add");
                finish();
            }

        }
    }


}

