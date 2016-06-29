package com.ksfc.newfarmer.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.RndApplication;
import com.ksfc.newfarmer.common.CommonAdapter;
import com.ksfc.newfarmer.common.CommonViewHolder;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.http.beans.AddOrderResult;
import com.ksfc.newfarmer.utils.StringUtil;


import java.util.List;

/**
 * Created by HePeng on 2016/1/14.
 */
public class SelectPayOrderActivity extends BaseActivity {
    private ListView listView;

    @Override
    public int getLayout() {
        return R.layout.activity_split_order;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {

        RndApplication.tempDestroyActivityList.add(SelectPayOrderActivity.this);
        setTitle("选择支付订单");
        Intent intent = getIntent();
        List<AddOrderResult.Orders> ordersList = (List<AddOrderResult.Orders>) intent.getSerializableExtra("orderInfo");

        listView = ((ListView) findViewById(R.id.select_pay_order));
        if (ordersList!=null&&!ordersList.isEmpty()){
            OrderAdapter adapter = new OrderAdapter(SelectPayOrderActivity.this,ordersList);
            listView.setAdapter(adapter);
        }

    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {

    }


    class OrderAdapter extends CommonAdapter<AddOrderResult.Orders>{


        public OrderAdapter(Context context, List<AddOrderResult.Orders> data) {
            super(context, data, R.layout.item_select_pay_order);
        }

        @Override
        public void convert(CommonViewHolder holder, final AddOrderResult.Orders orders) {

            if (orders!=null){

                //订单号
                if (StringUtil.checkStr(orders.id)) {
                    holder.setText(R.id.order_id, "订单号：" + orders.id);
                }

                //订单类型（总额 or 订金）
                if (orders.deposit==0) {
                    holder.setText(R.id.order_type, "订单总额");
                } else {
                    holder.setText(R.id.order_type, "阶段一：订金");
                }

                //订单金额（总额 and 待付）
                if (orders.payment != null) {
                    if (StringUtil.checkStr(orders.payment.price)) {
                        holder.setText(R.id.to_pay_price, StringUtil.toTwoString(orders.payment.price) + "元");
                    }
                }
                if (StringUtil.checkStr(orders.price)) {
                    holder.setText(R.id.order_total_price, StringUtil.toTwoString(orders.price) + "元");
                }

                //订单金额（商品 and 个数）
                if (orders.SKUs != null && !orders.SKUs.isEmpty()) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < orders.SKUs.size(); i++) {
                        if (StringUtil.checkStr(orders.SKUs.get(i).productName)) {
                            builder.append(orders.SKUs.get(i).productName)
                                    .append("-")
                                    .append(orders.SKUs.get(i).count).append("件").append("，");
                        }
                    }
                    String names_counts = builder.toString().substring(0, builder.toString().length() - 1);
                    if (StringUtil.checkStr(names_counts)) {
                        holder.setText(R.id.goods_info, names_counts);
                    }
                }

                holder.getView(R.id.go_to_pay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SelectPayOrderActivity.this,
                                PaywayActivity.class);
                        if (StringUtil.checkStr(orders.id)) {
                            intent.putExtra("orderId", orders.id);
                            startActivity(intent);
                        }
                    }
                });



            }
        }
    }



}
