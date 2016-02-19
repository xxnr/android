package com.ksfc.newfarmer.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.LoginResult;
import com.ksfc.newfarmer.protocol.beans.TownList;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.StringUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import net.yangentao.util.msg.MsgCenter;


import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CAI on 2015/12/10.
 */
public class ChoiceHomeAddress extends BaseActivity {
    private final int cityRequestCode = 1;//省市区
    private final int townRequestCode = 2;//乡镇

    private TextView choice_city_text;
    private TextView choice_town_text;

    private String cityareaid="";
    private String queueid = "";
    private String buildid = "";
    private String townid="";
    private String city;
    private String town;

    private TextView name_submit_tv;


    private LoginResult.UserInfo me;

    @Override
    public int getLayout() {
        return R.layout.choice_home_address_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("修改所在地区");
        me = Store.User.queryMe();
        initView();
        if (me!=null){
            setData();
        }
    }


    private void initView() {
        choice_city_text = (TextView) findViewById(R.id.choice_city_text);
        choice_town_text = (TextView) findViewById(R.id.choice_town_text);

        name_submit_tv = (TextView) findViewById(R.id.name_submit_tv);
        setViewClick(R.id.choice_city_layout);
        setViewClick(R.id.choice_town_layout);
        setViewClick(R.id.name_submit_tv);
        name_submit_tv.setClickable(false);

    }

    private void setData() {
        cityareaid = me.provinceid;
        queueid = me.cityid;
        buildid = me.countyid;
        townid = me.townid;
        choice_city_text.setText(me.addressCity);
        choice_town_text.setText(me.addressTown);
        city = me.addressCity;
        town = me.addressTown;

        if (StringUtil.checkStr(buildid)||StringUtil.checkStr(queueid)){
            //预加载乡镇
            RequestParams params = new RequestParams();
            params.put("countyId", buildid);
            params.put("cityId", queueid);
            execApi(ApiType.QUERYTOWNID, params);
            showProgressDialog();
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
                Bundle bundle = new Bundle();
                bundle.putInt("tag", 1);
                bundle.putString("queueid", queueid);
                bundle.putString("buildid", buildid);
                IntentUtil.startActivityForResult(this, SelectAddressActivity.class,
                        townRequestCode, bundle);
                break;

            case R.id.name_submit_tv:

                if (!StringUtil.empty(choice_town_text.getText().toString().trim())
                        && !StringUtil.empty(choice_city_text.getText().toString().trim())) {
                    showProgressDialog("正在保存中...");

                    Map<String, String> map = new HashMap<>();
                    map.put("provinceId", cityareaid);
                    map.put("cityId", queueid);
                    map.put("countyId", buildid);
                    map.put("townId", townid);
                    Gson gson = new Gson();
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("address", map);
                    if (isLogin()){
                        map1.put("userId", Store.User.queryMe().userid);
                        map1.put("token", Store.User.queryMe().token);
                    }
                    String jsonStr = gson.toJson(map1);
                    upAddress(jsonStr);
                } else {
                    showToast("地址不能为空");
                }


                break;

        }

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
                    choice_city_text.setText(city);
                    choice_town_text.setText("");
                    townid = "";
                    town = "";
                    if (StringUtil.checkStr(buildid)||StringUtil.checkStr(queueid)){
                        //预加载乡镇
                        RequestParams params = new RequestParams();
                        params.put("countyId", buildid);
                        params.put("cityId", queueid);
                        execApi(ApiType.QUERYTOWNID, params);
                        showProgressDialog();
                    }
                    name_submit_tv.setBackgroundColor(getResources().getColor(R.color.green));
                    name_submit_tv.setClickable(true);
                    break;
                case townRequestCode:
                    town = arg2.getExtras().getString("town");
                    townid = arg2.getExtras().getString("townid");
                    choice_town_text.setText(town);
                    name_submit_tv.setBackgroundColor(getResources().getColor(R.color.green));
                    name_submit_tv.setClickable(true);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (req.getApi() == ApiType.QUERYTOWNID) {
            TownList add = (TownList) req.getData();
            if (add.datas != null) {
                if (add.datas.rows.size() == 0) {
                    townid = "";
                    town = "";
                    choice_town_text.setText("暂无街道");

                    findViewById(R.id.choice_town_layout).setOnClickListener(null);
                    findViewById(R.id.choice_town_layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showToast("此地区暂无街道");
                        }
                    });
                } else {
                    choice_town_text.setText(town);

                    findViewById(R.id.choice_town_layout).setOnClickListener(null);
                    setViewClick(R.id.choice_town_layout);
                }
            }
        }


    }


    private void upAddress(String value) {
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        try {
            RndLog.d(TAG, value);
            StringEntity entity = new StringEntity(value);
            params.setBodyEntity(entity);
            params.setContentType("application/json");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, ApiType.SAVE_MYUSER.getOpt(), params,
                new RequestCallBack<String>() {
                    @Override
                    public void onFailure(HttpException arg0, String arg1) {

                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {

                        if (arg0.result.contains("1000")) {
                            LoginResult.UserInfo queryMe = Store.User.queryMe();
                            if (queryMe != null) {
                                queryMe.addressCity = city;
                                queryMe.addressTown = town;
                                queryMe.provinceid = cityareaid;
                                queryMe.cityid = queueid;
                                queryMe.countyid = buildid;
                                queryMe.townid = townid;
                                Store.User.saveMe(queryMe);
                            }
                            showToast("保存成功！");
                            Intent intent = new Intent();
                            intent.putExtra("str", city + town);
                            setResult(0x13, intent);
                            MsgCenter.fireNull(MsgID.UPDATE_USER, "update");
                            finish();
                        }

                    }

                });
    }

}
