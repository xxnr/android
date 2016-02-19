package com.ksfc.newfarmer.fragment;

import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.sax.RootElement;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.MyOrderDetailActivity;
import com.ksfc.newfarmer.activitys.PaywayActivity;
import com.ksfc.newfarmer.activitys.ShangpinListActivity;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.LoginResult;
import com.ksfc.newfarmer.protocol.beans.WaitingPay;
import com.ksfc.newfarmer.utils.ExpandViewTouch;
import com.ksfc.newfarmer.utils.PullToRefreshUtils;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.RecyclerImageView;
import com.ksfc.newfarmer.widget.UnSwipeListView;
import com.ksfc.newfarmer.widget.WidgetUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import net.yangentao.util.msg.MsgCenter;
import net.yangentao.util.msg.MsgListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by CAI on 2015/12/3.
 */
public class MyDetailFragment extends BaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private PullToRefreshListView waitingpay_lv;
    private int page = 1;
    private int TYPE = 0;//订单类型 1:未付款 2:待发货 3: 已发货 4:已完成
    private OrderAdapter adapter;
    private List<WaitingPay.Orders> list;
    private RelativeLayout null_layout;

    @Override
    public View InItView() {
        View view = inflater.inflate(R.layout.waitingpaylistview_layout, null);
        waitingpay_lv = (PullToRefreshListView) view.findViewById(R.id.waitingpay_lv);

        waitingpay_lv.setMode(PullToRefreshBase.Mode.BOTH);
        waitingpay_lv.setOnRefreshListener(this);
        //设置刷新的文字
        PullToRefreshUtils.setFreshText(waitingpay_lv);
        //无订单下的状态
        null_layout = ((RelativeLayout) view.findViewById(R.id.null_shop_cart_layout));
        view.findViewById(R.id.my_login_sure).setOnClickListener(this);
        view.findViewById(R.id.my_login_cancel).setOnClickListener(this);

        Bundle bundle = getArguments();
        TYPE = bundle.getInt("TYPE", 0);
        list = new ArrayList<>();
        //刷新时候的只出现一个
        if (TYPE == 0) {
            showProgressDialog();
        }

        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                page = 1;
                getData(page);
            }
        }, "Pay_success");

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
        if (userInfo!=null){
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
                    if (page == 1) {
                        if (adapter == null) {
                            adapter = new OrderAdapter(rows);
                            WidgetUtil.setListViewHeightBasedOnChildren(waitingpay_lv);
                            waitingpay_lv.setAdapter(adapter);
                        } else {
                            adapter.clear();
                            adapter.addAll(rows);
                        }
                    } else {
                        adapter.addAll(rows);
                    }
                } else {
                    if (page == 1) {
                        null_layout.setVisibility(View.VISIBLE);
                    } else {
                        page--;
                        (activity).showToast("暂时没有数据");
                    }
                }
            } else {
                (activity).showToast("暂时没有数据");
            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_login_sure:
                Intent intent = new Intent(getActivity(),
                        ShangpinListActivity.class);
                intent.putExtra("goods", "huafei");
                startActivity(intent);
                break;
            case R.id.my_login_cancel:
                Intent intent1 = new Intent(getActivity(),
                        ShangpinListActivity.class);
                intent1.putExtra("goods", "qiche");
                startActivity(intent1);
                break;


        }
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.my_order_list_item, null);
                convertView.setTag(new ViewHolder(convertView));
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            final WaitingPay.Orders order = rows.get(position);
            if (StringUtil.checkStr(order.orderId)) {
                holder.order_id_tv.setText("订单号：" + order.orderId);
            }


            if (order.order != null) {

                if (order.order.orderStatus != null) {
                    //订单状态
                    if (StringUtil.checkStr(order.order.orderStatus.value)) {
                        holder.pay_state_tv.setText(order.order.orderStatus.value);
                    }
                    //如果是待付款1的订单或者是部分付款的2的订单，点击可以去支付
                    if (order.order.orderStatus.type == 1 || order.order.orderStatus.type == 2) {
                        holder.go_to_pay_rel.setVisibility(View.VISIBLE);
                        holder.go_to_pay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (StringUtil.checkStr(order.orderId)) {
                                    Intent intent = new Intent(getActivity(),
                                            PaywayActivity.class);
                                    intent.putExtra("orderId", order.orderId);
                                    intent.putExtra("payType", order.payType);
                                    startActivity(intent);
                                }
                            }
                        });
                    } else {
                        holder.go_to_pay_rel.setVisibility(View.GONE);
                        holder.go_to_pay.setOnClickListener(null);
                    }

                }

                //订单合计金额
                if (StringUtil.checkStr(order.order.totalPrice)) {
                    holder.price_tv.setText("¥" + order.order.totalPrice);
                }


            }

            //子商品列表
            if (order.SKUs != null && !order.SKUs.isEmpty()) {
                ProductAdapter carAdapter = new ProductAdapter(order, order.SKUs);
                holder.my_order_list.setAdapter(carAdapter);
            } else {
                ProductAdapter carAdapter = new ProductAdapter(order.products, order);
                holder.my_order_list.setAdapter(carAdapter);
            }
//            设置ListView子listView的高度，避免只显示一个listView item 但是比较耗内存
            WidgetUtil.setListViewHeightBasedOnChildren(holder.my_order_list);
            return convertView;
        }

        class ViewHolder {
            private TextView order_id_tv, pay_state_tv, price_tv;
            private Button go_to_pay;
            private UnSwipeListView my_order_list;
            private RelativeLayout go_to_pay_rel;

            ViewHolder(View convertView) {
                order_id_tv = (TextView) convertView.findViewById(R.id.my_order_id);
                pay_state_tv = (TextView) convertView.findViewById(R.id.my_order_pay_state);
                price_tv = (TextView) convertView.findViewById(R.id.my_order_pay_price);
                go_to_pay = (Button) convertView.findViewById(R.id.go_to_pay);
                my_order_list = (UnSwipeListView) convertView.findViewById(R.id.my_order_list);
                go_to_pay_rel = (RelativeLayout) convertView.findViewById(R.id.go_to_pay_rel);
                ExpandViewTouch.expandViewTouchDelegate(go_to_pay, 50, 50, 50, 50);
            }
        }

    }


    //内层的商品列表，已订单区分
    public class ProductAdapter extends BaseAdapter {
        private List<WaitingPay.Product> goodsList;
        private List<WaitingPay.SKUS> SKUsList;
        private WaitingPay.Orders order;

        public ProductAdapter(WaitingPay.Orders order, List<WaitingPay.SKUS> SKUsList) {
            this.SKUsList = SKUsList;
            this.order = order;
        }

        public ProductAdapter(List<WaitingPay.Product> goodsList, WaitingPay.Orders order) {
            this.goodsList = goodsList;
            this.order = order;
        }


        @Override
        public int getCount() {
            if (SKUsList != null && !SKUsList.isEmpty()) {
                return SKUsList.size() > 0 ? SKUsList.size() : 0;
            } else {
                return goodsList.size() > 0 ? goodsList.size() : 0;
            }
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            if (SKUsList != null && !SKUsList.isEmpty()) {
                return SKUsList.size();
            } else {
                return goodsList.size();
            }
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        class ViewHolder {
            private LinearLayout goods_car_bar, additions_lin;
            private RecyclerImageView ordering_item_img;
            private TextView ordering_now_pri, ordering_item_name, goods_car_deposit,
                    goods_car_weikuan, ordering_item_geshu, goods_car_attr;
            private TextView additions_text, additions_price;

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

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater
                        .inflate(R.layout.order_list_item_item, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();

            if (SKUsList != null && !SKUsList.isEmpty()) {//新订单
                //商品图片
                if (StringUtil.checkStr(SKUsList.get(position).thumbnail)) {
                    Picasso.with(getActivity())
                            .load(MsgID.IP + SKUsList.get(position).thumbnail)
                            .error(R.drawable.error)
                            .placeholder(R.drawable.zhanweitu)
                            .into(holder.ordering_item_img);

                }
                //商品个数
                holder.ordering_item_geshu.setText("X " + SKUsList.get(position).count + "");
                //商品名
                if (StringUtil.checkStr(SKUsList.get(position).productName)) {
                    holder.ordering_item_name.setText(SKUsList.get(position).productName);
                }

                //附加选项
                StringBuilder stringAdditions = new StringBuilder();
                float car_additions_price = 0;
                if (SKUsList.get(position).additions != null && !SKUsList.get(position).additions.isEmpty()) {
                    holder.additions_lin.setVisibility(View.VISIBLE);
                    stringAdditions.append("附加项目:");
                    for (int k = 0; k < SKUsList.get(position).additions.size(); k++) {
                        if (StringUtil.checkStr(SKUsList.get(position).additions.get(k).name)) {
                            stringAdditions.append(SKUsList.get(position).additions.get(k).name + ";");
                            car_additions_price += SKUsList.get(position).additions.get(k).price;
                        }
                    }
                    String car_additions = stringAdditions.toString().substring(0, stringAdditions.toString().length() - 1);
                    if (StringUtil.checkStr(car_additions)) {
                        holder.additions_text.setText(car_additions);
                        holder.additions_price.setText("¥" + StringUtil.toTwoString(car_additions_price + ""));
                    }
                } else {
                    holder.additions_lin.setVisibility(View.GONE);
                }
                //商品 单价 阶段 订金 尾款
                holder.ordering_now_pri.setTextColor(getResources().getColor(R.color.orange_goods_price));
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
                StringBuilder stringSku = new StringBuilder();
                if (SKUsList.get(position).attributes != null && !SKUsList.get(position).attributes.isEmpty()) {
                    for (int k = 0; k < SKUsList.get(position).attributes.size(); k++) {
                        if (StringUtil.checkStr(SKUsList.get(position).attributes.get(k).name)
                                && StringUtil.checkStr(SKUsList.get(position).attributes.get(k).value)) {
                            stringSku.append(SKUsList.get(position).attributes.get(k).name + ":")
                                    .append(SKUsList.get(position).attributes.get(k).value + ";");
                        }
                    }
                    String car_attr = stringSku.toString().substring(0, stringSku.toString().length() - 1);
                    if (StringUtil.checkStr(car_attr)) {
                        holder.goods_car_attr.setText(car_attr);
                    }
                }


            } else {  //兼容老订单
                //商品图片
                if (StringUtil.checkStr(goodsList.get(position).thumbnail)) {
                    ImageLoader.getInstance().displayImage(
                            MsgID.IP + goodsList.get(position).thumbnail, holder.ordering_item_img);
                }
                //商品个数
                holder.ordering_item_geshu.setText("X " + goodsList.get(position).count + "");
                //商品名
                if (StringUtil.checkStr(goodsList.get(position).name)) {
                    holder.ordering_item_name.setText(goodsList.get(position).name);
                }

                if (goodsList.get(position).deposit == 0) {
                    holder.ordering_now_pri.setTextColor(getResources().getColor(R.color.orange_goods_price));
                    holder.goods_car_bar.setVisibility(View.GONE);
                } else {
                    holder.ordering_now_pri.setTextColor(getResources().getColor(R.color.black_goods_titile));
                    holder.goods_car_bar.setVisibility(View.VISIBLE);
                    String deposit = StringUtil.toTwoString(goodsList
                            .get(position).deposit * goodsList.get(position).count + "");
                    if (StringUtil.checkStr(deposit)) {
                        holder.goods_car_deposit.setText("¥" + deposit);
                    }
                    String weiKuan = StringUtil.toTwoString((goodsList.get(position).price - goodsList
                            .get(position).deposit) * goodsList.get(position).count + "");
                    if (StringUtil.checkStr(weiKuan)) {
                        holder.goods_car_weikuan.setText("¥" + weiKuan);
                    }
                }

                String now_pri = StringUtil.toTwoString(goodsList
                        .get(position).price + "");
                if (StringUtil.checkStr(now_pri)) {
                    holder.ordering_now_pri.setText("¥" + now_pri);
                }
            }

            //点击商品跳转到订单详情界面
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),
                            MyOrderDetailActivity.class);
                    intent.putExtra("orderId",
                            order.orderId);
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (TYPE == 1 || TYPE == 0) {
//            page = 1;
//            getData(page);
//        }
    }
}