package com.ksfc.newfarmer.activitys;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.common.CommonAdapter;
import com.ksfc.newfarmer.common.CommonViewHolder;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.event.AddressChangeEvent;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.beans.AddressList;
import com.ksfc.newfarmer.beans.LoginResult;
import com.ksfc.newfarmer.widget.dialog.CustomDialog;
import com.ksfc.newfarmer.utils.StringUtil;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by HePeng on 2015/12/4.
 */
public class ChooseAddressActivity extends BaseActivity {
    private ListView address_list;
    HashMap<String, Integer> map = new HashMap<>();
    private AddressAdapter adapter;
    private List<AddressList.Address> addlist;
    private AddressList.Address state;//接受addressId
    private RelativeLayout none_address_rel;
    private int addressCount = -1;//地址列表的数量


    @Override
    public int getLayout() {
        return R.layout.activity_address_manage;
    }


    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        initView();
        setTitle("收货地址");
        showRightTextView();
    }

    private void initView() {
        state = (AddressList.Address) getIntent().getSerializableExtra("state");
        setRightTextView("添加");
        addlist = new ArrayList<>();
        address_list = (ListView) findViewById(R.id.address_list);
        none_address_rel = (RelativeLayout) findViewById(R.id.none_address_rel);


        //设置返回监听的，回传数据
        setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new AddressChangeEvent(state));
                finish();
            }
        });

        //新增地址
        setRightTextViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent callingIntent = AddAddressActivity.getCallingIntent(ChooseAddressActivity.this, addressCount);
                startActivity(callingIntent);

            }
        });
    }

    //设置返回监听的，回传数据
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            EventBus.getDefault().post(new AddressChangeEvent(state));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    //获得地址列表
    private void getAddressList() {
        addlist.clear();
        showProgressDialog();
        RequestParams params = new RequestParams();
        if (isLogin()) {
            params.put("userId", Store.User.queryMe().userid);

        }
        execApi(ApiType.ADDRESS_LIST, params);
    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (req.getApi() == ApiType.ADDRESS_LIST) {
            AddressList data = (AddressList) req.getData();
            if (data == null) {
                return;
            }
            if (data.datas == null) {
                return;
            }
            List<AddressList.Address> rows = data.datas.rows;
            if (rows != null && rows.size() > 0) {
                none_address_rel.setVisibility(View.GONE);
                addlist.addAll(rows);
                addressCount = -1;
                for (int i = 0; i < rows.size(); i++) {
                    if (rows.get(i).type.equals("1")) {
                        map.put("pos", i);
                    }
                }
            } else {
                addlist.clear();
                none_address_rel.setVisibility(View.VISIBLE);
                showToast("请先新增地址");
                addressCount = 0;
                state = null;
            }
            adapter = new AddressAdapter(this, addlist);
            address_list.setAdapter(adapter);
        } else if (req.getApi() == ApiType.DELETE_ADDRESS) {
            if ("1000".equals(req.getData().getStatus())) {
                showToast("删除成功！");
                getAddressList();
            }
        } else if (req.getApi() == ApiType.UPDATE_ADDRESS) {
            if ("1000".equals(req.getData().getStatus())) {
                showToast("更改默认地址成功");
                getAddressList();
            }
        }
    }

    public class AddressAdapter extends CommonAdapter<AddressList.Address> {

        public AddressAdapter(Context context, List<AddressList.Address> data) {
            super(context, data, R.layout.item_address_list_choice);
        }

        @Override
        public void convert(CommonViewHolder holder, final AddressList.Address address) {

            if (address != null) {
                //文本内容
                holder.setText(R.id.address_tv, StringUtil.checkBufferStrWithSpace
                        (address.areaName,
                                address.cityName,
                                address.countyName,
                                address.townName,
                                address.address));
                holder.setText(R.id.address_name_tv, address.receiptPeople);
                holder.setText(R.id.address_phone_tv, address.receiptPhone);

                if (state != null) {
                    if (address.addressId.equals(state.addressId)) {
                        holder.getView(R.id.btn_check_item).setBackgroundResource(R.drawable.circle_green);
                        state = address;
                    }
                }

                //修改地址
                holder.getView(R.id.edit_address_img).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChooseAddressActivity.this,
                                UpdateAddressActivity.class);
                        intent.putExtra("address", address);
                        startActivity(intent);
                    }
                });
                //删除地址
                holder.getView(R.id.delete_address_img).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // app/buildUser/deleteAddress
                        // id:主键
                        // locationUserId:操作人ID
                        // app/user/saveUserAddress
                        // userId:用户ID,
                        // addressId:地址唯一标识
                        CustomDialog.Builder builder = new CustomDialog.Builder(
                                ChooseAddressActivity.this);
                        builder.setMessage("确认要删除该地址吗")
                                .setPositiveButton("确定",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                LoginResult.UserInfo queryMe = Store.User.queryMe();
                                                if (address.type.equals("1")) {
                                                    if (queryMe != null) {
                                                        queryMe.defaultAddress = "";
                                                        Store.User.saveMe(queryMe);
                                                    }
                                                    map.put("pos", -1);
                                                }

                                                if (state !=null&&address.addressId.equals(state.addressId)) {
                                                    state = null;
                                                    EventBus.getDefault().post(new AddressChangeEvent(state));
                                                }
                                                showProgressDialog();
                                                RequestParams params = new RequestParams();
                                                if (queryMe != null) {
                                                    params.put("userId", queryMe.userid);
                                                }
                                                params.put("addressId", address.addressId);
                                                execApi(ApiType.DELETE_ADDRESS, params);
                                                dialog.dismiss();
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
                    }
                });
                if (map.get("pos") != null) {
                    if (holder.getPosition() == map.get("pos")) {
                        holder.getView(R.id.address_default_tv).setVisibility(View.VISIBLE);
                    } else {
                        holder.getView(R.id.address_default_tv).setVisibility(View.GONE);
                    }
                }

                //选择收货的地址
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        state = address;
                        EventBus.getDefault().post(new AddressChangeEvent(state));
                        finish();
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        map.clear();
        getAddressList();
        super.onResume();
    }

}
