package com.ksfc.newfarmer.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MainActivity;
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
public class ImprovePersonActivity extends BaseActivity {
    private EditText name;
    private final int cityrequestCode = 1;//省市区
    private final int townrequestCode = 2;//乡镇
    private final int userCode = 4;//类型

    private TextView choice_city_text;
    private TextView choice_town_text;
    private TextView choice_type_text;

    private String queueid = "";
    private String buildid = "";
    private String cityareaid;
    private String townid;

    private String type_key;

    private String city;
    private String town;


    private Map<String, String> map = new HashMap<>();
    private String jsonStr;
    private LoginResult.UserInfo me;
    private CheckBox boy_box;
    private CheckBox girl_box;


    @Override
    public int getLayout() {
        return R.layout.improve_person_data_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("完善个人资料");
        me = Store.User.queryMe();
        intView();
        setData();

        setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MsgCenter.fireNull(MsgID.UPDATE_USER, "update");
                Intent intent = new Intent(ImprovePersonActivity.this, MainActivity.class);
                intent.putExtra("id", 4);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            MsgCenter.fireNull(MsgID.UPDATE_USER, "update");

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("id", 4);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void intView() {
        name = (EditText) findViewById(R.id.name_tv);

        choice_city_text = (TextView) findViewById(R.id.choice_city_text);
        choice_town_text = (TextView) findViewById(R.id.choice_town_text);
        choice_type_text = (TextView) findViewById(R.id.choice_type_text);

        boy_box = (CheckBox) findViewById(R.id.btn_check_item_item);
        girl_box = (CheckBox) findViewById(R.id.btn_check_item_item1);

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

        choice_town_text = (TextView) findViewById(R.id.choice_town_text);

        setViewClick(R.id.choice_city_layout);
        setViewClick(R.id.choice_town_layout);
        setViewClick(R.id.choose_type_ll);
        setViewClick(R.id.choice_compelet);
        setViewClick(R.id.choice_cancel);
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
        type_key = me.userType;
        if (!StringUtil.empty(me.userType)) {
            choice_type_text.setText(setUserType(me.userType));
        } else {
            choice_type_text.setText("还有没有填写呦~");
        }


        name.setText(me.name);
        // 光标移到最后
        Editable eText = name.getText();
        Selection.setSelection(eText, eText.length());

        if (me.sex) {
            boy_box.setChecked(false);
            girl_box.setChecked(true);
        } else {
            boy_box.setChecked(true);
            girl_box.setChecked(false);
        }

        if (StringUtil.checkStr(buildid) || StringUtil.checkStr(queueid)) {
            //预加载乡镇
            RequestParams params = new RequestParams();
            params.put("countyId", buildid);
            params.put("cityId", queueid);
            execApi(ApiType.QUERYTOWNID, params);
            showProgressDialog();
        }
    }


    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        if (arg1 == RESULT_OK) {
            switch (arg0) {
                case cityrequestCode:
                    city = arg2.getExtras().getString("city");
                    cityareaid = arg2.getExtras().getString("cityareaid");
                    queueid = arg2.getExtras().getString("queueid");
                    buildid = arg2.getExtras().getString("buildid");
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
                case townrequestCode:
                    town = arg2.getExtras().getString("town");
                    townid = arg2.getExtras().getString("townid");
                    choice_town_text.setText(town);
                    break;
                default:
                    break;
            }
        }

        if (arg1 == 0x14) {
            choice_type_text.setText(arg2.getStringExtra("str"));
            type_key = arg2.getStringExtra("type_key");
        }
    }


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {

            case R.id.choice_city_layout:
                Bundle bundle1 = new Bundle();
                bundle1.putInt("tag", 0);
                IntentUtil.startActivityForResult(this, SelectAddressActivity.class,
                        cityrequestCode, bundle1);
                break;
            case R.id.choice_town_layout:
                Bundle bundle = new Bundle();
                bundle.putInt("tag", 1);
                bundle.putString("queueid", queueid);
                bundle.putString("buildid", buildid);
                IntentUtil.startActivityForResult(this, SelectAddressActivity.class,
                        townrequestCode, bundle);
                break;
            case R.id.choose_type_ll:
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("flag", false);
                IntentUtil.startActivityForResult(this, SelectUserTypeActivity.class,
                        userCode, bundle2);
                break;
            case R.id.choice_compelet:
                if (!TextUtils.isEmpty(name.getText().toString().trim())
                        && !TextUtils.isEmpty(choice_city_text.getText().toString().trim())
                        && !TextUtils.isEmpty(choice_town_text.getText().toString().trim())) {
                    saveInfo();
                } else {
                    showToast("请完善信息");
                }
                break;
            case R.id.choice_cancel:
                MsgCenter.fireNull(MsgID.UPDATE_USER, "update");
                Intent intent = new Intent(ImprovePersonActivity.this, MainActivity.class);
                intent.putExtra("id", 4);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void saveInfo() {

        map.put("provinceId", cityareaid);
        map.put("cityId", queueid);
        map.put("countyId", buildid);
        map.put("townId", townid);
        Gson gson = new Gson();

        Map<String, Object> map1 = new HashMap();
        map1.put("address", map);
        map1.put("token", Store.User.queryMe().token);
        map1.put("userName", name.getText().toString().trim());
        map1.put("type", type_key);
        if (boy_box.isChecked()) {
            map1.put("sex", 0);
        }
        if (girl_box.isChecked()) {
            map1.put("sex", 1);
        }
        jsonStr = gson.toJson(map1);
        upAddress(jsonStr);
    }

    @Override
    public void onResponsed(Request req) {

        disMissDialog();
        if (req.getApi() == ApiType.SAVE_MYUSER) {
            if (req.getData().getStatus().equals("1000")) {
                showToast("保存成功！");
                MsgCenter.fireNull(MsgID.UPDATE_USER, "update");
                Intent intent = new Intent(ImprovePersonActivity.this, MainActivity.class);
                intent.putExtra("id", 4);
                startActivity(intent);
                finish();
            }
        } else if (req.getApi() == ApiType.QUERYTOWNID) {
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

    public String setUserType(String i) {
        String userType = "";
        int i1 = 1;
        try {
            i1 = Integer.parseInt(i);
        } catch (Exception e) {
            RndLog.d(TAG, e.getMessage());
            i1 = 1;
        }
        switch (i1) {
            case 1:
                userType = "其他";
                break;
            case 2:
                userType = "种植大户";
                break;
            case 3:
                userType = "村级经销商";
                break;
            case 4:
                userType = "乡镇经销商";
                break;
            case 5:
                userType = "县级经销商";
                break;
            default:
                userType = "其他";
                break;
        }
        return userType;
    }

    private void upAddress(String value) {
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        try {
            StringEntity entity = new StringEntity(value, "UTF-8");
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
                        showToast("保存失败");
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {

                        if (arg0.result.toString().contains("1000")) {
                            showToast("保存成功！");
                            MsgCenter.fireNull(MsgID.UPDATE_USER, "update");
                            Intent intent = new Intent(ImprovePersonActivity.this, MainActivity.class);
                            intent.putExtra("id", 4);
                            startActivity(intent);
                            finish();
                        }

                    }

                });
    }

}

