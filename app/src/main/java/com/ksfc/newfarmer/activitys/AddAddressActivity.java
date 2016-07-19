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
import com.ksfc.newfarmer.beans.LoginResult.UserInfo;
import com.ksfc.newfarmer.utils.BundleUtils;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.BaseViewUtils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


/**
 * 项目名称：QianXihe518 类名称：AddAddressActivity 类描述： 创建人：王蕾 创建时间：2015-5-26 上午11:19:21
 * 修改备注：
 */
public class AddAddressActivity extends BaseActivity {


    private final int cityRequestCode = 1;//省市区
    private final int townRequestCode = 2;//乡镇

    private TextView choice_town_text;
    private TextView choice_city_text;
    private EditText room_edit, receipt_name, receipt_phone, zipCode_text;//详细地址 收货人 收货电话 邮编

    private String cityareaid = "";//省 id
    private String queueid = "";//市 id
    private String buildid = "";//县 id
    private String townid = "";//乡镇 id

    private CheckBox default_address;//是否是默认地址

    private int count = -1;//之前是否有地址

    @Override
    public int getLayout() {
        return R.layout.activity_add_edit_address;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("新增收货地址");
        initView();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            count = bundle.getInt("addressCount");
        }
        //如果新加地址是第一个 ，默认选中默认
        if (count == 0) {
            UserInfo userInfo = Store.User.queryMe();
            if (userInfo != null) {
                receipt_name.setText(userInfo.name);
                receipt_phone.setText(userInfo.phone);
            }
            default_address.setChecked(true);
        } else {
            default_address.setChecked(false);
        }
    }

    private void initView() {
        choice_city_text = (TextView) findViewById(R.id.choice_city_text);//选择省市县
        choice_town_text = (TextView) findViewById(R.id.choice_town_text);//选择镇
        room_edit = (EditText) findViewById(R.id.choice_detail_room_edit);
        zipCode_text = (EditText) findViewById(R.id.choice_zipCode_edit);

        // room_edit加一个完成按钮
        room_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //关闭软键盘
                BaseViewUtils.hideSoftInput(AddAddressActivity.this,room_edit);
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });

        receipt_phone = (EditText) findViewById(R.id.shouhuo_tel);
        receipt_name = (EditText) findViewById(R.id.shouhuo_name);
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
                IntentUtil.startActivityForResult(this, SelectAddressActivity.class,
                        cityRequestCode, BundleUtils.Put("tag", 0));
                break;
            case R.id.choice_town_layout:
                if (TextUtils.isEmpty(choice_city_text.getText().toString())) {
                    showToast("请先选择省市县区地址");
                } else {
                    Bundle bundle = BundleUtils.Put("tag", 1, "queueid", queueid, "buildid", buildid);
                    IntentUtil.startActivityForResult(this, SelectAddressActivity.class,
                            townRequestCode, bundle);
                }
                break;

            case R.id.choice_compelet:

                if (StringUtil.empty(receipt_name.getEditableText().toString()
                        .trim())) {
                    showToast("请输入收货人姓名");
                    return;
                }
                if (StringUtil.empty(receipt_phone.getText().toString().trim())) {
                    showToast("请输入手机号码");
                    return;
                }
                if (!isMobileNum(receipt_phone.getText().toString().trim())) {
                    showToast("请输入正确的手机号码");
                    return;
                }
                if (StringUtil.empty(choice_city_text.getText().toString().trim())) {
                    showToast("请选择城市");
                    return;
                }
                String room = room_edit.getEditableText().toString().trim();

                if (StringUtil.empty(room)) {
                    showToast("请输入您的详细地址");
                    return;
                }
                String zipCode = zipCode_text.getEditableText().toString().trim();

                // userId:用户ID,
                // areaId：省份ID
                // address:手动填写的具体地址,
                // type:（1.默认地址2.非默认地址）
                // receiptPhone:收货人手机号
                // receiptPeople：收货人名称

                showProgressDialog();
                RequestParams params = new RequestParams();
                if (isLogin()) {
                    params.put("userId", Store.User.queryMe().userid);
                }
                params.put("areaId", cityareaid);
                params.put("cityId", queueid);
                params.put("countyId", buildid);
                params.put("townId", townid);
                if (StringUtil.checkStr(zipCode)) {
                    params.put("zipCode", zipCode);
                }
                params.put("address", room);

                if (default_address.isChecked()) {
                    params.put("type", 1);
                } else {
                    params.put("type", 2);
                }
                params.put("receiptPeople", receipt_name.getEditableText()
                        .toString().trim());
                params.put("receiptPhone", receipt_phone.getEditableText().toString()
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
                case cityRequestCode:
                    Bundle bundle = arg2.getExtras();
                    if (bundle != null) {
                        cityareaid = bundle.getString("cityareaid");
                        queueid = bundle.getString("queueid");
                        buildid = bundle.getString("buildid");

                        String city = bundle.getString("city");//省市县区
                        choice_city_text.setText(city);
                        choice_town_text.setText("");
                        room_edit.setText("");
                        townid = "";
                    }
                    break;
                case townRequestCode:
                    townid = arg2.getExtras().getString("townid");

                    String town = arg2.getExtras().getString("town");
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
            if ("1000".equals(req.getData().getStatus())) {
                showToast("成功新增了地址");
                finish();
            }
        }
    }
}
