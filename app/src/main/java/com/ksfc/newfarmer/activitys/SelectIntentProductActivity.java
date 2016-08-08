package com.ksfc.newfarmer.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.beans.IntentProductsResult;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by HePeng on 2016/2/2.
 */
public class SelectIntentProductActivity extends BaseActivity {
    @BindView(R.id.expandListView)
    ExpandableListView expandableListView;
    @BindView(R.id.choice_compelet)
    TextView choice_compelet;


    private ProductAdapter adapter;

    private HashMap<String, Boolean> checkMap = new HashMap<>();
    private HashMap<String, Boolean> checkNameMap = new HashMap<>();


    @Override
    public int getLayout() {
        return R.layout.activity_select_intent_product;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setTitle("选择意向商品");
        expandableListView.setGroupIndicator(null);
        showProgressDialog();
        if (isLogin()) {
            execApi(ApiType.GET_INTENT_PRODUCTS.setMethod(ApiType.RequestMethod.GET), null);
        }
    }

    @OnClick(R.id.choice_compelet)
    void choice_compelet() {
        if (adapter != null) {
            List<String> list = new ArrayList<>();
            StringBuilder builder = new StringBuilder();
            String substring = null;
            for (Map.Entry<String, Boolean> entry : checkMap.entrySet()) {
                if (entry.getValue()) {
                    list.add(entry.getKey());
                }
            }
            for (Map.Entry<String, Boolean> entry : checkNameMap.entrySet()) {
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
    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (req.getApi() == ApiType.GET_INTENT_PRODUCTS) {
            IntentProductsResult data = (IntentProductsResult) req.getData();
            if (data.getStatus().equals("1000")) {
                List<IntentProductsResult.IntentionProductsBean> products = data.intentionProducts;
                if (products != null && !products.isEmpty()) {
                    adapter = new ProductAdapter(products);
                    expandableListView.setAdapter(adapter);
                }
            }
        }
    }


    class ProductAdapter extends BaseExpandableListAdapter {
        private List<IntentProductsResult.IntentionProductsBean> products;

        public ProductAdapter(List<IntentProductsResult.IntentionProductsBean> products) {
            this.products = products;
        }

        @Override
        public int getGroupCount() {
            return products.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            IntentProductsResult.IntentionProductsBean productsBean = products.get(groupPosition);
            if (productsBean != null && productsBean.products != null) {
                return productsBean.products.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            return products.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            IntentProductsResult.IntentionProductsBean productsBean = products.get(groupPosition);
            if (productsBean != null && productsBean.products != null) {
                return productsBean.products.get(childPosition);
            } else {
                return null;
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(SelectIntentProductActivity.this)
                        .inflate(R.layout.item_intent_products_group, null);
                convertView.setTag(new GroupViewHolder(convertView));
            }
            GroupViewHolder holder = (GroupViewHolder) convertView.getTag();

            if (isExpanded) {
                holder.arrow.setBackgroundResource(R.drawable.arrow_top_light_gary);
            } else {
                holder.arrow.setBackgroundResource(R.drawable.arrow_bottom_light_gary);
            }
            IntentProductsResult.IntentionProductsBean productsBean = products.get(groupPosition);
            if (productsBean != null) {
                holder.brandName.setText(StringUtil.checkStr(productsBean.brand) ? productsBean.brand : "");
                List<String> ids = new ArrayList<>();
                int count = 0;
                int totalCount = 0;
                try {
                    for (int i = 0; i < productsBean.products.size(); i++) {
                        ids.add(productsBean.products.get(i)._id);
                    }
                    for (Map.Entry<String, Boolean> entry : checkMap.entrySet()) {
                        if (entry.getValue()) {
                            if (ids.contains(entry.getKey())) {
                                count++;
                            }
                            totalCount++;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (count > 0) {
                    holder.selectCount.setText("已选" + count + "项");
                } else {
                    holder.selectCount.setText("");
                }
                if (totalCount > 0) {
                    choice_compelet.setText("确定(" + totalCount + ")");
                } else {
                    choice_compelet.setText("确定");
                }

            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(SelectIntentProductActivity.this)
                        .inflate(R.layout.item_intent_products_child, null);
                convertView.setTag(new ChildViewHolder(convertView));
            }
            final ChildViewHolder holder = (ChildViewHolder) convertView.getTag();

            IntentProductsResult.IntentionProductsBean productsBean = products.get(groupPosition);
            if (productsBean != null && productsBean.products != null) {
                final IntentProductsResult.IntentionProductsBean.ProductsBean product = productsBean.products.get(childPosition);
                if (product != null) {
                    holder.itemSelectIntentName.setText(product.name);
                    RxView.clicks(convertView)
                            .throttleFirst(500, TimeUnit.MILLISECONDS)
                            .subscribe(new Action1<Void>() {
                                @Override
                                public void call(Void aVoid) {
                                    Boolean aBoolean = checkMap.get(product._id);
                                    if (aBoolean != null && aBoolean) {
                                        checkMap.put(product._id, false);
                                        checkNameMap.put(product.name, false);
                                    } else {
                                        checkMap.put(product._id, true);
                                        checkNameMap.put(product.name, true);
                                    }
                                    notifyDataSetChanged();
                                }
                            });
                    if (checkMap.get(product._id) != null && checkMap.get(product._id)) {
                        holder.itemSelectIntentName.setTextColor(getResources().getColor(R.color.green));
                        holder.itemSelectIntentCheckbox.setVisibility(View.VISIBLE);
                    } else {
                        holder.itemSelectIntentName.setTextColor(getResources().getColor(R.color.black_goods_titile));
                        holder.itemSelectIntentCheckbox.setVisibility(View.INVISIBLE);
                    }
                }
            }

            return convertView;
        }


    }

    static class GroupViewHolder {
        @BindView(R.id.brand_name)
        TextView brandName;
        @BindView(R.id.select_count)
        TextView selectCount;
        @BindView(R.id.arrow)
        ImageView arrow;

        GroupViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ChildViewHolder {
        @BindView(R.id.item_select_intent_name)
        TextView itemSelectIntentName;
        @BindView(R.id.item_select_intent_checkbox)
        ImageView itemSelectIntentCheckbox;

        ChildViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
