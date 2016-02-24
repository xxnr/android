package com.ksfc.newfarmer.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.IntentionProductsResult;
import com.ksfc.newfarmer.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HePeng on 2016/2/2.
 */
public class SelectIntentProductActivity extends BaseActivity {
    private ListView listView;
    private ProductAdapter adapter;


    @Override
    public int getLayout() {
        return R.layout.select_intent_product_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {

        setTitle("选择意向商品");
        listView = ((ListView) findViewById(R.id.listView));
        showProgressDialog();
        RequestParams params = new RequestParams();
        if (isLogin()){
            execApi(ApiType.GET_PURPOSE_GOODS_LIST
                    .setMethod(ApiType.RequestMethod.GET)
                    .setOpt("/api/v2.1/intentionProducts" + "?token=" + Store.User.queryMe().token), params);
        }
        setViewClick(R.id.choice_compelet);
    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.choice_compelet:
                if (adapter != null) {
                    List<String> list = new ArrayList<>();
                    StringBuilder builder = new StringBuilder();
                    String substring = null;
                    for (Map.Entry<String, Boolean> entry : adapter.checkMap.entrySet()) {
                        if (entry.getValue()) {
                            list.add(entry.getKey());
                        }
                    }
                    for (Map.Entry<String, Boolean> entry : adapter.checkNameMap.entrySet()) {
                        if (entry.getValue()) {
                            builder.append(entry.getKey()).append("；");
                        }
                    }
                    if (StringUtil.checkStr(builder.toString())){
                        substring = builder.toString().substring(0, builder.toString().length() - 1);
                    }
                    if (StringUtil.checkStr(substring)&&!list.isEmpty()){
                        Intent intent = new Intent();
                        intent.putExtra("productIdList", (Serializable) list);
                        intent.putExtra("str", substring);
                        setResult(0x14, intent);
                        finish();
                    }else {
                        showToast("请至少选择一个意向商品");
                    }
                }


                break;
        }
    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (req.getApi() == ApiType.GET_PURPOSE_GOODS_LIST) {
            IntentionProductsResult data = (IntentionProductsResult) req.getData();
            if (data.getStatus().equals("1000")) {
                if (data.intentionProducts != null && !data.intentionProducts.isEmpty()) {
                    adapter = new ProductAdapter(data.intentionProducts);
                    listView.setAdapter(adapter);
                }

            }

        }
    }

    class ProductAdapter extends BaseAdapter {
        private List<IntentionProductsResult.IntentionProductsEntity> list;
        private HashMap<String, Boolean> checkMap = new HashMap<>();
        private HashMap<String, Boolean> checkNameMap = new HashMap<>();

        public ProductAdapter(List<IntentionProductsResult.IntentionProductsEntity> list) {
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(SelectIntentProductActivity.this).inflate(R.layout.item_select_intent_layout, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.product_name.setText(list.get(position).name);
            holder.checkbox.setEnabled(false);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!holder.checkbox.isChecked()) {
                        holder.checkbox.setChecked(true);
                        checkMap.put(list.get(position)._id, true);
                        checkNameMap.put(list.get(position).name, true);
                    } else {
                        holder.checkbox.setChecked(false);
                        checkMap.put(list.get(position)._id, false);
                        checkNameMap.put(list.get(position).name, false);
                    }

                }
            });
            return convertView;
        }

        class ViewHolder {
            private TextView product_name;
            private CheckBox checkbox;

            ViewHolder(View convertView) {
                this.checkbox = (CheckBox) convertView.findViewById(R.id.item_select_intent_checkbox);
                this.product_name = (TextView) convertView.findViewById(R.id.item_select_intent_name);
            }

        }
    }
}
