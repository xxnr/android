/**
 *
 */
package com.ksfc.newfarmer.activitys;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.ResponseResult;
import com.ksfc.newfarmer.protocol.beans.AddressList;
import com.ksfc.newfarmer.protocol.beans.LoginResult.UserInfo;
import com.ksfc.newfarmer.widget.dialog.CustomDialog;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.StringUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import net.yangentao.util.msg.MsgCenter;

/**
 * 项目名称：QianXihe518 类名称：AddAddressActivity 类描述： 创建人：王蕾 创建时间：2015-5-26 上午11:19:21
 * 修改备注：
 */
public class UpdateAddressActivity extends BaseActivity {

    private final int cityrequestCode = 1;//省市区
    private final int townrequestCode = 2;//乡镇
    private String city = "";
    private String town = "";
    private String room = "";
    private String type = "";

    private TextView choice_town_text;
    private TextView choice_city_text;

    private EditText room_edit, shouhuo_name, shouhuo_tel, zipCode_text;

    private String cityareaid = "";
    private String queueid = "";
    private String buildid = "";
    private String townid = "";
    private String zipCode = "";
    private String addressId = "";

    private CheckBox default_address;


    @Override
    public int getLayout() {
        return R.layout.choose_address;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("编辑收货地址");
        initView();
        setData();
    }

    private void initView() {
        choice_city_text = (TextView) findViewById(R.id.choice_city_text);
        choice_town_text = (TextView) findViewById(R.id.choice_town_text);
        room_edit = (EditText) findViewById(R.id.choice_detail_room_edit);
        zipCode_text = (EditText) findViewById(R.id.choice_zipCode_edit);

        shouhuo_tel = (EditText) findViewById(R.id.shouhuo_tel);
        shouhuo_name = (EditText) findViewById(R.id.shouhuo_name);
        // 完成框
        room_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });


        // 光标移到最后
        Editable eText = shouhuo_name.getText();
        Selection.setSelection(eText, eText.length());

        default_address = (CheckBox) findViewById(R.id.default_address);
        findViewById(R.id.choose_address_delete).setVisibility(View.VISIBLE);

        setViewClick(R.id.choice_city_layout);
        setViewClick(R.id.choice_town_layout);

        setViewClick(R.id.choice_compelet);
        setViewClick(R.id.choice_detail_room_edit);
        setViewClick(R.id.choose_address_delete);
    }

    public void setData() {

        AddressList.Address address = ((AddressList.Address) getIntent().getSerializableExtra("address"));
        if (address!=null){
            shouhuo_name.setText(address.receiptPeople);
            shouhuo_tel.setText(address.receiptPhone);
            choice_city_text.setText(address.areaName + address.cityName + address.countyName);
            choice_town_text.setText(address.townName);
            zipCode_text.setText(address.zipCode);
            room_edit.setText(address.address);

            if (address.type.equals("1")) {
                default_address.setChecked(true);
            } else {
                default_address.setChecked(false);
            }

            city = address.areaName + address.cityName + address.countyName;//省市县区
            room = address.address;

            cityareaid = address.areaId;
            queueid = address.cityId;
            buildid = address.countyId;
            townid = address.townId;
            addressId = address.addressId;
            type = address.type;

        }


    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {

            case R.id.choose_address_delete:
                CustomDialog.Builder builder = new CustomDialog.Builder(
                        UpdateAddressActivity.this);
                builder.setMessage("确认要删除该地址吗")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        UserInfo queryMe = Store.User.queryMe();

                                        if (type.equals("1")) {
                                            if (queryMe!=null){
                                                queryMe.defaultAddress = "";
                                                Store.User.saveMe(queryMe);
                                            }

                                        }
                                        showProgressDialog("删除中");
                                        RequestParams params1 = new RequestParams();
                                        if (queryMe!=null){
                                            params1.put("userId", queryMe.userid);
                                        }
                                        params1.put("addressId", addressId);
                                        execApi(ApiType.DELETE_ADDRESS, params1);
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                builder.create().show();
                break;

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

                if (!StringUtil.empty(zipCode_text.getEditableText().toString()
                        .trim())) {
                    zipCode = zipCode_text.getEditableText().toString().trim();
                }

                showProgressDialog();
                RequestParams params = new RequestParams();
                if (isLogin()){
                    params.put("userId", Store.User.queryMe().userid);
                }
                params.put("addressId", addressId);
                params.put("receiptPhone", shouhuo_tel.getEditableText().toString()
                        .trim());
                params.put("receiptPeople", shouhuo_name.getEditableText()
                        .toString().trim());
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
                execApi(ApiType.UPDATE_ADDRESS, params);
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
                    choice_town_text.setText("");
                    room_edit.setText("");
                    town = "";
                    townid = "";
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
        if (req.getApi() == ApiType.UPDATE_ADDRESS) {
            ResponseResult save = (ResponseResult) req.getData();
            if ("1000".equals(save.getStatus())) {
                if (default_address.isChecked()) {
                    String addr = city + town
                            + room_edit.getEditableText().toString().trim();
                    UserInfo queryMe = Store.User.queryMe();
                    if (queryMe!=null){
                        queryMe.defaultAddress = addr;
                        Store.User.saveMe(queryMe);
                    }
                }
                finish();
                showToast("成功更改了地址");
            } else {
                showToast("更改地址失败");
            }

        } else if (req.getApi() == ApiType.DELETE_ADDRESS) {
            ResponseResult data = (ResponseResult) req.getData();
            if ("1000".equals(data.getStatus())) {
                disMissDialog();
                showToast("删除成功！");
                finish();
            }
        }
    }
}
