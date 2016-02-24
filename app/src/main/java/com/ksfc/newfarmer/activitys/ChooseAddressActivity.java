package com.ksfc.newfarmer.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.ResponseResult;
import com.ksfc.newfarmer.protocol.beans.AddressList;
import com.ksfc.newfarmer.protocol.beans.LoginResult;
import com.ksfc.newfarmer.widget.dialog.CustomDialog;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.StringUtil;

import net.yangentao.util.msg.MsgCenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by HePeng on 2015/12/4.
 */
public class ChooseAddressActivity extends BaseActivity {
    private ListView address_list;
    HashMap<String, Integer> map = new HashMap<String, Integer>();
    private final int reqestcode = 1;
    private addressAdapter adapter;
    private List<AddressList.Address> rows;
    private List<AddressList.Address> addlist;
    private AddressList.Address state;//接受addressId
    private RelativeLayout none_address_rel;
    private int addressCount = -1;//地址列表的数量


    @Override
    public int getLayout() {
        return R.layout.address_manage_layout1;
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
                MsgCenter.fireNull("MSG.ADDRESS.CALL.BACK", state);
                finish();
            }
        });

        //新增地址
        setRightTextViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("addressCount", addressCount);
                IntentUtil.startActivityForResult(ChooseAddressActivity.this, ChoiceActivity.class,
                        reqestcode, bundle);
            }
        });
    }

    //设置返回监听的，回传数据
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            MsgCenter.fireNull("MSG.ADDRESS.CALL.BACK", state);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    //获得地址列表
    private void initData() {
        addlist.clear();
        getAddressList();
    }

    private void getAddressList() {
        showProgressDialog();
        RequestParams params = new RequestParams();
        if (isLogin()){
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
            rows = data.datas.rows;
            if (rows != null && rows.size() > 0) {
                none_address_rel.setVisibility(View.GONE);
                addlist.addAll(rows);
                addressCount = -1;
            } else {
                addlist.clear();
                none_address_rel.setVisibility(View.VISIBLE);
                showToast("请先新增地址");
                addressCount = 0;
                state = null;
            }
            for (int i = 0; i < rows.size(); i++) {
                if (rows.get(i).type == "1" || rows.get(i).type.equals("1")) {
                    map.put("pos", i);
                }
            }
            adapter = new addressAdapter();
            address_list.setAdapter(adapter);
        } else if (req.getApi() == ApiType.DELETE_ADDRESS) {
            ResponseResult data = (ResponseResult) req.getData();
            if ("1000".equals(data.getStatus())) {
                showToast("删除成功！");
                addlist.clear();
                getAddressList();
                adapter.notifyDataSetChanged();
            }
        } else if (req.getApi() == ApiType.UPDATE_ADDRESS) {
            ResponseResult data = (ResponseResult) req.getData();
            if ("1000".equals(data.getStatus())) {
                showToast("更改默认地址成功");
                addlist.clear();
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
            if (addlist == null)
                return 0;
            return addlist.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ChooseAddressActivity.this)
                        .inflate(R.layout.address_list_item_choice, null);
                holder = new ViewHolder();
                holder.address_tv = (TextView) convertView
                        .findViewById(R.id.address_tv);
                holder.btn_check_item = (ImageView) convertView.findViewById(R.id.btn_check_item);
                holder.edit_address_img = (Button) convertView
                        .findViewById(R.id.edit_address_img);
                holder.delete_address_img = (Button) convertView
                        .findViewById(R.id.delete_address_img);
                holder.address_name_tv = (TextView) convertView
                        .findViewById(R.id.address_name_tv);
                holder.address_phone_tv = (TextView) convertView
                        .findViewById(R.id.address_phone_tv);
                holder.address_default_tv = (TextView) convertView
                        .findViewById(R.id.address_default_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (state != null) {
                if (addlist.get(position).addressId.equals(state.addressId)) {
                    holder.btn_check_item.setBackgroundResource(R.drawable.circle_green);
                    state=addlist.get(position);
                }
            }

            holder.address_tv.setText(StringUtil.checkBufferStrWithSpace
                    (addlist.get(position).areaName,
                            addlist.get(position).cityName,
                            addlist.get(position).countyName,
                            addlist.get(position).townName,
                            addlist.get(position).address));
            holder.address_name_tv.setText(addlist.get(position).receiptPeople);
            holder.address_phone_tv.setText(addlist.get(position).receiptPhone);
            //修改地址
            holder.edit_address_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ChooseAddressActivity.this,
                            UpdateAddressActivity.class);
                    intent.putExtra("address", addlist.get(position));
                    startActivity(intent);
                }
            });

            holder.delete_address_img.setOnClickListener(new View.OnClickListener() {
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
                                            if (addlist.get(position).type.equals("1")) {
                                                if (queryMe != null) {
                                                    queryMe.defaultAddress = "";
                                                    Store.User.saveMe(queryMe);
                                                }
                                                map.put("pos", -1);
                                            }
                                            if (addlist.get(position).addressId.equals(state.addressId)) {
                                                state = null;
                                                MsgCenter.fireNull("MSG.ADDRESS.CALL.BACK", state);
                                            }
                                            showProgressDialog();
                                            RequestParams params = new RequestParams();
                                            params.put("userId", addlist.get(position).userId);
                                            params.put("addressId", addlist.get(position).addressId);
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
                    holder.address_default_tv.setVisibility(View.VISIBLE);
                } else {
                    holder.address_default_tv.setVisibility(View.GONE);
                }
            }

            //选择收货的地址
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    state = addlist.get(position);
                    MsgCenter.fireNull("MSG.ADDRESS.CALL.BACK", state);
                    finish();
                }
            });

            return convertView;
        }

        private class ViewHolder {
            private TextView address_tv, address_name_tv, address_phone_tv, address_default_tv;
            private Button edit_address_img;
            private Button delete_address_img;
            private ImageView btn_check_item;
        }

    }


    @Override
    protected void onResume() {
        map.clear();
        initData();
        super.onResume();
    }

}
