package com.ksfc.newfarmer.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.common.CommonAdapter;
import com.ksfc.newfarmer.common.CommonViewHolder;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.http.ApiType;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.http.RequestParams;
import com.ksfc.newfarmer.http.beans.IntentionProductsResult;
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
        return R.layout.activity_select_intent_product;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {

        setTitle("选择意向商品");
        listView = ((ListView) findViewById(R.id.listView));
        showProgressDialog();
        RequestParams params = new RequestParams();
        if (isLogin()) {
            params.put("userId", Store.User.queryMe().userid);
            execApi(ApiType.GET_PURPOSE_GOODS_LIST
                    .setMethod(ApiType.RequestMethod.GET)
                    , params);
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
                    if (StringUtil.checkStr(builder.toString())) {
                        substring = builder.toString().substring(0, builder.toString().length() - 1);
                    }
                    if (StringUtil.checkStr(substring) && !list.isEmpty()) {
                        Intent intent = new Intent();
                        intent.putExtra("productIdList", (Serializable) list);
                        intent.putExtra("str", substring);
                        setResult(0x14, intent);
                        finish();
                    } else {
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
                    adapter = new ProductAdapter(SelectIntentProductActivity.this,data.intentionProducts);
                    listView.setAdapter(adapter);
                }

            }

        }
    }


    class ProductAdapter extends CommonAdapter<IntentionProductsResult.IntentionProductsEntity> {
        private HashMap<String, Boolean> checkMap = new HashMap<>();
        private HashMap<String, Boolean> checkNameMap = new HashMap<>();

        public ProductAdapter(Context context, List<IntentionProductsResult.IntentionProductsEntity> data) {
            super(context, data, R.layout.item_select_intent_goods);
        }

        @Override
        public void convert(final CommonViewHolder holder, final IntentionProductsResult.IntentionProductsEntity intentionProductsEntity) {
            if (intentionProductsEntity != null) {

                holder.setText(R.id.item_select_intent_name, intentionProductsEntity.name);
                final CheckBox checkBox = (CheckBox) holder.getView(R.id.item_select_intent_checkbox);
                checkBox.setEnabled(false);
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!checkBox.isChecked()) {
                            checkBox.setChecked(true);
                            checkMap.put(intentionProductsEntity._id, true);
                            checkNameMap.put(intentionProductsEntity.name, true);
                        } else {
                            checkBox.setChecked(false);
                            checkMap.put(intentionProductsEntity._id, false);
                            checkNameMap.put(intentionProductsEntity.name, false);
                        }

                    }
                });
            }
        }
    }


}
