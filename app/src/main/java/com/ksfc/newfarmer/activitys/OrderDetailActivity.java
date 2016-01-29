package com.ksfc.newfarmer.activitys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.RndApplication;

import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.AddOrderResult;
import com.ksfc.newfarmer.protocol.beans.AddressList;
import com.ksfc.newfarmer.protocol.beans.GetGoodsDetail;
import com.ksfc.newfarmer.protocol.beans.GetshopCart;
import com.ksfc.newfarmer.protocol.beans.AddressList.Address;
import com.ksfc.newfarmer.protocol.beans.GetshopCart.shopCart;


import com.ksfc.newfarmer.utils.SPUtils;
import com.ksfc.newfarmer.utils.StringUtil;

import com.ksfc.newfarmer.widget.RecyclerImageView;
import com.ksfc.newfarmer.widget.UnSwipeListView;
import com.ksfc.newfarmer.widget.WidgetUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.yangentao.util.app.App;
import net.yangentao.util.msg.MsgCenter;
import net.yangentao.util.msg.MsgListener;

public class OrderDetailActivity extends BaseActivity {

    private TextView goods_sum_price, name_phone_tv, order_detail_address_tv;
    private ListView order_shangpin_list;
    private List<Address> rows;
    Data data = null;
    private orderListAdapter adapter;
    private View head_layout;
    private View head_layout_none;
    private TextView ordering_go_bt_tv;


    //本地构造的实体类
    static class Data {
        List<Category> category;

        static class Category {
            String title;
            List<Goods> goods;
        }

        static class Goods {
            String id;
            String SKUId;
            String name;
            String pic;
            List<GetshopCart.SKU.Additions> additionsList;
            int num;
            float additionPrice;
            String attr;
            float xianjia;
            float dingjin;
        }
    }

    //当前地址
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
        data.category = new ArrayList<>();
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
        showProgressDialog("加载中");
        if (flags == 1) {
            String count = intent.getStringExtra("count");
            String SKUId = intent.getStringExtra("SKUId");
            List<GetGoodsDetail.GoodsDetail.SKUAdditions> skuAdditions =
                    (List<GetGoodsDetail.GoodsDetail.SKUAdditions>) intent.getSerializableExtra("additions");
            List<Map<String, Object>> goodsList = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("_id", SKUId);
            map.put("count", count);
            map.put("additions", skuAdditions);
            goodsList.add(map);

            Gson gson = new Gson();
            Map<String, Object> map1 = new HashMap<>();
            map1.put("SKUs", goodsList);
            String jsonString = gson.toJson(map1);

            RequestParams params = new RequestParams();
            params.put("JSON", jsonString);
            execApi(ApiType.GET_LOCAL_SHOPCART_LIST.setMethod(ApiType.RequestMethod.POSTJSON), params);
        } else if (flags == 2) {
            List<Map<String, Object>> goodsList = (List<Map<String, Object>>) intent.getSerializableExtra("goodsList");
            Gson gson = new Gson();
            Map<String, Object> map1 = new HashMap<>();
            map1.put("SKUs", goodsList);
            String jsonString = gson.toJson(map1);
            RequestParams params = new RequestParams();
            params.put("JSON", jsonString);
            execApi(ApiType.GET_LOCAL_SHOPCART_LIST.setMethod(ApiType.RequestMethod.POSTJSON), params);
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
                // 确认订单:
                Intent intent = getIntent();
                int flags = intent.getFlags();
                if (flags == 1) {
                    String count = intent.getStringExtra("count");
                    String SKUId = intent.getStringExtra("SKUId");

                    List<GetGoodsDetail.GoodsDetail.SKUAdditions> skuAdditions =
                            (List<GetGoodsDetail.GoodsDetail.SKUAdditions>) intent.getSerializableExtra("additions");
                    List<Map<String, Object>> goodsList = new ArrayList<>();
                    Map<String, Object> map = new HashMap<>();
                    map.put("_id", SKUId);
                    map.put("count", count);
                    map.put("additions", skuAdditions);
                    goodsList.add(map);
                    Gson gson = new Gson();
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("SKUs", goodsList);
                    map1.put("shopCartId", SPUtils.get(OrderDetailActivity.this,
                            "shopCartId", ""));
                    map1.put("addressId", selectedAddress.addressId);
                    map1.put("token", Store.User.queryMe().token);
                    String jsonString = gson.toJson(map1);
                    RequestParams params = new RequestParams();
                    params.put("JSON", jsonString);
                    execApi(ApiType.ADD_ORDER.setMethod(ApiType.RequestMethod.POSTJSON), params);
                } else if (flags == 2) {
                    List<Map<String, String>> goodsList = (List<Map<String, String>>) intent.getSerializableExtra("goodsList");
                    Gson gson = new Gson();
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("SKUs", goodsList);
                    map1.put("shopCartId", SPUtils.get(OrderDetailActivity.this,
                            "shopCartId", ""));
                    map1.put("addressId", selectedAddress.addressId);
                    map1.put("token", Store.User.queryMe().token);
                    String jsonString = gson.toJson(map1);
                    RequestParams params = new RequestParams();
                    params.put("JSON", jsonString);
                    execApi(ApiType.ADD_ORDER.setMethod(ApiType.RequestMethod.POSTJSON), params);
                }
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
            if (StringUtil.checkStr(data.category.get(position).title)) {
                holder.car_name.setText(data.category.get(position).title);
            }
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
            private RecyclerImageView ordering_item_img;
            private TextView ordering_now_pri, ordering_item_name, goods_car_deposit,
                    goods_car_weikuan, ordering_item_geshu, ordering_item_attr;
            private ListView additions_listView;

            public ViewHolder(View convertView) {
                goods_car_bar = (LinearLayout) convertView.findViewById(R.id.goods_car_item_bar);
                ordering_item_img = (RecyclerImageView) convertView//商品图
                        .findViewById(R.id.ordering_item_img);
                ordering_item_geshu = (TextView) convertView//商品个数
                        .findViewById(R.id.ordering_item_geshu);
                ordering_item_attr = (TextView) convertView   //商品sku
                        .findViewById(R.id.ordering_item_attr);
                ordering_now_pri = (TextView) convertView//商品价格
                        .findViewById(R.id.ordering_now_pri);
                ordering_item_name = (TextView) convertView//商品名
                        .findViewById(R.id.ordering_item_name);
                goods_car_deposit = (TextView) convertView//汽车定金
                        .findViewById(R.id.goods_car_item_bar_deposit);
                goods_car_weikuan = (TextView) convertView//汽车尾款
                        .findViewById(R.id.goods_car_item_bar_weikuan);
                additions_listView = (ListView) convertView//附加选项
                        .findViewById(R.id.additions_listView);
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

            try{
                //图片
                if (StringUtil.checkStr(goodsList.get(position).pic)) {
                    Picasso.with(OrderDetailActivity.this)
                            .load(MsgID.IP +  goodsList.get(position).pic)
                            .error(R.drawable.error)
                            .placeholder(R.drawable.zhanweitu)
                            .into(holder.ordering_item_img);

                }
            }catch (Exception e){
                e.printStackTrace();
            }

            //数量
            holder.ordering_item_geshu.setText("X " + goodsList.get(position).num + "");
            //名字
            if (StringUtil.checkStr(goodsList.get(position).name)) {
                holder.ordering_item_name.setText(goodsList.get(position).name);
            }
            //Sku
            if (StringUtil.checkStr(goodsList.get(position).attr)) {
                holder.ordering_item_attr.setText(goodsList.get(position).attr);
            }
            //是否显示阶段
            if (goodsList.get(position).dingjin == 0) {
                holder.ordering_now_pri.setTextColor(getResources().getColor(R.color.orange_goods_price));
                holder.goods_car_bar.setVisibility(View.GONE);
            } else {
                holder.ordering_now_pri.setTextColor(getResources().getColor(R.color.black_goods_titile));
                holder.goods_car_bar.setVisibility(View.VISIBLE);
                holder.goods_car_deposit.setTextColor(getResources().getColor(R.color.orange_goods_price));
                holder.goods_car_deposit.setText("¥" + StringUtil.toTwoString(goodsList
                        .get(position).dingjin * goodsList.get(position).num + ""));
                if (goodsList.get(position).additionPrice == 0) {
                    holder.goods_car_weikuan.setText("¥" + StringUtil.toTwoString((goodsList.get(position).xianjia - goodsList
                            .get(position).dingjin) * goodsList.get(position).num + ""));
                } else {
                    holder.goods_car_weikuan.setText("¥" + StringUtil.toTwoString((goodsList.get(position).xianjia + goodsList.get(position).additionPrice - goodsList
                            .get(position).dingjin) * goodsList.get(position).num + ""));
                }
            }
            //单价
            holder.ordering_now_pri.setText("¥" + StringUtil.toTwoString(goodsList
                    .get(position).xianjia + ""));
            //附加选项
            if (goodsList.get(position).additionsList != null && !goodsList.get(position).additionsList.isEmpty()) {
                holder.additions_listView.setVisibility(View.VISIBLE);
                AdditionsAdapter adapter = new AdditionsAdapter(goodsList.get(position).additionsList);
                holder.additions_listView.setAdapter(adapter);
                WidgetUtil.setListViewHeightBasedOnChildren(holder.additions_listView);
            } else {
                holder.additions_listView.setVisibility(View.GONE);
            }

            return convertView;
        }

    }


    class AdditionsAdapter extends BaseAdapter {
        private List<GetshopCart.SKU.Additions> list;

        public AdditionsAdapter(List<GetshopCart.SKU.Additions> list) {
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
                convertView = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.item_for_additions_layout, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (StringUtil.checkStr(list.get(position).name) && StringUtil.checkStr(list.get(position).price+"")) {
                holder.item_additions_name.setText(list.get(position).name);
                holder.item_additions_price.setText("¥" + StringUtil.toTwoString(list.get(position).price+""));
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
    public void onResponsed(Request req) {
        disMissDialog();
        if (req.getApi() == ApiType.ADD_ORDER) {
            AddOrderResult data = (AddOrderResult) req.getData();
            if (data.getStatus().equals("1000")) {
                if (data.orders != null && !data.orders.isEmpty()) {
                    if (data.orders.size() > 1) {
                        Intent intent = new Intent(OrderDetailActivity.this,
                                SelectPayOrderActivity.class);
                        intent.putExtra("orderInfo", (Serializable) data.orders);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(OrderDetailActivity.this,
                                PaywayActivity.class);
                        if (StringUtil.checkStr(data.orders.get(0).id)) {
                            intent.putExtra("orderId", data.orders.get(0).id);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }

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
                c.goods = new ArrayList<>();
                for (int j = 0; j < rows.get(i).SKUList.size(); j++) {
                    //商品的属性 id SKUId productName num imageUrl
                    Data.Goods goods = new Data.Goods();
                    goods.id = rows.get(i).SKUList.get(j).goodsId;
                    goods.SKUId = rows.get(i).SKUList.get(j)._id;//SKUId
                    goods.name = rows.get(i).SKUList.get(j).productName;
                    goods.num = Integer
                            .parseInt(rows.get(i).SKUList.get(j).count);
                    goods.pic = rows.get(i).SKUList.get(j).imgUrl;
                    //价格
                    if (StringUtil
                            .empty(rows.get(i).SKUList.get(j).price)) {
                        goods.xianjia = 0;
                    } else {
                        goods.xianjia = Float.parseFloat(rows.get(i).SKUList
                                .get(j).price);
                    }
                    //订金
                    if (StringUtil.empty(rows.get(i).SKUList.get(j).deposit)) {
                        goods.dingjin = 0;
                    } else {
                        goods.dingjin = Float.parseFloat(rows.get(i).SKUList
                                .get(j).deposit);
                    }
                    //Sku属性
                    StringBuilder stringBuilder = new StringBuilder();
                    if (rows.get(i).SKUList.get(j).attributes != null && !rows.get(i).SKUList.get(j).attributes.isEmpty()) {
                        for (int k = 0; k < rows.get(i).SKUList.get(j).attributes.size(); k++) {
                            if (StringUtil.checkStr(rows.get(i).SKUList.get(j).attributes.get(k).name)
                                    && StringUtil.checkStr(rows.get(i).SKUList.get(j).attributes.get(k).value)) {
                                stringBuilder.append(rows.get(i).SKUList.get(j).attributes.get(k).name + ":")
                                        .append(rows.get(i).SKUList.get(j).attributes.get(k).value + ";");
                            }
                        }
                        goods.attr = stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
                    }
                    //附加选项
                    if (rows.get(i).SKUList.get(j).additions != null && !rows.get(i).SKUList.get(j).additions.isEmpty()) {
                        goods.additionsList = rows.get(i).SKUList.get(j).additions;
                        float addi_price = 0;
                        for (int k = 0; k < rows.get(i).SKUList.get(j).additions.size(); k++) {
                            addi_price += rows.get(i).SKUList.get(j).additions.get(k).price;
                        }
                        goods.additionPrice=addi_price;
                    }

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
