package com.ksfc.newfarmer.activitys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.daimajia.swipe.SwipeLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.ShoppingCartActivity.Data.Category;
import com.ksfc.newfarmer.activitys.ShoppingCartActivity.Data.Goods;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.db.dao.ShoppingDao;
import com.ksfc.newfarmer.http.ApiType;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.http.RequestParams;
import com.ksfc.newfarmer.http.beans.GetGoodsDetail;
import com.ksfc.newfarmer.http.beans.GetshopCart;
import com.ksfc.newfarmer.http.beans.GetshopCart.shopCart;
import com.ksfc.newfarmer.widget.dialog.CustomDialog;
import com.ksfc.newfarmer.widget.dialog.CustomDialogForShopCarCount;
import com.ksfc.newfarmer.utils.ExpandViewTouch;
import com.ksfc.newfarmer.utils.PullToRefreshUtils;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.SPUtils;
import com.ksfc.newfarmer.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.graphics.Color;
import android.text.Editable;
import android.text.Selection;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import android.widget.CheckBox;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.yangentao.util.PreferenceUtil;


public class ShoppingCartActivity extends BaseActivity {
    private PullToRefreshExpandableListView shopCart_list;
    private ExpandableListView shopCart_expandListView;
    private LinearLayout toPay_ll;//去结算按钮
    private RelativeLayout null_shop_cart_layout;//没有商品时View
    private static ShoppingCartActivity instance; //当前类的实例
    private TextView goto_pay_tv;   //去结算上的文字
    public TextView ordering_sum_pri;// 合计

    public static List<Goods> list;
    public String str = "";// 数据向服务器传递时传递的报文

    private List<Map> allShoppings;
    private List<Map<String, Object>> localToNet;
    private shopCartAdapter adapter;
    private boolean isEdit; // 是否正在编辑
    private LinearLayout goods_bar_ll;   //商品下方bar

    private double price; // 总价
    private static int num = 0; // 选中的商品数
    private int total = 0;
    private int offCount = 0;
    private HashMap<String, Boolean> inCartMap = new HashMap<>();// 用于存放选中的项
    private HashMap<String, Boolean> inShopMap = new HashMap<>();// 用于店铺选中的项
    private CheckBox mBtnCheckAll;
    private int car_num = 1; //对话框里的数量
    private boolean isQuery = true;//是否请求购物车列表


    private String huaFeiClassId = "531680A5";
    private String carClassId = "6C7D8F66";

    //当前activity适配器所用到的实体类
    Data data = null;
    ShoppingDao dao;
    /**
     * 全选按钮监听器
     */
    private OnCheckedChangeListener checkAllListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.btn_check_all:
                    if (isChecked) {
                        checkAll();
                    } else {
                        //清空两个存放选中状态的的map
                        inCartMap.clear();
                        inShopMap.clear();
                    }
                    notifyCheckedChanged();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    break;
            }

        }
    };


    /**
     * 获取实例对象
     */
    public static ShoppingCartActivity getInstance() {
        if (instance == null) {
            instance = new ShoppingCartActivity();
        }
        return instance;
    }

    /**
     * 全选，将数据加入inCartMap
     */
    private void checkAll() {
        for (int i = 0; i < data.category.size(); i++) {
            inShopMap.put(data.category.get(i).title, true);
            for (int j = 0; j < data.category.get(i).goods.size(); j++) {
                if (data.category.get(i).goods.get(j).online) {
                    inCartMap.put(data.category.get(i).goods.get(j).SKUId, true);
                }
            }
        }
    }

    /**
     * 选中商品改变
     */
    private void notifyCheckedChanged() {
        price = 0;
        num = 0;
        for (int i = 0; i < data.category.size(); i++) {
            for (int j = 0; j < data.category.get(i).goods.size(); j++) {
                Boolean isChecked = inCartMap.get(data.category.get(i).goods.get(j).SKUId);
                if (isChecked != null && isChecked) {
                    Goods good = data.category.get(i).goods.get(j);
                    num += good.num;
                    // 判断是否是一口价商品
                    if (data.category.get(i).goods.get(j).dingjin != 0) {
                        price += good.num
                                * good.dingjin;
                    } else {
                        price += good.num
                                * good.xianjia;
                        //如果有附加选项加上附加选项的金额
                        if (good.jsonList != null) {
                            int additionsPrice = 0;
                            for (int k = 0; k < good.jsonList.size(); k++) {

                                GetshopCart.SKU.Additions additions = good.jsonList.get(k);
                                additionsPrice += additions.price;
                            }
                            price += good.num * additionsPrice;
                        }
                    }
                }
            }
        }

        ordering_sum_pri.setText("¥" + StringUtil.toTwoString(price + ""));
        if (isEdit) {
            goto_pay_tv.setText("删除(" + num + ")");
        } else {
            goto_pay_tv.setText("去结算(" + num + ")");
        }

    }

    /**
     * 购物车数量改变
     */
    private void notifyNumChanged() {
        int totalNum = 0;
        for (int i = 0; i < data.category.size(); i++) {
            for (int j = 0; j < data.category.get(i).goods.size(); j++) {
                totalNum += data.category.get(i).goods.get(j).num;
            }
        }
        setTitle("购物车" + "(" + totalNum + ")");
    }


    @Override
    public int getLayout() {
        return R.layout.activity_shop_cart;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        data = new Data();
        data.category = new ArrayList<>();
        setTitle("购物车");
        hideLeft();
        showRightTextView();
        setRightTextView("编辑");
        setRightTextViewListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //编辑购物车 删除等
                editInCart();
            }
        });
        initView();
    }

    /**
     * 编辑 购物车
     */
    private void editInCart() {
        isEdit = !isEdit;
        if (isEdit) {
            goods_bar_ll.setVisibility(View.INVISIBLE);
            setRightTextView("完成");
            goto_pay_tv.setText("删除" + "(" + num + ")");
        } else {
            goods_bar_ll.setVisibility(View.VISIBLE);
            setRightTextView("编辑");
            goto_pay_tv.setText("去结算" + "(" + num + ")");
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void initView() {
        toPay_ll = (LinearLayout) findViewById(R.id.topay_ll);
        goods_bar_ll = (LinearLayout) findViewById(R.id.goods_car_price_lin);
        goto_pay_tv = (TextView) findViewById(R.id.go_to_pay);
        mBtnCheckAll = (CheckBox) findViewById(R.id.btn_check_all);

        //扩大点击区域
        ExpandViewTouch.expandViewTouchDelegate(mBtnCheckAll, 100, 100, 100, 100);
        mBtnCheckAll.setOnCheckedChangeListener(checkAllListener);

        null_shop_cart_layout = (RelativeLayout) findViewById(R.id.null_shop_cart_layout);
        ordering_sum_pri = (TextView) findViewById(R.id.shopcart_sum_pri);
        shopCart_list = (PullToRefreshExpandableListView) findViewById(R.id.shopcart_list);
        shopCart_list.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        shopCart_expandListView = shopCart_list.getRefreshableView();

        shopCart_expandListView.setGroupIndicator(null);
        shopCart_expandListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        shopCart_expandListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //滚动时，保持左滑出的view复原
                if (adapter != null && scrollState == 1) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        //设置刷新的文字
        PullToRefreshUtils.setFreshText(shopCart_list);
        //设置下拉刷新

        shopCart_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ExpandableListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
                PullToRefreshUtils.setFreshClose(refreshView);
                if (!isLogin()) {
                    shopCart_list.onRefreshComplete();
                }
                getData();
            }
        });

        setViewClick(R.id.my_login_sure);
        setViewClick(R.id.my_login_cancel);
        setViewClick(R.id.ordering_go_bt);

        //设置classId

        PreferenceUtil pu = new PreferenceUtil(this, "config");
        huaFeiClassId = pu.getString("huafei", "531680A5");
        carClassId = pu.getString("qiche", "6C7D8F66");

    }

    /**
     * 获取数据
     */
    private void getData() {

        if (isLogin()) {
            // 走网络
            // app/shopCart/getShopCartList
            // locationUserId:操作人ID
            // userId:用户ID
            // 把本地的数据加载到用户的购物车中
            dao = new ShoppingDao(getApplicationContext());
            allShoppings = dao.getAllShoppings("汽车");
            localToNet = new ArrayList<>();
            if (allShoppings.size() > 0) {
                for (int i = 0; i < allShoppings.size(); i++) {
                    HashMap<String, Object> map = new HashMap<>();
                    if (allShoppings.get(i).get("numbers") != null) {
                        map.put("sum",
                                (String) allShoppings.get(i).get("numbers"));
                    }
                    if (allShoppings.get(i).get("additions") != null) {
                        String additions = (String) allShoppings.get(i).get("additions");
                        Gson gson = new Gson();
                        List<GetGoodsDetail.GoodsDetail.SKUAdditions> jsonList = gson.fromJson(additions, new TypeToken<List<GetGoodsDetail.GoodsDetail.SKUAdditions>>() {
                        }.getType());
                        map.put("additions", jsonList);
                    }
                    if (allShoppings.get(i).get("SKUId") != null) {
                        map.put("SKUId",
                                (String) allShoppings.get(i).get("SKUId"));
                    }

                    localToNet.add(map);
                }
            }


            //将本地的购物车 同步到用户
            for (int i = 0; i < localToNet.size(); i++) {
                RequestParams params = new RequestParams();
                Gson gson = new Gson();
                Map<String, Object> map = new HashMap<>();
                if (isLogin()) {
                    map.put("token", Store.User.queryMe().token);
                }
                map.put("quantity", localToNet.get(i).get("sum"));
                map.put("SKUId", localToNet.get(i).get("SKUId"));
                map.put("update_by_add", "true");

                List<GetGoodsDetail.GoodsDetail.SKUAdditions> jsonList = (List<GetGoodsDetail.GoodsDetail.SKUAdditions>) localToNet.get(i).get("additions");
                if (jsonList != null && !jsonList.isEmpty()) {
                    map.put("additions", jsonList);
                }
                String json = gson.toJson(map);
                params.put("JSON", json);
                execApi(ApiType.ADDTOCART.setMethod(ApiType.RequestMethod.POSTJSON), params);
            }

            // 加载用户购物车数据  GET 请求
            RequestParams params = new RequestParams();
            if (isLogin()) {
                params.put("userId", Store.User.queryMe().userid);
                execApi(ApiType.GET_SHOPCART_LIST.setMethod(ApiType.RequestMethod.GET), params);
            }
            // 删除本地购物车数据
            dao.deleteAllShopping();
        } else {
            //未登录加载本地购物车
            disMissDialog();
            jiadata(); // 本地数据库数据（假数据）
        }
    }

    private void jiadata() {

        dao = new ShoppingDao(getApplicationContext());
        allShoppings = dao.getAllShoppings(null);
        localToNet = new ArrayList<>();
        if (allShoppings.size() > 0) {
            for (int i = 0; i < allShoppings.size(); i++) {
                HashMap<String, Object> map = new HashMap<>();
                if (allShoppings.get(i).get("SKUId") != null) {
                    map.put("_id", (String) allShoppings.get(i)
                            .get("SKUId"));
                }

                if (allShoppings.get(i).get("additions") != null) {

                    String additions = (String) allShoppings.get(i).get("additions");
                    Gson gson = new Gson();
                    List<GetGoodsDetail.GoodsDetail.SKUAdditions> jsonList = gson.fromJson(additions, new TypeToken<List<GetGoodsDetail.GoodsDetail.SKUAdditions>>() {
                    }.getType());
                    map.put("additions", jsonList);
                }
                if (allShoppings.get(i).get("numbers") != null) {
                    map.put("count", (String) allShoppings.get(i)
                            .get("numbers"));
                }
                localToNet.add(map);
            }
        }

        if (localToNet.size() == 0) {
            if (adapter != null) {
                adapter.clear();
            } else {
                adapter = new shopCartAdapter();
                adapter.clear();
                shopCart_expandListView.setAdapter(adapter);
            }
            toPay_ll.setVisibility(View.GONE);
            null_shop_cart_layout
                    .setVisibility(View.VISIBLE);
            hideRight();
            setTitle("购物车");
        }
        RequestParams params = new RequestParams();
        if (localToNet.size() > 0) {
            Gson gson = new Gson();
            Map<String, Object> map = new HashMap<>();
            map.put("SKUs", localToNet);
            String jsonString = gson.toJson(map);
            params.put("JSON", jsonString);
            execApi(ApiType.GET_LOCAL_SHOPCART_LIST.setMethod(ApiType.RequestMethod.POSTJSON), params);
            showProgressDialog();
            if (adapter == null) {
                adapter = new shopCartAdapter();
                shopCart_expandListView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    //本地构造的实体类
    static class Data implements Serializable {
        List<Category> category;

        static class Category implements Serializable {
            String title;
            List<Goods> goods;
            int offCount;
        }

        static class Goods implements Serializable {
            String id;
            String product_id;
            String SKUId;
            boolean online;
            String name;
            String pic;
            int num;
            float additionPrice;
            String attr;
            String additions;
            float xianjia;
            float dingjin;
            List<GetshopCart.SKU.Additions> jsonList;
        }
    }


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.ordering_go_bt:
                //结算状态
                if (!isEdit) {
                    if (!isLogin()) {
                        startActivity(LoginActivity.class);
                        return;
                    } else {
                        //从购物车去支付
                        if (num > 0) {
                            //SKU列表
                            List<String> productIds = new ArrayList<>();
                            List<Map<String, Object>> list = new ArrayList<>();
                            for (int i = 0; i < data.category.size(); i++) {
                                for (int j = 0; j < data.category.get(i).goods.size(); j++) {
                                    Boolean isChecked = inCartMap.get(data.category.get(i).goods.get(j).SKUId);
                                    if (isChecked != null && isChecked) {
                                        Goods good = data.category.get(i).goods.get(j);
                                        Map<String, Object> map = new HashMap<>();
                                        if (good.jsonList != null && !good.jsonList.isEmpty()) {
                                            map.put("additions", good.jsonList);
                                        }
                                        map.put("_id", good.SKUId);
                                        map.put("count", good.num + "");
                                        list.add(map);
                                        productIds.add(good.product_id);
                                    }
                                }
                            }
                            Intent intent = new Intent(ShoppingCartActivity.this,
                                    AddOrderActivity.class);
                            intent.putExtra("goodsList", (Serializable) list);
                            intent.putExtra("productIds", (Serializable) productIds);
                            intent.addFlags(2);//通过此flag区别 立即购买和加入购物车
                            startActivity(intent);// 支付
                        } else {
                            showToast("请您至少选择一件商品");
                        }
                    }

                    //编辑状态
                } else {
                    if (num > 0) {
                        CustomDialog.Builder builder = new CustomDialog.Builder(
                                ShoppingCartActivity.this);
                        builder.setMessage("您确定要删除吗？")
                                .setPositiveButton("是",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                if (isLogin()) {
                                                    for (int i = 0; i < data.category.size(); i++) {
                                                        for (int j = 0; j < data.category.get(i).goods.size(); j++) {
                                                            Boolean isChecked = inCartMap.get(data.category.get(i).goods.get(j).SKUId);
                                                            if (isChecked != null && isChecked) {
                                                                Goods good = data.category.get(i).goods.get(j);
                                                                RequestParams params = new RequestParams();
                                                                if (isLogin()) {
                                                                    params.put("userId", Store.User.queryMe().userid);
                                                                }
                                                                params.put("SKUId", good.SKUId);
                                                                params.put("quantity", "0");
                                                                execApi(ApiType.CHANGE_NUM,
                                                                        params);
                                                            }
                                                            showProgressDialog("删除中");
                                                        }
                                                    }

                                                } else {
                                                    for (int i = 0; i < data.category.size(); i++) {
                                                        for (int j = 0; j < data.category.get(i).goods.size(); j++) {
                                                            Boolean isChecked = inCartMap.get(data.category.get(i).goods.get(j).SKUId);
                                                            if (isChecked != null && isChecked) {
                                                                Goods good = data.category.get(i).goods.get(j);
                                                                dao.deleteShopping(good.SKUId);
                                                            }
                                                        }
                                                    }
                                                    getData();
                                                    showToast("商品删除成功");


                                                }
                                                //重置 map 防止出问题
                                                if (inShopMap != null && inCartMap != null) {
                                                    inCartMap.clear();
                                                    inShopMap.clear();
                                                }

                                                dialog.dismiss();
                                            }
                                        })
                                .setNegativeButton("否",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                dialog.dismiss();
                                            }
                                        });
                        CustomDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        showToast("请您至少选择一件商品");
                    }


                }
                break;
            case R.id.my_login_sure:
                Intent intent = new Intent(ShoppingCartActivity.this,
                        GoodsListActivity.class);
                intent.putExtra("className", "化肥");
                intent.putExtra("classId", huaFeiClassId);
                startActivity(intent);
                break;
            case R.id.my_login_cancel:
                Intent intent1 = new Intent(ShoppingCartActivity.this,
                        GoodsListActivity.class);
                intent1.putExtra("className", "汽车");
                intent1.putExtra("classId", carClassId);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResponsed(Request req) {
        shopCart_list.onRefreshComplete();
        disMissDialog();
        //获得购物车列表
        int total_count = 0;
        if (req.getApi() == ApiType.GET_SHOPCART_LIST) {
            if (req.getData().getStatus().equals("1000")) {
                GetshopCart res = (GetshopCart) req.getData();
                List<shopCart> rows = res.datas.rows;
                if (rows == null || rows.size() == 0) {
                    if (adapter != null) {
                        adapter.clear();
                    } else {
                        adapter = new shopCartAdapter();
                        adapter.clear();
                        shopCart_expandListView.setAdapter(adapter);
                    }
                    setTitle("购物车");
                    ordering_sum_pri.setText("¥0");
                    toPay_ll.setVisibility(View.GONE);
                    null_shop_cart_layout.setVisibility(View.VISIBLE);
                    hideRight();
                    return;
                }
                total_count = res.datas.totalCount;
                total = res.datas.total;
                offCount = res.datas.offlineEntryCount;

                toPay_ll.setVisibility(View.VISIBLE);
                null_shop_cart_layout.setVisibility(View.GONE);
                setTitle("购物车" + "(" + total_count + ")");
                showRightTextView();

                if (data != null && data.category != null && data.category.size() > 0) {
                    data.category.clear();
                }
                //保存到本地此购物车的Id
                SPUtils.put(ShoppingCartActivity.this, "shopCartId",
                        res.datas.shopCartId);
                //转移到实体类中
                for (int i = 0; i < rows.size(); i++) {
                    Category c = new Category();
                    c.title = rows.get(i).brandName;
                    c.offCount = rows.get(i).offlineEntryCount;
                    c.goods = new ArrayList<>();
                    for (int j = 0; j < rows.get(i).SKUList.size(); j++) {
                        Goods goods = new Goods();
                        //商品的四个属性
                        goods.id = rows.get(i).SKUList.get(j).goodsId;//id
                        goods.product_id = rows.get(i).SKUList.get(j).product_id;
                        goods.SKUId = rows.get(i).SKUList.get(j)._id;//SKUId
                        goods.name = rows.get(i).SKUList.get(j).productName;//名称
                        goods.num = Integer
                                .parseInt(rows.get(i).SKUList.get(j).count);//数量
                        goods.pic = rows.get(i).SKUList.get(j).imgUrl;//图片

                        //是否online
                        goods.online = rows.get(i).SKUList.get(j).online;

                        //价格
                        if (StringUtil
                                .empty(rows.get(i).SKUList.get(j).price)) {
                            goods.xianjia = 0;
                        } else {
                            goods.xianjia = Float.parseFloat(rows.get(i).SKUList
                                    .get(j).price);
                        }


                        //定金
                        if (StringUtil.empty(rows.get(i).SKUList.get(j).deposit)) {
                            goods.dingjin = 0;
                        } else {
                            goods.dingjin = Float.parseFloat(rows.get(i).SKUList
                                    .get(j).deposit);
                        }
                        //Sku属性
                        StringBuilder stringSku = new StringBuilder();
                        if (rows.get(i).SKUList.get(j).attributes != null && !rows.get(i).SKUList.get(j).attributes.isEmpty()) {
                            for (int k = 0; k < rows.get(i).SKUList.get(j).attributes.size(); k++) {
                                if (StringUtil.checkStr(rows.get(i).SKUList.get(j).attributes.get(k).name)
                                        && StringUtil.checkStr(rows.get(i).SKUList.get(j).attributes.get(k).value)) {
                                    stringSku.append(rows.get(i).SKUList.get(j).attributes.get(k).name).append(":").append(rows.get(i).SKUList.get(j).attributes.get(k).value).append(";");
                                }
                            }
                            goods.attr = stringSku.toString().substring(0, stringSku.toString().length() - 1);
                        }

                        //附加选项
                        StringBuilder stringAddition = new StringBuilder();
                        if (rows.get(i).SKUList.get(j).additions != null && !rows.get(i).SKUList.get(j).additions.isEmpty()) {
                            goods.jsonList = rows.get(i).SKUList.get(j).additions;
                            stringAddition.append("附加项目:");
                            goods.additionPrice = 0;
                            for (int k = 0; k < rows.get(i).SKUList.get(j).additions.size(); k++) {
                                if (StringUtil.checkStr(rows.get(i).SKUList.get(j).additions.get(k).name)) {
                                    stringAddition.append(rows.get(i).SKUList.get(j).additions.get(k).name).append(";");
                                    try {
                                        goods.additionPrice += Double.parseDouble(rows.get(i).SKUList.get(j).additions.get(k).price + "");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                            goods.additions = stringAddition.substring(0, stringAddition.length() - 1);
                        }
                        //附加选项的List(用于query)
                        c.goods.add(goods);
                    }
                    data.category.add(c);
                }
                if (adapter == null) {
                    adapter = new shopCartAdapter();
                    shopCart_expandListView.setAdapter(adapter);
                }
                adapter.notifyDataSetChanged();
                //获得商品的总价格
                notifyCheckedChanged();
                if (adapter != null) {
                    for (int i = 0; i < adapter.getGroupCount(); i++) {
                        shopCart_expandListView.expandGroup(i);
                    }
                }

            }


            //改变数量
        } else if (req.getApi() == ApiType.CHANGE_NUM) {
            if ("1000".equals(req.getData().getStatus())) {
                if (isQuery) {
                    // 加载用户购物车数据  GET 请求
                    RequestParams params = new RequestParams();
                    if (isLogin()) {
                        params.put("userId", Store.User.queryMe().userid);
                        execApi(ApiType.GET_SHOPCART_LIST.setMethod(ApiType.RequestMethod.GET), params);
                    }
                }
                isQuery = true;

            }
            //获得本地的商品列表
        } else if (req.getApi() == ApiType.GET_LOCAL_SHOPCART_LIST) {
            GetshopCart res = (GetshopCart) req.getData();
            List<shopCart> rows = res.datas.rows;
            if (rows == null || rows.size() == 0) {
                if (adapter != null) {
                    adapter.clear();
                } else {
                    adapter = new shopCartAdapter();
                    adapter.clear();
                    shopCart_expandListView.setAdapter(adapter);
                }
                ordering_sum_pri.setText("¥0");
                setTitle("购物车");
                toPay_ll.setVisibility(View.GONE);
                null_shop_cart_layout.setVisibility(View.VISIBLE);
                hideRight();
                return;
            }
            total_count = res.datas.totalCount;
            total = res.datas.total;
            offCount = res.datas.offlineEntryCount;
            setTitle("购物车" + "(" + total_count + ")");
            toPay_ll.setVisibility(View.VISIBLE);
            null_shop_cart_layout.setVisibility(View.GONE);
            showRightTextView();
            if (data != null && data.category.size() > 0) {
                data.category.clear();
            }
            // 转义到自定义的实体类中
            for (int i = 0; i < rows.size(); i++) {
                Category c = new Category();
                c.title = rows.get(i).brandName;
                c.goods = new ArrayList<>();
                for (int j = 0; j < rows.get(i).SKUList.size(); j++) {
                    //商品的四个属性 id name count imageUrl //SKUId;
                    Goods goods = new Goods();
                    goods.id = rows.get(i).SKUList.get(j).goodsId;
                    goods.product_id = rows.get(i).SKUList.get(j).product_id;
                    goods.SKUId = rows.get(i).SKUList.get(j)._id;//SKUId
                    goods.name = rows.get(i).SKUList.get(j).productName;
                    goods.num = Integer
                            .parseInt(rows.get(i).SKUList.get(j).count);
                    goods.pic = rows.get(i).SKUList.get(j).imgUrl;

                    //是否online
                    goods.online = rows.get(i).SKUList.get(j).online;

                    //商品价格
                    if (StringUtil
                            .empty(rows.get(i).SKUList.get(j).price)) {
                        goods.xianjia = 0;
                    } else {
                        goods.xianjia = Float.parseFloat(rows.get(i).SKUList
                                .get(j).price);
                    }
                    //商品定金
                    if (StringUtil.empty(rows.get(i).SKUList.get(j).deposit)) {
                        goods.dingjin = 0;
                    } else {
                        goods.dingjin = Float.parseFloat(rows.get(i).SKUList
                                .get(j).deposit);
                    }

                    //Sku属性
                    StringBuilder stringSku = new StringBuilder();
                    if (rows.get(i).SKUList.get(j).attributes != null && !rows.get(i).SKUList.get(j).attributes.isEmpty()) {
                        for (int k = 0; k < rows.get(i).SKUList.get(j).attributes.size(); k++) {
                            if (StringUtil.checkStr(rows.get(i).SKUList.get(j).attributes.get(k).name)
                                    && StringUtil.checkStr(rows.get(i).SKUList.get(j).attributes.get(k).value)) {
                                stringSku.append(rows.get(i).SKUList.get(j).attributes.get(k).name).append(":").append(rows.get(i).SKUList.get(j).attributes.get(k).value).append(";");
                            }
                        }
                        goods.attr = stringSku.substring(0, stringSku.length() - 1);
                    }


                    //附加选项
                    StringBuilder stringAddition = new StringBuilder();
                    if (rows.get(i).SKUList.get(j).additions != null && !rows.get(i).SKUList.get(j).additions.isEmpty()) {
                        goods.jsonList = rows.get(i).SKUList.get(j).additions;
                        stringAddition.append("附加项目:");
                        goods.additionPrice = 0;
                        for (int k = 0; k < rows.get(i).SKUList.get(j).additions.size(); k++) {
                            if (StringUtil.checkStr(rows.get(i).SKUList.get(j).additions.get(k).name)) {
                                stringAddition.append(rows.get(i).SKUList.get(j).additions.get(k).name).append(";");
                                try {
                                    goods.additionPrice += Double.parseDouble(rows.get(i).SKUList.get(j).additions.get(k).price + "");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        goods.additions = stringAddition.substring(0, stringAddition.length() - 1);
                    }

                    c.goods.add(goods);
                }
                data.category.add(c);
            }
            adapter = new shopCartAdapter();
            shopCart_expandListView.setAdapter(adapter);
            notifyCheckedChanged();
            if (adapter != null) {
                for (int i = 0; i < adapter.getGroupCount(); i++) {
                    shopCart_list.getRefreshableView().expandGroup(i);
                }
            }
        }
    }

    private int showNum;

    //外层的购物车，已店铺区分
    public class shopCartAdapter extends BaseExpandableListAdapter {

        public void clear() {
            if (data.category != null) {
                data.category.clear();
                notifyDataSetChanged();
            }
        }

        @Override
        public int getGroupCount() {
            if (data.category != null) {
                return data.category.size() > 0 ? data.category.size() : 0;
            }
            return 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (data.category != null) {
                Category category = data.category.get(groupPosition);
                if (category != null) {
                    if (category.goods != null && !category.goods.isEmpty()) {
                        return category.goods.size();
                    }
                }
            }

            return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            if (data.category != null) {
                return data.category.get(groupPosition);
            }
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            if (data.category != null) {
                Category category = data.category.get(groupPosition);
                if (category != null) {
                    if (category.goods != null && !category.goods.isEmpty()) {
                        return category.goods.get(childPosition);
                    }
                }
            }
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ShoppingCartActivity.this)
                        .inflate(R.layout.item_shopcart_list, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.car_name.setText(data.category.get(groupPosition).title);
            //是否店铺内商品全属于下架商品
            if (data.category.get(groupPosition).goods.size() - data.category.get(groupPosition).offCount == 0) {
                holder.shop_checkBox.setVisibility(View.INVISIBLE);
            } else {
                holder.shop_checkBox.setVisibility(View.VISIBLE);
            }
            //是否显示分割线
            if (groupPosition == 0) {
                holder.expandListViewDivider.setVisibility(View.GONE);
            } else {
                holder.expandListViewDivider.setVisibility(View.VISIBLE);
            }
            //设置其是否选中
            Boolean isChecked = inShopMap.get(data.category.get(groupPosition).title);
            if (isChecked != null && isChecked) {
                holder.shop_checkBox.setChecked(true);
            } else {
                holder.shop_checkBox.setChecked(false);
            }
            //通过店铺全选店铺下的商品
            holder.shop_checkBox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean checked = holder.shop_checkBox.isChecked();
                    if (checked) {
                        inShopMap.put(data.category.get(groupPosition).title, true);
                        if (inShopMap.size() == data.category.size()) {
                            // 如果所有店铺都被选中，则点亮全选按钮
                            mBtnCheckAll.setChecked(true);
                        }
                        //选中店铺下的全部商品
                        for (int i = 0; i < data.category.get(groupPosition).goods.size(); i++) {
                            if (data.category.get(groupPosition).goods.get(i).online) {
                                inCartMap.put(data.category.get(groupPosition).goods.get(i).SKUId, true);
                            }
                        }
                    } else {

                        if (inShopMap.size() == data.category.size()) {
                            // 如果所有店铺都被选中，则取消全选按钮
                            mBtnCheckAll
                                    .setOnCheckedChangeListener(null);
                            mBtnCheckAll.setChecked(false);
                            mBtnCheckAll
                                    .setOnCheckedChangeListener(checkAllListener);
                        }
                        inShopMap.remove(data.category.get(groupPosition).title);
                        for (int i = 0; i < data.category.get(groupPosition).goods.size(); i++) {
                            inCartMap.remove(data.category.get(groupPosition).goods.get(i).SKUId);
                        }
                    }
                    //更新状态
                    notifyDataSetChanged();
                    notifyCheckedChanged();
                    notifyNumChanged();
                }
            });
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ShoppingCartActivity.this)
                        .inflate(R.layout.item_item_shopcart_list, null);
                convertView.setTag(new ViewHolderChild(convertView));
            }
            final ViewHolderChild holder = (ViewHolderChild) convertView.getTag();
            if (data.category != null) {
                final Category category = data.category.get(groupPosition);
                if (category != null) {
                    final List<Goods> goodsList = category.goods;
                    if (goodsList != null && !goodsList.isEmpty()) {
                        if (goodsList.get(childPosition) != null) {
                            showNum = goodsList.get(childPosition).num;
                            //数量
                            if (showNum != 0) {
                                holder.ordering_item_geshu.setText(showNum + "");
                            } else {
                                holder.ordering_item_geshu.setText("");
                            }
                            //图片
                            ImageLoader.getInstance().displayImage(
                                    MsgID.IP + goodsList.get(childPosition).pic, holder.ordering_item_img);
                            //名称
                            if (StringUtil.checkStr(goodsList.get(childPosition).name)) {
                                holder.ordering_item_name.setText(goodsList.get(childPosition).name);
                            } else {
                                holder.ordering_item_name.setText("");
                            }
                            //删除按钮
                            if (isEdit && !goodsList.get(childPosition).online) {
                                holder.ordering_now_delete.setVisibility(View.VISIBLE);
                            } else {
                                holder.ordering_now_delete.setVisibility(View.GONE);
                            }
                            //删除单个商品 这里只用于删除失效商品
                            holder.ordering_now_delete.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CustomDialog.Builder builder = new CustomDialog.Builder(
                                            ShoppingCartActivity.this);
                                    builder.setMessage("您确定要删除吗？")
                                            .setPositiveButton("是",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog,
                                                                            int which) {
                                                            if (isLogin()) {
                                                                RequestParams params = new RequestParams();
                                                                if (isLogin()) {
                                                                    params.put("userId", Store.User.queryMe().userid);
                                                                }
                                                                params.put("SKUId", goodsList.get(childPosition).SKUId);
                                                                params.put("quantity", "0");
                                                                execApi(ApiType.CHANGE_NUM,
                                                                        params);
                                                                showProgressDialog("删除中");

                                                            } else {
                                                                dao.deleteShopping(goodsList.get(childPosition).SKUId);
                                                                getData();
                                                                showToast("商品删除成功");
                                                            }
                                                            dialog.dismiss();
                                                        }
                                                    })
                                            .setNegativeButton("否",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog,
                                                                            int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                    CustomDialog dialog = builder.create();
                                    dialog.show();
                                }
                            });

                            holder.swipeLayout.close();
                            holder.shopCart_swipe_layout_child_lin.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CustomDialog.Builder builder = new CustomDialog.Builder(
                                            ShoppingCartActivity.this);
                                    builder.setMessage("您确定要删除吗？")
                                            .setPositiveButton("是",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog,
                                                                            int which) {
                                                            if (isLogin()) {
                                                                RequestParams params = new RequestParams();
                                                                if (isLogin()) {
                                                                    params.put("userId", Store.User.queryMe().userid);
                                                                }
                                                                params.put("SKUId", goodsList.get(childPosition).SKUId);
                                                                params.put("quantity", "0");
                                                                execApi(ApiType.CHANGE_NUM,
                                                                        params);
                                                                showProgressDialog("删除中");

                                                            } else {
                                                                dao.deleteShopping(goodsList.get(childPosition).SKUId);
                                                                getData();
                                                                showToast("商品删除成功");
                                                            }
                                                            dialog.dismiss();
                                                        }
                                                    })
                                            .setNegativeButton("否",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog,
                                                                            int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                    CustomDialog dialog = builder.create();
                                    dialog.show();
                                }
                            });


                            //是否online
                            if (goodsList.get(childPosition).online) {

                                convertView.setBackgroundColor(Color.WHITE);
                                holder.btn_check_item_offline.setVisibility(View.GONE);

                                holder.ordering_item_jia.setVisibility(View.VISIBLE);
                                holder.ordering_item_jian.setVisibility(View.VISIBLE);
                                holder.ordering_item_jia.setEnabled(true);
                                holder.ordering_item_jian.setEnabled(true);
                                holder.checkBox_item.setVisibility(View.VISIBLE);

                                holder.step1.setTextColor(getResources().getColor(R.color.black_goods_titile));
                                holder.step2.setTextColor(getResources().getColor(R.color.black_goods_titile));
                                holder.goods_car_weikuan.setTextColor(getResources().getColor(R.color.black_goods_titile));
                                holder.goods_car_deposit.setTextColor(getResources().getColor(R.color.orange_goods_price));
                                holder.ordering_now_pri.setTextColor(getResources().getColor(R.color.black_goods_titile));
                                holder.ordering_item_geshu.setBackgroundResource(R.drawable.round_line_gary_write_1px);
                            } else {


                                convertView.setBackgroundColor(getResources().getColor(R.color.list_bg));
                                holder.btn_check_item_offline.setVisibility(View.VISIBLE);

                                holder.ordering_item_jia.setVisibility(View.INVISIBLE);
                                holder.ordering_item_jian.setVisibility(View.INVISIBLE);
                                holder.ordering_item_jia.setEnabled(false);
                                holder.ordering_item_jian.setEnabled(false);
                                holder.checkBox_item.setVisibility(View.GONE);

                                holder.ordering_item_geshu.setBackgroundColor(getResources().getColor(R.color.list_bg));

                                holder.step1.setTextColor(getResources().getColor(R.color.main_index_gary));
                                holder.step2.setTextColor(getResources().getColor(R.color.main_index_gary));
                                holder.goods_car_weikuan.setTextColor(getResources().getColor(R.color.main_index_gary));
                                holder.goods_car_deposit.setTextColor(getResources().getColor(R.color.main_index_gary));
                                holder.ordering_now_pri.setTextColor(getResources().getColor(R.color.main_index_gary));
                            }
                            //是否有订金 有的话显示阶段 ，否则不显示
                            if (goodsList.get(childPosition).dingjin == 0) {
                                if (goodsList.get(childPosition).online) {
                                    holder.ordering_now_pri.setTextColor(getResources().getColor(R.color.orange_goods_price));
                                }
                                holder.goods_car_bar.setVisibility(View.GONE);
                            } else {
                                if (goodsList.get(childPosition).online) {
                                    holder.ordering_now_pri.setTextColor(getResources().getColor(R.color.black_goods_titile));
                                }
                                holder.goods_car_bar.setVisibility(View.VISIBLE);
                                holder.goods_car_deposit.setText("¥" + StringUtil.toTwoString(goodsList
                                        .get(childPosition).dingjin * goodsList.get(childPosition).num + ""));
                                if (goodsList.get(childPosition).additionPrice == 0) {
                                    holder.goods_car_weikuan.setText("¥" + StringUtil.toTwoString((goodsList.get(childPosition).xianjia - goodsList
                                            .get(childPosition).dingjin) * goodsList.get(childPosition).num + ""));
                                } else {
                                    holder.goods_car_weikuan.setText("¥" + StringUtil.toTwoString((goodsList.get(childPosition).xianjia + goodsList.get(childPosition).additionPrice - goodsList
                                            .get(childPosition).dingjin) * goodsList.get(childPosition).num + ""));
                                }

                            }
                            //sku属性 和附加选项
                            if (StringUtil.checkStr(goodsList.get(childPosition).attr)) {
                                holder.ordering_item_attr.setText(goodsList.get(childPosition).attr);
                            } else {
                                holder.ordering_item_attr.setText("");
                            }
                            if (StringUtil.checkStr(goodsList.get(childPosition).additions)) {

                                holder.additions_lin.setVisibility(View.VISIBLE);
                                holder.additions_text.setText(goodsList.get(childPosition).additions);
                                holder.additions_price.setText("¥" + StringUtil.toTwoString(goodsList.get(childPosition).additionPrice + ""));
                                if (goodsList.get(childPosition).online) {
                                    holder.additions_price.setTextColor(getResources().getColor(R.color.black_goods_titile));
                                    holder.additions_text.setTextColor(getResources().getColor(R.color.black_goods_titile));
                                } else {
                                    holder.additions_price.setTextColor(getResources().getColor(R.color.main_index_gary));
                                    holder.additions_text.setTextColor(getResources().getColor(R.color.main_index_gary));
                                }
                            } else {
                                holder.additions_text.setText("");
                                holder.additions_lin.setVisibility(View.GONE);
                            }

                            //sku单价
                            holder.ordering_now_pri.setText("¥" + StringUtil.toTwoString(goodsList
                                    .get(childPosition).xianjia + ""));

                            // 避免由于复用触发onChecked()事件
                            holder.checkBox_item.setOnCheckedChangeListener(null);
                            Boolean isChecked = inCartMap.get(goodsList.get(childPosition).SKUId);
                            if (isChecked != null && isChecked) {
                                holder.checkBox_item.setChecked(true);
                            } else {
                                holder.checkBox_item.setChecked(false);
                            }
                            //是否选中
                            holder.checkBox_item.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        inCartMap.put(goodsList.get(childPosition).SKUId, true);
                                        // 如果所有项都被选中，则点亮全选按钮
                                        if (inCartMap.size() == total - offCount) {
                                            mBtnCheckAll.setChecked(true);
                                        }
                                        // 如果一个店铺内的所有项都被选中 ，则点亮店铺按钮
                                        int count = 0;
                                        for (int i = 0; i < goodsList.size(); i++) {
                                            Boolean aBoolean = inCartMap.get(goodsList.get(i).SKUId);
                                            if (aBoolean != null && aBoolean) {
                                                count++;
                                            }
                                        }
                                        if (count == goodsList.size() - category.offCount) {
                                            inShopMap.put(category.title, true);
                                            adapter.notifyDataSetChanged();
                                        }

                                    } else {
                                        // 如果之前是全选状态，则取消全选状态
                                        if (inCartMap.size() == total - offCount) {
                                            mBtnCheckAll
                                                    .setOnCheckedChangeListener(null);
                                            mBtnCheckAll.setChecked(false);
                                            mBtnCheckAll
                                                    .setOnCheckedChangeListener(checkAllListener);
                                        }
                                        // 如果一个店铺内的所有项都被选中 ，则点亮店铺按钮
                                        int count = 0;
                                        for (int i = 0; i < goodsList.size(); i++) {
                                            Boolean aBoolean = inCartMap.get(goodsList.get(i).SKUId);
                                            if (aBoolean != null && aBoolean) {
                                                count++;
                                            }
                                        }
                                        if (count == goodsList.size() - category.offCount) {
                                            inShopMap.remove(category.title);
                                            adapter.notifyDataSetChanged();
                                        }
                                        inCartMap.remove(goodsList.get(childPosition).SKUId);
                                    }
                                    notifyCheckedChanged();
                                }


                            });

                            //修改商品数量
                            if (goodsList.get(childPosition).online) {
                                holder.ordering_item_geshu.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CustomDialogForShopCarCount.Builder builder = new CustomDialogForShopCarCount.Builder(
                                                ShoppingCartActivity.this);
                                        car_num = Integer.parseInt(holder.ordering_item_geshu.getText().toString().trim());
                                        builder.setMessage("修改购买数量").setEditText(car_num + "")
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        String str = CustomDialogForShopCarCount.editText.getText().toString().trim();
                                                        if (StringUtil.checkStr(str)) {
                                                            int i;
                                                            try {
                                                                i = Integer.parseInt(str);
                                                            } catch (NumberFormatException e) {
                                                                i = 1;
                                                            }
                                                            if (i != 0) {
                                                                holder.ordering_item_geshu.setText(str);
                                                                if (isLogin()) {
                                                                    RequestParams params = new RequestParams();
                                                                    if (isLogin()) {
                                                                        params.put("userId", Store.User.queryMe().userid);
                                                                    }
                                                                    params.put("SKUId", goodsList.get(childPosition).SKUId);
                                                                    params.put("quantity", Integer.valueOf(str));
                                                                    execApi(ApiType.CHANGE_NUM, params);
                                                                    showProgressDialog("提交中");
                                                                } else {
                                                                    goodsList.get(childPosition).num = Integer.valueOf(str);
                                                                    dao.updateShopping(goodsList.get(childPosition).SKUId,
                                                                            holder.ordering_item_geshu.getText().toString()
                                                                                    .trim());
                                                                    RndLog.i("pid..............jian1", goodsList.size()
                                                                            + "name,," + goodsList.get(childPosition).name
                                                                            + "SKUId--" + goodsList.get(childPosition).SKUId
                                                                            + "///////");
                                                                    notifyDataSetChanged();
                                                                    // 如果被选中，更新价格
                                                                    if (holder.checkBox_item.isChecked()) {
                                                                        notifyCheckedChanged();
                                                                    }
                                                                    notifyNumChanged();
                                                                }
                                                                dialog.dismiss();

                                                            } else {
                                                                showToast("请输入正确的商品数量哟");
                                                            }
                                                        } else {
                                                            showToast("请输入正确的商品数量哟");
                                                        }

                                                    }
                                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        //编辑框
                                        builder.SetLiftButton(new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                car_num = Integer.parseInt(CustomDialogForShopCarCount.editText.getText().toString().trim());
                                                if (car_num == 1) {
                                                    showToast("商品不能再减少了哦");

                                                } else {
                                                    car_num--;
                                                    CustomDialogForShopCarCount.editText.setText(car_num + "");
                                                    // 光标移到最后
                                                    Editable eText = CustomDialogForShopCarCount.editText.getText();
                                                    Selection.setSelection(eText, eText.length());
                                                }

                                            }
                                        }).SetRightButton(new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                car_num = Integer.parseInt(CustomDialogForShopCarCount.editText.getText().toString().trim());
                                                if (car_num >= 9999) {
                                                    showToast("商品数量不能大于9999");

                                                } else {
                                                    car_num++;
                                                    CustomDialogForShopCarCount.editText.setText(car_num + "");
                                                    // 光标移到最后
                                                    Editable eText = CustomDialogForShopCarCount.editText.getText();
                                                    Selection.setSelection(eText, eText.length());
                                                }

                                            }
                                        });
                                        CustomDialogForShopCarCount dialog = builder.create();
                                        dialog.setCanceledOnTouchOutside(false);
                                        dialog.show();

                                    }

                                });
                            } else {
                                holder.ordering_item_geshu.setOnClickListener(null);
                                holder.ordering_item_geshu.setTextColor(getResources().getColor(R.color.main_index_gary));
                                holder.ordering_item_geshu.setText("X" + goodsList.get(childPosition).num);
                            }

                            // 减少
                            holder.ordering_item_jian.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (goodsList.get(childPosition).num <= 1) {
                                        ShoppingCartActivity.this.showToast("商品不能再减少了哦");
                                        return;
                                    }

                                    goodsList.get(childPosition).num--;
                                    holder.ordering_item_geshu.setText(goodsList.get(childPosition).num
                                            + "");
                                    if (isLogin()) {
                                        RequestParams params = new RequestParams();
                                        if (isLogin()) {
                                            params.put("userId", Store.User.queryMe().userid);
                                        }
                                        params.put("SKUId", goodsList.get(childPosition).SKUId);
                                        params.put("quantity", -1);
                                        params.put("update_by_add", "true");
                                        execApi(ApiType.CHANGE_NUM, params);
                                        showProgressDialog("提交中");
                                        isQuery = false;
                                    } else {
                                        dao.updateShopping(goodsList.get(childPosition).SKUId,
                                                holder.ordering_item_geshu.getText().toString()
                                                        .trim());
                                        RndLog.i("pid..............jian1", goodsList.size()
                                                + "name,," + goodsList.get(childPosition).name
                                                + "SKUId--" + goodsList.get(childPosition).SKUId
                                                + "///////");

                                    }

                                    notifyDataSetChanged();
                                    // 如果被选中，更新价格
                                    if (holder.checkBox_item.isChecked()) {
                                        notifyCheckedChanged();
                                    }
                                    notifyNumChanged();

                                }
                            });
                            // 增加
                            holder.ordering_item_jia.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (goodsList.get(childPosition).num >= 9999) {
                                        ShoppingCartActivity.this.showToast("商品数量不能大于9999");
                                        return;
                                    }
                                    goodsList.get(childPosition).num++;
                                    holder.ordering_item_geshu.setText(goodsList.get(childPosition).num
                                            + "");
                                    if (isLogin()) {
                                        showProgressDialog("修改中..");
                                        RequestParams params = new RequestParams();
                                        if (isLogin()) {
                                            params.put("userId", Store.User.queryMe().userid);
                                        }
                                        params.put("SKUId", goodsList.get(childPosition).SKUId);
                                        params.put("quantity", goodsList.get(childPosition).num);
                                        execApi(ApiType.CHANGE_NUM, params);
                                        isQuery = false;
                                    } else {


                                        dao.updateShopping(goodsList.get(childPosition).SKUId,
                                                holder.ordering_item_geshu.getText().toString()
                                                        .trim());
                                        RndLog.i("pid..............jia1", goodsList.size()
                                                + "name,," + goodsList.get(childPosition).name
                                                + "SKUId--" + goodsList.get(childPosition).SKUId
                                                + "///////");

                                    }

                                    notifyDataSetChanged();
                                    // 如果被选中，更新价格
                                    if (holder.checkBox_item.isChecked()) {
                                        notifyCheckedChanged();
                                    }
                                    notifyNumChanged();
                                }
                            });
                            //点击 商品图片 或者 标题所在的布局 去商品详情
                            holder.ordering_item_img.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ShoppingCartActivity.this, GoodsDetailActivity.class);
                                    intent.putExtra("goodId", goodsList.get(childPosition).id);
                                    startActivity(intent);

                                }
                            });

                            holder.shoppingCar_attr_name_ll.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ShoppingCartActivity.this, GoodsDetailActivity.class);
                                    intent.putExtra("goodId", goodsList.get(childPosition).id);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                }


            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        class ViewHolder {
            private TextView car_name;
            private CheckBox shop_checkBox;
            private TextView expandListViewDivider;

            public ViewHolder(View convertView) {
                shop_checkBox = (CheckBox) convertView.findViewById(R.id.check_box_item);
                expandListViewDivider = (TextView) convertView.findViewById(R.id.expandListViewDivider);
                ExpandViewTouch.expandViewTouchDelegate(shop_checkBox, 100, 100, 100, 100);
                car_name = (TextView) convertView.findViewById(R.id.car_name);
            }
        }


        class ViewHolderChild {
            private LinearLayout goods_car_bar, additions_lin, shoppingCar_attr_name_ll;
            private ImageView ordering_item_jian, ordering_item_jia, ordering_item_img, ordering_now_delete;
            private CheckBox checkBox_item;
            private TextView ordering_item_geshu, ordering_item_attr, btn_check_item_offline;
            private TextView ordering_now_pri, ordering_item_name, goods_car_deposit, goods_car_weikuan;
            private TextView step1, step2, additions_text, additions_price;
            private SwipeLayout swipeLayout;
            private LinearLayout shopCart_swipe_layout_child_lin;

            public ViewHolderChild(View convertView) {
                goods_car_bar = (LinearLayout) convertView.findViewById(R.id.goods_car_item_bar);
                ordering_item_jian = (ImageView) convertView//商品-
                        .findViewById(R.id.ordering_item_jian1);
                ordering_item_jia = (ImageView) convertView//商品+
                        .findViewById(R.id.ordering_item_jia1);
                ordering_item_img = (ImageView) convertView//商品图
                        .findViewById(R.id.ordering_item_img);
                checkBox_item = (CheckBox) convertView.findViewById(R.id.btn_check_item_item);
                ExpandViewTouch.expandViewTouchDelegate(checkBox_item, 100, 100, 100, 100);
                ordering_now_delete = (ImageView) convertView
                        .findViewById(R.id.ordering_now_delete);
                ExpandViewTouch.expandViewTouchDelegate(ordering_now_delete, 100, 100, 100, 100);
                ordering_item_geshu = (TextView) convertView//商品个数
                        .findViewById(R.id.ordering_item_geshu);
                btn_check_item_offline = (TextView) convertView    //已下架
                        .findViewById(R.id.btn_check_item_offline);
                ordering_now_pri = (TextView) convertView//商品价格
                        .findViewById(R.id.ordering_now_pri);
                ordering_item_name = (TextView) convertView//商品名
                        .findViewById(R.id.ordering_item_name);
                ordering_item_attr = (TextView) convertView
                        .findViewById(R.id.ordering_item_attr);//商品Sku属性
                goods_car_deposit = (TextView) convertView//汽车定金
                        .findViewById(R.id.goods_car_item_bar_deposit);
                goods_car_weikuan = (TextView) convertView//汽车尾款
                        .findViewById(R.id.goods_car_item_bar_weikuan);
                shoppingCar_attr_name_ll = (LinearLayout) convertView.findViewById(R.id.shoppingCar_attr_name_ll);

                step1 = (TextView) convertView.findViewById(R.id.goods_car_item_bar_step1);
                step2 = (TextView) convertView.findViewById(R.id.goods_car_item_bar_step2);

                additions_lin = (LinearLayout) convertView.findViewById(R.id.additions_lin);//附加选项所在的布局
                additions_text = (TextView) convertView//附加选项
                        .findViewById(R.id.additions_text);
                additions_price = (TextView) convertView//附加选项价格
                        .findViewById(R.id.additions_price);

                swipeLayout = (SwipeLayout) convertView.findViewById(R.id.shopCart_swipe_layout);
                //set show mode.
                swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
                //set drag edge.
                swipeLayout.setDragEdge(SwipeLayout.DragEdge.Bottom);
                shopCart_swipe_layout_child_lin = (LinearLayout) convertView.findViewById(R.id.shopCart_swipe_layout_child_lin);
            }
        }

    }


    //每次回到购物车 ，清除之前的状态
    @Override
    protected void onResume() {
        super.onResume();
        inCartMap.clear();
        inShopMap.clear();
        if (adapter != null) {
            adapter.clear();
        } else {
            adapter = new shopCartAdapter();
            adapter.clear();
            shopCart_expandListView.setAdapter(adapter);
        }
        isEdit = true;
        mBtnCheckAll.setChecked(false);
        editInCart();
        notifyCheckedChanged();
        null_shop_cart_layout
                .setVisibility(View.GONE);
        showProgressDialog();
        getData();
    }
}



