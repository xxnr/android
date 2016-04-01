package com.ksfc.newfarmer.activitys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.adapter.CommonAdapter;
import com.ksfc.newfarmer.adapter.CommonViewHolder;
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

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import net.yangentao.util.msg.MsgCenter;

/**
 * 地址管理页
 */
public class AddressmanageActivity extends BaseActivity {
    private ListView address_list;
    HashMap<String, Integer> map = new HashMap<>();//创建一个map集合用于存放  是否是默认地址
    private final int reqestcode = 1;
    private AddressAdapter adapter;
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

        //添加数据
        setRightTextViewListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("addressCount", addressCount);
                IntentUtil.startActivityForResult(AddressmanageActivity.this, AddAddressActivity.class,
                        reqestcode, bundle);
            }
        });
    }


    //获得地址列表
    private void initData() {
        getAddressList();
    }

    private void getAddressList() {
        addList.clear();
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
            if (req.getData().getStatus().equals("1000")) {
                AddressList data = (AddressList) req.getData();

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
                adapter = new AddressAdapter(AddressmanageActivity.this,addList);
                address_list.setAdapter(adapter);
            }

        } else if (req.getApi() == ApiType.DELETE_ADDRESS) {
            if ("1000".equals(req.getData().getStatus())) {
                showToast("删除成功！");
                getAddressList();
                adapter.notifyDataSetChanged();

            }
        } else if (req.getApi() == ApiType.UPDATE_ADDRESS) {
            if ("1000".equals(req.getData().getStatus())) {
                showToast("更改默认地址成功");
                getAddressList();
                adapter.notifyDataSetChanged();
            }
        } else if (req.getApi() == ApiType.SELECT_ADDRESS) {
            if ("1000".equals( req.getData().getStatus())) {
                MsgCenter.fireNull(MsgID.MSG_Change_ADDRESS,
                        rows.get(map.get("pos")));
                finish();
            }
        }
    }

    public class AddressAdapter extends CommonAdapter<AddressList.Address> {


        public AddressAdapter(Context context, List<Address> data) {
            super(context, data, R.layout.address_list_item);
        }

        @Override
        public void convert(CommonViewHolder holder, final Address address) {

            if (address!=null){
                //文本内容
                holder.setText(R.id.address_tv, StringUtil.checkBufferStrWithSpace
                        (address.areaName,
                                address.cityName,
                                address.countyName,
                                address.townName,
                                address.address));
                holder.setText(R.id.address_name_tv,address.receiptPeople);
                holder.setText(R.id.address_phone_tv,address.receiptPhone);
                //初始化其他组件
                CheckBox btn_check_item = (CheckBox) holder.getView(R.id.btn_check_item);
                //设置控件点击区域扩大
                ExpandViewTouch.expandViewTouchDelegate(btn_check_item, 100, 100, 100, 100);

                //编辑地址
                holder.getView(R.id.edit_address_img).setOnClickListener(new OnClickListener() {

                     @Override
                     public void onClick(View v) {
                         Intent intent = new Intent(AddressmanageActivity.this,
                                 UpdateAddressActivity.class);
                         intent.putExtra("address", address);
                         startActivity(intent);
                     }
                 });

                //删除地址
                holder.getView(R.id.delete_address_img).setOnClickListener(new OnClickListener() {

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
                                               if (address.type.equals("1")) {
                                                   UserInfo queryMe = Store.User.queryMe();
                                                   if (queryMe != null) {
                                                       queryMe.defaultAddress = "";
                                                       Store.User.saveMe(queryMe);
                                                       map.put("pos", -1);
                                                       selectedAddress = null;
                                                       MsgCenter.fireNull(MsgID.MSG_ADD_ADDRESS, selectedAddress);
                                                   }
                                               }
                                               showProgressDialog();
                                               RequestParams params = new RequestParams();
                                               UserInfo queryMe = Store.User.queryMe();
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
                        btn_check_item.setChecked(true);
                    } else {
                        btn_check_item.setChecked(false);
                    }
                }

                btn_check_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        showProgressDialog();
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
            }

        }
    }
    @Override
    protected void onResume() {
        initData();
        super.onResume();
    }
}
