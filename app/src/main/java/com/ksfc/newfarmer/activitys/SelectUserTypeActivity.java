package com.ksfc.newfarmer.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.common.CommonAdapter;
import com.ksfc.newfarmer.common.CommonViewHolder;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.NetworkHelper;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.beans.LoginResult;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.StringUtil;

import net.yangentao.util.msg.MsgCenter;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by HePeng on 2015/12/9.
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
        return R.layout.activity_selector_address_list;
    }


    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("选择用户类型");
        listView = ((ListView) findViewById(R.id.select_address_list));
        listView.setOnItemClickListener(this);
        if (getIntent().getExtras() != null) {
            flag = getIntent().getExtras().getBoolean("flag");
        }
        showProgressDialog();
        getData();
    }

    //加载用户类型
    private void getData() {
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(ApiType.USER_TYPE.getOpt())
                .get()
                .build();
        NetworkHelper.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                disMissDialog();
                showToast("数据加载失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                disMissDialog();
                if (response.isSuccessful() && response.body() != null) {
                    String result = response.body().string();
                    if (StringUtil.checkStr(result)) {
                        RndLog.v(TAG, result);
                        list_key = new ArrayList<>();
                        list_value = new ArrayList<>();
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            JSONObject data = jsonObject.getJSONObject("data");
                            Iterator<String> iterator = data.keys();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                list_key.add(key);
                                list_value.add(data.getString(key));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Collections.reverse(list_key);
                        Collections.reverse(list_value);

                        adapter = new AddressAdapter(SelectUserTypeActivity.this, list_value);
                        listView.setAdapter(adapter);
                    }
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
            if (queryMe != null) {
                queryMe.userType = select;
                Store.User.saveMe(queryMe);
            }
            showToast("保存成功");
            MsgCenter.fireNull(MsgID.UPDATE_USER_TYPE);
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
            if (isLogin()) {
                params.put("userId", Store.User.queryMe().userid);
            }
            params.put("type", list_key.get(position));
            execApi(ApiType.SAVE_MYUSER.setMethod(ApiType.RequestMethod.GET), params);
        } else {
            finish();
        }

    }


    class AddressAdapter extends CommonAdapter<String> {

        public AddressAdapter(Context context, List<String> data) {
            super(context, data, R.layout.item_only_text);
        }

        @Override
        public void convert(CommonViewHolder holder, String s) {
            if (StringUtil.checkStr(s)) {
                holder.setText(R.id.cityTextView, s);
            }
        }
    }


}
