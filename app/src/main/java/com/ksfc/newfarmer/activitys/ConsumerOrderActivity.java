package com.ksfc.newfarmer.activitys;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.ResponseResult;
import com.ksfc.newfarmer.protocol.beans.ConsumerOrderResult;
import com.ksfc.newfarmer.protocol.beans.InviteeResult;
import com.ksfc.newfarmer.utils.DateFormatUtils;
import com.ksfc.newfarmer.utils.PullToRefreshUtils;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.WidgetUtil;

import java.util.Collection;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by CAI on 2015/12/24.
 */
public class ConsumerOrderActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2 {
    private TextView name;
    private TextView phone;
    private PullToRefreshListView listView;
    private InviteeResult.Invitee consumer;
    private int page = 1;
    private TextView consumer_count;
    private OrderAdapter adapter;

    @Override
    public int getLayout() {
        return R.layout.consumer_order_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("客户订单");
        initView();
        getData();

    }

    private void getData() {
        if (consumer != null) {
            RequestParams params = new RequestParams();
            params.put("inviteeId", consumer.userId);
            params.put("page", page);
            params.put("max", 20);
            params.put("userId", Store.User.queryMe().userid);
            execApi(ApiType.GET_INVITEE_ORDERS, params);
        }
    }

    private void initView() {
        name = ((TextView) findViewById(R.id.consumer_name));
        phone = ((TextView) findViewById(R.id.consumer_phone));
        listView = ((PullToRefreshListView) findViewById(R.id.consumer_listView));
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        //设置刷新的文字
        PullToRefreshUtils.setFreshText(listView);
        consumer_count = (TextView) findViewById(R.id.consumer_count);

        consumer = (InviteeResult.Invitee) getIntent().getSerializableExtra("consumer");
        if (consumer != null) {
            if (!StringUtil.empty(consumer.name)) {
                name.setText("姓名：" + consumer.name);
            } else {
                name.setText("姓名：该好友未填写姓名");
            }

            if (!StringUtil.empty(consumer.account)) {
                phone.setText("手机号：" + consumer.account);
            }
        }

    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {
        listView.onRefreshComplete();
        if (req.getApi() == ApiType.GET_INVITEE_ORDERS) {
            ConsumerOrderResult data = (ConsumerOrderResult) req.getData();
            if (data.getStatus().equals("1000")) {
                ConsumerOrderResult.Datas datas = data.datas;
                if (datas != null) {
                    if (datas.total != null) {
                        consumer_count.setText(datas.total);
                    }
                    List<ConsumerOrderResult.Rows> rows = datas.rows;
                    if (rows != null) {
                        if (rows.size() > 0) {
                            if (page == 1) {
                                if (adapter == null) {
                                    adapter = new OrderAdapter(rows);
                                    WidgetUtil.setListViewHeightBasedOnChildren(listView);
                                    listView.setAdapter(adapter);
                                } else {
                                    adapter.clear();
                                    adapter.addAll(rows);
                                }
                            } else {
                                adapter.addAll(rows);
                            }
                        } else {
                            showToast("没有更多订单");
                        }
                    } else {
                        showToast("没有更多订单");
                    }
                }
            }


        }

    }

    //上拉 下拉刷新
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        page = 1;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        page++;
        getData();
    }

    //外层的适配器
    class OrderAdapter extends BaseAdapter {

        private List<ConsumerOrderResult.Rows> list;

        public OrderAdapter(List<ConsumerOrderResult.Rows> list) {
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

        public void clear() {
            list.clear();
            notifyDataSetChanged();
        }

        public void addAll(Collection<? extends ConsumerOrderResult.Rows> collection) {
            list.addAll(collection);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.consumer_order_item, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (StringUtil.checkStr(list.get(position).totalPrice)) {
                holder.orderPrice.setText("¥" + list.get(position).totalPrice);
            }
            String state = "";
            switch (list.get(position).typeValue) {
                case 0:
                    state = "交易关闭";
                    break;
                case 1:
                    state = "待付款";
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
            holder.orderType.setText(state);

            if (StringUtil.checkStr(list.get(position).dateCreated)) {
                holder.orderTime.setText("下单时间：" + DateFormatUtils.convertTime(list.get(position).dateCreated));
            }

            if (list.get(position).products != null) {
                if (list.get(position).products.size() > 0) {
                    ProductsAdapter carAdapter = new ProductsAdapter(list.get(position).products);
                    holder.listView.setAdapter(carAdapter);
                    WidgetUtil.setListViewHeightBasedOnChildren(holder.listView);
                }
            }


            return convertView;
        }

        class ViewHolder {
            private TextView orderTime, orderType, orderPrice;
            private ListView listView;

            public ViewHolder(View convertView) {
                orderTime = (TextView) convertView.findViewById(R.id.consumer_item_orderTime);
                orderType = (TextView) convertView.findViewById(R.id.consumer_item_payType);
                orderPrice = (TextView) convertView.findViewById(R.id.consumer_item_orderPrice);
                listView = (ListView) convertView.findViewById(R.id.consumer_item_listView);
            }
        }
    }

    //内层的商品列表，已订单区分
    class ProductsAdapter extends BaseAdapter {
        private List<ConsumerOrderResult.Product> list;

        public ProductsAdapter(List<ConsumerOrderResult.Product> products) {
            this.list = products;
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
                convertView = getLayoutInflater().inflate(R.layout.consumer_order_item_tem, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (list.get(position) != null) {
                if (StringUtil.checkStr(list.get(position).name)) {
                    holder.productName.setText(list.get(position).name);
                }
                holder.productCount.setText("X " + list.get(position).count);
            }
            return convertView;
        }

        class ViewHolder {
            private TextView productName, productCount;

            public ViewHolder(View convertView) {
                productName = (TextView) convertView.findViewById(R.id.product_name);
                productCount = (TextView) convertView.findViewById(R.id.product_count);
            }
        }
    }

    public String formatDate(String time) {
        String substring = time.substring(0, 19);
        return substring.replace("T", " ");
    }

}
