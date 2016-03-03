package com.ksfc.newfarmer.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.RndApplication;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.beans.AddOrderResult;
import com.ksfc.newfarmer.utils.StringUtil;

import net.yangentao.util.app.App;

import java.util.List;

/**
 * Created by HePeng on 2016/1/14.
 */
public class SelectPayOrderActivity extends BaseActivity {
    private ListView listView;

    @Override
    public int getLayout() {
        return R.layout.select_pay_order_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {

        App.getApp().addActivity(this);
        RndApplication.tempDestroyActivityList.add(SelectPayOrderActivity.this);
        setTitle("选择支付订单");
        Intent intent = getIntent();
        List<AddOrderResult.Orders> ordersList = (List<AddOrderResult.Orders>) intent.getSerializableExtra("orderInfo");

        listView = ((ListView) findViewById(R.id.select_pay_order));
        OrderAdapter adapter = new OrderAdapter(ordersList);
        listView.setAdapter(adapter);
    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {

    }

    class OrderAdapter extends BaseAdapter {
        private List<AddOrderResult.Orders> ordersList;

        public OrderAdapter(List<AddOrderResult.Orders> ordersList) {
            this.ordersList = ordersList;
        }

        @Override
        public int getCount() {
            return ordersList.size();
        }

        @Override
        public Object getItem(int position) {
            return ordersList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(SelectPayOrderActivity.this).inflate(R.layout.item_select_payorder, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            //订单号
            if (StringUtil.checkStr(ordersList.get(position).id)) {
                holder.orderId.setText("订单号：" + ordersList.get(position).id);
            }
            //订单类型（总额 or 订金）
            if (ordersList.get(position).deposit==0) {
                holder.order_type.setText("订单总额");
            } else {
                holder.order_type.setText("阶段一：订金");
            }
            //订单金额（总额 and 待付）
            if (ordersList.get(position).payment != null) {
                if (StringUtil.checkStr(ordersList.get(position).payment.price)) {
                    holder.to_pay_price.setText(StringUtil.toTwoString(ordersList.get(position).payment.price) + "元");
                }
            }

            if (StringUtil.checkStr(ordersList.get(position).price)) {
                holder.order_total_price.setText(StringUtil.toTwoString(ordersList.get(position).price) + "元");
            }
            //订单金额（商品 and 个数）
            if (ordersList.get(position).SKUs != null && !ordersList.get(position).SKUs.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < ordersList.get(position).SKUs.size(); i++) {
                    if (StringUtil.checkStr(ordersList.get(position).SKUs.get(i).productName)) {
                        builder.append(ordersList.get(position).SKUs.get(i).productName)
                                .append("-")
                                .append(ordersList.get(position).SKUs.get(i).count).append("件").append("，");
                    }
                }
                String names_counts = builder.toString().substring(0, builder.toString().length() - 1);
                if (StringUtil.checkStr(names_counts)) {
                    holder.goods_info.setText(names_counts);
                }
            }

            holder.go_to_pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SelectPayOrderActivity.this,
                            PaywayActivity.class);
                    if (StringUtil.checkStr(ordersList.get(position).id)) {
                        intent.putExtra("orderId", ordersList.get(position).id);
                        startActivity(intent);
                    }
                }
            });

            return convertView;
        }

        class ViewHolder {
            private TextView orderId, order_type, to_pay_price, order_total_price, goods_info;
            private Button go_to_pay;

            public ViewHolder(View convertView) {
                this.orderId = (TextView) convertView.findViewById(R.id.order_id);
                this.order_type = (TextView) convertView.findViewById(R.id.order_type);
                this.to_pay_price = (TextView) convertView.findViewById(R.id.to_pay_price);
                this.order_total_price = (TextView) convertView.findViewById(R.id.order_total_price);
                this.goods_info = (TextView) convertView.findViewById(R.id.goods_info);
                this.go_to_pay = (Button) convertView.findViewById(R.id.go_to_pay);
            }

        }


    }


}
