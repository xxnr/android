package com.ksfc.newfarmer.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.ResponseResult;
import com.ksfc.newfarmer.protocol.beans.BuildingList;
import com.ksfc.newfarmer.protocol.beans.CityList;
import com.ksfc.newfarmer.protocol.beans.LoginResult;
import com.ksfc.newfarmer.protocol.beans.QueueList;
import com.ksfc.newfarmer.protocol.beans.TownList;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.StringUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import net.yangentao.util.msg.MsgCenter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by CAI on 2015/12/9.
 */
public class SelectUserTypeActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView listView;
    private AddressAdapter adapter;
    private List<String> list_key;
    private List<String> list_value;

    private String select = "";
    private boolean flag;//判断 是否是从完善资料页启动此activity 是否在保存的时候执行api

    @Override
    public int getLayout() {
        return R.layout.selector_address_list_layout;
    }


    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("选择用户类型");
        listView = ((ListView) findViewById(R.id.select_address_list));
        listView.setOnItemClickListener(this);
        flag = getIntent().getExtras().getBoolean("flag");
        showProgressDialog();
        getData();
    }

    //加载用户类型
    private void getData() {

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET, ApiType.USER_TYPE.getOpt(), null,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        disMissDialog();
                        showToast("数据加载失败");
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        disMissDialog();
                        if (!StringUtil.empty(arg0.result.toString())) {
                            RndLog.v(TAG, arg0.result);
                            list_key = new ArrayList<>();
                            list_value = new ArrayList<>();
                            JSONObject jsonObject = JSON.parseObject(arg0.result.toString());
                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            Set<Map.Entry<String, Object>> entrySet = jsonObject1.entrySet();
                            for (Map.Entry<String, Object> entry : entrySet) {
                                list_value.add((String) entry.getValue());
                                list_key.add(entry.getKey());
                            }
                            adapter = new AddressAdapter(list_value);
                            listView.setAdapter(adapter);
                        }
                    }
                });
    }


    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (ApiType.SAVE_MYUSER == req.getApi()) {
            LoginResult.UserInfo queryMe = Store.User.queryMe();
            queryMe.userType = select;
            Store.User.saveMe(queryMe);
            showToast("保存成功！");
            MsgCenter.fireNull(MsgID.UPDATE_USER, "update");
            finish();
        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        select = list_key.get(position);
        Intent intent = new Intent();
        intent.putExtra("str", list_value.get(position));
        intent.putExtra("type_key", list_key.get(position));
        setResult(0x14, intent);
        if (flag) {
            showProgressDialog("正在保存中...");
            RequestParams params = new RequestParams();
            params.put("userId", Store.User.queryMe().userid);
            params.put("type", list_key.get(position));
            execApi(ApiType.SAVE_MYUSER, params);
        } else {
            finish();
        }

    }


    class AddressAdapter extends BaseAdapter {
        private List<String> list;

        public AddressAdapter(List<String> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(SelectUserTypeActivity.this).inflate(R.layout.city, null);
                convertView.setTag(new ViewHolder(convertView));
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.name.setText(list.get(position));
            return convertView;
        }

        public void clear() {
            list.clear();
            notifyDataSetChanged();
        }


        class ViewHolder {
            private TextView name;

            ViewHolder(View convertView) {
                name = ((TextView) convertView.findViewById(R.id.cityTextView));
            }

        }

    }


}
