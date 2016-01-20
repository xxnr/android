package com.ksfc.newfarmer.activitys;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.beans.MyOrderDetailResult;
import com.ksfc.newfarmer.utils.DateFormatUtils;
import com.ksfc.newfarmer.utils.StringUtil;

import java.util.List;

/**
 * Created by CAI on 2016/1/20.
 */
public class CheckPayDetailActivity extends BaseActivity {
    private ListView listView;
    private TextView orderId_tv, orderStep_tv, orderState_tv, to_pay_price_tv, order_yet_tv;

    @Override
    public int getLayout() {
        return R.layout.my_order_detail;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("查看支付详情");
        String orderId = getIntent().getStringExtra("orderId");
        MyOrderDetailResult.Rows.SubOrders subOrder = (MyOrderDetailResult.Rows.SubOrders) getIntent().getSerializableExtra("payInfo");
        initView();
        if (StringUtil.checkStr(orderId)) {
            orderId_tv.setText("订单号：" + orderId);
        }
        if (subOrder != null) {
            //支付阶段
            if (subOrder.type.equals("deposit")) {
                orderStep_tv.setText("阶段一：订金");
            } else if (subOrder.type.equals("balance")) {
                orderStep_tv.setText("阶段二：尾款");
            } else if (subOrder.type.equals("full")) {
                orderStep_tv.setText("订单总额");
            }
            //支付状态
            if (subOrder.payStatus.equals("1")) {
                orderState_tv.setTextColor(getResources().getColor(R.color.orange));
                orderState_tv.setText("待付款");
            } else if (subOrder.payStatus.equals("2")) {
                orderState_tv.setTextColor(getResources().getColor(R.color.black_goods_titile));
                orderState_tv.setText("已付款");
            } else if (subOrder.payStatus.equals("3")) {
                orderState_tv.setTextColor(getResources().getColor(R.color.orange));
                orderState_tv.setText("部分付款");
            }
            //应支付金额
            to_pay_price_tv.setText("¥"+subOrder.price);
            //已支付金额
            order_yet_tv.setText("¥"+subOrder.paidPrice);
            PayInfoAdapter payInfoAdapter = new PayInfoAdapter(subOrder.payments);
            listView.setAdapter(payInfoAdapter);
        }


    }

    private void initView() {
        View headView = LayoutInflater.from(this).inflate(R.layout.check_pay_detail_head_layout, null);
        orderId_tv = ((TextView) headView.findViewById(R.id.check_pay_orderId));    //订单号
        orderStep_tv = ((TextView) headView.findViewById(R.id.check_pay_orderStep)); //阶段状态
        orderState_tv = ((TextView) headView.findViewById(R.id.check_pay_orderState));//付款状态
        to_pay_price_tv = ((TextView) headView.findViewById(R.id.to_pay_price));//应支付金额
        order_yet_tv = ((TextView) headView.findViewById(R.id.order_yet_price));//已支付金额
        listView = ((ListView) findViewById(R.id.order_shangpin_list));
        listView.addHeaderView(headView);
    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {

    }


    class PayInfoAdapter extends BaseAdapter {
        private List<MyOrderDetailResult.Rows.SubOrders.Payments> payments;

        public PayInfoAdapter(List<MyOrderDetailResult.Rows.SubOrders.Payments> payments) {
            this.payments = payments;
        }

        @Override
        public int getCount() {
            return payments.size();
        }

        @Override
        public Object getItem(int position) {
            return payments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(CheckPayDetailActivity.this).inflate(R.layout.item_payinfo_detail, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            MyOrderDetailResult.Rows.SubOrders.Payments payment = this.payments.get(position);
            //支付类型
            if (payment.payType == 1) {
                holder.pay_way.setText("支付宝支付");
            } else if (payment.payType == 2) {
                holder.pay_way.setText("银联支付");
            }
            //支付金额
            if (StringUtil.checkStr(payment.price)) {
                holder.pay_price.setText(payment.price);
            }
            //支付时间
            if (StringUtil.checkStr(payment.dateCreated)) {
                holder.pay_time.setText(DateFormatUtils.convertTime(payment.dateCreated));
            }
            //支付结果
            if (payment.payStatus == 1) {
                holder.pay_time.setVisibility(View.GONE);
            } else if (payment.payStatus == 2) {
                holder.pay_result.setText("支付成功");
            }
            //第几次付款
            holder.pay_times.setText("第" + payment.slice + "次");
            return convertView;
        }

        class ViewHolder {
            private TextView pay_times, pay_price, pay_time, pay_way, pay_result;

            ViewHolder(View convertView) {
                pay_times = (TextView) convertView.findViewById(R.id.item_payInfo_times);
                pay_price = (TextView) convertView.findViewById(R.id.pay_price);
                pay_time = (TextView) convertView.findViewById(R.id.order_pay_time);
                pay_way = (TextView) convertView.findViewById(R.id.order_pay_type);
                pay_result = (TextView) convertView.findViewById(R.id.item_payInfo_state);

            }

        }
    }
}
