package com.ksfc.newfarmer.activitys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.RndApplication;

import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.db.dao.ShoppingDao;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.AddOrderResult;
import com.ksfc.newfarmer.protocol.beans.AddressList;
import com.ksfc.newfarmer.protocol.beans.GetshopCart;
import com.ksfc.newfarmer.protocol.beans.AddressList.Address;
import com.ksfc.newfarmer.protocol.beans.GetshopCart.Goods;
import com.ksfc.newfarmer.protocol.beans.GetshopCart.shopCart;

import com.ksfc.newfarmer.protocol.beans.SureOrder.OrderSubList;
import com.ksfc.newfarmer.protocol.beans.SureOrderResult;
import com.ksfc.newfarmer.protocol.beans.SureOrderResult.Datas;
import com.ksfc.newfarmer.protocol.beans.SureOrderResult.Rows;
import com.ksfc.newfarmer.protocol.beans.WaitingPay.Orders;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.SPUtils;
import com.ksfc.newfarmer.utils.StringUtil;

import com.ksfc.newfarmer.widget.WidgetUtil;
import com.ksfc.newfarmer.widget.wheel.AbstractWheelTextAdapter;
import com.ksfc.newfarmer.widget.wheel.WheelView;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.yangentao.util.app.App;
import net.yangentao.util.msg.MsgCenter;
import net.yangentao.util.msg.MsgListener;

import org.json.JSONObject;

public class OrderDetailActivity extends BaseActivity {

    private TextView goods_sum_price, name_phone_tv, order_detail_address_tv;
    private ListView order_shangpin_list;
    private String price_sum;
    private List<Address> rows;
    Data data = null;
    private orderListAdapter adapter;
    private View head_layout;
    private View head_layout_none;
    private String jsonStr;
    private TextView ordering_go_bt_tv;


    static class Data {
        List<Category> category;

        static class Category {
            String title;
            List<Goods> goods;
        }

        static class Goods {
            String id;
            String name;
            String pic;
            int num;
            float yuangjia;
            float xianjia;
            float jifen;
            float dingjin;
        }
    }

    private static Address selectedAddress = new Address();

    @Override
    public int getLayout() {
        return R.layout.order_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        App.getApp().addActivity(this);
        RndApplication.tempDestroyActivityList.add(OrderDetailActivity.this);
        data = new Data();
        data.category = new ArrayList<Data.Category>();
        setTitle("提交订单");
        initView();
    }

    private void initView() {
        head_layout = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.orderdetail_head_layout, null);
        head_layout_none = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.orderdetail_head_layout_none, null);
        goods_sum_price = (TextView) findViewById(R.id.order_sum_pri);
        order_shangpin_list = (ListView) findViewById(R.id.order_shangpin_list);
        ordering_go_bt_tv = (TextView) findViewById(R.id.ordering_go_bt_tv);
        //headView
        RelativeLayout address_shouhuo_ll = (RelativeLayout) head_layout.findViewById(R.id.address_shouhuo_ll);
        //headView_none
        RelativeLayout address_shouhuo_ll_none = (RelativeLayout) head_layout_none.findViewById(R.id.address_shouhuo_ll);
        name_phone_tv = (TextView) head_layout.findViewById(R.id.order_detail_name_tv);
        order_detail_address_tv = (TextView) head_layout.findViewById(R.id.order_detail_address_tv);
        address_shouhuo_ll.setOnClickListener(this);
        address_shouhuo_ll_none.setOnClickListener(this);
        // 或的地区地址
        getAddress();
        // 获得商品列表
        initData();
        // 显示地址
        MsgCenter.addListener(new MsgListener() {// 收货人信息
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                Address str = (Address) args[0];
                if (str == null) {
                    order_detail_address_tv.setText("");
                    order_shangpin_list.removeHeaderView(head_layout);
                    order_shangpin_list.removeHeaderView(head_layout_none);
                    getAddress();
                } else {
                    order_shangpin_list.removeHeaderView(head_layout_none);
                    order_shangpin_list.removeHeaderView(head_layout);
                    order_shangpin_list.addHeaderView(head_layout);
                    String name = str.receiptPeople + "  " + str.receiptPhone;
                    selectedAddress = str;

                    order_detail_address_tv.setText(StringUtil.checkBufferStrWithSpace(str.areaName, str.cityName
                            , str.countyName
                            , str.townName
                            , str.address));

                    name_phone_tv.setText(name);
                }
            }
        }, "MSG.ADDRESS.CALL.BACK");
    }

    private void initData() {
        Intent intent = getIntent();
        int flags = intent.getFlags();
        if (flags == 1) {
            String count = intent.getStringExtra("count");
            String goodsId = intent.getStringExtra("goodId");
            List<Map<String, String>> goodsList = new ArrayList<Map<String, String>>();
            Map<String, String> map = new HashMap<String, String>();
            map.put("productId", goodsId);
            map.put("count", count);
            goodsList.add(map);
            jsonStr = JSONArray.toJSONString(goodsList);
            RequestParams params = new RequestParams();
            params.put("products", jsonStr);
            execApi(ApiType.GET_LOCAL_SHOPCART_LIST, params);
        } else if (flags == 2) {
            List<Map<String, String>> goodsList = (List<Map<String, String>>) intent.getSerializableExtra("goodsList");
            jsonStr = JSONArray.toJSONString(goodsList);
            RequestParams params = new RequestParams();
            params.put("products", jsonStr);
            execApi(ApiType.GET_LOCAL_SHOPCART_LIST, params);
        }
    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {

            case R.id.address_shouhuo_ll:
                Intent i = new Intent(OrderDetailActivity.this,
                        ChooseAddressActivity.class);
                i.putExtra("state", selectedAddress);
                startActivity(i);
                break;
            case R.id.ordering_go_bt:// 订单确认按钮
                if (TextUtils.isEmpty(order_detail_address_tv.getText().toString().trim())) {
                    showToast("收货地址不能为空");
                    return;
                }
                // add order:
                String jsonStr_new = jsonStr.replaceAll("productId", "id");
                showProgressDialog();
                RequestParams params = new RequestParams();
                params.put("userId", Store.User.queryMe().userid);
                params.put("addressId", selectedAddress.addressId);
                params.put("shopCartId", (String) SPUtils.get(OrderDetailActivity.this,
                        "shopCartId", ""));
                params.put("products", jsonStr_new);
                execApi(ApiType.ADD_ORDER, params);
                break;
            default:
                break;
        }
    }

    /**
     * 获得小计总额
     */
    private void getSmallPrice(int position, TextView smallPrice) {
        float price = 0;
        for (int j = 0; j < data.category.get(position).goods.size(); j++) {
            Data.Goods good = data.category.get(position).goods.get(j);
            // 判断是汽车还是化肥，如果是汽车dingjin应该是0
            if (data.category.get(position).goods.get(j).dingjin != 0) {
                price += good.num
                        * good.dingjin;
            } else {
                price += good.num
                        * good.xianjia;
            }
        }
        smallPrice.setText("¥" + StringUtil.toTwoString(price + ""));
    }


    /**
     * 获得商品总额
     */
    private float getTotalPrice() {
        float price = 0;
        for (int i = 0; i < data.category.size(); i++) {
            for (int j = 0; j < data.category.get(i).goods.size(); j++) {
                Data.Goods good = data.category.get(i).goods.get(j);
                // 判断是汽车还是化肥，如果是汽车dingjin应该是0
                if (data.category.get(i).goods.get(j).dingjin != 0) {
                    price += good.num
                            * good.dingjin;
                } else {
                    price += good.num
                            * good.xianjia;
                }
            }
        }
        goods_sum_price.setText("¥" + StringUtil.toTwoString(price + ""));
        return price;
    }

    //外层商品列表

    public class orderListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if (data.category != null) {
                return data.category.size() > 0 ? data.category.size() : 0;
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (data.category != null) {
                return data.category.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(OrderDetailActivity.this)
                        .inflate(R.layout.order_list_item, null);
                convertView.setTag(new ViewHolder(convertView));
            }


            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.car_name.setText(data.category.get(position).title);
            getSmallPrice(position, holder.small_price_tv);
            carAdapter carAdapter = new carAdapter(data.category.get(position).goods);
            holder.car_list.setAdapter(carAdapter);
            WidgetUtil.setListViewHeightBasedOnChildren(holder.car_list);
            return convertView;
        }

        class ViewHolder {
            private TextView car_name, small_price_tv;
            private ListView car_list;

            public ViewHolder(View convertView) {
                car_name = (TextView) convertView.findViewById(R.id.car_name);
                small_price_tv = (TextView) convertView.findViewById(R.id.good_list_small_price);
                car_list = (ListView) convertView.findViewById(R.id.car_list);
            }
        }

    }

    //内层的商品列表，已店铺区分
    public class carAdapter extends BaseAdapter {

        private List<Data.Goods> goodsList;

        public carAdapter(List<Data.Goods> goodsList) {
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
                convertView = LayoutInflater.from(OrderDetailActivity.this)
                        .inflate(R.layout.order_list_item_item, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            ImageLoader.getInstance().displayImage(
                    MsgID.IP + goodsList.get(position).pic, holder.ordering_item_img);
            holder.ordering_item_geshu.setText("X " + goodsList.get(position).num + "");
            holder.ordering_item_name.setText(goodsList.get(position).name);
            if (goodsList.get(position).dingjin == 0) {
                holder.ordering_now_pri.setTextColor(getResources().getColor(R.color.orange_goods_price));
                holder.goods_car_bar.setVisibility(View.GONE);
            } else {
                holder.ordering_now_pri.setTextColor(getResources().getColor(R.color.black_goods_titile));
                holder.goods_car_bar.setVisibility(View.VISIBLE);
                holder.goods_car_deposit.setText("¥" + StringUtil.toTwoString(goodsList
                        .get(position).dingjin + ""));
                holder.goods_car_weikuan.setText("¥" + StringUtil.toTwoString(goodsList.get(position).xianjia - goodsList
                        .get(position).dingjin + ""));
            }
            holder.ordering_now_pri.setText("¥" + StringUtil.toTwoString(goodsList
                    .get(position).xianjia + ""));
            return convertView;
        }

    }


    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.ADD_ORDER) {
            AddOrderResult data = (AddOrderResult) req.getData();
            Orders order = new Orders();
            //自己计算的总价
            price_sum = StringUtil.toTwoString(getTotalPrice() + "");
            order.orderId = data.id;
            order.orderNo = data.paymentId;
            order.deposit = data.deposit;
            Intent intent = new Intent(OrderDetailActivity.this,
                    PaywayActivity.class);
            intent.putExtra("orderInfo", order);
            startActivity(intent);
        } else if (ApiType.ADDRESS_LIST == req.getApi()) {// 添加地址
            AddressList data = (AddressList) req.getData();
            rows = data.datas.rows;
            if (rows.size() > 0) {
                String name = rows.get(0).receiptPeople + "  " + rows.get(0).receiptPhone;
                String areaNameDetail = rows.get(0).address;
                String areaName = rows.get(0).areaName;
                String cityName = rows.get(0).cityName;
                String countyName = rows.get(0).countyName;
                String townName = rows.get(0).townName;
                selectedAddress = rows.get(0);
                if (townName == null || townName.equals("undefined")) {
                    order_detail_address_tv.setText(areaName + " " + cityName + " " + countyName + " " + areaNameDetail);
                } else {
                    order_detail_address_tv.setText(areaName + " " + cityName + " " + countyName + " " + townName + " " + areaNameDetail);
                }

                name_phone_tv.setText(name);
                //列表加头布局
                order_shangpin_list.addHeaderView(head_layout);
            } else {
                order_shangpin_list.addHeaderView(head_layout_none);
            }

        } else if (ApiType.GET_LOCAL_SHOPCART_LIST == req.getApi()) {
            GetshopCart res = (GetshopCart) req.getData();
            List<shopCart> rows = res.datas.rows;
            if (rows == null || rows.size() == 0) {
                order_shangpin_list.setAdapter(null);
                goods_sum_price.setText("¥0");
                return;
            }
            ordering_go_bt_tv.setText("提交订单(" + res.datas.totalCount + ")");
            setViewClick(R.id.ordering_go_bt);
            if (data != null && data.category.size() > 0) {
                data.category.clear();
            }
            for (int i = 0; i < rows.size(); i++) {
                Data.Category c = new Data.Category();
                c.title = rows.get(i).brandName;
                c.goods = new ArrayList<Data.Goods>();
                for (int j = 0; j < rows.get(i).goodsList.size(); j++) {
                    Data.Goods goods = new Data.Goods();
                    goods.id = rows.get(i).goodsList.get(j).goodsId;
                    goods.name = rows.get(i).goodsList.get(j).goodsName;
                    goods.num = Integer
                            .parseInt(rows.get(i).goodsList.get(j).goodsCount);
                    goods.pic = rows.get(i).goodsList.get(j).imgUrl;
                    if (StringUtil
                            .empty(rows.get(i).goodsList.get(j).unitPrice)) {
                        goods.xianjia = 0;
                    } else {
                        goods.xianjia = Float.parseFloat(rows.get(i).goodsList
                                .get(j).unitPrice);
                    }
                    if (StringUtil.empty(rows.get(i).goodsList.get(j).deposit)) {
                        goods.dingjin = 0;
                    } else {
                        goods.dingjin = Float.parseFloat(rows.get(i).goodsList
                                .get(j).deposit);
                    }
                    goods.yuangjia = Float.parseFloat(rows.get(i).goodsList
                            .get(j).originalPrice);
                    goods.jifen = Float
                            .parseFloat(rows.get(i).goodsList.get(j).point);
                    c.goods.add(goods);
                }
                data.category.add(c);
            }
            adapter = new orderListAdapter();
            WidgetUtil.setListViewHeightBasedOnChildren(order_shangpin_list);
            order_shangpin_list.setAdapter(adapter);
            getTotalPrice();
        }
    }

    private void getAddress() {
        showProgressDialog();
        RequestParams params = new RequestParams();
        params.put("userId", Store.User.queryMe().userid);
        execApi(ApiType.ADDRESS_LIST, params);
    }

}
