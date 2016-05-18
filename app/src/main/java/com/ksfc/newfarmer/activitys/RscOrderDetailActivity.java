package com.ksfc.newfarmer.activitys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.order.OrderUtils;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.adapter.CommonAdapter;
import com.ksfc.newfarmer.adapter.CommonViewHolder;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.LoginResult;
import com.ksfc.newfarmer.protocol.beans.OfflinePayWayResult;
import com.ksfc.newfarmer.protocol.beans.RscOrderDetailResult;
import com.ksfc.newfarmer.utils.DateFormatUtils;
import com.ksfc.newfarmer.utils.PopWindowUtils;
import com.ksfc.newfarmer.utils.ShowHideUtils;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.KeyboardListenRelativeLayout;
import com.ksfc.newfarmer.widget.RecyclerImageView;
import com.ksfc.newfarmer.widget.UnSwipeGridView;
import com.ksfc.newfarmer.widget.UnSwipeListView;
import com.squareup.picasso.Picasso;

import net.yangentao.util.msg.MsgCenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RscOrderDetailActivity extends BaseActivity implements KeyboardListenRelativeLayout.IOnKeyboardStateChangedListener {


    private TextView name_phone_tv, order_detail_address_tv, order_tv, pay_state_tv, total_price_tv;
    private ListView order_shangpin_list;
    private RelativeLayout go_to_pay_rel;
    private String orderId; //订单号
    private int order_type = 1; //订单类型
    private UnSwipeListView pay_info_listView;//支付信息列表
    private ImageView delivery_icon;
    private TextView delivery_text;
    private Button go_to_pay;
    private Button change_pay_type;

    private Map<String, Boolean> checkedMap = new HashMap<>();//用于存放popWindow中选中的确认收货商品
    private PopupWindow popupWindow, popupWindowDelivery, popupWindowSelfDelivery;
    private ListView pop_listView;
    private RelativeLayout pop_bg;
    private LinearLayout state_address_ll;
    private LinearLayout address_shouhuo_ll;
    private TextView RSC_state_person_info;
    private TextView add_order_time;
    private TextView check_price;
    private TextView recipient_name;
    private UnSwipeGridView pay_way_gridView;
    private List<OfflinePayWayResult.OfflinePayTypeEntity> offlinePayType;
    private String offlinePaymentId;
    private String offlinePayPrice;
    private TextView pop_sureDelivery;
    private TextView pop_self_delivery_sure;
    private EditText self_delivery_code_et;
    private TextView pop_slef_delivery_order_title;
    private RelativeLayout pop_self_delivery_code_Rel;
    private ListView pop_self_delivery_listView;
    private boolean self_delivery_tag = false;
    private KeyboardListenRelativeLayout rootView;


    @Override
    public int getLayout() {
        return R.layout.rsc_order_detail;
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
        getPayWay();
    }

    private void initView() {
        go_to_pay_rel = (RelativeLayout) findViewById(R.id.go_to_pay_rel);
        go_to_pay = (Button) findViewById(R.id.go_to_pay);
        change_pay_type = (Button) findViewById(R.id.change_pay_type);
        pop_bg = (RelativeLayout) findViewById(R.id.pop_bg);

        rootView = (KeyboardListenRelativeLayout) findViewById(R.id.order_detail_rl);
        rootView.setOnKeyboardStateChangedListener(this);

        //头部信息 订单号： 交易状态 送货人 地址
        View head_layout = LayoutInflater.from(RscOrderDetailActivity.this).inflate(R.layout.rsc_orderdetail_head_layout, null);
        name_phone_tv = (TextView) head_layout.findViewById(R.id.order_detail_name_tv);
        order_detail_address_tv = (TextView) head_layout.findViewById(R.id.order_detail_address_tv);
        order_tv = (TextView) head_layout.findViewById(R.id.my_order_detail_id);
        pay_state_tv = (TextView) head_layout.findViewById(R.id.pay_state);
        add_order_time = (TextView) head_layout.findViewById(R.id.add_order_time);

        state_address_ll = (LinearLayout) head_layout.findViewById(R.id.select_state_address_ll); //网点自提
        address_shouhuo_ll = (LinearLayout) head_layout.findViewById(R.id.address_shouhuo_ll); //配送到户
        state_address_ll.setVisibility(View.GONE);
        address_shouhuo_ll.setVisibility(View.GONE);

        RSC_state_person_info = (TextView) head_layout.findViewById(R.id.select_state_person_info);

        delivery_icon = (ImageView) head_layout.findViewById(R.id.delivery_icon);
        delivery_text = (TextView) head_layout.findViewById(R.id.delivery_text);

        pay_info_listView = (UnSwipeListView) head_layout.findViewById(R.id.pay_info_listView);
        //尾部信息 去付款
        View foot_layout = LayoutInflater.from(RscOrderDetailActivity.this).inflate(R.layout.my_orderdetail_foot_layout, null);
        total_price_tv = (TextView) foot_layout.findViewById(R.id.my_order_detail_price);

        order_shangpin_list = (ListView) findViewById(R.id.order_shangpin_list);
        order_shangpin_list.addHeaderView(head_layout);
        order_shangpin_list.addFooterView(foot_layout);
    }

    // 请求网络，获取数据
    private void requestData(String orderId) {
        showProgressDialog();

        RequestParams params = new RequestParams();
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            params.put("userId", userInfo.userid);
        }
        if (StringUtil.checkStr(orderId)) {
            params.put("orderId", orderId);
        }
        execApi(ApiType.GET_RSC_ORDER_Detail.setMethod(ApiType.RequestMethod.GET), params);
    }


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {

            case R.id.pop_close:
                if (null != popupWindow && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                if (null != popupWindowSelfDelivery && popupWindowSelfDelivery.isShowing()) {
                    popupWindowSelfDelivery.dismiss();
                }
                if (null != popupWindowDelivery && popupWindowDelivery.isShowing()) {
                    popupWindowDelivery.dismiss();
                }

                break;
            //开始配送
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
                        execApi(ApiType.RSC_ORDER_DELIVERING.setMethod(ApiType.RequestMethod.POSTJSON), params);
                    }
                }
                if (null != popupWindowDelivery && popupWindowDelivery.isShowing()) {
                    popupWindowDelivery.dismiss();
                }
                break;

            //审核确认付款
            case R.id.pop_save:
                if (checkedMap != null && !checkedMap.isEmpty()) {
                    String offlinePayType = "3";
                    for (String key : checkedMap.keySet()) {
                        if (checkedMap.get(key)) {
                            offlinePayType = key;
                        }
                    }
                    RequestParams params = new RequestParams();
                    if (StringUtil.checkStr(offlinePaymentId) && StringUtil.checkStr(offlinePayPrice)) {
                        params.put("paymentId", offlinePaymentId);
                        params.put("price", offlinePayPrice);
                        params.put("offlinePayType", offlinePayType);

                        LoginResult.UserInfo userInfo = Store.User.queryMe();
                        if (userInfo != null) {
                            params.put("userId", userInfo.userid);
                        }
                        execApi(ApiType.CONFIRM_OFFLINE_PAY.setMethod(ApiType.RequestMethod.GET), params);
                    }
                }
                if (null != popupWindow && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                break;

            //开始自提
            case R.id.pop_save_self_delivery:

                if (self_delivery_tag) {

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
                            if (StringUtil.checkStr(self_delivery_code_et.getText().toString())) {
                                map.put("code", self_delivery_code_et.getText().toString().trim());
                            }
                            LoginResult.UserInfo userInfo = Store.User.queryMe();
                            if (userInfo != null) {
                                map.put("token", userInfo.token);
                            }
                            Gson gson = new Gson();
                            String toJson = gson.toJson(map);
                            params.put("JSON", toJson);
                            execApi(ApiType.RSC_ORDER_SELF_DELIVERY.setMethod(ApiType.RequestMethod.POSTJSON), params);
                        }
                    }

                } else {
                    ShowHideUtils.hideLeftFadeIn(pop_self_delivery_listView);
                    ShowHideUtils.showRightFadeOut(pop_self_delivery_code_Rel);
                    pop_self_delivery_listView.setVisibility(View.GONE);
                    pop_self_delivery_code_Rel.setVisibility(View.VISIBLE);
                    pop_slef_delivery_order_title.setText("客户自提-自提码");
                    self_delivery_tag = true;
                    pop_self_delivery_sure.setEnabled(false);
                    pop_self_delivery_sure.setText("确定");
                }

                break;

        }
    }


    @Override
    public void onResponsed(Request req) {
        if (ApiType.GET_RSC_ORDER_Detail == req.getApi()) {
            RscOrderDetailResult data = (RscOrderDetailResult) req.getData();
            if (data.getStatus().equals("1000")) {
                final RscOrderDetailResult.OrderEntity order = data.order;
                if (order != null) {
                    //配送方式
                    if (order.deliveryType != null) {

                        if (order.deliveryType.type == 1) {  //网点自提
                            delivery_icon.setVisibility(View.VISIBLE);
                            delivery_icon.setBackgroundResource(R.drawable.home_delivery_icon);
                        } else {
                            delivery_icon.setVisibility(View.VISIBLE);//其他的暂用 送货到家图标
                            delivery_icon.setBackgroundResource(R.drawable.state_delivery_icon);
                        }
                        if (StringUtil.checkStr(order.deliveryType.value)) {
                            delivery_text.setVisibility(View.VISIBLE);
                            delivery_text.setText(order.deliveryType.value);
                        }
                        //收货地址网点等信息
                        if (order.deliveryType.type == 1) { //展示网点信息
                            state_address_ll.setVisibility(View.VISIBLE);
                            //设置RSCInfo
                            RSC_state_person_info.setText(order.consigneeName + " " + order.consigneePhone);

                        } else {   //暂时送货到家地址信息
                            address_shouhuo_ll.setVisibility(View.VISIBLE);
                            //联系人 及电话
                            name_phone_tv.setText(order.consigneeName + " " + order.consigneePhone);
                            //联系人地址
                            order_detail_address_tv.setText(order.consigneeAddress);
                        }
                    }

                    //订单号
                    order_tv.setText("订单号：" + order.id);
                    //下单时间
                    String time = DateFormatUtils.convertTime(order.dateCreated);
                    if (StringUtil.checkStr(time)) {
                        add_order_time.setText(time);
                    }
                    //合计 与 去支付
                    total_price_tv.setText("¥" + order.totalPrice);
                    if (order.orderStatus != null) {
                        //订单状态
                        if (StringUtil.checkStr(order.orderStatus.value)) {
                            pay_state_tv.setText(order.orderStatus.value);
                        }
                        //不支付
                        go_to_pay_rel.setVisibility(View.GONE);
                        change_pay_type.setVisibility(View.GONE);

                        switch (order.orderStatus.type) {
                            //审核付款中的订单 点击审核付款
                            case 2:
                                go_to_pay_rel.setVisibility(View.VISIBLE);
                                go_to_pay.setText("审核付款");
                                go_to_pay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(final View v) {
                                        if (StringUtil.checkStr(orderId)) {

                                            Handler handler = new Handler() {
                                                @Override
                                                public void handleMessage(Message msg) {
                                                    super.handleMessage(msg);
                                                    if (msg.what == 2) {
                                                        showCheckOfflinePayPopUp(v, order);
                                                    } else {
                                                        requestData(orderId);
                                                        MsgCenter.fireNull(MsgID.Rsc_order_Change, "CONFIRM");
                                                    }
                                                }
                                            };
                                            OrderUtils.CheckOffline(handler, orderId);
                                        }
                                    }
                                });
                                break;
                            //待配送，点击去配送
                            case 4:
                            case 6:
                                boolean isSure = false;
                                final List<RscOrderDetailResult.OrderEntity.SKUListEntity> skUs = new ArrayList<>();
                                List<RscOrderDetailResult.OrderEntity.SKUListEntity> skUs1 = order.SKUList;
                                if (skUs1 != null) {

                                    for (int i = 0; i < skUs1.size(); i++) {
                                        RscOrderDetailResult.OrderEntity.SKUListEntity skus = skUs1.get(i);
                                        if (skus != null && skus.deliverStatus.equals("4")) {
                                            skUs.add(skus);
                                            isSure = true;
                                        }
                                    }
                                }

                                if (isSure) {
                                    go_to_pay_rel.setVisibility(View.VISIBLE);
                                    go_to_pay.setText("开始配送");
                                    go_to_pay.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            showPopDelivery(v, skUs);
                                        }
                                    });
                                }

                                break;
                            //如果是待自提的订单，可以帮用户去自提
                            case 5:
                                boolean flag = false;
                                final List<RscOrderDetailResult.OrderEntity.SKUListEntity> skUsSelf = new ArrayList<>();
                                List<RscOrderDetailResult.OrderEntity.SKUListEntity> skUsSelf1 = order.SKUList;
                                if (skUsSelf1 != null) {

                                    for (int i = 0; i < skUsSelf1.size(); i++) {

                                        RscOrderDetailResult.OrderEntity.SKUListEntity skus = skUsSelf1.get(i);
                                        if (skus != null && skus.deliverStatus.equals("4")) {
                                            skUsSelf.add(skus);
                                            flag = true;
                                        }
                                    }

                                }
                                if (flag) {
                                    go_to_pay_rel.setVisibility(View.VISIBLE);
                                    go_to_pay.setText("客户自提");
                                    go_to_pay.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showSelfDeliveryPopUp(v, skUsSelf);
                                        }
                                    });
                                }
                                break;
                            default:
                                break;


                        }
                    }


                    //支付信息列表
                    if (order.subOrders != null && !order.subOrders.isEmpty()) {
                        PayInfoAdapter payInfoAdapter = new PayInfoAdapter(this, order.subOrders);
                        pay_info_listView.setAdapter(payInfoAdapter);
                    }

                    //子商品列表
                    if (order.SKUList != null && !order.SKUList.isEmpty()) {
                        CarAdapter carAdapter = new CarAdapter(this, order.SKUList);
                        order_shangpin_list.setAdapter(carAdapter);
                    }
                }
            }
        } else if (ApiType.GET_OFFLINE_PAY_WAY == req.getApi()) {
            if (req.getData().getStatus().equals("1000")) {
                OfflinePayWayResult data = (OfflinePayWayResult) req.getData();
                offlinePayType = data.offlinePayType;
            }

        } else if (ApiType.CONFIRM_OFFLINE_PAY == req.getApi()) {
            if (req.getData().getStatus().equals("1000")) {
                showCustomToast("审核成功", R.drawable.toast_success_icon);
                requestData(orderId);
                MsgCenter.fireNull(MsgID.Rsc_order_Change, "CONFIRM");
            }
        } else if (req.getApi() == ApiType.RSC_ORDER_DELIVERING) {
            if (req.getData().getStatus().equals("1000")) {
                showCustomToast("发货成功", R.drawable.toast_success_icon);
                requestData(orderId);
                MsgCenter.fireNull(MsgID.Rsc_order_Change, "DELIVERING");
            }
        } else if (ApiType.RSC_ORDER_SELF_DELIVERY == req.getApi()) {
            if (req.getData().getStatus().equals("1000")) {
                showCustomToast("自提成功", R.drawable.toast_success_icon);
                requestData(orderId);
                MsgCenter.fireNull(MsgID.Rsc_order_Change, "SELF_DELIVERY");
                if (null != popupWindowSelfDelivery && popupWindowSelfDelivery.isShowing()) {
                    popupWindowSelfDelivery.dismiss();
                }
            }

        }
    }


    //支付信息列表

    public class PayInfoAdapter extends CommonAdapter<RscOrderDetailResult.OrderEntity.SubOrdersEntity> {
        private List<RscOrderDetailResult.OrderEntity.SubOrdersEntity> data;

        public PayInfoAdapter(Context context, List<RscOrderDetailResult.OrderEntity.SubOrdersEntity> data) {
            super(context, data, R.layout.item_payinfo_orderdetail);
            this.data = data;
        }

        @Override
        public void convert(CommonViewHolder holder, final RscOrderDetailResult.OrderEntity.SubOrdersEntity subOrder) {
            if (subOrder != null) {
                //已支付金额影藏
                LinearLayout order_yet_price_ll = (LinearLayout) holder.getView(R.id.order_yet_price_ll);
                order_yet_price_ll.setVisibility(View.GONE);

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
                if (order_type != 0) { //如果交易状态 是已关闭 下方设置已关闭
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
                //支付类型
                holder.getView(R.id.order_pay_type_ll).setVisibility(View.GONE);
                //查看详情
                TextView to_get_pay_detail = (TextView) holder.getView(R.id.to_get_pay_detail);
                to_get_pay_detail.setVisibility(View.GONE);
            }
        }
    }


    //商品列表

    class CarAdapter extends CommonAdapter<RscOrderDetailResult.OrderEntity.SKUListEntity> {

        public CarAdapter(Context context, List<RscOrderDetailResult.OrderEntity.SKUListEntity> data) {
            super(context, data, R.layout.rsc_order_list_item_item);

        }

        @Override
        public void convert(CommonViewHolder holder, RscOrderDetailResult.OrderEntity.SKUListEntity skUsEntity) {

            if (skUsEntity != null) {

                //商品图片
                if (StringUtil.checkStr(skUsEntity.thumbnail)) {
                    Picasso.with(RscOrderDetailActivity.this)
                            .load(MsgID.IP + skUsEntity.thumbnail)
                            .error(R.drawable.error)
                            .placeholder(R.drawable.zhanweitu)
                            .into(((RecyclerImageView) holder.getView(R.id.ordering_item_img)));

                }
                //商品个数
                TextView goodsCount = (TextView) holder.getView(R.id.ordering_item_geshu_for_rsc_detail);
                goodsCount.setVisibility(View.VISIBLE);
                goodsCount.setText("X " + skUsEntity.count + "");
                //商品名
                if (StringUtil.checkStr(skUsEntity.productName)) {
                    holder.setText(R.id.ordering_item_name, skUsEntity.productName);
                }

                //Sku属性
                StringBuilder stringSku = new StringBuilder();
                if (skUsEntity.attributes != null && !skUsEntity.attributes.isEmpty()) {
                    for (int k = 0; k < skUsEntity.attributes.size(); k++) {
                        if (StringUtil.checkStr(skUsEntity.attributes.get(k).name)
                                && StringUtil.checkStr(skUsEntity.attributes.get(k).value)) {
                            stringSku.append(skUsEntity.attributes.get(k).name).append(":").append(skUsEntity.attributes.get(k).value).append(";");
                        }
                    }
                    String car_attr = stringSku.substring(0, stringSku.length() - 1);
                    if (StringUtil.checkStr(car_attr)) {
                        holder.setText(R.id.ordering_item_attr, car_attr);
                    }
                }

                //附加选项
                LinearLayout ordering_item_additions_ll = (LinearLayout) holder.getView(R.id.ordering_item_additionsll);

                StringBuilder stringAdditions = new StringBuilder();
                if (skUsEntity.additions != null && !skUsEntity.additions.isEmpty()) {
                    ordering_item_additions_ll.setVisibility(View.VISIBLE);
                    stringAdditions.append("附加项目:");
                    for (int k = 0; k < skUsEntity.additions.size(); k++) {
                        if (StringUtil.checkStr(skUsEntity.additions.get(k).name)) {
                            stringAdditions.append(skUsEntity.additions.get(k).name).append(";");
                        }
                    }
                    String car_additions = stringAdditions.substring(0, stringAdditions.length() - 1);
                    if (StringUtil.checkStr(car_additions)) {
                        holder.setText(R.id.ordering_item_additions, car_additions);
                    }
                } else {
                    ordering_item_additions_ll.setVisibility(View.GONE);
                }

                TextView skuDeliveryState = (TextView) holder.getView(R.id.ordering_item_state);
                skuDeliveryState.setVisibility(View.VISIBLE);
//                商品发货状态
                if (StringUtil.checkStr(skUsEntity.deliverStatus)) {
                    switch (skUsEntity.deliverStatus) {
                        case "1":
                            skuDeliveryState.setText("未发货");
                            break;
                        case "2":
                            skuDeliveryState.setText("配送中");
                            break;
                        case "4":
                            skuDeliveryState.setText("已到服务站");
                            break;
                        case "5":
                            skuDeliveryState.setText("已收货");
                            break;
                    }
                }
            }

        }
    }


    /***********************************以下是网点发货相关*************************************************************/
    /**
     * 创建PopupWindow
     */
    private void initPopDeliveryWindow() {
        // TODO Auto-generated method stub
        // 获取自定义布局文件activity_popupwindow_left.xml的视图
        @SuppressLint("InflateParams") View popupWindow_view = getLayoutInflater().inflate(
                R.layout.pop_layout_sureorder_dialog, null, false);
        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popupWindowDelivery = new PopupWindow(popupWindow_view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindowDelivery.setAnimationStyle(R.style.popWindow_anim_style);
        popupWindowDelivery.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindowDelivery.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                PopWindowUtils.setBackgroundBlack(pop_bg, 1);
                checkedMap.clear();
            }
        });

        //初始化组件

        ImageView pop_close = (ImageView) popupWindow_view.findViewById(R.id.pop_close);
        TextView pop_order_title = (TextView) popupWindow_view.findViewById(R.id.pop_order_title);
        pop_order_title.setText("开始配送");
        pop_close.setOnClickListener(this);
        pop_sureDelivery = (TextView) popupWindow_view.findViewById(R.id.save);
        pop_sureDelivery.setOnClickListener(this);

        pop_listView = (ListView) popupWindow_view.findViewById(R.id.pop_listView);


    }

    //显示popWindow
    private void showPopDelivery(View parent, List<RscOrderDetailResult.OrderEntity.SKUListEntity> skusList) {
        if (null != popupWindowDelivery) {
            popupWindowDelivery.dismiss();
        } else {
            initPopDeliveryWindow();
        }
        //初始化按钮
        pop_sureDelivery.setEnabled(false);
        pop_sureDelivery.setText("确定");
        //初始化数据
        PopSkusDeliveryAdapter popSkusDeliveryAdapter = new PopSkusDeliveryAdapter(this, skusList);
        pop_listView.setAdapter(popSkusDeliveryAdapter);


        //设置背景及展示
        PopWindowUtils.setBackgroundBlack(pop_bg, 0);
        popupWindowDelivery.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }


    //网点开始发货的Sku列表
    class PopSkusDeliveryAdapter extends CommonAdapter<RscOrderDetailResult.OrderEntity.SKUListEntity> {
        private List<RscOrderDetailResult.OrderEntity.SKUListEntity> list;

        public PopSkusDeliveryAdapter(Context context, List<RscOrderDetailResult.OrderEntity.SKUListEntity> data) {
            super(context, data, R.layout.item_pop_sureorder_layout);
            this.list = data;
        }

        @Override
        public void convert(CommonViewHolder holder, final RscOrderDetailResult.OrderEntity.SKUListEntity skus) {

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
                        int count = setCheckedDeliveryGoodsCount(list);
                        if (count != 0) {
                            pop_sureDelivery.setText("确定(" + count + ")");
                            pop_sureDelivery.setEnabled(true);
                        } else {
                            pop_sureDelivery.setText("确定");
                            pop_sureDelivery.setEnabled(false);
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


    /***********************************
     * 以下是网点审核付款相关
     *************************************************************/


    //显示popWindow
    private void showCheckOfflinePayPopUp(View parent, RscOrderDetailResult.OrderEntity ordersEntity) {
        if (null != popupWindow) {
            popupWindow.dismiss();
        } else {
            initCheckOfflinePayPopUptWindow();
        }

        //设置背景及展示
        PopWindowUtils.setBackgroundBlack(pop_bg, 0);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);


        //初始化数据
        if (offlinePayType != null && !offlinePayType.isEmpty()) {
            checkedMap.put("3", true);//默认选中现金
            PayWayAdapter popSkusAdapter = new PayWayAdapter(this, offlinePayType);
            pay_way_gridView.setAdapter(popSkusAdapter);
        }

        if (ordersEntity != null) {
            //收货人和金额
            recipient_name.setText(ordersEntity.consigneeName);
            if (ordersEntity.payment != null) {
                check_price.setText("¥" + StringUtil.toTwoString(ordersEntity.payment.price + "") + "元");
                offlinePaymentId = ordersEntity.payment.id;
                offlinePayPrice = ordersEntity.payment.price + "";
            }
        }

    }


    /**
     * 创建PopupWindow
     */
    private void initCheckOfflinePayPopUptWindow() {
        // TODO Auto-generated method stub
        // 获取自定义布局文件activity_popupwindow_left.xml的视图
        @SuppressLint("InflateParams") View popupWindow_view = getLayoutInflater().inflate(
                R.layout.pop_layout_rsc_check_pay_dialog, null, false);

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
                checkedMap.clear();
            }
        });

        //初始化组件

        ImageView pop_close = (ImageView) popupWindow_view.findViewById(R.id.pop_close);
        pop_close.setOnClickListener(this);
        check_price = (TextView) popupWindow_view.findViewById(R.id.check_price); //待审金额
        recipient_name = (TextView) popupWindow_view.findViewById(R.id.recipient_name); //收货人姓名
        TextView pop_sure = (TextView) popupWindow_view.findViewById(R.id.pop_save);
        pop_sure.setOnClickListener(this);
        pay_way_gridView = (UnSwipeGridView) popupWindow_view.findViewById(R.id.pay_way_gridView);

    }


    class PayWayAdapter extends CommonAdapter<OfflinePayWayResult.OfflinePayTypeEntity> {

        public PayWayAdapter(Context context, List<OfflinePayWayResult.OfflinePayTypeEntity> data) {
            super(context, data, R.layout.item_rsc_pay_way_gird_layout);
        }

        @Override
        public void convert(final CommonViewHolder holder, final OfflinePayWayResult.OfflinePayTypeEntity offlinePayTypeEntity) {
            if (offlinePayTypeEntity != null) {

                final CheckBox checkBox = (CheckBox) holder.getView(R.id.offline_pay_way_checkBox);
                checkBox.setText(offlinePayTypeEntity.name);

                //如果选中，集合中的值为true 否则为false
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 重置，确保最多只有一项被选中
                        for (String key : checkedMap.keySet()) {
                            checkedMap.put(key, false);
                        }
                        checkedMap.put(offlinePayTypeEntity.type + "", true);
                        PayWayAdapter.this.notifyDataSetChanged();
                    }

                });


                //根据map刷新适配器的选中状态
                Boolean res = false;
                if (checkedMap.get(offlinePayTypeEntity.type + "") != null) {
                    res = checkedMap.get(offlinePayTypeEntity.type + "");
                }
                checkBox.setChecked(res);

            }

        }
    }


    /***********************************
     * 以下是网点自提相关
     *************************************************************/

    //显示popWindow
    private void showSelfDeliveryPopUp(View parent, List<RscOrderDetailResult.OrderEntity.SKUListEntity> skusList) {
        if (null != popupWindowSelfDelivery) {
            popupWindowSelfDelivery.dismiss();
        } else {
            initSelfDeliveryPopUptWindow();
        }
        //初始化按钮
        pop_self_delivery_sure.setEnabled(false);
        pop_self_delivery_sure.setText("下一步");
        self_delivery_code_et.setText("");

        pop_slef_delivery_order_title.setText("客户自提-选择商品");
        pop_self_delivery_code_Rel.setVisibility(View.GONE);
        pop_self_delivery_listView.setVisibility(View.VISIBLE);

        //初始化数据
        PopSkusSelfDeliveryAdapter popSkusSelfDeliveryAdapter = new PopSkusSelfDeliveryAdapter(this, skusList);
        pop_self_delivery_listView.setAdapter(popSkusSelfDeliveryAdapter);
        //设置背景及展示
        PopWindowUtils.setBackgroundBlack(pop_bg, 0);
        popupWindowSelfDelivery.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }


    /**
     * 创建PopupWindow
     */
    private void initSelfDeliveryPopUptWindow() {
        // TODO Auto-generated method stub
        // 获取自定义布局文件activity_popupwindow_left.xml的视图
        @SuppressLint("InflateParams") View popupWindow_view = getLayoutInflater().inflate(
                R.layout.pop_layout_self_delivery_dialog, null, true);

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popupWindowSelfDelivery = new PopupWindow(popupWindow_view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindowSelfDelivery.setAnimationStyle(R.style.popWindow_anim_style);
        popupWindowSelfDelivery.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindowSelfDelivery.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                PopWindowUtils.setBackgroundBlack(pop_bg, 1);
                checkedMap.clear();
                self_delivery_tag = false;
            }
        });

        //初始化组件


        ImageView pop_close = (ImageView) popupWindow_view.findViewById(R.id.pop_close);
        pop_close.setOnClickListener(this);
        pop_slef_delivery_order_title = (TextView) popupWindow_view.findViewById(R.id.pop_order_title);

        self_delivery_code_et = (EditText) popupWindow_view.findViewById(R.id.self_delivery_code_et);
        pop_self_delivery_code_Rel = (RelativeLayout) popupWindow_view.findViewById(R.id.pop_self_delivery_code_Rel);
        pop_self_delivery_listView = (ListView) popupWindow_view.findViewById(R.id.pop_listView);
        pop_self_delivery_sure = (TextView) popupWindow_view.findViewById(R.id.pop_save_self_delivery);

        pop_self_delivery_sure.setOnClickListener(this);
        self_delivery_code_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (self_delivery_tag) {
                    if (s.length() >= 7) {
                        pop_self_delivery_sure.setEnabled(true);
                    } else {
                        pop_self_delivery_sure.setEnabled(false);
                    }
                }
            }
        });

    }


    //网点自提的Sku列表
    class PopSkusSelfDeliveryAdapter extends CommonAdapter<RscOrderDetailResult.OrderEntity.SKUListEntity> {
        private List<RscOrderDetailResult.OrderEntity.SKUListEntity> list;

        public PopSkusSelfDeliveryAdapter(Context context, List<RscOrderDetailResult.OrderEntity.SKUListEntity> data) {
            super(context, data, R.layout.item_pop_sureorder_layout);
            this.list = data;
        }

        @Override
        public void convert(CommonViewHolder holder, final RscOrderDetailResult.OrderEntity.SKUListEntity skus) {

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
                        int count = setCheckedDeliveryGoodsCount(list);
                        if (count != 0) {
                            pop_self_delivery_sure.setText("已选(" + count + "),下一步");
                            pop_self_delivery_sure.setEnabled(true);
                        } else {
                            pop_self_delivery_sure.setText("下一步");
                            pop_self_delivery_sure.setEnabled(false);
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


    //获取线下付款方式
    private void getPayWay() {
        RequestParams params = new RequestParams();
        execApi(ApiType.GET_OFFLINE_PAY_WAY.setMethod(ApiType.RequestMethod.GET), params);
    }


    //确定选中按钮的数量
    public int setCheckedDeliveryGoodsCount(List<RscOrderDetailResult.OrderEntity.SKUListEntity> list) {
        int count = 0;
        List<String> refList = new ArrayList<>();
        for (String key : checkedMap.keySet()) {
            if (checkedMap.get(key)) {
                refList.add(key);
            }
        }
        for (RscOrderDetailResult.OrderEntity.SKUListEntity skus : list) {
            if (refList.contains(skus.ref)) {
                count += skus.count;
            }
        }
        return count;
    }

    //监听软键盘收起时，清除editText的焦点
    @Override
    public void onKeyboardStateChanged(int state) {
        if (state == KeyboardListenRelativeLayout.KEYBOARD_STATE_HIDE) {

            if (self_delivery_code_et != null) {
                self_delivery_code_et.clearFocus();
            }
        }
    }


}