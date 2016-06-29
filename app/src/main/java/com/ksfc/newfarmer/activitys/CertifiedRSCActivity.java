package com.ksfc.newfarmer.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.http.ApiType;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.http.RequestParams;
import com.ksfc.newfarmer.http.beans.LoginResult;
import com.ksfc.newfarmer.http.beans.RSCInfoResult;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.MaxLengthWatcher;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.Utils;

import net.yangentao.util.msg.MsgCenter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HePeng on 2016/3/4.
 */
public class CertifiedRSCActivity extends BaseActivity {
    private EditText name_tv; //经销商姓名
    private EditText id_card_number_tv;//经销商身份证号
    private EditText store_name_tv;//经销商门店名称
    private EditText phone_tv;//经销商手机号
    private TextView choice_city_text; //省市县区
    private TextView choice_town_text;  //乡镇
    private EditText store_address_tv; //经销商详细门店地址

    private final int cityRequestCode = 1;//省市区
    private final int townRequestCode = 2;//乡镇
    private String queueid = "";
    private String buildid = "";

    //以下是_id
    private String cityareaid2;
    private String queueid2;
    private String buildid2;
    private String townid2;

    private String city;
    private String town;


    @Override
    public int getLayout() {
        return R.layout.activity_certified_rsc;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {

        initView();


    }

    private void initView() {
        name_tv = (EditText) findViewById(R.id.name_tv);
        phone_tv = (EditText) findViewById(R.id.phone_tv);
        store_name_tv = (EditText) findViewById(R.id.store_name_tv);
        choice_city_text = (TextView) findViewById(R.id.choice_city_text);
        choice_town_text = (TextView) findViewById(R.id.choice_town_text);
        id_card_number_tv = (EditText) findViewById(R.id.id_card_number_tv);
        store_address_tv = (EditText) findViewById(R.id.store_address_tv);

        LoginResult.UserInfo me = Store.User.queryMe();

        if (me != null) {
            if (!me.RSCInfoVerifing && !me.isRSC) {
                setTitle("服务站认证");
                //限制字数
                name_tv.addTextChangedListener(new MaxLengthWatcher(12, name_tv, CertifiedRSCActivity.this));
                store_name_tv.addTextChangedListener(new MaxLengthWatcher(40, store_name_tv, CertifiedRSCActivity.this));
                store_address_tv.addTextChangedListener(new MaxLengthWatcher(60, store_address_tv, CertifiedRSCActivity.this));

                setViewClick(R.id.choice_city_layout);
                setViewClick(R.id.choice_town_layout);
                setViewClick(R.id.choice_compelet);
            } else {
                setTitle("查看认证信息");

                name_tv.setEnabled(false);
                phone_tv.setEnabled(false);
                store_name_tv.setEnabled(false);
                id_card_number_tv.setEnabled(false);
                store_address_tv.setEnabled(false);

                name_tv.setHint("");
                phone_tv.setHint("");
                store_name_tv.setHint("");
                id_card_number_tv.setHint("");
                store_address_tv.setHint("");
                choice_city_text.setHint("");
                choice_town_text.setHint("");

                name_tv.setTextColor(getResources().getColor(R.color.black_goods_titile));
                phone_tv.setTextColor(getResources().getColor(R.color.black_goods_titile));
                store_name_tv.setTextColor(getResources().getColor(R.color.black_goods_titile));
                id_card_number_tv.setTextColor(getResources().getColor(R.color.black_goods_titile));
                store_address_tv.setTextColor(getResources().getColor(R.color.black_goods_titile));

                findViewById(R.id.choice_compelet).setVisibility(View.GONE);//隐藏提交按钮
                findViewById(R.id.description_add_rsc).setVisibility(View.GONE);//隐藏解释信息

                showProgressDialog();
                RequestParams params = new RequestParams();
                params.put("userId", me.userid);
                execApi(ApiType.GET_RSC_INFO.setMethod(ApiType.RequestMethod.GET), params);
            }

        }


    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {

            case R.id.choice_city_layout:
                Bundle bundle1 = new Bundle();
                bundle1.putInt("tag", 0);
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
            case R.id.choice_compelet:
                if (StringUtil.checkStr(name_tv.getText().toString().trim())
                        && StringUtil.checkStr(phone_tv.getText().toString().trim())
                        && StringUtil.checkStr(id_card_number_tv.getText().toString().trim())
                        && StringUtil.checkStr(choice_city_text.getText().toString().trim())
                        && StringUtil.checkStr(choice_town_text.getText().toString().trim())
                        && StringUtil.checkStr(store_name_tv.getText().toString().trim())
                        && StringUtil.checkStr(store_address_tv.getText().toString().trim())
                        ) {
                    if (isMobileNum(phone_tv.getText().toString().trim())
                            && Utils.isIDCardNum(id_card_number_tv.getText().toString().trim())) {
                        saveInfo();
                    } else {
                        showToast("请检查手机号或者身份证号码是否正确");
                    }
                } else {
                    showToast("请完善信息");
                }
                break;


        }
    }

    //提交
    private void saveInfo() {

        Map<String, Object> map = new HashMap<>();
        map.put("name", name_tv.getText().toString().trim());
        map.put("phone", phone_tv.getText().toString().trim());
        map.put("IDNo", id_card_number_tv.getText().toString().trim());
        map.put("companyName", store_name_tv.getText().toString().trim());
        if (isLogin()) {
            map.put("token", Store.User.queryMe().token);
        }
        Map<String, Object> map1 = new HashMap<>();
        map1.put("province", cityareaid2);
        map1.put("city", queueid2);
        map1.put("county", buildid2);
        map1.put("town", townid2);
        map1.put("details", store_address_tv.getText().toString().trim());

        map.put("companyAddress", map1);

        Gson gson = new Gson();
        String toJson = gson.toJson(map);
        RequestParams params = new RequestParams();
        params.put("JSON", toJson);
        execApi(ApiType.ADD_RSC_INFO.setMethod(ApiType.RequestMethod.POSTJSON), params);
    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (req.getApi() == ApiType.ADD_RSC_INFO) {
            if (req.getData().getStatus().equals("1000")) {
                showToast("提交成功");

                LoginResult.UserInfo me = Store.User.queryMe();
                if (me != null) {
                    me.RSCInfoVerifing = true;
                    Store.User.saveMe(me);
                    MsgCenter.fireNull(MsgID.UPDATE_USER_TYPE, "update");
                }
                finish();
            }
        } else if (req.getApi() == ApiType.GET_RSC_INFO) {
            if (req.getData().getStatus().equals("1000")) {
                RSCInfoResult data = (RSCInfoResult) req.getData();

                if (data.RSCInfo != null) {
                    name_tv.setText(data.RSCInfo.name);
                    phone_tv.setText(data.RSCInfo.phone);
                    id_card_number_tv.setText(data.RSCInfo.IDNo);
                    store_name_tv.setText(data.RSCInfo.companyName);

                    RSCInfoResult.RSCInfoEntity.CompanyAddressEntity companyAddress = data.RSCInfo.companyAddress;
                    if (companyAddress != null) {
                        StringBuilder builder = new StringBuilder();
                        if (companyAddress.province != null) {
                            if (StringUtil.checkStr(companyAddress.province.name))
                                builder.append(companyAddress.province.name).append(" ");
                        }
                        if (companyAddress.city != null) {
                            if (StringUtil.checkStr(companyAddress.city.name))
                                builder.append(companyAddress.city.name).append(" ");
                        }
                        if (companyAddress.county != null) {
                            if (StringUtil.checkStr(companyAddress.county.name))
                                builder.append(companyAddress.county.name);
                        }
                        choice_city_text.setText(builder);
                        if (companyAddress.town != null) {
                            if (StringUtil.checkStr(companyAddress.town.name))
                                choice_town_text.setText(companyAddress.town.name);
                        }
                        store_address_tv.setText(companyAddress.details);

                    }
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case cityRequestCode:
                    city = data.getExtras().getString("city");
                    queueid = data.getExtras().getString("queueid");
                    buildid = data.getExtras().getString("buildid");

                    cityareaid2 = data.getExtras().getString("cityareaid2");
                    queueid2 = data.getExtras().getString("queueid2");
                    buildid2 = data.getExtras().getString("buildid2");

                    choice_city_text.setText(city);
                    choice_town_text.setText("");

                    town = "";

                    //预加载乡镇
                    RequestParams params = new RequestParams();
                    params.put("countyId", buildid);
                    params.put("cityId", queueid);
                    execApi(ApiType.QUERYTOWNID, params);
                    showProgressDialog();

                    break;
                case townRequestCode:
                    town = data.getExtras().getString("town");
                    townid2 = data.getExtras().getString("townid2");
                    choice_town_text.setText(town);
                    break;
                default:
                    break;

            }
        }
    }
}
