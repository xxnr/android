package com.ksfc.newfarmer.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
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

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ksfc.newfarmer.BaseFragment;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.common.LoadMoreOnsrcollListener;
import com.ksfc.newfarmer.common.OrderUtils;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.MyOrderListActivity;
import com.ksfc.newfarmer.activitys.MyOrderDetailActivity;
import com.ksfc.newfarmer.activitys.OfflinePayActivity;
import com.ksfc.newfarmer.activitys.PaywayActivity;
import com.ksfc.newfarmer.activitys.GoodsListActivity;
import com.ksfc.newfarmer.activitys.PickUpStateActivity;
import com.ksfc.newfarmer.common.CommonAdapter;
import com.ksfc.newfarmer.common.CommonViewHolder;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.http.ApiType;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.http.RequestParams;
import com.ksfc.newfarmer.http.beans.LoginResult;
import com.ksfc.newfarmer.http.beans.WaitingPay;
import com.ksfc.newfarmer.utils.ExpandViewTouch;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.PullToRefreshUtils;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.LoadingFooter;
import com.ksfc.newfarmer.widget.RecyclerImageView;
import com.ksfc.newfarmer.widget.WidgetUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.yangentao.util.PreferenceUtil;
import net.yangentao.util.msg.MsgCenter;
import net.yangentao.util.msg.MsgListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HePeng on 2015/12/3.
 */
public class MyOrderDetailFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener {

    private PullToRefreshListView waitingpay_lv;
    private int page = 1;
    private int TYPE = 0;//订单类型 1:未付款 2:待发货 3: 已发货 4:已完成
    private OrderAdapter adapter;
    private RelativeLayout null_layout;
    private PopupWindow popupWindow;
    private TextView pop_sure;
    private ListView pop_listView;
    private Map<String, Boolean> checkedMap = new HashMap<>();//用于存放popWindow中选中的确认收货商品

    private String sureOrderId;

    private String huaFeiClassId = "531680A5";
    private String carClassId = "6C7D8F66";

    private LoadingFooter loadingFooter;

    private LoadMoreOnsrcollListener moreOnsrcollListener =new LoadMoreOnsrcollListener() {
        @Override
        public void loadMore() {
            //加载更多
            if (loadingFooter.getState() == LoadingFooter.State.Idle) {
                loadingFooter.setState(LoadingFooter.State.Loading);
                page++;
                getData(page);
            }
        }
    };


    @Override
    public View InItView() {
        View view = inflater.inflate(R.layout.waitingpaylistview_layout, null);
        waitingpay_lv = (PullToRefreshListView) view.findViewById(R.id.waitingpay_lv);


        waitingpay_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        waitingpay_lv.setOnRefreshListener(this);


        waitingpay_lv.setOnScrollListener(moreOnsrcollListener);
        loadingFooter = new LoadingFooter(activity,waitingpay_lv.getRefreshableView());
        //设置刷新的文字
        PullToRefreshUtils.setFreshText(waitingpay_lv);
        //无订单下的状态
        null_layout = ((RelativeLayout) view.findViewById(R.id.null_shop_cart_layout));
        null_layout.setVisibility(View.GONE);
        view.findViewById(R.id.my_login_sure).setOnClickListener(this);
        view.findViewById(R.id.my_login_cancel).setOnClickListener(this);

        Bundle bundle = getArguments();
        TYPE = bundle.getInt("TYPE", 0);
        //刷新时候的只出现一个
        if (TYPE == 0) {
            showProgressDialog();
        }
        //订单支付成功时刷新
        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                page = 1;
                getData(page);
            }
        }, MsgID.Pay_success);


        //订单状态改变时刷新
        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                page = 1;
                getData(page);
            }
        }, MsgID.order_Change);

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
        }, MsgID.swipe_reFlash);

        //设置classId
        PreferenceUtil pu = new PreferenceUtil(activity, "config");
        huaFeiClassId = pu.getString("huafei", "531680A5");
        carClassId = pu.getString("qiche", "6C7D8F66");

        getData(page);
        return view;
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
            params.put("typeValue", TYPE);
        }
        execApi(ApiType.GETORDERLIST, params);
    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (ApiType.GETORDERLIST == req.getApi()) {
            WaitingPay data = (WaitingPay) req.getData();
            waitingpay_lv.onRefreshComplete();
            if (data.getStatus().equals("1000")) {
                List<WaitingPay.Orders> rows = data.datas.rows;
                if (!rows.isEmpty()) {
                    null_layout.setVisibility(View.GONE);
                        loadingFooter.setSize(page,rows.size());

                    if (page == 1) {

                        if (adapter == null) {
                            adapter = new OrderAdapter(rows);
                            WidgetUtil.setListViewHeightBasedOnChildren(waitingpay_lv);
                            waitingpay_lv.setAdapter(adapter);
                        } else {
                            adapter.clear();
                            adapter.addAll(rows);
                        }
                        waitingpay_lv.getRefreshableView().setSelection(0);
                    } else {
                        if (adapter != null) {
                            adapter.addAll(rows);
                        }
                    }
                } else {
                    loadingFooter.setSize(page,0);

                    if (page == 1) {
                        if (adapter != null) {
                            adapter.clear();
                        }
                        null_layout.setVisibility(View.VISIBLE);
                    } else {
                        page--;
                    }
                }
            }
        } else if (req.getApi() == ApiType.SURE_GET_GOODS) {
            if (req.getData().getStatus().equals("1000")) {
                showCustomToast("收货成功", R.drawable.toast_success_icon);
                page = 1;
                getData(page);
            }
        }

    }


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.my_login_sure:
                Intent intent = new Intent(activity,
                        GoodsListActivity.class);
                intent.putExtra("className", "化肥");
                intent.putExtra("classId", huaFeiClassId);
                startActivity(intent);
                break;
            case R.id.my_login_cancel:
                Intent intent1 = new Intent(activity,
                        GoodsListActivity.class);
                intent1.putExtra("className", "汽车");
                intent1.putExtra("classId", carClassId);
                startActivity(intent1);
                break;

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
                    if (StringUtil.checkStr(sureOrderId)) {
                        showProgressDialog();
                        RequestParams params = new RequestParams();
                        Map<String, Object> map = new HashMap<>();
                        map.put("SKURefs", list);
                        map.put("orderId", sureOrderId);
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

        }
    }


    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        PullToRefreshUtils.setFreshClose(refreshView);
        page = 1;
        getData(page);
    }

    //外层的适配器

    class OrderAdapter extends BaseAdapter {
        private List<WaitingPay.Orders> rows;

        OrderAdapter(List<WaitingPay.Orders> rows) {
            this.rows = rows;
        }

        @Override
        public int getCount() {
            return rows.size();
        }

        @Override
        public Object getItem(int position) {
            return rows.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addAll(Collection<? extends WaitingPay.Orders> collection) {
            rows.addAll(collection);
            notifyDataSetChanged();
        }

        public void clear() {
            rows.clear();
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_my_order_list, null);
                convertView.setTag(new ViewHolder(convertView));
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            final WaitingPay.Orders order = rows.get(position);

            //设置订单号
            if (StringUtil.checkStr(order.orderId)) {
                holder.order_id_tv.setText("订单号：" + order.orderId);
            }

            if (order.order != null) {

                //发货方式
                if (order.order.deliveryType != null) {
                    if (StringUtil.checkStr(order.order.deliveryType.value)) {
                        holder.my_order_delivery_type.setText(order.order.deliveryType.value);
                    }
                }

                if (order.order.orderStatus != null) {
                    //订单状态
                    if (StringUtil.checkStr(order.order.orderStatus.value)) {
                        holder.pay_state_tv.setText(order.order.orderStatus.value);
                    }
                    holder.go_to_pay_rel.setVisibility(View.GONE);
                    holder.go_to_pay.setOnClickListener(null);
                    holder.change_pay_type.setVisibility(View.GONE);

                    switch (order.order.orderStatus.type) {
                        //如果是待付款1的订单或者是部分付款的2的订单，点击可以去支付
                        case 1:
                        case 2:
                            holder.go_to_pay_rel.setVisibility(View.VISIBLE);
                            holder.go_to_pay.setText("去付款");
                            holder.go_to_pay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (StringUtil.checkStr(order.orderId)) {
                                        Intent intent = new Intent(activity,
                                                PaywayActivity.class);
                                        intent.putExtra("orderId", order.orderId);
                                        intent.putExtra("payType", order.payType);
                                        startActivity(intent);
                                    }
                                }
                            });
                            break;
                        //如果是付款待审核的订单，点击可以去更改支付方式 查看付款信息
                        case 7:
                            holder.go_to_pay_rel.setVisibility(View.VISIBLE);
                            holder.change_pay_type.setVisibility(View.VISIBLE);
                            holder.go_to_pay.setText("查看付款信息");
                            holder.go_to_pay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (StringUtil.checkStr(order.orderId)) {
                                        Handler handler =new Handler(){
                                            @Override
                                            public void handleMessage(Message msg) {
                                                super.handleMessage(msg);
                                                if (msg.what==0){
                                                    Bundle bundle = new Bundle();
                                                    if (order.RSCInfo != null) {
                                                        bundle.putString("companyName", order.RSCInfo.companyName);
                                                        bundle.putString("RSCPhone", order.RSCInfo.RSCPhone);
                                                        bundle.putString("RSCAddress", order.RSCInfo.RSCAddress);
                                                    }
                                                    bundle.putString("orderId", order.orderId);
                                                    IntentUtil.activityForward(activity, OfflinePayActivity.class, bundle, false);
                                                }else {
                                                    page=1;
                                                    getData(page);
                                                }
                                            }
                                        };
                                        OrderUtils.isChecked(handler,order.orderId);
                                    }
                                }
                            });
                            //更改支付方式
                            holder.change_pay_type.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (StringUtil.checkStr(order.orderId)) {
                                        Intent intent = new Intent(activity,
                                                PaywayActivity.class);
                                        intent.putExtra("orderId", order.orderId);
                                        intent.putExtra("payType", order.payType);
                                        startActivity(intent);
                                    }
                                }
                            });
                            break;
                        //如果是配送中的订单，用户可以确认收货
                        case 4:
                            boolean isSure = false;
                            final List<WaitingPay.SKUS> skusList = new ArrayList<>();
                            List<WaitingPay.SKUS> skUsList1 = order.SKUs;
                            if (skUsList1 != null) {

                                for (int i = 0; i < skUsList1.size(); i++) {

                                    WaitingPay.SKUS skus = skUsList1.get(i);
                                    if (skus != null && skus.deliverStatus == 2) {
                                        skusList.add(skus);
                                        isSure = true;
                                    }
                                }

                            }

                            if (isSure) {
                                holder.go_to_pay_rel.setVisibility(View.VISIBLE);
                                holder.go_to_pay.setText("确认收货");
                                holder.go_to_pay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showPopUp(v, skusList);
                                        sureOrderId = order.orderId;
                                    }
                                });
                            }

                            break;
                        //如果是待自提的订单，且有带自提的商品   用户可以去自提

                        case 5:
                            boolean flag = false;
                            if (order.SKUs != null) {
                                for (int i = 0; i < order.SKUs.size(); i++) {
                                    WaitingPay.SKUS skus = order.SKUs.get(i);
                                    if (skus != null && skus.deliverStatus == 4) {
                                        flag = true;
                                    }
                                }
                            }
                            if (flag) {
                                holder.go_to_pay_rel.setVisibility(View.VISIBLE);
                                holder.go_to_pay.setText("去自提");
                                holder.go_to_pay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(activity,
                                                PickUpStateActivity.class);
                                        intent.putExtra("orderId", order.orderId);
                                        startActivity(intent);
                                    }
                                });
                            } else {
                                holder.go_to_pay_rel.setVisibility(View.GONE);
                                holder.go_to_pay.setOnClickListener(null);
                            }
                            break;
                        default:
                            break;
                    }
                }

                //订单合计金额
                if (StringUtil.checkStr(order.order.totalPrice)) {
                    holder.price_tv.setText("¥" + StringUtil.toTwoString(order.order.totalPrice));
                }
            }

            List<WaitingPay.SKUS> SKUsList = order.SKUs;
            List<WaitingPay.Product> goodsList = order.products;

            if (holder.llCommerceContainer.getChildCount() > 0) {
                holder.llCommerceContainer.removeAllViews();
            }
            if (SKUsList != null && !SKUsList.isEmpty()) {

                for (int i = 0; i < SKUsList.size(); i++) {
                    ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.item_item_order_list, null);

                    ViewHolderChild viewHolderChild = new ViewHolderChild(rootView);
                    //商品图片
                    if (StringUtil.checkStr(SKUsList.get(i).thumbnail)) {

                        ImageLoader.getInstance().displayImage(
                                MsgID.IP + SKUsList.get(i).thumbnail, viewHolderChild.ordering_item_img);

                    }
                    //商品个数
                    viewHolderChild.ordering_item_geshu.setText("X " + SKUsList.get(i).count + "");
                    //商品名
                    if (StringUtil.checkStr(SKUsList.get(i).productName)) {
                        viewHolderChild.ordering_item_name.setText(SKUsList.get(i).productName);
                    }

                    //附加选项
                    StringBuilder stringAdditions = new StringBuilder();
                    float car_additions_price = 0;
                    if (SKUsList.get(i).additions != null && !SKUsList.get(i).additions.isEmpty()) {
                        viewHolderChild.additions_lin.setVisibility(View.VISIBLE);
                        stringAdditions.append("附加项目:");
                        for (int k = 0; k < SKUsList.get(i).additions.size(); k++) {
                            if (StringUtil.checkStr(SKUsList.get(i).additions.get(k).name)) {
                                stringAdditions.append(SKUsList.get(i).additions.get(k).name).append(";");
                                car_additions_price += SKUsList.get(i).additions.get(k).price;
                            }
                        }
                        String car_additions = stringAdditions.toString().substring(0, stringAdditions.toString().length() - 1);
                        if (StringUtil.checkStr(car_additions)) {
                            viewHolderChild.additions_text.setText(car_additions);
                            viewHolderChild.additions_price.setText("¥" + StringUtil.toTwoString(car_additions_price + ""));
                        }
                    } else {
                        viewHolderChild.additions_lin.setVisibility(View.GONE);
                    }
                    //商品 单价 阶段 订金 尾款
                    viewHolderChild.ordering_now_pri.setTextColor(getResources().getColor(R.color.orange_goods_price));
                    if (SKUsList.get(i).deposit == 0) {
                        viewHolderChild.goods_car_bar.setVisibility(View.GONE);
                    } else {
                        viewHolderChild.goods_car_bar.setVisibility(View.VISIBLE);
                        String deposit = StringUtil.toTwoString(SKUsList
                                .get(i).deposit * SKUsList.get(i).count + "");
                        if (StringUtil.checkStr(deposit)) {
                            viewHolderChild.goods_car_deposit.setText("¥" + deposit);
                        }
                        String weiKuan = StringUtil.toTwoString((SKUsList.get(i).price + car_additions_price - SKUsList
                                .get(i).deposit) * SKUsList.get(i).count + "");
                        if (StringUtil.checkStr(weiKuan)) {
                            viewHolderChild.goods_car_weikuan.setText("¥" + weiKuan);
                        }
                    }
                    viewHolderChild.ordering_now_pri.setText("¥" + StringUtil.toTwoString(SKUsList
                            .get(i).price + ""));

                    //Sku属性
                    StringBuilder stringSku = new StringBuilder();
                    if (SKUsList.get(i).attributes != null && !SKUsList.get(i).attributes.isEmpty()) {
                        for (int k = 0; k < SKUsList.get(i).attributes.size(); k++) {
                            if (StringUtil.checkStr(SKUsList.get(i).attributes.get(k).name)
                                    && StringUtil.checkStr(SKUsList.get(i).attributes.get(k).value)) {
                                stringSku.append(SKUsList.get(i).attributes.get(k).name).append(":").append(SKUsList.get(i).attributes.get(k).value).append(";");
                            }
                        }
                        String car_attr = stringSku.toString().substring(0, stringSku.toString().length() - 1);
                        if (StringUtil.checkStr(car_attr)) {
                            viewHolderChild.goods_car_attr.setText(car_attr);
                        }
                    }

                    holder.llCommerceContainer.addView(rootView);

                }
                //老的订单
            } else if (goodsList != null && !goodsList.isEmpty()) {

                for (int i = 0; i < goodsList.size(); i++) {
                    ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.item_item_order_list, null);
                    ViewHolderChild viewHolderChild = new ViewHolderChild(rootView);
                    //商品图片
                    if (StringUtil.checkStr(goodsList.get(i).thumbnail)) {
                        ImageLoader.getInstance().displayImage(
                                MsgID.IP + goodsList.get(i).thumbnail, viewHolderChild.ordering_item_img);
                    }
                    //商品个数
                    viewHolderChild.ordering_item_geshu.setText("X " + goodsList.get(i).count + "");
                    //商品名
                    if (StringUtil.checkStr(goodsList.get(i).name)) {
                        viewHolderChild.ordering_item_name.setText(goodsList.get(i).name);
                    }

                    if (goodsList.get(i).deposit == 0) {
                        viewHolderChild.ordering_now_pri.setTextColor(getResources().getColor(R.color.orange_goods_price));
                        viewHolderChild.goods_car_bar.setVisibility(View.GONE);
                    } else {
                        viewHolderChild.ordering_now_pri.setTextColor(getResources().getColor(R.color.black_goods_titile));
                        viewHolderChild.goods_car_bar.setVisibility(View.VISIBLE);
                        String deposit = StringUtil.toTwoString(goodsList
                                .get(i).deposit * goodsList.get(i).count + "");
                        if (StringUtil.checkStr(deposit)) {
                            viewHolderChild.goods_car_deposit.setText("¥" + deposit);
                        }
                        String weiKuan = StringUtil.toTwoString((goodsList.get(i).price - goodsList
                                .get(i).deposit) * goodsList.get(i).count + "");
                        if (StringUtil.checkStr(weiKuan)) {
                            viewHolderChild.goods_car_weikuan.setText("¥" + weiKuan);
                        }
                    }
                    String now_pri = StringUtil.toTwoString(goodsList
                            .get(i).price + "");
                    if (StringUtil.checkStr(now_pri)) {
                        viewHolderChild.ordering_now_pri.setText("¥" + now_pri);
                    }
                    holder.llCommerceContainer.addView(rootView);
                }
            }
            holder.llCommerceContainer.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //点击商品跳转到订单详情界面
                    Intent intent = new Intent(activity,
                            MyOrderDetailActivity.class);
                    intent.putExtra("orderId",
                            order.orderId);
                    intent.putExtra("callByMyOrderListActivity",
                            true);
                    startActivity(intent);

                }
            });

            return convertView;
        }

        class ViewHolder {
            private TextView order_id_tv, pay_state_tv, price_tv, my_order_delivery_type;
            private Button go_to_pay, change_pay_type;
            private RelativeLayout go_to_pay_rel;
            private LinearLayout llCommerceContainer;

            ViewHolder(View convertView) {
                order_id_tv = (TextView) convertView.findViewById(R.id.my_order_id);
                pay_state_tv = (TextView) convertView.findViewById(R.id.my_order_pay_state);
                price_tv = (TextView) convertView.findViewById(R.id.my_order_pay_price);
                my_order_delivery_type = (TextView) convertView.findViewById(R.id.my_order_delivery_type);
                go_to_pay = (Button) convertView.findViewById(R.id.go_to_pay);
                change_pay_type = (Button) convertView.findViewById(R.id.change_pay_type);
                go_to_pay_rel = (RelativeLayout) convertView.findViewById(R.id.go_to_pay_rel);
                ExpandViewTouch.expandViewTouchDelegate(go_to_pay, 50, 50, 50, 50);
                llCommerceContainer = (LinearLayout) convertView.findViewById(R.id.my_order_llCommerceContainer);
            }
        }


        class ViewHolderChild {
            private LinearLayout goods_car_bar, additions_lin;
            private RecyclerImageView ordering_item_img;
            private TextView ordering_now_pri, ordering_item_name, goods_car_deposit,
                    goods_car_weikuan, ordering_item_geshu, goods_car_attr;
            private TextView additions_text, additions_price;

            public ViewHolderChild(View convertView) {
                goods_car_bar = (LinearLayout) convertView.findViewById(R.id.goods_car_item_bar);
                ordering_item_img = (RecyclerImageView) convertView//商品图
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
                goods_car_attr = (TextView) convertView//汽车尾款
                        .findViewById(R.id.ordering_item_attr);
                additions_text = (TextView) convertView//附加选项
                        .findViewById(R.id.additions_text);
                additions_price = (TextView) convertView//附加选项价格
                        .findViewById(R.id.additions_price);
                additions_lin = (LinearLayout) convertView
                        .findViewById(R.id.additions_lin);
            }
        }

    }


    private void showDialogBg(int bg, BgSwitch bgSwitch) {
        bgSwitch.backgroundSwitch(bg);
    }

    public interface BgSwitch {
        void backgroundSwitch(int bg);
    }


    /**
     * 创建PopupWindow
     */
    private void initPopuptWindow() {
        // TODO Auto-generated method stub
        // 获取自定义布局文件activity_popupwindow_left.xml的视图
        @SuppressLint("InflateParams") View popupWindow_view = inflater.inflate(
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
                showDialogBg(1, (MyOrderListActivity) activity);
                checkedMap.clear();
            }
        });

        //初始化组件

        ImageView pop_close = (ImageView) popupWindow_view.findViewById(R.id.pop_close);
        TextView pop_order_title = (TextView) popupWindow_view.findViewById(R.id.pop_order_title);
        pop_order_title.setText("确认收货");
        pop_close.setOnClickListener(this);
        pop_sure = (TextView) popupWindow_view.findViewById(R.id.save);
        pop_sure.setOnClickListener(this);

        pop_listView = (ListView) popupWindow_view.findViewById(R.id.pop_listView);

    }

    //显示popWindow
    private void showPopUp(View parent, List<WaitingPay.SKUS> skusList) {
        if (null != popupWindow) {
            popupWindow.dismiss();
        } else {
            initPopuptWindow();
        }
        pop_sure.setEnabled(false);
        pop_sure.setText("确定");
        //初始化数据
        PopSkusAdapter popSkusAdapter = new PopSkusAdapter(activity, skusList);
        pop_listView.setAdapter(popSkusAdapter);


        //设置背景及展示
        showDialogBg(0, (MyOrderListActivity) activity);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }


    class PopSkusAdapter extends CommonAdapter<WaitingPay.SKUS> {
        private List<WaitingPay.SKUS> list;

        public PopSkusAdapter(Context context, List<WaitingPay.SKUS> data) {
            super(context, data, R.layout.item_pop_sure_order);
            this.list = data;
        }

        @Override
        public void convert(CommonViewHolder holder, final WaitingPay.SKUS skus) {

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
    public int setCheckedGoodsCount(List<WaitingPay.SKUS> list) {
        int count = 0;
        List<String> refList = new ArrayList<>();
        for (String key : checkedMap.keySet()) {
            if (checkedMap.get(key)) {
                refList.add(key);
            }
        }
        for (WaitingPay.SKUS skus : list) {

            if (refList.contains(skus.ref)) {
                count += skus.count;
            }

        }
        return count;
    }


}

