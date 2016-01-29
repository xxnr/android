package com.ksfc.newfarmer.activitys;

import java.io.Serializable;
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
import com.ksfc.newfarmer.widget.RecyclerImageView;
import com.ksfc.newfarmer.widget.UnSwipeListView;
import com.ksfc.newfarmer.widget.WidgetUtil;
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


    private TextView name_phone_tv, order_detail_address_tv, order_tv, pay_state_tv,
            total_price_tv;
    private ListView order_shangpin_list;
    private RelativeLayout go_to_pay_rel;
    private String orderId; //订单号
    private int payType = 1;//支付类型
    private int order_type = 1; //订单类型
    private UnSwipeListView pay_info_listView;//支付信息列表


    @Override
    public int getLayout() {
        return R.layout.my_order_detail;
    }


    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        if (!StringUtil.empty(getIntent().getStringExtra("orderId"))) {
            orderId = getIntent().getStringExtra("orderId");
        }
        if (TextUtils.isEmpty(orderId)) {
            return;
        }
        setTitle("订单详情");
        initView();
        requestData(orderId);
    }

    private void initView() {
        go_to_pay_rel = (RelativeLayout) findViewById(R.id.go_to_pay_rel);
        //头部信息 订单号： 交易状态 送货人 地址
        View head_layout = LayoutInflater.from(MyOrderDetailActivity.this).inflate(R.layout.my_orderdetail_head_layout, null);
        name_phone_tv = (TextView) head_layout.findViewById(R.id.order_detail_name_tv);
        order_detail_address_tv = (TextView) head_layout.findViewById(R.id.order_detail_address_tv);
        order_tv = (TextView) head_layout.findViewById(R.id.my_order_detail_id);
        pay_state_tv = (TextView) head_layout.findViewById(R.id.pay_state);

        pay_info_listView = (UnSwipeListView) head_layout.findViewById(R.id.pay_info_listView);
        //尾部信息 去付款
        View foot_layout = LayoutInflater.from(MyOrderDetailActivity.this).inflate(R.layout.my_orderdetail_foot_layout, null);
        total_price_tv = (TextView) foot_layout.findViewById(R.id.my_order_detail_price);

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
            intent.putExtra("orderId", orderId);
            intent.putExtra("payType", payType);
            startActivity(intent);
        }
    }


    @Override
    public void onResponsed(Request req) {
        if (ApiType.GET_ORDER_DETAILS == req.getApi()) {
            MyOrderDetailResult data = (MyOrderDetailResult) req.getData();
            if (data.getStatus().equals("1000")) {
                MyOrderDetailResult.Datas datas = data.datas;
                if (datas != null && datas.rows != null) {
                    MyOrderDetailResult.Rows rows = datas.rows;
                    //联系人 及电话
                    name_phone_tv.setText(rows.recipientName + " " + rows.recipientPhone);
                    //联系人地址
                    order_detail_address_tv.setText(rows.address);
                    //订单号
                    order_tv.setText("订单号：" + rows.id);
                    //支付类型
                    payType = rows.payType;
                    //合计 与 去支付
                    if (rows.order != null) {
                        total_price_tv.setText("¥" + rows.order.totalPrice);
                        if (rows.order.orderStatus != null) {
                            pay_state_tv.setText(rows.order.orderStatus.value);
                            order_type = rows.order.orderStatus.type;
                            if (rows.order.orderStatus.type == 1 || rows.order.orderStatus.type == 2) {
                                //可支付
                                go_to_pay_rel.setVisibility(View.VISIBLE);
                                setViewClick(R.id.go_to_pay);
                            } else {
                                //不支付
                                go_to_pay_rel.setVisibility(View.GONE);
                            }
                        }
                    }
                    //支付信息列表
                    if (rows.subOrders != null && !rows.subOrders.isEmpty()) {
                        PayInfoAdapter payInfoAdapter = new PayInfoAdapter(rows.subOrders);
                        pay_info_listView.setAdapter(payInfoAdapter);
                    }

                    //子商品列表
                    if (rows.SKUList != null && !rows.SKUList.isEmpty()) {
                        CarAdapter carAdapter = new CarAdapter(true, rows.SKUList);
                        order_shangpin_list.setAdapter(carAdapter);
                    } else {
                        CarAdapter carAdapter = new CarAdapter(rows.orderGoodsList, false);
                        order_shangpin_list.setAdapter(carAdapter);
                    }
                }
            }
        }
    }

    //支付信息列表

    public class PayInfoAdapter extends BaseAdapter {
        private List<MyOrderDetailResult.Rows.SubOrders> subOrders;

        public PayInfoAdapter(List<MyOrderDetailResult.Rows.SubOrders> subOrders) {
            this.subOrders = subOrders;
        }

        @Override
        public int getCount() {
            return subOrders.size();
        }

        @Override
        public Object getItem(int position) {
            return subOrders.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(MyOrderDetailActivity.this).inflate(R.layout.item_payinfo_orderdetail, parent, false);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            final MyOrderDetailResult.Rows.SubOrders subOrder = subOrders.get(position);
            if (subOrder != null) {
                //支付阶段
                if (subOrder.type.equals("deposit")) {
                    holder.item_payInfo_step.setText("阶段一：订金");
                } else if (subOrder.type.equals("balance")) {
                    holder.item_payInfo_step.setText("阶段二：尾款");
                } else if (subOrder.type.equals("full")) {
                    holder.item_payInfo_step.setText("订单总额");
                }
                //支付状态
                if (order_type != 0) { //如果交易状态 是已关闭 下方设置已关闭
                    if (subOrder.type.equals("balance")) {//阶段二的子订单
                        try {
                            if (subOrders.get(position - 1).payStatus.equals("2")) {//如果阶段一的子订单 已付款

                                if (subOrder.payStatus.equals("1")) {
                                    holder.item_payInfo_type.setTextColor(getResources().getColor(R.color.orange));
                                    holder.item_payInfo_type.setText("待付款");
                                } else if (subOrder.payStatus.equals("2")) {
                                    holder.item_payInfo_type.setTextColor(getResources().getColor(R.color.black_goods_titile));
                                    holder.item_payInfo_type.setText("已付款");
                                } else if (subOrder.payStatus.equals("3")) {
                                    holder.item_payInfo_type.setTextColor(getResources().getColor(R.color.orange));
                                    holder.item_payInfo_type.setText("部分付款");
                                }

                            } else {
                                holder.item_payInfo_type.setTextColor(getResources().getColor(R.color.black_goods_titile));
                                holder.item_payInfo_type.setText("未开始");
                            }
                        } catch (Exception e) {//如果下标越界 就 设置 默认设置

                            if (subOrder.payStatus.equals("1")) {
                                holder.item_payInfo_type.setTextColor(getResources().getColor(R.color.orange));
                                holder.item_payInfo_type.setText("待付款");
                            } else if (subOrder.payStatus.equals("2")) {
                                holder.item_payInfo_type.setTextColor(getResources().getColor(R.color.black_goods_titile));
                                holder.item_payInfo_type.setText("已付款");
                            } else if (subOrder.payStatus.equals("3")) {
                                holder.item_payInfo_type.setTextColor(getResources().getColor(R.color.orange));
                                holder.item_payInfo_type.setText("部分付款");
                            }
                        }


                    } else {
                        if (subOrder.payStatus.equals("1")) {
                            holder.item_payInfo_type.setTextColor(getResources().getColor(R.color.orange));
                            holder.item_payInfo_type.setText("待付款");
                        } else if (subOrder.payStatus.equals("2")) {
                            holder.item_payInfo_type.setTextColor(getResources().getColor(R.color.black_goods_titile));
                            holder.item_payInfo_type.setText("已付款");
                        } else if (subOrder.payStatus.equals("3")) {
                            holder.item_payInfo_type.setTextColor(getResources().getColor(R.color.orange));
                            holder.item_payInfo_type.setText("部分付款");
                        }
                    }


                } else {
                    holder.item_payInfo_type.setTextColor(getResources().getColor(R.color.black_goods_titile));
                    holder.item_payInfo_type.setText("已关闭");
                }

                //应支付金额
                holder.to_pay_price.setText("¥" + subOrder.price);
                //已支付金额
                holder.order_yet_price.setText("¥" + subOrder.paidPrice);
                //支付类型
                if (StringUtil.checkStr(subOrder.payType)) {
                    holder.order_pay_type_ll.setVisibility(View.VISIBLE);
                    if (subOrder.payType.equals("1")) {
                        holder.order_pay_type.setText("支付宝支付");
                    } else if (subOrder.payType.equals("2")) {
                        holder.order_pay_type.setText("银联支付");
                    }
                } else {
                    holder.order_pay_type_ll.setVisibility(View.GONE);
                }
                //查看详情
                if (subOrder.payments != null && !subOrder.payments.isEmpty()) {
                    holder.to_get_pay_detail.setVisibility(View.VISIBLE);
                    holder.to_get_pay_detail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MyOrderDetailActivity.this, CheckPayDetailActivity.class);
                            intent.putExtra("payInfo", (Serializable) subOrder);
                            intent.putExtra("orderId", orderId);
                            startActivity(intent);
                        }
                    });
                } else {
                    holder.to_get_pay_detail.setVisibility(View.GONE);
                }


            }

            return convertView;
        }

        class ViewHolder {
            private TextView item_payInfo_step,//支付阶段
                    item_payInfo_type,//支付状态
                    to_pay_price,//应支付金额
                    order_yet_price,//已支付金额
                    order_pay_type,//支付类型
                    to_get_pay_detail;//查看详情
            private LinearLayout order_pay_type_ll;//支付类型所在的布局

            ViewHolder(View convertView) {
                order_pay_type_ll = (LinearLayout) convertView.findViewById(R.id.order_pay_type_ll);
                item_payInfo_step = (TextView) convertView.findViewById(R.id.item_payInfo_step);
                item_payInfo_type = (TextView) convertView.findViewById(R.id.item_payInfo_type);
                to_pay_price = (TextView) convertView.findViewById(R.id.to_pay_price);
                order_yet_price = (TextView) convertView.findViewById(R.id.order_yet_price);
                order_pay_type = (TextView) convertView.findViewById(R.id.order_pay_type);
                to_get_pay_detail = (TextView) convertView.findViewById(R.id.to_get_pay_detail);
            }

        }


    }


    //商品列表
    public class CarAdapter extends BaseAdapter {

        private List<MyOrderDetailResult.OrderGood> goodsList;
        private List<MyOrderDetailResult.Rows.SKUS> SKUsList;
        private boolean flag;

        public CarAdapter(List<MyOrderDetailResult.OrderGood> goodsList, boolean flag) {
            this.goodsList = goodsList;
            this.flag = flag;
        }

        public CarAdapter(boolean flag, List<MyOrderDetailResult.Rows.SKUS> SKUsList) {
            this.flag = flag;
            this.SKUsList = SKUsList;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if (flag) {
                return SKUsList.size() > 0 ? SKUsList.size() : 0;
            } else {
                return goodsList.size() > 0 ? goodsList.size() : 0;
            }
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            if (flag) {
                return SKUsList.get(position);
            } else {
                return goodsList.get(position);
            }

        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        class ViewHolder {
            private LinearLayout goods_car_bar;
            private RecyclerImageView ordering_item_img;
            private TextView ordering_now_pri, ordering_item_name, goods_car_deposit,
                    goods_car_weikuan, ordering_item_geshu, ordering_item_attr, ordering_item_orderType;
            private ListView additions_listView;


            public ViewHolder(View convertView) {
                goods_car_bar = (LinearLayout) convertView.findViewById(R.id.goods_car_item_bar);
                ordering_item_img = (RecyclerImageView) convertView//商品图
                        .findViewById(R.id.ordering_item_img);
                ordering_item_geshu = (TextView) convertView//商品个数
                        .findViewById(R.id.ordering_item_geshu);
                ordering_now_pri = (TextView) convertView//商品价格
                        .findViewById(R.id.ordering_now_pri);
                ordering_item_name = (TextView) convertView//商品名
                        .findViewById(R.id.ordering_item_name);
                ordering_item_attr = (TextView) convertView  //商品sku属性
                        .findViewById(R.id.ordering_item_attr);
                ordering_item_orderType = (TextView) convertView//商品发货状态
                        .findViewById(R.id.ordering_item_orderType);
                goods_car_deposit = (TextView) convertView//汽车定金
                        .findViewById(R.id.goods_car_item_bar_deposit);
                goods_car_weikuan = (TextView) convertView//汽车尾款
                        .findViewById(R.id.goods_car_item_bar_weikuan);
                additions_listView = (ListView) convertView//附加选项
                        .findViewById(R.id.additions_listView);
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(MyOrderDetailActivity.this)
                        .inflate(R.layout.order_list_item_item, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();


            if (flag) {
                //商品图片
                if (StringUtil.checkStr(SKUsList.get(position).imgs)) {
                    ImageLoader.getInstance().displayImage(
                            MsgID.IP + SKUsList.get(position).imgs, holder.ordering_item_img);
                }
                //商品个数
                holder.ordering_item_geshu.setText("X " + SKUsList.get(position).count + "");
                //商品名
                if (StringUtil.checkStr(SKUsList.get(position).productName)) {
                    holder.ordering_item_name.setEms(10);
                    holder.ordering_item_name.setText(SKUsList.get(position).productName);
                }

                //商品发货状态
                if (StringUtil.checkStr(SKUsList.get(position).deliverStatus)) {
                    holder.ordering_item_orderType.setVisibility(View.VISIBLE);
                    if (SKUsList.get(position).deliverStatus.equals("1")) {
                        if (order_type == 0) {
                            holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.black_goods_titile));
                            holder.ordering_item_orderType.setText("待发货");
                        } else {
                            holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.orange));
                            holder.ordering_item_orderType.setText("待发货");
                        }
                    } else if (SKUsList.get(position).deliverStatus.equals("2")) {
                        holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.black_goods_titile));
                        holder.ordering_item_orderType.setText("已发货");
                    }
                }
                //附加选项的总价
                float car_additions_price = 0;
                if (SKUsList.get(position).additions != null && !SKUsList.get(position).additions.isEmpty()) {
                    for (int k = 0; k < SKUsList.get(position).additions.size(); k++) {
                        if (StringUtil.checkStr(SKUsList.get(position).additions.get(k).name)) {
                            car_additions_price += SKUsList.get(position).additions.get(k).price;
                        }
                    }
                }

                if (SKUsList.get(position).deposit == 0) {
                    holder.goods_car_bar.setVisibility(View.GONE);
                } else {
                    holder.goods_car_bar.setVisibility(View.VISIBLE);
                    String deposit = StringUtil.toTwoString(SKUsList
                            .get(position).deposit * SKUsList.get(position).count + "");
                    if (StringUtil.checkStr(deposit)) {
                        holder.goods_car_deposit.setText("¥" + deposit);
                    }
                    String weiKuan = StringUtil.toTwoString((SKUsList.get(position).price + car_additions_price - SKUsList
                            .get(position).deposit) * SKUsList.get(position).count + "");
                    if (StringUtil.checkStr(weiKuan)) {
                        holder.goods_car_weikuan.setText("¥" + weiKuan);
                    }
                }
                holder.ordering_now_pri.setText("¥" + StringUtil.toTwoString(SKUsList
                        .get(position).price + ""));

                //Sku属性
                StringBuilder stringBuilder = new StringBuilder();
                if (SKUsList.get(position).attributes != null && !SKUsList.get(position).attributes.isEmpty()) {
                    for (int k = 0; k < SKUsList.get(position).attributes.size(); k++) {
                        if (StringUtil.checkStr(SKUsList.get(position).attributes.get(k).name)
                                && StringUtil.checkStr(SKUsList.get(position).attributes.get(k).value)) {
                            stringBuilder.append(SKUsList.get(position).attributes.get(k).name + ":")
                                    .append(SKUsList.get(position).attributes.get(k).value + ";");
                        }
                    }
                    String car_attr = stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
                    if (StringUtil.checkStr(car_attr)) {
                        holder.ordering_item_attr.setEms(10);
                        holder.ordering_item_attr.setText(car_attr);
                    }
                }
                //附加选项
                if (SKUsList.get(position).additions != null && !SKUsList.get(position).additions.isEmpty()) {
                    holder.additions_listView.setVisibility(View.VISIBLE);
                    AdditionsAdapter adapter = new AdditionsAdapter(SKUsList.get(position).additions);
                    holder.additions_listView.setAdapter(adapter);
                    WidgetUtil.setListViewHeightBasedOnChildren(holder.additions_listView);
                } else {
                    holder.additions_listView.setVisibility(View.GONE);
                }


            } else {
                //商品图片
                if (StringUtil.checkStr(goodsList.get(position).imgs)) {
                    ImageLoader.getInstance().displayImage(
                            MsgID.IP + goodsList.get(position).imgs, holder.ordering_item_img);
                }
                //商品个数
                holder.ordering_item_geshu.setText("X " + goodsList.get(position).goodsCount + "");
                //商品名
                if (StringUtil.checkStr(goodsList.get(position).goodsName)) {
                    holder.ordering_item_name.setText(goodsList.get(position).goodsName);
                }
                //商品发货状态
                if (StringUtil.checkStr(goodsList.get(position).deliverStatus)) {
                    holder.ordering_item_orderType.setVisibility(View.VISIBLE);
                    if (goodsList.get(position).deliverStatus.equals("1")) {
                        if (order_type == 0) {
                            holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.black_goods_titile));
                            holder.ordering_item_orderType.setText("待发货");
                        } else {
                            holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.orange));
                            holder.ordering_item_orderType.setText("待发货");
                        }
                    } else if (goodsList.get(position).deliverStatus.equals("2")) {
                        holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.black_goods_titile));
                        holder.ordering_item_orderType.setText("已发货");
                    }
                }

                if (goodsList.get(position).deposit == 0) {
                    holder.goods_car_bar.setVisibility(View.GONE);
                } else {
                    holder.goods_car_bar.setVisibility(View.VISIBLE);
                    String deposit = StringUtil.toTwoString(goodsList
                            .get(position).deposit * goodsList.get(position).goodsCount + "");
                    if (StringUtil.checkStr(deposit)) {
                        holder.goods_car_deposit.setText("¥" + deposit);
                        String weiKuan = StringUtil.toTwoString((goodsList.get(position).unitPrice - goodsList
                                .get(position).deposit) * goodsList.get(position).goodsCount + "");
                        if (StringUtil.checkStr(weiKuan)) {
                            holder.goods_car_weikuan.setText("¥" + weiKuan);
                        }
                    }
                    String now_pri = StringUtil.toTwoString(goodsList
                            .get(position).unitPrice + "");
                    if (StringUtil.checkStr(now_pri)) {
                        holder.ordering_now_pri.setText("¥" + now_pri);
                    }
                }

            }
            return convertView;
        }
    }

    class AdditionsAdapter extends BaseAdapter {
        private List<MyOrderDetailResult.Rows.SKUS.Additions> list;

        public AdditionsAdapter(List<MyOrderDetailResult.Rows.SKUS.Additions> list) {
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
                convertView = LayoutInflater.from(MyOrderDetailActivity.this).inflate(R.layout.item_for_additions_layout, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (StringUtil.checkStr(list.get(position).name)) {
                holder.item_additions_name.setText(list.get(position).name);
                holder.item_additions_price.setText("¥" + StringUtil.toTwoString(list.get(position).price + ""));
            }
            return convertView;
        }

        class ViewHolder {
            private TextView item_additions_name, item_additions_price;

            ViewHolder(View convertView) {
                this.item_additions_name = ((TextView) convertView.findViewById(R.id.item_additions_name));
                this.item_additions_price = ((TextView) convertView.findViewById(R.id.item_additions_price));
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestData(orderId);
    }
}