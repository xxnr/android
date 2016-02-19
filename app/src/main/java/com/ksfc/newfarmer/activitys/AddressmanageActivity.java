package com.ksfc.newfarmer.activitys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.ResponseResult;
import com.ksfc.newfarmer.protocol.beans.AddressList;
import com.ksfc.newfarmer.protocol.beans.AddressList.Address;
import com.ksfc.newfarmer.protocol.beans.LoginResult.UserInfo;
import com.ksfc.newfarmer.widget.dialog.CustomDialog;
import com.ksfc.newfarmer.utils.ExpandViewTouch;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.StringUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.yangentao.util.msg.MsgCenter;

/**
 * 地址管理页
 */
public class AddressmanageActivity extends BaseActivity {
    private ListView address_list;
    HashMap<String, Integer> map = new HashMap<String, Integer>();//创建一个map集合用于存放  是否是默认地址
    private final int reqestcode = 1;
    private addressAdapter adapter;
    private List<AddressList.Address> rows;
    private List<AddressList.Address> addList;
    private AddressList.Address selectedAddress;
    private RelativeLayout none_address_rel;

    private int addressCount = -1;//地址列表的数量


    @Override
    public int getLayout() {
        return R.layout.address_manage_layout1;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        initView();
        setTitle("地址管理");
        showRightTextView();
        setRightTextView("添加");
    }

    private void initView() {

        addList = new ArrayList<>();
        address_list = (ListView) findViewById(R.id.address_list);
        none_address_rel = (RelativeLayout) findViewById(R.id.none_address_rel);

        //设置返回监听的，回传数据
        setLeftClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MsgCenter.fireNull("MSG.ADDR", selectedAddress);
                finish();
            }
        });
        //添加数据
        setRightTextViewListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("addressCount", addressCount);
                IntentUtil.startActivityForResult(AddressmanageActivity.this, ChoiceActivity.class,
                        reqestcode, bundle);
            }
        });
    }

    //设置返回监听的，回传数据
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            MsgCenter.fireNull("MSG.ADDR", selectedAddress);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //获得地址列表
    private void initData() {
        addList.clear();
        getAddressList();
    }

    private void getAddressList() {
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
            if (req.getData().getStatus().equals("1000")){
                AddressList data = (AddressList) req.getData();
                if (data == null) {
                    return;
                }
                if (data.datas == null) {
                    return;
                }
                rows = data.datas.rows;
                map.clear();
                for (int i = 0; i < rows.size(); i++) {
                    if (rows.get(i).type.equals("1")) {
                        map.put("pos", i);
                    }
                }
                if (rows != null && rows.size() > 0) {
                    addList.addAll(rows);
                    none_address_rel.setVisibility(View.GONE);
                    addressCount = -1;
                    selectedAddress = rows.get(0);
                } else {
                    addList.clear();
                    none_address_rel.setVisibility(View.VISIBLE);
                    showToast("请先新增地址");
                    addressCount = 0;
                }
                adapter = new addressAdapter();
                address_list.setAdapter(adapter);
            }

        } else if (req.getApi() == ApiType.DELETE_ADDRESS) {
            ResponseResult data = (ResponseResult) req.getData();
            if ("1000".equals(data.getStatus())) {
                showToast("删除成功！");
                addList.clear();
                getAddressList();
                adapter.notifyDataSetChanged();

            }
        } else if (req.getApi() == ApiType.UPDATE_ADDRESS) {
            ResponseResult data = (ResponseResult) req.getData();
            if ("1000".equals(data.getStatus())) {
                showToast("更改默认地址成功");
                addList.clear();
                getAddressList();
                adapter.notifyDataSetChanged();
            }
        } else if (req.getApi() == ApiType.SELECT_ADDRESS) {
            ResponseResult data = (ResponseResult) req.getData();
            if ("1000".equals(data.getStatus())) {
                MsgCenter.fireNull("MSG.ADDRESS.CALL.BACK",
                        rows.get(map.get("pos")));
                finish();
            }
        }
    }

    public class addressAdapter extends BaseAdapter {
        ViewHolder holder;

        @Override
        public int getCount() {

            if (addList == null)
                return 0;

            return addList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {


            if (convertView == null) {
                convertView = LayoutInflater.from(AddressmanageActivity.this)
                        .inflate(R.layout.address_list_item, null);
                holder = new ViewHolder();
                holder.address_tv = (TextView) convertView
                        .findViewById(R.id.address_tv);
                holder.btn_check_item = (CheckBox) convertView.findViewById(R.id.btn_check_item);
                //设置控件点击区域扩大
                ExpandViewTouch.expandViewTouchDelegate(holder.btn_check_item, 100, 100, 100, 100);
                holder.edit_address_img = (Button) convertView
                        .findViewById(R.id.edit_address_img);
                holder.delete_address_img = (Button) convertView
                        .findViewById(R.id.delete_address_img);
                holder.address_name_tv = (TextView) convertView
                        .findViewById(R.id.address_name_tv);
                holder.address_phone_tv = (TextView) convertView
                        .findViewById(R.id.address_phone_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.address_tv.setText(StringUtil.checkBufferStrWithSpace
                    (addList.get(position).areaName,
                            addList.get(position).cityName,
                            addList.get(position).countyName,
                            addList.get(position).townName,
                            addList.get(position).address));
            holder.address_name_tv.setText(addList.get(position).receiptPeople);
            holder.address_phone_tv.setText(addList.get(position).receiptPhone);

            //编辑地址
            holder.edit_address_img.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddressmanageActivity.this,
                            UpdateAddressActivity.class);
                    intent.putExtra("address", addList.get(position));
                    startActivity(intent);
                }
            });
            //删除地址
            holder.delete_address_img.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // app/buildUser/deleteAddress
                    // id:主键
                    // locationUserId:操作人ID
                    // app/user/saveUserAddress
                    // userId:用户ID,
                    // addressId:地址唯一标识

                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            AddressmanageActivity.this);
                    builder.setMessage("确认要删除该地址吗")
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            if (addList.get(position).type.equals("1")) {
                                                UserInfo queryMe = Store.User.queryMe();
                                                if (queryMe != null) {
                                                    queryMe.defaultAddress = "";
                                                    Store.User.saveMe(queryMe);
                                                    map.put("pos", -1);
                                                    selectedAddress = null;
                                                    MsgCenter.fireNull("MSG.ADDR", selectedAddress);
                                                }
                                            }
                                            showProgressDialog();
                                            RequestParams params = new RequestParams();
                                            params.put("userId", addList.get(position).userId);
                                            params.put("addressId", addList.get(position).addressId);
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
                if (position == map.get("pos")) {
                    holder.btn_check_item.setChecked(true);
                } else {
                    holder.btn_check_item.setChecked(false);
                }
            }

            holder.btn_check_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    showProgressDialog();

                    Address address = addList.get(position);

                    RequestParams params = new RequestParams();
                    if (isLogin()) {
                        params.put("userId", Store.User.queryMe().userid);
                    }
                    params.put("addressId", address.addressId);
                    params.put("receiptPhone", address.receiptPhone);
                    params.put("receiptPeople", address.receiptPeople);
                    params.put("areaId", address.areaId);
                    params.put("cityId", address.cityId);
                    params.put("countyId", address.countyId);
                    params.put("townId", address.townId);
                    params.put("zipCode", address.zipCode);
                    params.put("address", address.address);
                    if (isChecked) {
                        params.put("type", 1);
                        UserInfo queryMe = Store.User.queryMe();
                        if (queryMe != null) {
                            queryMe.defaultAddress = StringUtil.checkBufferStr(address.areaName + address.cityName, address.countyName, address.townName, address.address);
                            Store.User.saveMe(queryMe);
                        }
                    } else {
                        params.put("type", 2);
                        UserInfo queryMe = Store.User.queryMe();
                        if (queryMe != null) {
                            queryMe.defaultAddress = "";
                            Store.User.saveMe(queryMe);
                        }
                    }
                    execApi(ApiType.UPDATE_ADDRESS, params);
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView address_tv, address_name_tv, address_phone_tv;
            private Button edit_address_img;
            private Button delete_address_img;
            private CheckBox btn_check_item;
        }

    }


    @Override
    protected void onResume() {
        initData();
        super.onResume();
    }
}
