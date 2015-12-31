package com.ksfc.newfarmer.fragment;

import android.content.Intent;
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
import com.ksfc.newfarmer.protocol.beans.WaitingPay;
import com.ksfc.newfarmer.utils.ExpandViewTouch;
import com.ksfc.newfarmer.utils.PullToRefreshUtils;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.PullToRefreshView;
import com.ksfc.newfarmer.widget.UnSwipeListView;
import com.ksfc.newfarmer.widget.WidgetUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by CAI on 2015/12/3.
 */
public class MyDetailFragment extends BaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private PullToRefreshListView waitingpay_lv;
    private int page = 1;
    private int TYPE;//订单类型 1:未付款 2:待发货 3: 已发货 4:已完成
    private String typeValue = "";
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
        TYPE = bundle.getInt("TYPE");
        list = new ArrayList<>();
        getData(page);
        return view;
    }

    /**
     * 数据请求
     * @param page
     */
    @SuppressWarnings("unused")
    private void getData(int page) {
        RequestParams params = new RequestParams();
        params.put("userId", Store.User.queryMe().userid);
        params.put("page", page);
        if (TYPE != 0) {
            params.put("typeValue", TYPE);
        }
        execApi(ApiType.GETORDERLIST, params);
    }

    @Override
    public void onResponsed(Request req) {
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
        page = 1;
        getData(page);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {


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
            holder.order_id_tv.setText("订单号：" + order.orderId);
            //判断支付状态
            switch (order.typeValue) {
                case 0:
                    typeValue = "交易关闭";
                    break;
                case 1:
                    typeValue = "待付款";
                    break;
                case 2:
                    typeValue = "待发货";
                    break;
                case 3:
                    typeValue = "已发货";
                    break;
                case 4:
                    typeValue = "已完成";
                    break;
                default:
            }
            holder.pay_state_tv.setText(typeValue);
            //支付类型
            if (order.payType == 1) {
                holder.pay_type_tv.setText("支付宝付款");
            } else if (order.payType == 2) {
                holder.pay_type_tv.setText("银联支付");
            }
            holder.price_tv.setText("¥" + order.deposit);
            //如果是待付款的订单，点击可以去支付
            if (order.typeValue == 1) {
                holder.go_to_pay_rel.setVisibility(View.VISIBLE);
                holder.go_to_pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(),
                                PaywayActivity.class);
                        intent.putExtra("orderInfo", order);
                        startActivity(intent);
                    }
                });
            } else {
                holder.go_to_pay_rel.setVisibility(View.GONE);
                holder.go_to_pay.setOnClickListener(null);
            }
            ProductAdapter carAdapter = new ProductAdapter(order.products, order);
            holder.my_order_list.setAdapter(carAdapter);
            WidgetUtil.setListViewHeightBasedOnChildren(holder.my_order_list);
            return convertView;
        }

        class ViewHolder {
            private TextView order_id_tv, pay_state_tv, pay_type_tv, price_tv;
            private Button go_to_pay;
            private UnSwipeListView my_order_list;
            private RelativeLayout go_to_pay_rel;

            ViewHolder(View convertView) {
                order_id_tv = (TextView) convertView.findViewById(R.id.my_order_id);
                pay_state_tv = (TextView) convertView.findViewById(R.id.my_order_pay_state);
                pay_type_tv = (TextView) convertView.findViewById(R.id.my_order_pay_type);
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
        private WaitingPay.Orders order;

        public ProductAdapter(List<WaitingPay.Product> goodsList, WaitingPay.Orders order) {
            this.goodsList = goodsList;
            this.order = order;

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
                convertView = inflater
                        .inflate(R.layout.order_list_item_item, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            ImageLoader.getInstance().displayImage(
                    MsgID.IP + goodsList.get(position).thumbnail, holder.ordering_item_img);
            holder.ordering_item_geshu.setText("X " + goodsList.get(position).count + "");
            holder.ordering_item_name.setText(goodsList.get(position).name);
            if (goodsList.get(position).deposit == 0) {
                holder.ordering_now_pri.setTextColor(getResources().getColor(R.color.orange_goods_price));
                holder.goods_car_bar.setVisibility(View.GONE);
            } else {
                holder.ordering_now_pri.setTextColor(getResources().getColor(R.color.black_goods_titile));
                holder.goods_car_bar.setVisibility(View.VISIBLE);
                holder.goods_car_deposit.setText("¥" + StringUtil.toTwoString(goodsList
                        .get(position).deposit + ""));
                holder.goods_car_weikuan.setText("¥" + StringUtil.toTwoString(goodsList.get(position).price - goodsList
                        .get(position).deposit + ""));
            }
            holder.ordering_now_pri.setText("¥" + StringUtil.toTwoString(goodsList
                    .get(position).price + ""));

            //点击商品跳转到订单详情界面
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),
                            MyOrderDetailActivity.class);
                    intent.putExtra("orderId",
                            order.orderId);
                    intent.putExtra("orderNo",
                            order.orderNo);
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }
}