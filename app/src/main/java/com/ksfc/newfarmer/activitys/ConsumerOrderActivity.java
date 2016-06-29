package com.ksfc.newfarmer.activitys;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.common.CommonAdapter;
import com.ksfc.newfarmer.common.CommonViewHolder;
import com.ksfc.newfarmer.common.LoadMoreOnsrcollListener;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.http.ApiType;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.http.RequestParams;
import com.ksfc.newfarmer.http.beans.ConsumerOrderResult;
import com.ksfc.newfarmer.http.beans.InviteeResult;
import com.ksfc.newfarmer.utils.DateFormatUtils;
import com.ksfc.newfarmer.utils.PullToRefreshUtils;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.Utils;
import com.ksfc.newfarmer.widget.LoadingFooter;
import com.ksfc.newfarmer.widget.UnSwipeListView;
import com.ksfc.newfarmer.widget.WidgetUtil;

import java.util.List;

/**
 * Created by HePeng on 2015/12/24.
 */
public class ConsumerOrderActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener {
    private TextView name;
    private TextView phone;
    private PullToRefreshListView listView;
    private InviteeResult.InviteeEntity consumer;
    private int page = 1;
    private TextView consumer_count;
    private OrderAdapter adapter;
    private TextView consumer_address;
    private LoadingFooter loadingFooter;

    @Override
    public int getLayout() {
        return R.layout.activity_consumer_order;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("客户订单");
        initView();
        showProgressDialog();
        getData();

    }

    private void getData() {
        if (consumer != null) {
            RequestParams params = new RequestParams();
            params.put("inviteeId", consumer.userId);
            params.put("page", page);
            params.put("max", 20);
            if (isLogin()) {
                params.put("userId", Store.User.queryMe().userid);
            }
            execApi(ApiType.GET_INVITEE_ORDERS, params);
        }
    }

    private void initView() {
        name = ((TextView) findViewById(R.id.consumer_name));
        phone = ((TextView) findViewById(R.id.consumer_phone));
        consumer_address = (TextView) findViewById(R.id.consumer_address);//所在地区

        listView = ((PullToRefreshListView) findViewById(R.id.consumer_listView));
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.setOnRefreshListener(this);

        listView.setOnScrollListener(moreOnsrcollListener);
        loadingFooter = new LoadingFooter(this, listView.getRefreshableView());
        //设置刷新的文字
        PullToRefreshUtils.setFreshText(listView);
        consumer_count = (TextView) findViewById(R.id.consumer_count);

        consumer = (InviteeResult.InviteeEntity) getIntent().getSerializableExtra("consumer");
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

        setViewClick(R.id.consumer_phone);
        setViewClick(R.id.consumer_phone_icon);


    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.consumer_phone:
            case R.id.consumer_phone_icon:
                if (consumer != null && StringUtil.checkStr(consumer.account)) {
                    Utils.dial(this, consumer.account);
                }
                break;

        }
    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        listView.onRefreshComplete();
        if (req.getApi() == ApiType.GET_INVITEE_ORDERS) {
            ConsumerOrderResult data = (ConsumerOrderResult) req.getData();
            if (data.getStatus().equals("1000")) {
                ConsumerOrderResult.Datas datas = data.datas;
                if (datas != null) {
                    if (datas.total != null) {
                        consumer_count.setText(datas.total);
                    }
                    if (datas.address != null) {

                        String province = "";
                        String city = "";
                        String county = "";
                        String town = "";
                        if (datas.address.province != null) {
                            province = datas.address.province.name;
                        }
                        if (datas.address.city != null) {
                            city = datas.address.city.name;
                        }
                        if (datas.address.county != null) {
                            county = datas.address.county.name;
                        }
                        if (datas.address.town != null) {
                            town = datas.address.town.name;
                        }

                        String address = StringUtil.checkBufferStr
                                (province, city, county, town);
                        if (address.equals("")) {
                            consumer_address.setText("所在地区：");
                        } else {
                            consumer_address.setText("所在地区：" + address);
                        }

                    }

                    List<ConsumerOrderResult.Rows> rows = datas.rows;
                    if (rows != null && !rows.isEmpty()) {
                        loadingFooter.setSize(page, rows.size());
                        if (page == 1) {
                            if (adapter == null) {
                                adapter = new OrderAdapter(this, rows);
                                WidgetUtil.setListViewHeightBasedOnChildren(listView);
                                listView.setAdapter(adapter);
                            } else {
                                adapter.clear();
                                adapter.addAll(rows);
                            }
                        } else {
                            if (adapter != null) {
                                adapter.addAll(rows);
                            }
                        }
                    } else {
                        loadingFooter.setSize(page, 0);
                        if (page == 1) {
                            if (adapter != null) {
                                adapter.clear();
                            }
                        } else {
                            page--;
                        }
                    }
                }
            }


        }

    }


    //外层的适配器
    class OrderAdapter extends CommonAdapter<ConsumerOrderResult.Rows> {


        public OrderAdapter(Context context, List<ConsumerOrderResult.Rows> data) {
            super(context, data, R.layout.item_consumer_order);
        }

        @Override
        public void convert(CommonViewHolder holder, ConsumerOrderResult.Rows rows) {
            if (rows != null) {
                //设置文本
                if (StringUtil.checkStr(rows.totalPrice)) {
                    holder.setText(R.id.consumer_item_orderPrice, "¥" + rows.totalPrice);
                }
                String state = "";
                switch (rows.typeValue) {
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
                    case 4:
                        state = "已完成";
                        break;
                    default:
                        state = "";
                }
                holder.setText(R.id.consumer_item_payType, state);

                if (StringUtil.checkStr(rows.dateCreated)) {
                    holder.setText(R.id.consumer_item_orderTime, "下单时间：" + DateFormatUtils.convertTime(rows.dateCreated));
                }


                UnSwipeListView listView = (UnSwipeListView) holder.getView(R.id.consumer_item_listView);
                if (rows.SKUs != null && !rows.SKUs.isEmpty()) {
                    SkusAdapter carAdapter = new SkusAdapter(ConsumerOrderActivity.this, rows.SKUs);
                    listView.setAdapter(carAdapter);
                    WidgetUtil.setListViewHeightBasedOnChildren(listView);
                } else {
                    if (rows.products != null && !rows.products.isEmpty()) {
                        ProductsAdapter carAdapter = new ProductsAdapter(ConsumerOrderActivity.this, rows.products);
                        listView.setAdapter(carAdapter);
                        WidgetUtil.setListViewHeightBasedOnChildren(listView);
                    } else {
                        listView.setAdapter(null);
                    }
                }

            }
        }
    }

    //内层的商品列表，已订单区分(兼容老的商品)
    class ProductsAdapter extends CommonAdapter<ConsumerOrderResult.Product> {

        public ProductsAdapter(Context context, List<ConsumerOrderResult.Product> data) {
            super(context, data, R.layout.item_item_consumer_order);
        }

        @Override
        public void convert(CommonViewHolder holder, ConsumerOrderResult.Product product) {
            if (product != null) {
                //设置文本
                if (StringUtil.checkStr(product.name)) {
                    holder.setText(R.id.product_name, product.name);
                }
                holder.setText(R.id.product_count, "X " + product.count);
            }

        }
    }

    //内层的商品列表，已订单区分(兼容老的商品)
    class SkusAdapter extends CommonAdapter<ConsumerOrderResult.SKUS> {

        public SkusAdapter(Context context, List<ConsumerOrderResult.SKUS> data) {
            super(context, data, R.layout.item_item_consumer_order);
        }

        @Override
        public void convert(CommonViewHolder holder, ConsumerOrderResult.SKUS skus) {
            if (skus != null) {
                //设置文本
                if (StringUtil.checkStr(skus.productName)) {
                    holder.setText(R.id.product_name, skus.productName);
                }
                holder.setText(R.id.product_count, "X " + skus.count);
            }

        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        PullToRefreshUtils.setFreshClose(refreshView);
        page = 1;
        getData();
    }


    private LoadMoreOnsrcollListener moreOnsrcollListener = new LoadMoreOnsrcollListener() {
        @Override
        public void loadMore() {
            //加载更多
            if (loadingFooter.getState() == LoadingFooter.State.Idle) {
                loadingFooter.setState(LoadingFooter.State.Loading);
                page++;
                getData();
            }
        }
    };


}
