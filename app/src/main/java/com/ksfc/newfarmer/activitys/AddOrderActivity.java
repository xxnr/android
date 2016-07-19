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

import com.ksfc.newfarmer.common.CommonAdapter;
import com.ksfc.newfarmer.common.CommonViewHolder;
import com.ksfc.newfarmer.common.GlideHelper;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.beans.AddOrderResult;
import com.ksfc.newfarmer.beans.AddressList;
import com.ksfc.newfarmer.beans.ConsigneeResult;
import com.ksfc.newfarmer.beans.DeliveriesResult;
import com.ksfc.newfarmer.beans.GetGoodsDetail;
import com.ksfc.newfarmer.beans.GetshopCart;
import com.ksfc.newfarmer.beans.AddressList.Address;
import com.ksfc.newfarmer.beans.GetshopCart.shopCart;


import com.ksfc.newfarmer.beans.LoginResult;
import com.ksfc.newfarmer.beans.RSCStateInfoResult;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.SPUtils;
import com.ksfc.newfarmer.utils.StringUtil;

import com.ksfc.newfarmer.widget.UnSwipeListView;
import com.ksfc.newfarmer.widget.WidgetUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import net.yangentao.util.msg.MsgCenter;
import net.yangentao.util.msg.MsgListener;

public class AddOrderActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private TextView goods_sum_price, name_phone_tv, order_detail_address_tv;
    private ListView order_shangpin_list;
    private List<Address> rows;
    Data data = null;
    private OrderListAdapter adapter;
    private View head_layout;
    private TextView ordering_go_bt_tv;
    //四个不同状态下展示不同的View
    private View address_shouhuo_ll;
    private View add_address_shouhuo_ll;
    private View select_state_address_ll;
    private View none_state_address_ll;
    //选择配送方式
    private RadioGroup deliveries_way_radioGroup;
    //当前地址
    private static Address selectedAddress = new Address();
    private RadioButton deliveries_button1, deliveries_button2;

    //网点和收货人的请求码
    private final static int requestState = 1;
    private final static int requestPerson = 2;
    private TextView select_state_person_info;//自提点，收货人信息
    private TextView select_state_address_info;//自提点，网点信息
    private RSCStateInfoResult.RSCsEntity rsCsEntity;
    private String consigneeName;
    private String consigneePhone;

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


    @Override
    public int getLayout() {
        return R.layout.activity_add_order;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        RndApplication.tempDestroyActivityList.add(AddOrderActivity.this);
        data = new Data();
        data.category = new ArrayList<>();
        setTitle("提交订单");
        initView();
    }

    private void initView() {

        goods_sum_price = (TextView) findViewById(R.id.order_sum_pri);
        order_shangpin_list = (ListView) findViewById(R.id.order_shangpin_list);
        ordering_go_bt_tv = (TextView) findViewById(R.id.ordering_go_bt_tv);

        //头布局
        head_layout = LayoutInflater.from(AddOrderActivity.this).inflate(R.layout.head_add_order, null);

        address_shouhuo_ll = head_layout.findViewById(R.id.address_shouhuo_ll);
        add_address_shouhuo_ll = head_layout.findViewById(R.id.add_address_shouhuo_ll);
        select_state_address_ll = head_layout.findViewById(R.id.select_state_address_ll);
        none_state_address_ll = head_layout.findViewById(R.id.none_state_address_ll);

        deliveries_way_radioGroup = ((RadioGroup) head_layout.findViewById(R.id.deliveries_way_radioGroup));
        deliveries_way_radioGroup.setOnCheckedChangeListener(this);
        deliveries_way_radioGroup.setVisibility(View.GONE);

        deliveries_button1 = (RadioButton) head_layout.findViewById(R.id.deliveries_way_self);
        deliveries_button2 = (RadioButton) head_layout.findViewById(R.id.deliveries_way_home);
        deliveries_button1.setVisibility(View.GONE);
        deliveries_button2.setVisibility(View.GONE);

        select_state_person_info = (TextView) head_layout.findViewById(R.id.select_state_person_info);
        select_state_address_info = (TextView) head_layout.findViewById(R.id.select_state_address_info);

        name_phone_tv = (TextView) head_layout.findViewById(R.id.order_detail_name_tv);
        order_detail_address_tv = (TextView) head_layout.findViewById(R.id.order_detail_address_tv);
        address_shouhuo_ll.setOnClickListener(this);
        add_address_shouhuo_ll.setOnClickListener(this);
        order_shangpin_list.addHeaderView(head_layout);
        initHeadView();
        // 获得商品列表
        initData();
        //获取配送方式
        getDeliveries();
        //获取历史联系人
        getConsignees();
        // 显示地址
        MsgCenter.addListener(new MsgListener() {// 收货人信息
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                initHeadView();
                Address str = (Address) args[0];
                if (str == null) {
                    order_detail_address_tv.setText("");
                    getAddress();
                } else {

                    address_shouhuo_ll.setVisibility(View.VISIBLE);
                    String name = str.receiptPeople + "  " + str.receiptPhone;
                    selectedAddress = str;
                    order_detail_address_tv.setText(StringUtil.checkBufferStrWithSpace(str.areaName, str.cityName
                            , str.countyName
                            , str.townName
                            , str.address));
                    name_phone_tv.setText(name);
                }
            }
        }, MsgID.MSG_Change_ADDRESS);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.deliveries_way_self:
                // 获取配送网点
                initHeadView();
                getDeliveriesState();
                break;
            case R.id.deliveries_way_home:
                initHeadView();
                // 获得地区地址
                if (StringUtil.checkStr(order_detail_address_tv.getText().toString())
                        && StringUtil.checkStr(name_phone_tv.getText().toString())) {
                    address_shouhuo_ll.setVisibility(View.VISIBLE);
                } else {
                    getAddress();
                }
                break;
        }
    }


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {

            case R.id.address_shouhuo_ll:
            case R.id.add_address_shouhuo_ll:
                Intent i = new Intent(AddOrderActivity.this,
                        ChooseAddressActivity.class);
                i.putExtra("state", selectedAddress);
                startActivity(i);
                break;
            case R.id.select_state_address_ll_state://选择自提网点
                Bundle bundle = new Bundle();
                Intent intent1 = getIntent();
                int flags1 = intent1.getFlags();
                if (flags1 == 1) {
                    String product_id = intent1.getStringExtra("product_id");
                    bundle.putString("product_id", product_id);
                    bundle.putInt("flags", 1);
                } else if (flags1 == 2) {
                    List<String> productIds = (List<String>) intent1.getSerializableExtra("productIds");
                    bundle.putSerializable("productIds", (Serializable) productIds);
                    bundle.putInt("flags", 2);
                }
                IntentUtil.startActivityForResult(this, SelectDeliveriesStateActivity.class,
                        requestState, bundle);
                break;
            case R.id.select_state_address_ll_person://选择收货人

                Bundle bundle1 = new Bundle();
                bundle1.putString("consigneeName", consigneeName);
                bundle1.putString("consigneePhone", consigneePhone);
                IntentUtil.startActivityForResult(this, SelectDeliveriesPersonActivity.class,
                        requestPerson, bundle1);
                break;
            case R.id.ordering_go_bt:// 订单确认按钮


                //自提
                if (deliveries_button1.getVisibility() == View.VISIBLE && deliveries_button1.isChecked()) {

                    if (none_state_address_ll.getVisibility() == View.VISIBLE) {
                        showToast("您选择的商品不能在同一个网点自提，请返回购物车重新选择");
                        return;
                    }

                    if (rsCsEntity == null) {
                        showToast("请选择自提网点");
                        return;
                    }
                    if (!StringUtil.checkStr(select_state_person_info.getText().toString().trim())) {
                        showToast("请选择联系人");
                        return;
                    }
                    showProgressDialog();
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
                        map1.put("shopCartId", SPUtils.get(AddOrderActivity.this,
                                "shopCartId", ""));
                        map1.put("deliveryType", 1);
                        map1.put("RSCId", rsCsEntity._id);
                        map1.put("consigneeName", consigneeName);
                        map1.put("consigneePhone", consigneePhone);
                        if (isLogin()) {
                            map1.put("token", Store.User.queryMe().token);
                        }
                        String jsonString = gson.toJson(map1);
                        RequestParams params = new RequestParams();
                        params.put("JSON", jsonString);
                        execApi(ApiType.ADD_ORDER.setMethod(ApiType.RequestMethod.POSTJSON), params);
                    } else if (flags == 2) {
                        List<Map<String, String>> goodsList = (List<Map<String, String>>) intent.getSerializableExtra("goodsList");
                        Gson gson = new Gson();
                        Map<String, Object> map1 = new HashMap<>();
                        map1.put("SKUs", goodsList);
                        map1.put("shopCartId", SPUtils.get(AddOrderActivity.this,
                                "shopCartId", ""));
                        map1.put("deliveryType", 1);
                        map1.put("RSCId", rsCsEntity._id);
                        map1.put("consigneeName", consigneeName);
                        map1.put("consigneePhone", consigneePhone);

                        if (isLogin()) {
                            map1.put("token", Store.User.queryMe().token);
                        }
                        String jsonString = gson.toJson(map1);
                        RequestParams params = new RequestParams();
                        params.put("JSON", jsonString);
                        execApi(ApiType.ADD_ORDER.setMethod(ApiType.RequestMethod.POSTJSON), params);
                    }
                    //送货到家
                } else if (deliveries_button2.getVisibility() == View.VISIBLE && deliveries_button2.isChecked()) {

                    if (TextUtils.isEmpty(order_detail_address_tv.getText().toString().trim())) {
                        showToast("收货地址不能为空");
                        return;
                    }
                    showProgressDialog();
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
                        map1.put("shopCartId", SPUtils.get(AddOrderActivity.this,
                                "shopCartId", ""));
                        map1.put("addressId", selectedAddress.addressId);
                        map1.put("deliveryType", 2);
                        if (isLogin()) {
                            map1.put("token", Store.User.queryMe().token);
                        }
                        String jsonString = gson.toJson(map1);
                        RequestParams params = new RequestParams();
                        params.put("JSON", jsonString);
                        execApi(ApiType.ADD_ORDER.setMethod(ApiType.RequestMethod.POSTJSON), params);
                    } else if (flags == 2) {
                        List<Map<String, String>> goodsList = (List<Map<String, String>>) intent.getSerializableExtra("goodsList");
                        Gson gson = new Gson();
                        Map<String, Object> map1 = new HashMap<>();
                        map1.put("SKUs", goodsList);
                        map1.put("shopCartId", SPUtils.get(AddOrderActivity.this,
                                "shopCartId", ""));
                        map1.put("addressId", selectedAddress.addressId);
                        map1.put("deliveryType", 2);
                        if (isLogin()) {
                            map1.put("token", Store.User.queryMe().token);
                        }
                        String jsonString = gson.toJson(map1);
                        RequestParams params = new RequestParams();
                        params.put("JSON", jsonString);
                        execApi(ApiType.ADD_ORDER.setMethod(ApiType.RequestMethod.POSTJSON), params);
                    }

                } else {
                    showToast("订单暂时不能提交");
                }


                break;
            default:
                break;
        }
    }


    //外层商品列表

    public class OrderListAdapter extends CommonAdapter<Data.Category> {

        public OrderListAdapter(Context context, List<Data.Category> data) {
            super(context, data, R.layout.item_order_list);
        }

        @Override
        public void convert(CommonViewHolder holder, Data.Category category) {
            if (category != null) {
                holder.setText(R.id.car_name, category.title);
                CarAdapter carAdapter = new CarAdapter(AddOrderActivity.this, category.goods);
                UnSwipeListView car_list = holder.getView(R.id.car_list);
                car_list.setAdapter(carAdapter);
                WidgetUtil.setListViewHeightBasedOnChildren(car_list);
            }

        }
    }


    //内层的商品列表，已店铺区分
    public class CarAdapter extends CommonAdapter<Data.Goods> {


        public CarAdapter(Context context, List<Data.Goods> data) {
            super(context, data, R.layout.item_item_order_list);
        }

        @Override
        public void convert(CommonViewHolder holder, Data.Goods goods) {
            if (goods != null) {
                try {//图片
                    GlideHelper.setImageRes(AddOrderActivity.this, goods.pic, (ImageView) holder.getView(R.id.ordering_item_img));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //数量
                holder.setText(R.id.ordering_item_geshu, "X " + goods.num + "");
                //名字
                holder.setText(R.id.ordering_item_name, goods.name);
                //Sku
                holder.setText(R.id.ordering_item_attr, goods.attr);
                //是否显示阶段
                TextView ordering_now_pri = holder.getView(R.id.ordering_now_pri);
                TextView goods_car_deposit = holder.getView(R.id.goods_car_item_bar_deposit);
                TextView goods_car_weikuan = holder.getView(R.id.goods_car_item_bar_weikuan);
                if (goods.dingjin == 0) {
                    ordering_now_pri.setTextColor(getResources().getColor(R.color.orange_goods_price));
                    holder.getView(R.id.goods_car_item_bar).setVisibility(View.GONE);
                } else {
                    ordering_now_pri.setTextColor(getResources().getColor(R.color.black_goods_titile));
                    holder.getView(R.id.goods_car_item_bar).setVisibility(View.VISIBLE);

                    goods_car_deposit.setTextColor(getResources().getColor(R.color.orange_goods_price));
                    goods_car_deposit.setText("¥" + StringUtil.toTwoString(goods.dingjin * goods.num + ""));
                    if (goods.additionPrice == 0) {
                        goods_car_weikuan.setText("¥" + StringUtil.toTwoString((goods.xianjia - goods.dingjin) * goods.num + ""));
                    } else {
                        goods_car_weikuan.setText("¥" + StringUtil.toTwoString((goods.xianjia + goods.additionPrice - goods.dingjin) * goods.num + ""));
                    }
                }
                //单价
                ordering_now_pri.setText("¥" + StringUtil.toTwoString(goods.xianjia + ""));

                //附加选项
                UnSwipeListView additions_listView = holder.getView(R.id.additions_listView);
                if (goods.additionsList != null && !goods.additionsList.isEmpty()) {
                    additions_listView.setVisibility(View.VISIBLE);
                    AdditionsAdapter adapter = new AdditionsAdapter(AddOrderActivity.this, goods.additionsList);
                    additions_listView.setAdapter(adapter);
                    WidgetUtil.setListViewHeightBasedOnChildren(additions_listView);
                } else {
                    additions_listView.setVisibility(View.GONE);
                }


            }

        }
    }

    //附加选项
    class AdditionsAdapter extends CommonAdapter<GetshopCart.SKU.Additions> {

        public AdditionsAdapter(Context context, List<GetshopCart.SKU.Additions> data) {
            super(context, data, R.layout.item_for_additions);
        }

        @Override
        public void convert(CommonViewHolder holder, GetshopCart.SKU.Additions additions) {
            if (additions != null) {
                holder.setText(R.id.item_additions_name, additions.name);
                if (StringUtil.checkStr(additions.price + "")) {
                    holder.setText(R.id.item_additions_price, "¥" + StringUtil.toTwoString(additions.price + ""));
                }
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
                        Intent intent = new Intent(AddOrderActivity.this,
                                SelectPayOrderActivity.class);
                        intent.putExtra("orderInfo", (Serializable) data.orders);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(AddOrderActivity.this,
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
            if (rows != null && rows.size() > 0) {
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
                if (deliveries_way_radioGroup.getCheckedRadioButtonId() == R.id.deliveries_way_home) {
                    address_shouhuo_ll.setVisibility(View.VISIBLE);
                }
            } else {
                if (deliveries_way_radioGroup.getCheckedRadioButtonId() == R.id.deliveries_way_home) {
                    add_address_shouhuo_ll.setVisibility(View.VISIBLE);
                }
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
                                stringBuilder.append(rows.get(i).SKUList.get(j).attributes.get(k).name).append(":").append(rows.get(i).SKUList.get(j).attributes.get(k).value).append(";");
                            }
                        }
                        goods.attr = stringBuilder.substring(0, stringBuilder.length() - 1);
                    }
                    //附加选项
                    if (rows.get(i).SKUList.get(j).additions != null && !rows.get(i).SKUList.get(j).additions.isEmpty()) {
                        goods.additionsList = rows.get(i).SKUList.get(j).additions;
                        float addi_price = 0;
                        for (int k = 0; k < rows.get(i).SKUList.get(j).additions.size(); k++) {
                            addi_price += rows.get(i).SKUList.get(j).additions.get(k).price;
                        }
                        goods.additionPrice = addi_price;
                    }

                    c.goods.add(goods);
                }
                data.category.add(c);
            }
            if (data.category != null) {
                adapter = new OrderListAdapter(this, data.category);
                WidgetUtil.setListViewHeightBasedOnChildren(order_shangpin_list);
                order_shangpin_list.setAdapter(adapter);
                getTotalPrice();
            }

        } else if (req.getApi() == ApiType.GET_DELIVERIES) {
            deliveries_way_radioGroup.setVisibility(View.VISIBLE);
            if (req.getData().getStatus().equals("1000")) {
                DeliveriesResult data = (DeliveriesResult) req.getData();
                if (data.datas != null && data.datas.items != null && !data.datas.items.isEmpty()) {
                    for (int i = 0; i < data.datas.items.size(); i++) {
                        DeliveriesResult.DatasEntity.ItemsEntity itemsEntity = data.datas.items.get(i);
                        if (itemsEntity != null) {
                            switch (itemsEntity.deliveryType) {
                                case 1:
                                    deliveries_button1.setVisibility(View.VISIBLE);
                                    deliveries_button1.setText(itemsEntity.deliveryName);
                                    break;
                                case 2:
                                    deliveries_button2.setVisibility(View.VISIBLE);
                                    deliveries_button2.setText(itemsEntity.deliveryName);
                                    break;
                            }
                        }
                    }
                    if (deliveries_button1.getVisibility() == View.VISIBLE) {
                        deliveries_button1.setChecked(true);
                    } else {
                        if (deliveries_button2.getVisibility() == View.VISIBLE) {
                            deliveries_button2.setChecked(true);
                        }
                    }
                }
            }
        } else if (req.getApi() == ApiType.GET_RSC_STATE_INFO) {
            if (req.getData().getStatus().equals("1000")) {
                RSCStateInfoResult data = (RSCStateInfoResult) req.getData();
                if (data.count > 0) {
                    initHeadView();
                    select_state_address_ll.setVisibility(View.VISIBLE);
                } else {
                    initHeadView();
                    none_state_address_ll.setVisibility(View.VISIBLE);
                }
            } else {
                initHeadView();
            }
        } else if (req.getApi() == ApiType.GET_CONSIGNEE_INFO) {

            if (req.getData().getStatus().equals("1000")) {
                ConsigneeResult data = (ConsigneeResult) req.getData();
                List<ConsigneeResult.DatasEntity.RowsEntity> rows = data.datas.rows;
                if (rows != null && !rows.isEmpty()) {
                    ConsigneeResult.DatasEntity.RowsEntity rowsEntity = rows.get(0);
                    if (rowsEntity != null) {
                        consigneeName = rowsEntity.consigneeName;
                        consigneePhone = rowsEntity.consigneePhone;
                        StringBuilder person_info = new StringBuilder().append(consigneeName).append(" ").append(consigneePhone);
                        select_state_person_info.setText(person_info);
                    }
                }
            }
        }
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
                    //如果有附加选项加上附加选项的金额
                    price += good.additionPrice * good.num;
                }
            }
        }
        goods_sum_price.setText("¥" + StringUtil.toTwoString(price + ""));
        return price;
    }


    //获取商品列表
    private void initData() {
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


    //获取配送地址
    private void getAddress() {
        showProgressDialog();
        RequestParams params = new RequestParams();
        if (isLogin()) {
            params.put("userId", Store.User.queryMe().userid);
        }
        execApi(ApiType.ADDRESS_LIST, params);
    }

    //获取配送方式
    private void getDeliveries() {
        showProgressDialog();
        Intent intent = getIntent();
        int flags = intent.getFlags();
        if (flags == 1) {
            String SKUId = intent.getStringExtra("SKUId");
            Map<String, Object> map = new HashMap<>();
            LoginResult.UserInfo userInfo = Store.User.queryMe();
            if (userInfo != null) {
                map.put("token", userInfo.token);
            }
            List<Map<String, Object>> list = new ArrayList<>();
            Map<String, Object> map1 = new HashMap<>();
            map1.put("_id", SKUId);
            list.add(map1);
            map.put("SKUs", list);

            Gson gson = new Gson();
            String toJson = gson.toJson(map);
            RequestParams params = new RequestParams();
            params.put("JSON", toJson);
            execApi(ApiType.GET_DELIVERIES.setMethod(ApiType.RequestMethod.POSTJSON), params);
        } else if (flags == 2) {

            Map<String, Object> map = new HashMap<>();
            LoginResult.UserInfo userInfo = Store.User.queryMe();
            if (userInfo != null) {
                map.put("token", userInfo.token);
            }
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> goodsList = (List<Map<String, Object>>) intent.getSerializableExtra("goodsList");
            if (goodsList != null) {
                for (int j = 0; j < goodsList.size(); j++) {
                    String _id = (String) goodsList.get(j).get("_id");
                    Map<String, Object> map1 = new HashMap<>();
                    if (StringUtil.checkStr(_id)) {
                        map1.put("_id", _id);
                        list.add(map1);
                    }
                }
            }
            map.put("SKUs", list);

            Gson gson = new Gson();
            String toJson = gson.toJson(map);
            RequestParams params = new RequestParams();
            params.put("JSON", toJson);
            execApi(ApiType.GET_DELIVERIES.setMethod(ApiType.RequestMethod.POSTJSON), params);
        }
    }

    //获取配送网点
    private void getDeliveriesState() {

        showProgressDialog();

        Intent intent = getIntent();
        int flags = intent.getFlags();
        if (flags == 1) {
            String product_id = intent.getStringExtra("product_id");
            RequestParams params = new RequestParams();
            params.put("products", product_id);
            LoginResult.UserInfo userInfo = Store.User.queryMe();
            if (userInfo != null) {
                params.put("userId", userInfo.userid);
            }
            execApi(ApiType.GET_RSC_STATE_INFO.setMethod(ApiType.RequestMethod.GET), params);
        } else if (flags == 2) {
            RequestParams params = new RequestParams();
            LoginResult.UserInfo userInfo = Store.User.queryMe();
            if (userInfo != null) {
                params.put("userId", userInfo.userid);
            }
            List<String> productIds = (List<String>) intent.getSerializableExtra("productIds");
            StringBuilder builder = new StringBuilder();
            if (productIds != null && productIds.size() > 0) {
                for (int j = 0; j < productIds.size(); j++) {
                    String _id = productIds.get(j);
                    if (StringUtil.checkStr(_id)) {
                        builder.append(_id).append(",");
                    }
                }
                String subUrl = builder.substring(0, builder.length() - 1);
                params.put("products", subUrl);
            }
            execApi(ApiType.GET_RSC_STATE_INFO.setMethod(ApiType.RequestMethod.GET), params);
        }

        //临时的，暂时无网点的api
        select_state_address_ll.setVisibility(View.VISIBLE);
        setViewClick(R.id.select_state_address_ll_state);
        setViewClick(R.id.select_state_address_ll_person);
    }

    //还原头布局的不同的View
    private void initHeadView() {
        address_shouhuo_ll.setVisibility(View.GONE);
        add_address_shouhuo_ll.setVisibility(View.GONE);
        select_state_address_ll.setVisibility(View.GONE);
        none_state_address_ll.setVisibility(View.GONE);
    }

    //接收选择后的网点和收货人
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case requestPerson:
                if (resultCode == 0x11)//返回码
                {
                    consigneeName = data.getStringExtra("receipt_name");
                    consigneePhone = data.getStringExtra("receipt_phone");
                    StringBuilder person_info = new StringBuilder().append(consigneeName).append(" ").append(consigneePhone);
                    select_state_person_info.setText(person_info);

                }

                break;
            case requestState:

                if (resultCode == 0x12)//返回码
                {
                    //网点所在地址
                    rsCsEntity = (RSCStateInfoResult.RSCsEntity) data.getSerializableExtra("rsCsEntity");
                    if (rsCsEntity != null) {

                        String province = "";
                        String city = "";
                        String county = "";
                        String town = "";
                        RSCStateInfoResult.RSCsEntity.RSCInfoEntity.CompanyAddressEntity companyAddress = rsCsEntity.RSCInfo.companyAddress;
                        if (companyAddress != null && companyAddress.province != null) {
                            province = companyAddress.province.name;
                        }
                        if (companyAddress != null && companyAddress.city != null) {
                            city = companyAddress.city.name;
                        }
                        if (companyAddress != null && companyAddress.county != null) {
                            county = companyAddress.county.name;
                        }
                        if (companyAddress != null && companyAddress.town != null) {
                            town = companyAddress.town.name;
                        }
                        String address = StringUtil.checkBufferStr
                                (province, city, county, town);
                        StringBuilder builder = new StringBuilder();
                        builder.append(address);
                        if (companyAddress != null && StringUtil.checkStr(companyAddress.details)) {
                            builder.append(companyAddress.details);
                        }
                        select_state_address_info.setText(builder.toString());
                    }
                }

                break;
        }


    }


    //获取联系人
    private void getConsignees() {
        RequestParams params = new RequestParams();
        if (isLogin()) {
            params.put("userId", Store.User.queryMe().userid);
        }
        execApi(ApiType.GET_CONSIGNEE_INFO.setMethod(ApiType.RequestMethod.GET), params);
    }
}
