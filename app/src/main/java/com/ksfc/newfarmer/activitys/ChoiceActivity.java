/**
 *
 */
package com.ksfc.newfarmer.activitys;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.LoginResult.UserInfo;
import com.ksfc.newfarmer.protocol.beans.saveAddress;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.StringUtil;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import net.yangentao.util.msg.MsgCenter;

/**
 * 项目名称：QianXihe518 类名称：ChoiceActivity 类描述： 创建人：王蕾 创建时间：2015-5-26 上午11:19:21
 * 修改备注：
 */
public class ChoiceActivity extends BaseActivity {


    private final int cityrequestCode = 1;//省市区
    private final int townrequestCode = 2;//乡镇

    private String city = "";
    private String room = "";
    private String town = "";

    private TextView choice_town_text;
    private TextView choice_city_text;
    private EditText room_edit, shouhuo_name, shouhuo_tel, zipCode_text;

    private String cityareaid = "";
    private String queueid = "";
    private String buildid = "";
    private String zipCode = "";
    private String townid = "";

    private CheckBox default_address;

    private int count = -1;

    @Override
    public int getLayout() {
        return R.layout.choose_address;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("新增收货地址");
        initView();
        count = getIntent().getExtras().getInt("addressCount");
        if (count == 0) {
            default_address.setChecked(true);
        } else {
            default_address.setChecked(false);
        }
    }

    private void initView() {
        choice_city_text = (TextView) findViewById(R.id.choice_city_text);
        choice_town_text = (TextView) findViewById(R.id.choice_town_text);

        room_edit = (EditText) findViewById(R.id.choice_detail_room_edit);
        zipCode_text = (EditText) findViewById(R.id.choice_zipCode_edit);

        // 完成框
        room_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });

        shouhuo_tel = (EditText) findViewById(R.id.shouhuo_tel);
        shouhuo_name = (EditText) findViewById(R.id.shouhuo_name);
        default_address = (CheckBox) findViewById(R.id.default_address);

        setViewClick(R.id.choice_city_layout);
        setViewClick(R.id.choice_town_layout);


        setViewClick(R.id.choice_compelet);
        setViewClick(R.id.choice_detail_room_edit);
    }

    @SuppressLint("NewApi")
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
                if (TextUtils.isEmpty(choice_city_text.getText().toString())) {
                    showToast("请先选择省市县区地址");
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("tag", 1);
                    bundle.putString("queueid", queueid);
                    bundle.putString("buildid", buildid);
                    IntentUtil.startActivityForResult(this, SelectAddressActivity.class,
                            townrequestCode, bundle);
                }
                break;


            case R.id.choice_compelet:
                room = room_edit.getEditableText().toString().trim();

                if (StringUtil.empty(shouhuo_name.getEditableText().toString()
                        .trim())) {
                    showToast("请输入收货人姓名");
                    return;
                }
                if (TextUtils.isEmpty(shouhuo_tel.getText().toString().trim())) {
                    showToast("请输入手机号码");
                    return;
                }
                if (!isMobileNum(shouhuo_tel.getText().toString().trim())) {
                    showToast("请输入正确的手机号码");
                    return;
                }
                if (TextUtils
                        .isEmpty(choice_city_text.getText().toString().trim())) {
                    showToast("请选择城市");
                    return;
                }
                if (TextUtils.isEmpty(room_edit.getText().toString().trim())) {
                    showToast("请输入您的详细地址");
                    return;
                }

                // userId:用户ID,
                // areaId：省份ID
                // address:手动填写的具体地址,
                // type:（1.默认地址2.非默认地址）
                // receiptPhone:收货人手机号
                // receiptPeople：收货人名称

                if (StringUtil.empty(zipCode_text.getEditableText().toString()
                        .trim())) {
                    zipCode = zipCode_text.getEditableText().toString().trim();
                }

                showProgressDialog();
                RequestParams params = new RequestParams();
                params.put("userId", Store.User.queryMe().userid);
                params.put("areaId", cityareaid);
                params.put("cityId", queueid);
                params.put("countyId", buildid);
                params.put("townId", townid);
                params.put("zipCode", zipCode);
                params.put("address", room);

                if (default_address.isChecked()) {
                    params.put("type", 1);
                } else {
                    params.put("type", 2);
                }
                params.put("receiptPeople", shouhuo_name.getEditableText()
                        .toString().trim());
                params.put("receiptPhone", shouhuo_tel.getEditableText().toString()
                        .trim());
                execApi(ApiType.SAVE_ADDRESS, params);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        // TODO Auto-generated method stub
        if (arg1 == RESULT_OK) {
            switch (arg0) {
                case cityrequestCode:
                    city = arg2.getExtras().getString("city");
                    cityareaid = arg2.getExtras().getString("cityareaid");
                    queueid = arg2.getExtras().getString("queueid");
                    buildid = arg2.getExtras().getString("buildid");
                    choice_city_text.setText(city);
                    room_edit.setText("");
                    town="";
                    townid="";
                    break;
                case townrequestCode:
                    town = arg2.getExtras().getString("town");
                    townid = arg2.getExtras().getString("townid");
                    choice_town_text.setText(town);
                    room_edit.setText("");
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (req.getApi() == ApiType.SAVE_ADDRESS) {
            saveAddress save = (saveAddress) req.getData();
            if ("1000".equals(save.getStatus())) {
                if (default_address.isChecked()) {
                    String addr = city + town
                            + room_edit.getEditableText().toString().trim();
                    MsgCenter.fireNull("MSG.ADDR", addr);
                    UserInfo queryMe = Store.User.queryMe();
                    queryMe.defaultAddress = addr;
                    Store.User.saveMe(queryMe);
                }
                finish();
                showToast("成功新增了地址");
            } else {
                showToast("新增地址失败");
            }

        }
    }
}
