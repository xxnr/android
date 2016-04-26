package com.ksfc.newfarmer.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.RSCOrderListActivity;
import com.ksfc.newfarmer.activitys.RscOrderDetailActivity;
import com.ksfc.newfarmer.adapter.CommonAdapter;
import com.ksfc.newfarmer.adapter.CommonViewHolder;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.LoginResult;
import com.ksfc.newfarmer.protocol.beans.OfflinePayWayResult;
import com.ksfc.newfarmer.protocol.beans.RscOrderDetailResult;
import com.ksfc.newfarmer.protocol.beans.RscOrderResult;
import com.ksfc.newfarmer.utils.DateFormatUtils;
import com.ksfc.newfarmer.utils.PullToRefreshUtils;
import com.ksfc.newfarmer.utils.ShowHideUtils;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.KeyboardListenRelativeLayout;
import com.ksfc.newfarmer.widget.RecyclerImageView;
import com.ksfc.newfarmer.widget.UnSwipeGridView;
import com.ksfc.newfarmer.widget.WidgetUtil;
import com.squareup.picasso.Picasso;

import net.yangentao.util.msg.MsgCenter;
import net.yangentao.util.msg.MsgListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HePeng on 2015/12/3.
 */
public class RscOrderDetailFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2, KeyboardListenRelativeLayout.IOnKeyboardStateChangedListener {

    private PullToRefreshListView waitingpay_lv;
    private int page = 1;
    private int TYPE = 0;//订单类型 1:待付款 2:待审核 3: 待配送 4:带自提
    private OrderAdapter adapter;
    private RelativeLayout null_layout;
    private PopupWindow popupWindow, popupWindowDelivery, popupWindowSelfDelivery;
    private TextView pop_sureDelivery;
    private ListView pop_listView;
    private Map<String, Boolean> checkedMap = new HashMap<>();//用于存放popWindow中选中的确认收货商品
    private String deliveringOrderId;
    private String selfDeliveringOrderId;
    private String offlinePaymentId; //线下支付的paymentId
    private String offlinePayPrice; //线下支付的payPrice;
    private TextView recipient_name;
    private TextView check_price;
    private UnSwipeGridView pay_way_gridView;
    private List<OfflinePayWayResult.OfflinePayTypeEntity> offlinePayType;
    private TextView pop_self_delivery_sure;
    private ListView pop_self_delivery_listView;
    private TextView pop_slef_delivery_order_title;
    private EditText self_delivery_code_et;
    private RelativeLayout pop_self_delivery_code_Rel;
    private boolean self_delivery_tag = false;
    private KeyboardListenRelativeLayout rootView;


    @Override
    public View InItView() {
        View view = inflater.inflate(R.layout.rsc_order_list_layout, null);
        waitingpay_lv = (PullToRefreshListView) view.findViewById(R.id.waitingpay_lv);
        waitingpay_lv.setMode(PullToRefreshBase.Mode.BOTH);
        waitingpay_lv.setOnRefreshListener(this);

        rootView = (KeyboardListenRelativeLayout) view.findViewById(R.id.root_view);
        rootView.setOnKeyboardStateChangedListener(this);

        //设置刷新的文字
        PullToRefreshUtils.setFreshText(waitingpay_lv);
        //无订单下的状态
        null_layout = ((RelativeLayout) view.findViewById(R.id.null_shop_cart_layout));

        Bundle bundle = getArguments();
        TYPE = bundle.getInt("TYPE", 0);
        //刷新时候的只出现一个
        if (TYPE == 0) {
            showProgressDialog();
        }

        //滑动时刷新
        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                try {
                    if (((Integer) args[0]) == TYPE) {
                        showProgressDialog();
                        page = 1;
                        getData(page);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, MsgID.rsc_swipe_reFlash);

        //订单状态改变时刷新
        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                page = 1;
                getData(page);
            }
        }, MsgID.Rsc_order_Change);

        getData(page);
        getPayWay();
        return view;
    }


    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (ApiType.GET_RSC_ORDER_LIST == req.getApi()) {
            RscOrderResult data = (RscOrderResult) req.getData();
            waitingpay_lv.onRefreshComplete();
            if (data.getStatus().equals("1000")) {
                List<RscOrderResult.OrdersEntity> orders = data.orders;
                if (!orders.isEmpty()) {
                    null_layout.setVisibility(View.GONE);
                    if (page == 1) {
                        if (adapter == null) {
                            try {
                                adapter = new OrderAdapter(activity, orders);
                                WidgetUtil.setListViewHeightBasedOnChildren(waitingpay_lv);
                                waitingpay_lv.setAdapter(adapter);
                            } catch (NullPointerException e) {
                                //不知为何getActivity居然会空
                            }
                        } else {
                            adapter.clear();
                            adapter.addAll(orders);
                        }
                    } else {
                        if (adapter != null) {
                            adapter.addAll(orders);
                        }
                    }
                } else {
                    if (page == 1) {
                        if (adapter != null) {
                            adapter.clear();
                        }
                        null_layout.setVisibility(View.VISIBLE);
                    } else {
                        page--;
                        (activity).showToast("没有更多订单");
                    }
                }
            }
        } else if (req.getApi() == ApiType.RSC_ORDER_DELIVERING) {
            if (req.getData().getStatus().equals("1000")) {
                showCustomToast("发货成功", R.drawable.toast_success_icon);
                page = 1;
                getData(page);
            }
        } else if (ApiType.GET_OFFLINE_PAY_WAY == req.getApi()) {
            if (req.getData().getStatus().equals("1000")) {
                OfflinePayWayResult data = (OfflinePayWayResult) req.getData();
                offlinePayType = data.offlinePayType;

            }

        } else if (ApiType.GET_RSC_ORDER_Detail == req.getApi()) {
            if (req.getData().getStatus().equals("1000")) {
                RscOrderDetailResult data = (RscOrderDetailResult) req.getData();

                //初始化数据
                if (offlinePayType != null && !offlinePayType.isEmpty()) {
                    checkedMap.put("3", true);//默认选中现金
                    PayWayAdapter popSkusAdapter = new PayWayAdapter(activity, offlinePayType);
                    pay_way_gridView.setAdapter(popSkusAdapter);
                }

                if (data.order != null) {
                    //收货人和金额
                    recipient_name.setText(data.order.consigneeName);
                    if (data.order.payment != null) {
                        check_price.setText("¥" + StringUtil.toTwoString(data.order.payment.price + "") + "元");
                        offlinePaymentId = data.order.payment.id;
                        offlinePayPrice = data.order.payment.price + "";
                    }
                }
            }

        } else if (ApiType.CONFIRM_OFFLINE_PAY == req.getApi()) {
            if (req.getData().getStatus().equals("1000")) {
                showCustomToast("审核成功", R.drawable.toast_success_icon);
                page = 1;
                getData(page);
            }
        } else if (ApiType.RSC_ORDER_SELF_DELIVERY == req.getApi()) {
            if (req.getData().getStatus().equals("1000")) {
                showCustomToast("自提成功", R.drawable.toast_success_icon);
                if (null != popupWindowSelfDelivery && popupWindowSelfDelivery.isShowing()) {
                    popupWindowSelfDelivery.dismiss();
                }
                page = 1;
                getData(page);
            }
        }

    }


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.pop_close:
                //关闭popWindow
                if (null != popupWindow && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                if (null != popupWindowDelivery && popupWindowDelivery.isShowing()) {
                    popupWindowDelivery.dismiss();
                }
                if (null != popupWindowSelfDelivery && popupWindowSelfDelivery.isShowing()) {
                    popupWindowSelfDelivery.dismiss();
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

            //开始发货
            case R.id.save:
                if (checkedMap != null && !checkedMap.isEmpty()) {
                    List<String> list = new ArrayList<>();
                    for (String key : checkedMap.keySet()) {
                        if (checkedMap.get(key)) {
                            list.add(key);
                        }
                    }

                    if (StringUtil.checkStr(deliveringOrderId)) {
                        showProgressDialog();
                        RequestParams params = new RequestParams();
                        Map<String, Object> map = new HashMap<>();
                        map.put("SKURefs", list);
                        Log.d("RscOrderDetailFragment", "list:" + list);
                        map.put("orderId", deliveringOrderId);
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
                        if (StringUtil.checkStr(selfDeliveringOrderId)) {
                            showProgressDialog();
                            RequestParams params = new RequestParams();
                            Map<String, Object> map = new HashMap<>();
                            map.put("SKURefs", list);
                            map.put("orderId", selfDeliveringOrderId);
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


    //外层的适配器

    class OrderAdapter extends CommonAdapter<RscOrderResult.OrdersEntity> {


        public OrderAdapter(Context context, List<RscOrderResult.OrdersEntity> data) {
            super(context, data, R.layout.my_order_list_item);
        }

        @Override
        public void convert(CommonViewHolder holder, final RscOrderResult.OrdersEntity ordersEntity) {
            if (ordersEntity != null) {
                //时间和姓名
                //格式化时间
                String time = DateFormatUtils.convertTime(ordersEntity.dateCreated);
                StringBuilder builder = new StringBuilder();

                if (StringUtil.checkStr(time)) {
                    builder.append(time).append("  ");
                }

                if (StringUtil.checkStr(ordersEntity.consigneeName)) {
                    builder.append(ordersEntity.consigneeName);
                }
                holder.setText(R.id.my_order_id, builder.toString());

                //配送方式
                if (ordersEntity.deliveryType != null) {
                    if (StringUtil.checkStr(ordersEntity.deliveryType.value)) {
                        holder.setText(R.id.my_order_delivery_type, ordersEntity.deliveryType.value);
                    }
                }
                //订单状态 及不同订单状态下所对应的操作

                RelativeLayout go_to_pay_rel = (RelativeLayout) holder.getView(R.id.go_to_pay_rel);
                Button go_to_pay = (Button) holder.getView(R.id.go_to_pay);
                Button change_pay_type = (Button) holder.getView(R.id.change_pay_type);

                go_to_pay_rel.setVisibility(View.GONE);
                go_to_pay.setOnClickListener(null);
                change_pay_type.setVisibility(View.GONE);

                if (ordersEntity.type != null) {
                    if (StringUtil.checkStr(ordersEntity.type.value)) {
                        holder.setText(R.id.my_order_pay_state, ordersEntity.type.value);
                    }

                    switch (ordersEntity.type.type) {
                        //审核付款中的订单 点击审核付款
                        case 2:
                            go_to_pay_rel.setVisibility(View.VISIBLE);
                            go_to_pay.setText("审核付款");
                            go_to_pay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    showCheckOfflinePayPopUp(v, ordersEntity);

                                }
                            });
                            break;
                        //待配送，点击去配送
                        case 4:
                        case 6:
                            boolean isSure = false;
                            final List<RscOrderResult.OrdersEntity.SKUsEntity> skUs = new ArrayList<>();
                            List<RscOrderResult.OrdersEntity.SKUsEntity> skUs1 = ordersEntity.SKUs;
                            if (skUs1 != null) {

                                for (int i = 0; i < skUs1.size(); i++) {

                                    RscOrderResult.OrdersEntity.SKUsEntity skus = skUs1.get(i);
                                    if (skus != null && skus.deliverStatus == 4) {
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
                                        deliveringOrderId = ordersEntity.id;
                                    }
                                });
                            }

                            break;
                        //如果是待自提的订单，可以帮用户去自提
                        case 5:
                            boolean flag = false;
                            final List<RscOrderResult.OrdersEntity.SKUsEntity> skUsSelf = new ArrayList<>();
                            List<RscOrderResult.OrdersEntity.SKUsEntity> skUsSelf1 = ordersEntity.SKUs;
                            if (skUsSelf1 != null) {

                                for (int i = 0; i < skUsSelf1.size(); i++) {

                                    RscOrderResult.OrdersEntity.SKUsEntity skus = skUsSelf1.get(i);
                                    if (skus != null && skus.deliverStatus == 4) {
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
                                        selfDeliveringOrderId = ordersEntity.id;
                                    }
                                });
                            }
                            break;
                        default:
                            break;
                    }

                }
                //订单合计金额
                if (StringUtil.checkStr(ordersEntity.price + "")) {
                    holder.setText(R.id.my_order_pay_price, "¥" + StringUtil.toTwoString(ordersEntity.price + ""));
                }

                LinearLayout llCommerceContainer = (LinearLayout) holder.getView(R.id.my_order_llCommerceContainer);
                if (llCommerceContainer.getChildCount() > 0) {
                    llCommerceContainer.removeAllViews();
                }
                List<RscOrderResult.OrdersEntity.SKUsEntity> SKUsList = ordersEntity.SKUs;
                if (SKUsList != null && !SKUsList.isEmpty()) {
                    for (int i = 0; i < SKUsList.size(); i++) {
                        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.rsc_order_list_item_item, null);
                        ViewHolderChild viewHolderChild = new ViewHolderChild(rootView);
                        RscOrderResult.OrdersEntity.SKUsEntity skUsEntity = SKUsList.get(i);
                        if (skUsEntity != null) {
                            //商品图片
                            if (StringUtil.checkStr(skUsEntity.thumbnail)) {
                                Picasso.with(activity)
                                        .load(MsgID.IP + skUsEntity.thumbnail)
                                        .error(R.drawable.error)
                                        .placeholder(R.drawable.zhanweitu)
                                        .into(viewHolderChild.ordering_item_img);

                            }
                            //商品个数
                            viewHolderChild.ordering_item_geshu.setText("X " + skUsEntity.count);
                            //商品名
                            if (StringUtil.checkStr(skUsEntity.productName)) {
                                viewHolderChild.ordering_item_name.setText(skUsEntity.productName);
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
                                    viewHolderChild.ordering_item_attr.setText(car_attr);
                                }
                            }

                            //附加选项
                            StringBuilder stringAdditions = new StringBuilder();
                            if (skUsEntity.additions != null && !skUsEntity.additions.isEmpty()) {
                                viewHolderChild.ordering_item_additionsll.setVisibility(View.VISIBLE);
                                stringAdditions.append("附加项目:");
                                for (int k = 0; k < skUsEntity.additions.size(); k++) {
                                    if (StringUtil.checkStr(skUsEntity.additions.get(k).name)) {
                                        stringAdditions.append(skUsEntity.additions.get(k).name).append(";");
                                    }
                                }
                                String car_additions = stringAdditions.substring(0, stringAdditions.length() - 1);
                                if (StringUtil.checkStr(car_additions)) {
                                    viewHolderChild.ordering_item_additions.setText(car_additions);
                                }
                            } else {
                                viewHolderChild.ordering_item_additionsll.setVisibility(View.GONE);
                            }
                        }
                        llCommerceContainer.addView(rootView);
                    }
                }
                llCommerceContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, RscOrderDetailActivity.class);
                        intent.putExtra("orderId", ordersEntity.id);
                        startActivity(intent);
                    }
                });
            }
        }


        class ViewHolderChild {
            public RecyclerImageView ordering_item_img;
            public TextView ordering_item_name;
            public TextView ordering_item_state;
            public TextView ordering_item_geshu;
            public TextView ordering_item_attr;
            public TextView ordering_item_additions;
            public LinearLayout ordering_item_additionsll;

            public ViewHolderChild(View rootView) {
                this.ordering_item_img = (RecyclerImageView) rootView.findViewById(R.id.ordering_item_img);
                this.ordering_item_name = (TextView) rootView.findViewById(R.id.ordering_item_name);
                this.ordering_item_state = (TextView) rootView.findViewById(R.id.ordering_item_state);
                this.ordering_item_geshu = (TextView) rootView.findViewById(R.id.ordering_item_geshu);
                this.ordering_item_attr = (TextView) rootView.findViewById(R.id.ordering_item_attr);
                this.ordering_item_additions = (TextView) rootView.findViewById(R.id.ordering_item_additions);
                this.ordering_item_additionsll = (LinearLayout) rootView.findViewById(R.id.ordering_item_additionsll);
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
        @SuppressLint("InflateParams") View popupWindow_view = inflater.inflate(
                R.layout.pop_layout_sureorder_dialog, null, false);

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popupWindowDelivery = new PopupWindow(popupWindow_view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindowDelivery.setAnimationStyle(R.style.popWindow_anim_style);
        popupWindowDelivery.setFocusable(true);
        popupWindowDelivery.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindowDelivery.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                showDialogBg(1, (RSCOrderListActivity) activity);
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
    private void showPopDelivery(View parent, List<RscOrderResult.OrdersEntity.SKUsEntity> skusList) {
        if (null != popupWindowDelivery) {
            popupWindowDelivery.dismiss();
        } else {
            initPopDeliveryWindow();
        }
        //初始化按钮
        pop_sureDelivery.setEnabled(false);
        pop_sureDelivery.setText("确定");
        //初始化数据
        PopSkusDeliveryAdapter popSkusDeliveryAdapter = new PopSkusDeliveryAdapter(activity, skusList);
        pop_listView.setAdapter(popSkusDeliveryAdapter);


        //设置背景及展示
        showDialogBg(0, (RSCOrderListActivity) activity);
        popupWindowDelivery.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }


    //网点开始发货的Sku列表
    class PopSkusDeliveryAdapter extends CommonAdapter<RscOrderResult.OrdersEntity.SKUsEntity> {
        private List<RscOrderResult.OrdersEntity.SKUsEntity> list;

        public PopSkusDeliveryAdapter(Context context, List<RscOrderResult.OrdersEntity.SKUsEntity> data) {
            super(context, data, R.layout.item_pop_sureorder_layout);
            this.list = data;
        }

        @Override
        public void convert(CommonViewHolder holder, final RscOrderResult.OrdersEntity.SKUsEntity skus) {

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
    private void showCheckOfflinePayPopUp(View parent, RscOrderResult.OrdersEntity ordersEntity) {
        if (null != popupWindow) {
            popupWindow.dismiss();
        } else {
            initCheckOfflinePayPopUptWindow();
        }
        //请求Rsc订单详情
        getRscOrderDetail(ordersEntity.id);

        //设置背景及展示
        showDialogBg(0, (RSCOrderListActivity) activity);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

    }


    /**
     * 创建PopupWindow
     */
    private void initCheckOfflinePayPopUptWindow() {
        // TODO Auto-generated method stub
        // 获取自定义布局文件activity_popupwindow_left.xml的视图
        @SuppressLint("InflateParams") View popupWindow_view = inflater.inflate(
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
                showDialogBg(1, (RSCOrderListActivity) activity);
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
    private void showSelfDeliveryPopUp(View parent, List<RscOrderResult.OrdersEntity.SKUsEntity> skusList) {
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
        PopSkusSelfDeliveryAdapter popSkusSelfDeliveryAdapter = new PopSkusSelfDeliveryAdapter(activity, skusList);
        pop_self_delivery_listView.setAdapter(popSkusSelfDeliveryAdapter);
        //设置背景及展示
        showDialogBg(0, (RSCOrderListActivity) activity);
        popupWindowSelfDelivery.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }


    /**
     * 创建PopupWindow
     */
    private void initSelfDeliveryPopUptWindow() {
        // TODO Auto-generated method stub
        // 获取自定义布局文件activity_popupwindow_left.xml的视图
        @SuppressLint("InflateParams") View popupWindow_view = inflater.inflate(
                R.layout.pop_layout_self_delivery_dialog, null, false);

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popupWindowSelfDelivery = new PopupWindow(popupWindow_view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindowSelfDelivery.setAnimationStyle(R.style.popWindow_anim_style);
        popupWindowSelfDelivery.setFocusable(true);
        popupWindowSelfDelivery.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindowSelfDelivery.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                showDialogBg(1, (RSCOrderListActivity) activity);
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
    class PopSkusSelfDeliveryAdapter extends CommonAdapter<RscOrderResult.OrdersEntity.SKUsEntity> {
        private List<RscOrderResult.OrdersEntity.SKUsEntity> list;

        public PopSkusSelfDeliveryAdapter(Context context, List<RscOrderResult.OrdersEntity.SKUsEntity> data) {
            super(context, data, R.layout.item_pop_sureorder_layout);
            this.list = data;
        }

        @Override
        public void convert(CommonViewHolder holder, final RscOrderResult.OrdersEntity.SKUsEntity skus) {

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


    //确定选中按钮的数量
    public int setCheckedDeliveryGoodsCount(List<RscOrderResult.OrdersEntity.SKUsEntity> list) {
        int count = 0;
        List<String> refList = new ArrayList<>();
        for (String key : checkedMap.keySet()) {
            if (checkedMap.get(key)) {
                refList.add(key);
            }
        }
        for (RscOrderResult.OrdersEntity.SKUsEntity skus : list) {
            if (refList.contains(skus.ref)) {
                count += skus.count;
            }
        }
        return count;
    }


    //刷新的方法
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        PullToRefreshUtils.setFreshClose(refreshView);
        page = 1;
        getData(page);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

        PullToRefreshUtils.setFreshClose(refreshView);
        page++;
        getData(page);

    }


    /**
     * 数据请求
     *
     * @param page
     */
    @SuppressWarnings("unused")
    private void getData(int page) {
        RequestParams params = new RequestParams();
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            params.put("userId", userInfo.userid);
        }
        params.put("page", page);
        if (TYPE != 0) {
            params.put("type", TYPE);
        }
        execApi(ApiType.GET_RSC_ORDER_LIST.setMethod(ApiType.RequestMethod.GET), params);
    }


    //请求Rsc订单详情
    private void getRscOrderDetail(String orderId) {

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


    //获取线下付款方式
    private void getPayWay() {
        RequestParams params = new RequestParams();
        execApi(ApiType.GET_OFFLINE_PAY_WAY.setMethod(ApiType.RequestMethod.GET), params);
    }

    //控制背景灯光
    private void showDialogBg(int bg, BgSwitch bgSwitch) {
        bgSwitch.backgroundSwitch(bg);
    }

    public interface BgSwitch {
        void backgroundSwitch(int bg);
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

