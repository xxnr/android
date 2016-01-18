package com.ksfc.newfarmer.activitys;

import java.util.List;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.MyOrderDetailResult;
import com.ksfc.newfarmer.protocol.beans.WaitingPay;
import com.ksfc.newfarmer.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MyOrderDetailActivity extends BaseActivity {

    private String orderId = "";
    private String orderNo = "";
    private String total_price = "";
    private View head_layout;
    private TextView name_phone_tv, order_detail_address_tv, order_tv, pay_state_tv,
            total_price_tv;
    private ListView order_shangpin_list;
    private View foot_layout;
    private Button go_to_pay;
    private RelativeLayout go_to_pay_rel;
    private int PayType;
    private TextView pay_type_tv;

    @Override
    public int getLayout() {
        return R.layout.my_order_detail;
    }


    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        if (!StringUtil.empty(getIntent().getStringExtra("orderId"))) {
            orderId = getIntent().getStringExtra("orderId");
        }
        orderNo = getIntent().getStringExtra("orderNo");
        if (TextUtils.isEmpty(orderId)) {
            return;
        }
        setTitle("订单详情");
        initView();
        requestData(orderId);
    }

    private void initView() {
        go_to_pay = (Button) findViewById(R.id.go_to_pay);
        go_to_pay_rel = (RelativeLayout) findViewById(R.id.go_to_pay_rel);

        //头部信息 订单号： 交易状态 送货人 地址
        head_layout = LayoutInflater.from(MyOrderDetailActivity.this).inflate(R.layout.my_orderdetail_head_layout, null);
        name_phone_tv = (TextView) head_layout.findViewById(R.id.order_detail_name_tv);
        order_detail_address_tv = (TextView) head_layout.findViewById(R.id.order_detail_address_tv);
        order_tv = (TextView) head_layout.findViewById(R.id.my_order_detail_id);
        pay_state_tv = (TextView) head_layout.findViewById(R.id.pay_state);
        //尾部信息 去付款
        foot_layout = LayoutInflater.from(MyOrderDetailActivity.this).inflate(R.layout.my_orderdetail_foot_layout, null);
        total_price_tv = (TextView) foot_layout.findViewById(R.id.my_order_detail_price);
        pay_type_tv = (TextView) foot_layout.findViewById(R.id.pay_type_tv);

        order_shangpin_list = (ListView) findViewById(R.id.order_shangpin_list);
        order_shangpin_list.addHeaderView(head_layout);
        order_shangpin_list.addFooterView(foot_layout);
    }

    // 请求网络，获取数据
    private void requestData(String orderId) {
        showProgressDialog();
        RequestParams params = new RequestParams();
        params.put("userId", Store.User.queryMe().userid);
        params.put("orderId", orderId);
        execApi(ApiType.GET_ORDER_DETAILS, params);
    }


    @Override
    public void OnViewClick(View v) {
        if (v.getId() == R.id.go_to_pay) {
            Intent intent = new Intent(MyOrderDetailActivity.this,
                    PaywayActivity.class);
            WaitingPay.Orders order = new WaitingPay.Orders();
            order.orderId = orderId;
            order.orderNo = orderNo;
            order.deposit = total_price;
            order.payType = PayType;
            intent.putExtra("orderInfo", order);
            startActivity(intent);
        }
    }


    @Override
    public void onResponsed(Request req) {
        if (ApiType.GET_ORDER_DETAILS == req.getApi()) {
            MyOrderDetailResult data = (MyOrderDetailResult) req.getData();
            if (data.getStatus().equals("1000")) {
                MyOrderDetailResult.Datas datas = data.datas;
                if (datas != null) {
                    MyOrderDetailResult.Rows rows = datas.rows;
                    name_phone_tv.setText(rows.recipientName + " " + rows.recipientPhone);
                    order_detail_address_tv.setText(rows.address);
                    order_tv.setText("订单号：" + orderId);
                    PayType = rows.payType;
                    total_price = rows.deposit;
                    total_price_tv.setText("¥" + total_price);
                    String state = "";
                    go_to_pay_rel.setVisibility(View.GONE);
                    switch (rows.orderType) {
                        case 0:
                            state = "交易关闭";
                            break;
                        case 1:
                            state = "待付款";
                            go_to_pay.setOnClickListener(this);
                            go_to_pay_rel.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            state = "待发货";
                            break;
                        case 3:
                            state = "已发货";
                            break;
                        default:
                            state = "未知状态";
                    }

                    switch (rows.payType) {
                        case 1:
                            pay_type_tv.setText("支付宝付款");
                            break;
                        case 2:
                            pay_type_tv.setText("银联支付");
                            break;
                    }
                    pay_state_tv.setText(state);
                    setTitle(state);
                    List<MyOrderDetailResult.OrderGood> goodsList = rows.orderGoodsList;
                    CarAdapter carAdapter = new CarAdapter(goodsList);
                    order_shangpin_list.setAdapter(carAdapter);
                }
            }
        }
    }


    //商品列表
    public class CarAdapter extends BaseAdapter {

        private List<MyOrderDetailResult.OrderGood> goodsList;

        public CarAdapter(List<MyOrderDetailResult.OrderGood> goodsList) {
            this.goodsList = goodsList;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return goodsList.size() > 0 ? goodsList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return goodsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        class ViewHolder {
            private LinearLayout goods_car_bar;
            private ImageView ordering_item_img;
            private TextView ordering_now_pri, ordering_item_name, goods_car_deposit,
                    goods_car_weikuan, ordering_item_geshu;

            public ViewHolder(View convertView) {
                goods_car_bar = (LinearLayout) convertView.findViewById(R.id.goods_car_item_bar);
                ordering_item_img = (ImageView) convertView//商品图
                        .findViewById(R.id.ordering_item_img);
                ordering_item_geshu = (TextView) convertView//商品个数
                        .findViewById(R.id.ordering_item_geshu);
                ordering_now_pri = (TextView) convertView//商品价格
                        .findViewById(R.id.ordering_now_pri);
                ordering_item_name = (TextView) convertView//商品名
                        .findViewById(R.id.ordering_item_name);
                goods_car_deposit = (TextView) convertView//汽车定金
                        .findViewById(R.id.goods_car_item_bar_deposit);
                goods_car_weikuan = (TextView) convertView//汽车尾款
                        .findViewById(R.id.goods_car_item_bar_weikuan);
            }
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(MyOrderDetailActivity.this)
                        .inflate(R.layout.order_list_item_item, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            ImageLoader.getInstance().displayImage(
                    MsgID.IP + goodsList.get(position).imgs, holder.ordering_item_img);
            holder.ordering_item_geshu.setText("X " + goodsList.get(position).goodsCount + "");
            holder.ordering_item_name.setText(goodsList.get(position).goodsName);
            if (goodsList.get(position).deposit == 0) {
                holder.ordering_now_pri.setTextColor(getResources().getColor(R.color.orange_goods_price));
                holder.goods_car_bar.setVisibility(View.GONE);
            } else {
                holder.ordering_now_pri.setTextColor(getResources().getColor(R.color.black_goods_titile));
                holder.goods_car_bar.setVisibility(View.VISIBLE);
                holder.goods_car_deposit.setText("¥" + StringUtil.toTwoString(goodsList
                        .get(position).deposit + ""));
                holder.goods_car_weikuan.setText("¥" + StringUtil.toTwoString(goodsList.get(position).unitPrice - goodsList
                        .get(position).deposit + ""));
            }
            holder.ordering_now_pri.setText("¥" + StringUtil.toTwoString(goodsList
                    .get(position).unitPrice + ""));
            return convertView;
        }

    }


}
