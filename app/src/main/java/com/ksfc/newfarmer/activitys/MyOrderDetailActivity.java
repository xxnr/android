package com.ksfc.newfarmer.activitys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.common.OrderUtils;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.common.CommonAdapter;
import com.ksfc.newfarmer.common.CommonViewHolder;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.http.ApiType;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.http.RequestParams;
import com.ksfc.newfarmer.http.beans.LoginResult;
import com.ksfc.newfarmer.http.beans.MyOrderDetailResult;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.PopWindowUtils;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.RecyclerImageView;
import com.ksfc.newfarmer.widget.UnSwipeListView;
import com.ksfc.newfarmer.widget.WidgetUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.yangentao.util.msg.MsgCenter;

import rx.Subscriber;
import rx.functions.Action1;


public class MyOrderDetailActivity extends BaseActivity {


    private TextView name_phone_tv, order_detail_address_tv, order_tv, pay_state_tv,
            total_price_tv;
    private ListView order_shangpin_list;
    private RelativeLayout go_to_pay_rel;
    private String orderId; //订单号
    private UnSwipeListView pay_info_listView;//支付信息列表
    private TextView RSC_state_name;
    private TextView RSC_state_address;
    private TextView RSC_state_phone;
    private ImageView delivery_icon;
    private TextView delivery_text;
    private Button go_to_pay;
    private Button change_pay_type;

    private Map<String, Boolean> checkedMap = new HashMap<>();//用于存放popWindow中选中的确认收货商品
    private PopupWindow popupWindow;
    private TextView pop_sure;
    private ListView pop_listView;
    private RelativeLayout pop_bg;
    private LinearLayout state_address_ll;
    private LinearLayout address_shouhuo_ll;
    private TextView RSC_state_person_info;
    private MyOrderDetailResult.Datas datas;
    private LinearLayout integral_count_ll;
    private TextView integral_count_tv;


    @Override
    public int getLayout() {
        return R.layout.activity_my_order_detail;
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

        final boolean callByMyOrderListActivity = getIntent().getBooleanExtra("callByMyOrderListActivity", false);
        setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!callByMyOrderListActivity) {
                    Intent intent = new Intent(MyOrderDetailActivity.this,
                            MyOrderListActivity.class);
                    intent.putExtra("orderSelect", 0);
                    startActivity(intent);
                }
                finish();
            }
        });

    }

    private void initView() {
        go_to_pay_rel = (RelativeLayout) findViewById(R.id.go_to_pay_rel);
        go_to_pay = (Button) findViewById(R.id.go_to_pay);
        change_pay_type = (Button) findViewById(R.id.change_pay_type);
        pop_bg = (RelativeLayout) findViewById(R.id.pop_bg);

        //头部信息 订单号： 交易状态 送货人 地址
        View head_layout = LayoutInflater.from(MyOrderDetailActivity.this).inflate(R.layout.my_order_detail_head, null);
        name_phone_tv = (TextView) head_layout.findViewById(R.id.order_detail_name_tv);
        order_detail_address_tv = (TextView) head_layout.findViewById(R.id.order_detail_address_tv);
        order_tv = (TextView) head_layout.findViewById(R.id.my_order_detail_id);
        pay_state_tv = (TextView) head_layout.findViewById(R.id.pay_state);
        integral_count_ll = (LinearLayout) head_layout.findViewById(R.id.integral_count_ll);
        integral_count_tv = (TextView) head_layout.findViewById(R.id.integral_count_tv);
        integral_count_ll.setVisibility(View.INVISIBLE);

        state_address_ll = (LinearLayout) head_layout.findViewById(R.id.select_state_address_ll); //网点自提
        address_shouhuo_ll = (LinearLayout) head_layout.findViewById(R.id.address_shouhuo_ll); //配送到户
        state_address_ll.setVisibility(View.GONE);
        address_shouhuo_ll.setVisibility(View.GONE);

        RSC_state_name = (TextView) head_layout.findViewById(R.id.select_state_name);
        RSC_state_address = (TextView) head_layout.findViewById(R.id.select_state_address);
        RSC_state_phone = (TextView) head_layout.findViewById(R.id.select_state_phone);

        RSC_state_person_info = (TextView) head_layout.findViewById(R.id.select_state_person_info);

        delivery_icon = (ImageView) head_layout.findViewById(R.id.delivery_icon);
        delivery_text = (TextView) head_layout.findViewById(R.id.delivery_text);

        pay_info_listView = (UnSwipeListView) head_layout.findViewById(R.id.pay_info_listView);
        //尾部信息 去付款
        View foot_layout = LayoutInflater.from(MyOrderDetailActivity.this).inflate(R.layout.my_order_detail_foot, null);
        total_price_tv = (TextView) foot_layout.findViewById(R.id.my_order_detail_price);

        order_shangpin_list = (ListView) findViewById(R.id.order_shangpin_list);
        order_shangpin_list.addHeaderView(head_layout);
        order_shangpin_list.addFooterView(foot_layout);


    }

    // 请求网络，获取数据
    private void requestData(String orderId) {
        showProgressDialog();
        RequestParams params = new RequestParams();
        if (isLogin()) {
            params.put("userId", Store.User.queryMe().userid);
        }
        params.put("orderId", orderId);
        execApi(ApiType.GET_ORDER_DETAILS, params);
    }


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {

            case R.id.pop_close:
                if (null != popupWindow && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                break;
            //确认收货
            case R.id.save:
                if (checkedMap != null && !checkedMap.isEmpty()) {
                    List<String> list = new ArrayList<>();
                    for (String key : checkedMap.keySet()) {
                        if (checkedMap.get(key)) {
                            list.add(key);
                        }
                    }

                    if (StringUtil.checkStr(orderId)) {
                        showProgressDialog();
                        RequestParams params = new RequestParams();
                        Map<String, Object> map = new HashMap<>();
                        map.put("SKURefs", list);
                        map.put("orderId", orderId);
                        LoginResult.UserInfo userInfo = Store.User.queryMe();
                        if (userInfo != null) {
                            map.put("token", userInfo.token);
                        }
                        Gson gson = new Gson();
                        String toJson = gson.toJson(map);
                        params.put("JSON", toJson);
                        execApi(ApiType.SURE_GET_GOODS.setMethod(ApiType.RequestMethod.POSTJSON), params);
                    }
                }
                if (null != popupWindow && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                break;
            case R.id.to_order_state_rel:
                if (datas != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("order_state", datas);
                    IntentUtil.activityForward(this, MyOrderStateDetailActivity.class, bundle, false);
                }
                break;

        }
    }


    @Override
    public void onResponsed(Request req) {
        if (ApiType.GET_ORDER_DETAILS == req.getApi()) {
            MyOrderDetailResult data = (MyOrderDetailResult) req.getData();
            if (data.getStatus().equals("1000")) {

                datas = data.datas;
                if (datas != null && datas.rows != null) {
                    //配送方式
                    if (datas.rows.deliveryType != null) {

                        if (datas.rows.deliveryType.type == 1) {  //网点自提
                            delivery_icon.setVisibility(View.VISIBLE);
                            delivery_icon.setBackgroundResource(R.drawable.home_delivery_icon);
                        } else {
                            delivery_icon.setVisibility(View.VISIBLE);//其他的暂用 送货到家图标
                            delivery_icon.setBackgroundResource(R.drawable.state_delivery_icon);
                        }
                        if (StringUtil.checkStr(datas.rows.deliveryType.value)) {
                            delivery_text.setVisibility(View.VISIBLE);
                            delivery_text.setText(datas.rows.deliveryType.value);
                        }


                        //收货地址网点等信息

                        if (datas.rows.deliveryType.type == 1) { //展示网点信息
                            state_address_ll.setVisibility(View.VISIBLE);
                            //设置RSCInfo
                            if (datas.rows.RSCInfo != null) {
                                if (StringUtil.checkStr(datas.rows.RSCInfo.companyName)) {
                                    RSC_state_name.setText(datas.rows.RSCInfo.companyName);
                                }

                                if (StringUtil.checkStr(datas.rows.RSCInfo.RSCAddress)) {
                                    RSC_state_address.setText(datas.rows.RSCInfo.RSCAddress);
                                }

                                if (StringUtil.checkStr(datas.rows.RSCInfo.RSCPhone)) {
                                    RSC_state_phone.setText(datas.rows.RSCInfo.RSCPhone);
                                }
                            }
                            RSC_state_person_info.setText(datas.rows.recipientName + " " + datas.rows.recipientPhone);

                        } else {   //暂时送货到家地址信息
                            address_shouhuo_ll.setVisibility(View.VISIBLE);
                            //联系人 及电话
                            name_phone_tv.setText(datas.rows.recipientName + " " + datas.rows.recipientPhone);
                            //联系人地址
                            order_detail_address_tv.setText(datas.rows.address);
                        }
                    }

                    final MyOrderDetailResult.Rows rows = datas.rows;
                    //订单号
                    order_tv.setText("订单号：" + rows.id);

                    //合计 与 去支付
                    if (rows.order != null) {
                        total_price_tv.setText("¥" + rows.order.totalPrice);
                        if (rows.order.orderStatus != null) {
                            //订单状态
                            if (StringUtil.checkStr(rows.order.orderStatus.value)) {
                                pay_state_tv.setText(rows.order.orderStatus.value);
                            }
                            integral_count_ll.setVisibility(View.INVISIBLE);

                            if (rows.order.orderStatus.type == 6 && rows.order.orderStatus.value.equals("已完成")) {
                                if (rows.isRewardPoint && rows.rewardPoints > 0) {
                                    integral_count_ll.setVisibility(View.VISIBLE);
                                    integral_count_tv.setText(String.valueOf(rows.rewardPoints));
                                }
                            }

                            //不支付
                            go_to_pay_rel.setVisibility(View.GONE);
                            change_pay_type.setVisibility(View.GONE);

                            switch (rows.order.orderStatus.type) {
                                //如果是待付款1的订单或者是部分付款的2的订单，点击可以去支付
                                case 1:
                                case 2:
                                    go_to_pay_rel.setVisibility(View.VISIBLE);
                                    go_to_pay.setText("去付款");
                                    go_to_pay.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (StringUtil.checkStr(orderId)) {
                                                Intent intent = new Intent(MyOrderDetailActivity.this,
                                                        PaywayActivity.class);
                                                intent.putExtra("orderId", orderId);
                                                intent.putExtra("payType", rows.payType);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                                    break;
                                //如果是付款待审核的订单，点击可以去更改支付方式 查看付款信息
                                case 7:
                                    go_to_pay_rel.setVisibility(View.VISIBLE);
                                    change_pay_type.setVisibility(View.VISIBLE);
                                    go_to_pay.setText("查看付款信息");
                                    RxView.clicks(go_to_pay).throttleFirst(1, TimeUnit.SECONDS)
                                            .subscribe(new Action1<Void>() {
                                                @Override
                                                public void call(Void aVoid) {
                                                    Subscriber<Integer> subscriberOrderIsChecked = new Subscriber<Integer>() {
                                                        @Override
                                                        public void onCompleted() {
                                                        }

                                                        @Override
                                                        public void onError(Throwable e) {
                                                            e.printStackTrace();
                                                        }

                                                        @Override
                                                        public void onNext(Integer orderStatusType) {
                                                            if (orderStatusType == 7) {
                                                                Bundle bundle = new Bundle();
                                                                if (rows.RSCInfo != null) {
                                                                    bundle.putString("companyName", rows.RSCInfo.companyName);
                                                                    bundle.putString("RSCPhone", rows.RSCInfo.RSCPhone);
                                                                    bundle.putString("RSCAddress", rows.RSCInfo.RSCAddress);
                                                                }
                                                                bundle.putString("orderId", orderId);
                                                                if (rows.payment != null) {
                                                                    bundle.putString("payPrice", rows.payment.price);
                                                                }
                                                                IntentUtil.activityForward(MyOrderDetailActivity.this, OfflinePayActivity.class, bundle, false);
                                                            } else {
                                                                requestData(orderId);
                                                                MsgCenter.fireNull(MsgID.order_Change);
                                                            }
                                                        }
                                                    };
                                                    OrderUtils.isChecked(subscriberOrderIsChecked, orderId);
                                                }
                                            });
                                    //更改支付方式
                                    change_pay_type.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (StringUtil.checkStr(orderId)) {
                                                Intent intent = new Intent(MyOrderDetailActivity.this,
                                                        PaywayActivity.class);
                                                intent.putExtra("orderId", orderId);
                                                intent.putExtra("payType", rows.payType);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                                    break;
                                //如果是配送中的订单，用户可以确认收货
                                case 4:
                                    boolean isSure = false;

                                    final List<MyOrderDetailResult.Rows.SKUS> skusList = new ArrayList<>();
                                    List<MyOrderDetailResult.Rows.SKUS> skUsList1 = rows.SKUList;
                                    if (skUsList1 != null) {
                                        for (int i = 0; i < skUsList1.size(); i++) {
                                            MyOrderDetailResult.Rows.SKUS skus = skUsList1.get(i);
                                            if (skus != null && skus.deliverStatus.equals("2")) {
                                                skusList.add(skus);
                                                isSure = true;
                                            }
                                        }
                                    }

                                    if (isSure) {
                                        go_to_pay_rel.setVisibility(View.VISIBLE);
                                        go_to_pay.setText("确认收货");
                                        go_to_pay.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showPopUp(v, skusList);
                                            }
                                        });
                                    }

                                    break;
                                //如果是待自提的订单，且有带自提的商品   用户可以去自提
                                case 5:
                                    boolean flag = false;
                                    if (rows.SKUList != null) {
                                        for (int i = 0; i < rows.SKUList.size(); i++) {
                                            MyOrderDetailResult.Rows.SKUS skus = rows.SKUList.get(i);
                                            if (skus != null && skus.deliverStatus.equals("4")) {
                                                flag = true;
                                            }
                                        }
                                    }
                                    if (flag) {
                                        go_to_pay_rel.setVisibility(View.VISIBLE);
                                        go_to_pay.setText("去自提");
                                        go_to_pay.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(MyOrderDetailActivity.this,
                                                        PickUpStateActivity.class);
                                                intent.putExtra("orderId", orderId);
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }


                    //支付信息列表
                    if (rows.subOrders != null && !rows.subOrders.isEmpty()) {
                        PayInfoAdapter payInfoAdapter = new PayInfoAdapter(this, rows.subOrders, rows.orderType);
                        pay_info_listView.setAdapter(payInfoAdapter);
                    }

                    //子商品列表
                    if (rows.SKUList != null && !rows.SKUList.isEmpty()) {
                        CarAdapter carAdapter = new CarAdapter(true, rows.SKUList);
                        order_shangpin_list.setAdapter(carAdapter);
                    } else if (rows.orderGoodsList != null) {
                        CarAdapter carAdapter = new CarAdapter(rows.orderGoodsList, false);
                        order_shangpin_list.setAdapter(carAdapter);
                    } else {
                        order_shangpin_list.setAdapter(null);
                    }
                    //点击查看订单状态详情
                    setViewClick(R.id.to_order_state_rel);
                }
            }
        } else if (req.getApi() == ApiType.SURE_GET_GOODS) {
            if (req.getData().getStatus().equals("1000")) {
                showCustomToast("收货成功", R.drawable.toast_success_icon);
                requestData(orderId);
                MsgCenter.fireNull(MsgID.order_Change, "SURE_GET_GOODS");
            }


        }
    }

    //支付信息列表

    public class PayInfoAdapter extends CommonAdapter<MyOrderDetailResult.Rows.SubOrders> {
        private List<MyOrderDetailResult.Rows.SubOrders> data;
        private int orderType;

        public PayInfoAdapter(Context context, List<MyOrderDetailResult.Rows.SubOrders> data, int orderType) {
            super(context, data, R.layout.item_payinfo_order_detail);
            this.data = data;
            this.orderType = orderType;
        }

        @Override
        public void convert(CommonViewHolder holder, final MyOrderDetailResult.Rows.SubOrders subOrder) {
            if (subOrder != null) {

                //支付阶段
                TextView item_payInfo_step = ((TextView) holder.getView(R.id.item_payInfo_step));
                switch (subOrder.type) {
                    case "deposit":
                        item_payInfo_step.setText("阶段一：订金");
                        break;
                    case "balance":
                        item_payInfo_step.setText("阶段二：尾款");
                        break;
                    case "full":
                        item_payInfo_step.setText("订单总额");
                        break;
                }

                //支付状态
                TextView item_payInfo_type = (TextView) holder.getView(R.id.item_payInfo_type);
                if (orderType != 0) { //如果交易状态 是已关闭 下方设置已关闭
                    if (subOrder.type.equals("balance")) {//阶段二的子订单
                        try {
                            if (data.get(holder.getPosition() - 1).payStatus.equals("2")) {//如果阶段一的子订单 已付款

                                switch (subOrder.payStatus) {
                                    case "1":
                                        item_payInfo_type.setTextColor(getResources().getColor(R.color.orange));
                                        item_payInfo_type.setText("待付款");
                                        break;
                                    case "2":
                                        item_payInfo_type.setTextColor(getResources().getColor(R.color.black_goods_titile));
                                        item_payInfo_type.setText("已付款");
                                        break;
                                    case "3":
                                        item_payInfo_type.setTextColor(getResources().getColor(R.color.orange));
                                        item_payInfo_type.setText("部分付款");
                                        break;
                                }

                            } else {
                                item_payInfo_type.setTextColor(getResources().getColor(R.color.black_goods_titile));
                                item_payInfo_type.setText("未开始");
                            }
                        } catch (Exception e) {//如果下标越界 就 设置 默认设置

                            switch (subOrder.payStatus) {
                                case "1":
                                    item_payInfo_type.setTextColor(getResources().getColor(R.color.orange));
                                    item_payInfo_type.setText("待付款");
                                    break;
                                case "2":
                                    item_payInfo_type.setTextColor(getResources().getColor(R.color.black_goods_titile));
                                    item_payInfo_type.setText("已付款");
                                    break;
                                case "3":
                                    item_payInfo_type.setTextColor(getResources().getColor(R.color.orange));
                                    item_payInfo_type.setText("部分付款");
                                    break;
                            }
                        }

                    } else {
                        switch (subOrder.payStatus) {
                            case "1":
                                item_payInfo_type.setTextColor(getResources().getColor(R.color.orange));
                                item_payInfo_type.setText("待付款");
                                break;
                            case "2":
                                item_payInfo_type.setTextColor(getResources().getColor(R.color.black_goods_titile));
                                item_payInfo_type.setText("已付款");
                                break;
                            case "3":
                                item_payInfo_type.setTextColor(getResources().getColor(R.color.orange));
                                item_payInfo_type.setText("部分付款");
                                break;
                        }
                    }

                } else {
                    item_payInfo_type.setTextColor(getResources().getColor(R.color.black_goods_titile));
                    item_payInfo_type.setText("已关闭");
                }

                //应支付金额
                holder.setText(R.id.to_pay_price, "¥" + subOrder.price);
                //已支付金额
                holder.setText(R.id.order_yet_price, "¥" + subOrder.paidPrice);
                //支付类型
                if (StringUtil.checkStr(subOrder.payType)) {
                    switch (subOrder.payType) {
                        case "1":
                            holder.getView(R.id.order_pay_type_ll).setVisibility(View.VISIBLE);
                            holder.setText(R.id.order_pay_type, "支付宝支付");
                            break;
                        case "2":
                            holder.getView(R.id.order_pay_type_ll).setVisibility(View.VISIBLE);
                            holder.setText(R.id.order_pay_type, "银联支付");
                            break;
                        case "3":
                            holder.getView(R.id.order_pay_type_ll).setVisibility(View.VISIBLE);
                            holder.setText(R.id.order_pay_type, "现金");
                            break;
                        case "4":
                            holder.getView(R.id.order_pay_type_ll).setVisibility(View.VISIBLE);
                            holder.setText(R.id.order_pay_type, "线下POS机");
                            break;
                        case "5":
                            holder.getView(R.id.order_pay_type_ll).setVisibility(View.VISIBLE);
                            holder.setText(R.id.order_pay_type, "EPOS支付");
                            break;
                    }
                } else {
                    holder.getView(R.id.order_pay_type_ll).setVisibility(View.GONE);
                }
                //查看详情
                TextView to_get_pay_detail = (TextView) holder.getView(R.id.to_get_pay_detail);
                if (subOrder.payments != null && !subOrder.payments.isEmpty()) {
                    to_get_pay_detail.setVisibility(View.VISIBLE);
                    to_get_pay_detail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MyOrderDetailActivity.this, CheckPayDetailActivity.class);
                            intent.putExtra("payInfo", (Serializable) subOrder);
                            intent.putExtra("orderId", orderId);
                            startActivity(intent);
                        }
                    });
                } else {
                    to_get_pay_detail.setVisibility(View.GONE);
                }
            }
        }
    }


    //商品列表(方法特殊 暂时继承BaseAdapter)
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
            private UnSwipeListView additions_listView;


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
                additions_listView = (UnSwipeListView) convertView//附加选项
                        .findViewById(R.id.additions_listView);
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(MyOrderDetailActivity.this)
                        .inflate(R.layout.item_item_order_list, null);
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
                        holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.orange));
                        holder.ordering_item_orderType.setText("未发货");
                    } else if (SKUsList.get(position).deliverStatus.equals("2")) {
                        holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.orange));
                        holder.ordering_item_orderType.setText("配送中");
                    } else if (SKUsList.get(position).deliverStatus.equals("4")) {
                        holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.orange));
                        holder.ordering_item_orderType.setText("已到服务站");
                    } else if (SKUsList.get(position).deliverStatus.equals("5")) {
                        holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.orange));
                        holder.ordering_item_orderType.setText("已收货");
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
                            stringBuilder.append(SKUsList.get(position).attributes.get(k).name).append(":").append(SKUsList.get(position).attributes.get(k).value).append(";");
                        }
                    }
                    String car_attr = stringBuilder.substring(0, stringBuilder.length() - 1);
                    if (StringUtil.checkStr(car_attr)) {
                        holder.ordering_item_attr.setText(car_attr);
                    }
                }
                //附加选项
                if (SKUsList.get(position).additions != null && !SKUsList.get(position).additions.isEmpty()) {
                    holder.additions_listView.setVisibility(View.VISIBLE);
                    AdditionsAdapter adapter = new AdditionsAdapter(MyOrderDetailActivity.this, SKUsList.get(position).additions);
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
                    holder.ordering_item_name.setEms(10);
                    holder.ordering_item_name.setText(goodsList.get(position).goodsName);
                }
                //商品发货状态
                if (StringUtil.checkStr(goodsList.get(position).deliverStatus)) {
                    holder.ordering_item_orderType.setVisibility(View.VISIBLE);
                    if (goodsList.get(position).deliverStatus.equals("1")) {
                        holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.orange));
                        holder.ordering_item_orderType.setText("未发货");
                    } else if (goodsList.get(position).deliverStatus.equals("2")) {
                        holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.orange));
                        holder.ordering_item_orderType.setText("配送中");
                    } else if (goodsList.get(position).deliverStatus.equals("4")) {
                        holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.orange));
                        holder.ordering_item_orderType.setText("已到服务站");
                    } else if (goodsList.get(position).deliverStatus.equals("5")) {
                        holder.ordering_item_orderType.setTextColor(getResources().getColor(R.color.orange));
                        holder.ordering_item_orderType.setText("已收货");
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


    class AdditionsAdapter extends CommonAdapter<MyOrderDetailResult.Rows.SKUS.Additions> {


        public AdditionsAdapter(Context context, List<MyOrderDetailResult.Rows.SKUS.Additions> data) {
            super(context, data, R.layout.item_for_additions);
        }

        @Override
        public void convert(CommonViewHolder holder, MyOrderDetailResult.Rows.SKUS.Additions additions) {
            if (additions != null) {
                if (StringUtil.checkStr(additions.name)) {
                    holder.setText(R.id.item_additions_name, additions.name);
                    holder.setText(R.id.item_additions_price, "¥" + StringUtil.toTwoString(additions.price + ""));
                }
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestData(orderId);
    }


    /**
     * 创建PopupWindow
     */
    private void initPopuptWindow() {
        // TODO Auto-generated method stub
        // 获取自定义布局文件activity_popupwindow_left.xml的视图
        @SuppressLint("InflateParams") View popupWindow_view = getLayoutInflater().inflate(
                R.layout.pop_layout_sureorder_dialog, null, false);

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popupWindow = new PopupWindow(popupWindow_view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(R.style.popWindow_anim_style);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                PopWindowUtils.setBackgroundBlack(pop_bg, 1);
            }
        });

        //初始化组件

        ImageView pop_close = (ImageView) popupWindow_view.findViewById(R.id.pop_close);
        pop_close.setOnClickListener(this);
        pop_sure = (TextView) popupWindow_view.findViewById(R.id.save);
        pop_sure.setOnClickListener(this);
        pop_sure.setEnabled(false);
        pop_listView = (ListView) popupWindow_view.findViewById(R.id.pop_listView);


    }

    //显示popWindow
    private void showPopUp(View parent, List<MyOrderDetailResult.Rows.SKUS> skusList) {
        if (null != popupWindow) {
            popupWindow.dismiss();
        } else {
            initPopuptWindow();
        }

        //初始化数据
        PopSkusAdapter popSkusAdapter = new PopSkusAdapter(MyOrderDetailActivity.this, skusList);
        pop_listView.setAdapter(popSkusAdapter);

        //设置背景及展示
        PopWindowUtils.setBackgroundBlack(pop_bg, 0);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }


    class PopSkusAdapter extends CommonAdapter<MyOrderDetailResult.Rows.SKUS> {
        private List<MyOrderDetailResult.Rows.SKUS> list;


        public PopSkusAdapter(Context context, List<MyOrderDetailResult.Rows.SKUS> data) {
            super(context, data, R.layout.item_pop_sure_order);
            this.list = data;
        }

        @Override
        public void convert(CommonViewHolder holder, final MyOrderDetailResult.Rows.SKUS skus) {

            if (skus != null) {

                //商品个数
                holder.setText(R.id.sku_count, "X " + skus.count + "");
                //商品名
                if (StringUtil.checkStr(skus.productName)) {
                    holder.setText(R.id.sku_name, skus.productName);
                } else {
                    holder.setText(R.id.sku_name, "");
                }
                //Sku属性
                StringBuilder stringBuilder = new StringBuilder();
                if (skus.attributes != null && !skus.attributes.isEmpty()) {
                    for (int k = 0; k < skus.attributes.size(); k++) {
                        if (StringUtil.checkStr(skus.attributes.get(k).name)
                                && StringUtil.checkStr(skus.attributes.get(k).value)) {
                            stringBuilder.append(skus.attributes.get(k).name).append(":").append(skus.attributes.get(k).value).append(";");
                        }
                    }
                    String car_attr = stringBuilder.substring(0, stringBuilder.length() - 1);
                    if (StringUtil.checkStr(car_attr)) {
                        holder.setText(R.id.sku_attr, car_attr);
                    } else {
                        holder.setText(R.id.sku_attr, "");
                    }
                } else {
                    holder.setText(R.id.sku_attr, "");
                }

                //附加选项
                TextView sku_addiction = (TextView) holder.getView(R.id.sku_addiction);

                StringBuilder stringAdditions = new StringBuilder();
                if (skus.additions != null && !skus.additions.isEmpty()) {
                    stringAdditions.append("附加项目:");
                    for (int k = 0; k < skus.additions.size(); k++) {
                        if (StringUtil.checkStr(skus.additions.get(k).name)) {
                            stringAdditions.append(skus.additions.get(k).name).append(";");
                        }
                    }
                    String car_additions = stringAdditions.substring(0, stringAdditions.length() - 1);
                    if (StringUtil.checkStr(car_additions)) {
                        sku_addiction.setVisibility(View.VISIBLE);
                        sku_addiction.setText(car_additions);
                    } else {
                        sku_addiction.setText("");
                    }
                } else {
                    sku_addiction.setVisibility(View.GONE);
                }

                //CheckBox
                final CheckBox checkBox = (CheckBox) holder.getView(R.id.btn_surr_order_item);

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (checkBox.isChecked()) {
                            checkedMap.put(skus.ref,
                                    false);
                        } else {
                            checkedMap.put(skus.ref,
                                    true);
                        }
                        //刷新适配器中的选中状态
                        notifyDataSetChanged();
                        //刷新确定按钮的选中数量
                        int count = setCheckedGoodsCount(list);
                        if (count != 0) {
                            pop_sure.setText("确定(" + count + ")");
                            pop_sure.setEnabled(true);
                        } else {
                            pop_sure.setText("确定");
                            pop_sure.setEnabled(false);
                        }
                    }
                });
                Boolean res = checkedMap.get(skus.ref);
                if (res != null && res) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }


            }

        }
    }

    //确定选中按钮的数量
    public int setCheckedGoodsCount(List<MyOrderDetailResult.Rows.SKUS> list) {
        int count = 0;
        List<String> refList = new ArrayList<>();
        for (String key : checkedMap.keySet()) {
            if (checkedMap.get(key)) {
                refList.add(key);
            }
        }
        for (MyOrderDetailResult.Rows.SKUS skus : list) {

            if (refList.contains(skus.ref)) {
                count += skus.count;
            }

        }
        return count;
    }

}