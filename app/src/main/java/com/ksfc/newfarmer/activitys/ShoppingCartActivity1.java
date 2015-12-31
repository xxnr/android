package com.ksfc.newfarmer.activitys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.ShoppingCartActivity1.Data.Category;
import com.ksfc.newfarmer.activitys.ShoppingCartActivity1.Data.Goods;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.db.dao.ShoppingDao;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.ChangeNum;
import com.ksfc.newfarmer.protocol.beans.GetshopCart;
import com.ksfc.newfarmer.protocol.beans.GetshopCart.shopCart;
import com.ksfc.newfarmer.widget.CustomDialog;
import com.ksfc.newfarmer.widget.CustomDialogForShopCarCount;
import com.ksfc.newfarmer.utils.ExpandViewTouch;
import com.ksfc.newfarmer.utils.PullToRefreshUtils;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.SPUtils;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.PullToRefreshView;
import com.ksfc.newfarmer.widget.WidgetUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.text.TextUtils;
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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ShoppingCartActivity1 extends BaseActivity {
    private PullToRefreshListView shopcart_list;

    private LinearLayout topay_ll;
    private RelativeLayout null_shop_cart_layout;

    private static ShoppingCartActivity1 instance;
    private TextView goto_pay_tv;
    public TextView ordering_sum_pri;// 合计
    Data data = null;
    ShoppingDao dao;
    public static List<Goods> list;
    public String str = "";// 数据向服务器传递时传递的报文

    private List<Map> allShoppings;
    private List<Map<String, String>> localToNet;
    private shopcartAdapter adapter;
    private boolean isEdit; // 是否正在编辑
    private LinearLayout goods_bar_price;

    private double price; // 总价
    private int num; // 选中的商品数
    private int total_count = 0;
    private int total = 0;
    private HashMap<String, Boolean> inCartMap = new HashMap<String, Boolean>();// 用于存放选中的项
    private CheckBox mBtnCheckAll;

    private EditText dialog_edit;
    //选择监听器
    private OnCheckedChangeListener checkAllListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            mBtnCheckAll.setChecked(isChecked);
            if (isChecked) {
                checkAll();
            } else {
                inCartMap.clear();
            }
            notifyCheckedChanged();
            if (adapter != null && carAdapter != null) {
                adapter.notifyDataSetChanged();
                carAdapter.notifyDataSetChanged();
            }
        }
    };
    private int totlalNum;
    private int car_num = 1; //对话框里的数量
    private EditText editButton; //对话框的edit
    private PullToRefreshView option_pull;


    /**
     * 全选，将数据加入inCartMap
     */
    private void checkAll() {
        for (int i = 0; i < data.category.size(); i++) {
            for (int j = 0; j < data.category.get(i).goods.size(); j++) {
                inCartMap.put(data.category.get(i).goods.get(j).id, true);
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
                Boolean isChecked = inCartMap.get(data.category.get(i).goods.get(j).id);
                if (isChecked != null && isChecked) {
                    Goods good = data.category.get(i).goods.get(j);
                    num += good.num;

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
        price = 0;
        num = 0;
        totlalNum = 0;
        for (int i = 0; i < data.category.size(); i++) {
            for (int j = 0; j < data.category.get(i).goods.size(); j++) {
                totlalNum += data.category.get(i).goods.get(j).num;
            }
        }
        setTitle("购物车" + "(" + totlalNum + ")");
    }

    /**
     * 获取实例对象
     */
    public static ShoppingCartActivity1 getInstance() {
        if (instance == null) {
            instance = new ShoppingCartActivity1();
        }
        return instance;
    }


    @Override
    public int getLayout() {
        return R.layout.shop_cart_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        data = new Data();
        data.category = new ArrayList<Category>();
        setTitle("购物车");
        hideLeft();
        showRightTextView();
        setRightTextView("编辑");
        setRightTextViewListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editInCart();
            }
        });
        initView();
    }

    /**
     * 编辑
     */
    private void editInCart() {
        isEdit = !isEdit;
        if (isEdit) {
            goods_bar_price.setVisibility(View.INVISIBLE);
            setRightTextView("完成");
            goto_pay_tv.setText("删除" + "(" + num + ")");
        } else {
            goods_bar_price.setVisibility(View.VISIBLE);
            setRightTextView("编辑");
            goto_pay_tv.setText("去结算" + "(" + num + ")");
        }
    }

    private void initView() {
        topay_ll = (LinearLayout) findViewById(R.id.topay_ll);
        goods_bar_price = (LinearLayout) findViewById(R.id.goods_car_price_lin);
        goto_pay_tv = (TextView) findViewById(R.id.go_to_pay);
        mBtnCheckAll = (CheckBox) findViewById(R.id.btn_check_all);

        //设置点击区域
        ExpandViewTouch.expandViewTouchDelegate(mBtnCheckAll, 100, 100, 100, 100);
        mBtnCheckAll.setOnCheckedChangeListener(checkAllListener);
        setViewClick(R.id.my_login_sure);
        setViewClick(R.id.my_login_cancel);
        setViewClick(R.id.ordering_go_bt);

        null_shop_cart_layout = (RelativeLayout) findViewById(R.id.null_shop_cart_layout);
        ordering_sum_pri = (TextView) findViewById(R.id.shopcart_sum_pri);
        shopcart_list = (PullToRefreshListView) findViewById(R.id.shopcart_list);
        shopcart_list.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        //设置刷新的文字
        PullToRefreshUtils.setFreshText(shopcart_list);
        shopcart_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (!isLogin()) {
                    shopcart_list.onRefreshComplete();
                }
                getData();
            }
        });
    }

    /**
     *
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
            localToNet = new ArrayList<Map<String, String>>();
            if (allShoppings.size() > 0) {
                for (int i = 0; i < allShoppings.size(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    if (allShoppings.get(i).get("pid") != null) {
                        map.put("pid", (String) allShoppings.get(i).get("pid"));
                    }
                    if (allShoppings.get(i).get("numbers") != null) {
                        map.put("sum",
                                (String) allShoppings.get(i).get("numbers"));
                    }

                    localToNet.add(map);
                }
            }

            for (int i = 0; i < localToNet.size(); i++) {
                RequestParams params1 = new RequestParams();
                params1.put("locationUserId", Store.User.queryMe().userid);
                params1.put("userId", Store.User.queryMe().userid);
                params1.put("goodsId", localToNet.get(i).get("pid"));
                params1.put("count", localToNet.get(i).get("sum"));
                params1.put("update_by_add", "true");
                execApi(ApiType.ADDTOCART, params1);
            }

            // 加载用户本身已经有的数据化
            showProgressDialog();
            RequestParams params = new RequestParams();
            params.put("locationUserId", Store.User.queryMe().userid);
            params.put("userId", Store.User.queryMe().userid);
            execApi(ApiType.GET_SHOPCART_LIST, params);
            // 删除加载到用户的数据库
            dao.deleteAllShopping();
        } else {
            jiadata(); // 本地数据库数据
        }
    }

    private void jiadata() {
        dao = new ShoppingDao(getApplicationContext());
        allShoppings = dao.getAllShoppings(null);
        localToNet = new ArrayList<Map<String, String>>();
        if (allShoppings.size() > 0) {
            for (int i = 0; i < allShoppings.size(); i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                if (allShoppings.get(i).get("pid") != null) {
                    map.put("productId", (String) allShoppings.get(i)
                            .get("pid"));
                }

                if (allShoppings.get(i).get("numbers") != null) {
                    map.put("count", (String) allShoppings.get(i)
                            .get("numbers"));
                }
                localToNet.add(map);
            }
        }

        if (localToNet.size() == 0) {
            shopcart_list.setAdapter(null);
            topay_ll.setVisibility(View.GONE);
            null_shop_cart_layout
                    .setVisibility(View.VISIBLE);
            hideRight();
            setTitle("购物车");
        }
        RequestParams params = new RequestParams();
        if (localToNet.size() > 0) {
            String jsonStr = JSONArray.toJSONString(localToNet);
            params.put("products", jsonStr);
            execApi(ApiType.GET_LOCAL_SHOPCART_LIST, params);
            if (adapter == null) {
                adapter = new shopcartAdapter();
                shopcart_list.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
            WidgetUtil.setListViewHeightBasedOnChildren(shopcart_list);
        }
    }


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


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.ordering_go_bt:
                //结算状态
                if (!isEdit) {
                    if (!isLogin()) {
                        CustomDialog.Builder builder = new CustomDialog.Builder(
                                ShoppingCartActivity1.this);
                        builder.setMessage("您还没有登录,是否登录？")
                                .setPositiveButton("是",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                Intent intent = new Intent(
                                                        ShoppingCartActivity1.this,
                                                        LoginActivity.class);
                                                intent.putExtra("id", 3);
                                                startActivity(intent);
                                                finish();
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
                        return;
                    } else {
                        if (num > 0) {
                            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                            for (int i = 0; i < data.category.size(); i++) {
                                for (int j = 0; j < data.category.get(i).goods.size(); j++) {
                                    Boolean isChecked = inCartMap.get(data.category.get(i).goods.get(j).id);
                                    if (isChecked != null && isChecked) {
                                        Goods good = data.category.get(i).goods.get(j);
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("productId", good.id);
                                        map.put("count", good.num + "");
                                        list.add(map);
                                    }
                                }
                            }
                            Intent intent = new Intent(ShoppingCartActivity1.this,
                                    OrderDetailActivity.class);
                            intent.putExtra("goodsList", (Serializable) list);
                            intent.addFlags(2);
                            startActivity(intent);// 支付
                        } else {
                            showToast("请您至少选择一件商品");
                        }
                    }

                    //编辑状态
                } else {
                    if (num > 0) {
                        CustomDialog.Builder builder = new CustomDialog.Builder(
                                ShoppingCartActivity1.this);
                        builder.setMessage("您确定要删除吗？")
                                .setPositiveButton("是",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                if (isLogin()) {
                                                    for (int i = 0; i < data.category.size(); i++) {
                                                        for (int j = 0; j < data.category.get(i).goods.size(); j++) {
                                                            Boolean isChecked = inCartMap.get(data.category.get(i).goods.get(j).id);
                                                            if (isChecked != null && isChecked) {
                                                                Goods good = data.category.get(i).goods.get(j);
                                                                RequestParams params = new RequestParams();
                                                                params.put(
                                                                        "locationUserId",
                                                                        Store.User.queryMe().userid);
                                                                params.put(
                                                                        "userId",
                                                                        Store.User.queryMe().userid);
                                                                params.put(
                                                                        "goodsId",
                                                                        good.id);
                                                                params.put("quantity", 0);
                                                                execApi(ApiType.CHANGE_NUM,
                                                                        params);
                                                            }
                                                            showProgressDialog("删除中");
                                                        }
                                                    }

                                                } else {
                                                    for (int i = 0; i < data.category.size(); i++) {
                                                        for (int j = 0; j < data.category.get(i).goods.size(); j++) {
                                                            Boolean isChecked = inCartMap.get(data.category.get(i).goods.get(j).id);
                                                            if (isChecked != null && isChecked) {
                                                                Goods good = data.category.get(i).goods.get(j);
                                                                dao.deleteShopping(good.id);
                                                            }
                                                        }
                                                    }
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
                    } else {
                        showToast("请您至少选择一件商品");
                    }


                }
                break;
            case R.id.my_login_sure:
                Intent intent = new Intent(ShoppingCartActivity1.this,
                        ShangpinListActivity.class);
                intent.putExtra("goods", "huafei");

                startActivity(intent);

                break;
            case R.id.my_login_cancel:
                Intent intent1 = new Intent(ShoppingCartActivity1.this,
                        ShangpinListActivity.class);
                intent1.putExtra("goods", "qiche");
                startActivity(intent1);

                break;
            default:
                break;
        }
    }

    @Override
    public void onResponsed(Request req) {
        shopcart_list.onRefreshComplete();
        //获得购物车列表
        if (req.getApi() == ApiType.GET_SHOPCART_LIST) {
            GetshopCart res = (GetshopCart) req.getData();
            List<shopCart> rows = res.datas.rows;
            if (rows == null || rows.size() == 0) {
                setTitle("购物车");
                shopcart_list.setAdapter(null);
                ordering_sum_pri.setText("¥0");
                topay_ll.setVisibility(View.GONE);
                null_shop_cart_layout.setVisibility(View.VISIBLE);
                hideRight();
                return;
            }
            total_count = 0;
            total_count = res.datas.totalCount;
            total = res.datas.total;
            topay_ll.setVisibility(View.VISIBLE);
            setTitle("购物车" + "(" + total_count + ")");
            null_shop_cart_layout.setVisibility(View.GONE);
            showRightTextView();
            if (data != null && data.category.size() > 0) {
                data.category.clear();
            }
            //保存到本地此购物车的Id
            SPUtils.put(ShoppingCartActivity1.this, "shopCartId",
                    res.datas.shopCartId);

            for (int i = 0; i < rows.size(); i++) {

                Category c = new Category();
                c.title = rows.get(i).brandName;
                c.goods = new ArrayList<Goods>();
                for (int j = 0; j < rows.get(i).goodsList.size(); j++) {
                    Goods goods = new Goods();
                    //商品的四个属性
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
            if (adapter == null) {
                adapter = new shopcartAdapter();
                shopcart_list.setAdapter(adapter);
            }
            adapter.notifyDataSetChanged();
            WidgetUtil.setListViewHeightBasedOnChildren(shopcart_list);
            //获得商品的总价格
            notifyCheckedChanged();
            //改变数量
        } else if (req.getApi() == ApiType.CHANGE_NUM) {
            ChangeNum res = (ChangeNum) req.getData();
            if ("1000" .equals(res.getStatus())) {
                RequestParams params = new RequestParams();
                params.put("locationUserId", Store.User.queryMe().userid);
                params.put("userId", Store.User.queryMe().userid);
                execApi(ApiType.GET_SHOPCART_LIST, params);
                disMissDialog();
            }
            //获得本地的商品列表
        } else if (req.getApi() == ApiType.GET_LOCAL_SHOPCART_LIST) {
            GetshopCart res = (GetshopCart) req.getData();
            List<shopCart> rows = res.datas.rows;
            if (rows == null || rows.size() == 0) {
                shopcart_list.setAdapter(null);
                ordering_sum_pri.setText("¥0");
                setTitle("购物车");
                topay_ll.setVisibility(View.GONE);
                null_shop_cart_layout.setVisibility(View.VISIBLE);
                hideRight();
                return;
            }
            total_count = 0;
            total_count = res.datas.totalCount;
            total = res.datas.total;
            setTitle("购物车" + "(" + total_count + ")");
            topay_ll.setVisibility(View.VISIBLE);
            null_shop_cart_layout.setVisibility(View.GONE);
            showRightTextView();
            if (data != null && data.category.size() > 0) {
                data.category.clear();
            }
            for (int i = 0; i < rows.size(); i++) {
                Category c = new Category();
                c.title = rows.get(i).brandName;
                c.goods = new ArrayList<Goods>();
                for (int j = 0; j < rows.get(i).goodsList.size(); j++) {
                    Goods goods = new Goods();
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
            adapter = new shopcartAdapter();
            shopcart_list.setAdapter(adapter);
            WidgetUtil.setListViewHeightBasedOnChildren(shopcart_list);
            notifyCheckedChanged();
        }
    }

    private int showNum;
    private carAdapter carAdapter;

    //外层的购物车，已店铺区分
    public class shopcartAdapter extends BaseAdapter {
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
                convertView = LayoutInflater.from(ShoppingCartActivity1.this)
                        .inflate(R.layout.shopcart_list_item, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.car_name.setText(data.category.get(position).title);
            carAdapter = new carAdapter(data.category.get(position).goods);
            holder.car_list.setAdapter(carAdapter);
            WidgetUtil.setListViewHeightBasedOnChildren(holder.car_list);
            return convertView;
        }

        public void clear() {
            data.category.clear();
            notifyDataSetChanged();
        }

        class ViewHolder {
            private TextView car_name;
            private ListView car_list;

            public ViewHolder(View convertView) {
                car_name = (TextView) convertView.findViewById(R.id.car_name);
                car_list = (ListView) convertView.findViewById(R.id.car_list);
            }
        }

    }

    //内层的购物车，已店铺区分
    public class carAdapter extends BaseAdapter {

        private List<Goods> goodsList;

        public carAdapter(List<Goods> goodsList) {
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
            private ImageView ordering_item_jian, ordering_item_jia, ordering_item_img;
            private CheckBox checkBox_item;
            private TextView ordering_item_geshu;
            private TextView ordering_now_pri, ordering_item_name, goods_car_deposit, goods_car_weikuan;
            private RelativeLayout shopCart_list_item_rel;

            public ViewHolder(View convertView) {
                goods_car_bar = (LinearLayout) convertView.findViewById(R.id.goods_car_item_bar);
                ordering_item_jian = (ImageView) convertView//商品-
                        .findViewById(R.id.ordering_item_jian1);
                ordering_item_jia = (ImageView) convertView//商品+
                        .findViewById(R.id.ordering_item_jia1);
                ordering_item_img = (ImageView) convertView//商品图
                        .findViewById(R.id.ordering_item_img);
                checkBox_item = (CheckBox) convertView.findViewById(R.id.btn_check_item_item);
                ExpandViewTouch.expandViewTouchDelegate(checkBox_item, 100, 100, 100, 100);

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
                shopCart_list_item_rel = (RelativeLayout) convertView//点击去商品详情
                        .findViewById(R.id.shopCart_list_item_rel);
            }
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ShoppingCartActivity1.this)
                        .inflate(R.layout.shopcart_list_item_item, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag();
            showNum = goodsList.get(position).num;
            holder.ordering_item_geshu.setText(showNum + "");


            ImageLoader.getInstance().displayImage(
                    MsgID.IP + goodsList.get(position).pic, holder.ordering_item_img);
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
            // 避免由于复用触发onChecked()事件
            holder.checkBox_item.setOnCheckedChangeListener(null);
            Boolean isChecked = inCartMap.get(goodsList.get(position).id);
            if (isChecked != null && isChecked) {
                holder.checkBox_item.setChecked(true);
            } else {
                holder.checkBox_item.setChecked(false);
            }

            final ViewHolder holder2 = holder;
            //是否选中
            holder.checkBox_item.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        inCartMap.put(goodsList.get(position).id, isChecked);
                        // 如果所有项都被选中，则点亮全选按钮

                        if (inCartMap.size() == total) {
                            mBtnCheckAll.setChecked(true);
                        }
                    } else {
                        // 如果之前是全选状态，则取消全选状态
                        if (inCartMap.size() == total) {
                            mBtnCheckAll
                                    .setOnCheckedChangeListener(null);
                            mBtnCheckAll.setChecked(false);
                            mBtnCheckAll
                                    .setOnCheckedChangeListener(checkAllListener);
                        }
                        inCartMap.remove(goodsList.get(position).id);
                    }
                    notifyCheckedChanged();
                }
            });

            //修改商品数量
            holder.ordering_item_geshu.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomDialogForShopCarCount.Builder builder = new CustomDialogForShopCarCount.Builder(
                            ShoppingCartActivity1.this);
                    car_num = Integer.parseInt(holder.ordering_item_geshu.getText().toString().trim());
                    builder.setMessage("修改购买数量").setEditText(car_num + "")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String str = CustomDialogForShopCarCount.editText.getText().toString().trim();
                                    if (!TextUtils.isEmpty(str) && !str.equals("0")) {
                                        holder.ordering_item_geshu.setText(str);
                                        if (isLogin()) {
                                            RequestParams params = new RequestParams();
                                            params.put("locationUserId",
                                                    Store.User.queryMe().userid);
                                            params.put("userId", Store.User.queryMe().userid);
                                            params.put("goodsId", goodsList.get(position).id);
                                            params.put("quantity", Integer.valueOf(str));
                                            execApi(ApiType.CHANGE_NUM, params);
                                            showProgressDialog("提交中");
                                        } else {
                                            goodsList.get(position).num = Integer.valueOf(str);
                                            dao.updateShopping(goodsList.get(position).id,
                                                    holder2.ordering_item_geshu.getText().toString()
                                                            .trim());
                                            RndLog.i("pid..............jian1", goodsList.size()
                                                    + "name,," + goodsList.get(position).name
                                                    + "id--" + goodsList.get(position).id
                                                    + "///////");
                                            notifyDataSetChanged();
                                            // 如果被选中，更新价格
                                            if (holder2.checkBox_item.isChecked()) {
                                                notifyCheckedChanged();
                                            }
                                            notifyNumChanged();
                                        }

                                        dialog.dismiss();
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

                            if (car_num == 1) {
                                showToast("商品不能再减少了哦");
                            } else {
                                car_num--;
                                CustomDialogForShopCarCount.editText.setText(car_num + "");
                            }

                        }
                    }).SetRightButton(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            car_num++;
                            CustomDialogForShopCarCount.editText.setText(car_num + "");
                        }
                    });
                    CustomDialogForShopCarCount dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                }

            });


            // 减少
            holder.ordering_item_jian.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (goodsList.get(position).num <= 1) {
                        ShoppingCartActivity1.this.showToast("商品不能再减少了哦");
                        return;
                    }
                    if (isLogin()) {
                        RequestParams params = new RequestParams();
                        params.put("locationUserId",
                                Store.User.queryMe().userid);
                        params.put("userId", Store.User.queryMe().userid);
                        params.put("goodsId", goodsList.get(position).id);
                        params.put("quantity", goodsList.get(position).num - 1);
                        execApi(ApiType.CHANGE_NUM, params);
                        showProgressDialog("提交中");
                    } else {
                        goodsList.get(position).num--;
                        holder2.ordering_item_geshu.setText(goodsList.get(position).num
                                + "");
                        dao.updateShopping(goodsList.get(position).id,
                                holder2.ordering_item_geshu.getText().toString()
                                        .trim());
                        RndLog.i("pid..............jian1", goodsList.size()
                                + "name,," + goodsList.get(position).name
                                + "id--" + goodsList.get(position).id
                                + "///////");
                        notifyDataSetChanged();
                        // 如果被选中，更新价格
                        if (holder2.checkBox_item.isChecked()) {
                            notifyCheckedChanged();
                        }
                        notifyNumChanged();

                    }
                }
            });
            // 增加
            holder.ordering_item_jia.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isLogin()) {
                        showProgressDialog("修改中..");
                        RequestParams params = new RequestParams();
                        params.put("locationUserId",
                                Store.User.queryMe().userid);
                        params.put("userId", Store.User.queryMe().userid);
                        params.put("goodsId", goodsList.get(position).id);
                        params.put("quantity", goodsList.get(position).num + 1);
                        execApi(ApiType.CHANGE_NUM, params);
                    } else {
                        goodsList.get(position).num++;
                        holder2.ordering_item_geshu.setText(goodsList.get(position).num
                                + "");
                        dao.updateShopping(goodsList.get(position).id,
                                holder2.ordering_item_geshu.getText().toString()
                                        .trim());
                        RndLog.i("pid..............jia1", goodsList.size()
                                + "name,," + goodsList.get(position).name
                                + "id--" + goodsList.get(position).id
                                + "///////");
                        notifyDataSetChanged();
                        // 如果被选中，更新价格
                        if (holder2.checkBox_item.isChecked()) {
                            notifyCheckedChanged();
                        }
                        notifyNumChanged();
                    }
                }
            });
            //点击 去商品详情

            holder.ordering_item_img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShoppingCartActivity1.this, ShangpinDetailActivity.class);
                    intent.putExtra("goodId", goodsList.get(position).id);
                    startActivity(intent);

                }
            });
            holder.shopCart_list_item_rel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShoppingCartActivity1.this, ShangpinDetailActivity.class);
                    intent.putExtra("goodId", goodsList.get(position).id);
                    startActivity(intent);

                }
            });
            return convertView;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        shopcart_list.setAdapter(null);
        isEdit = true;
        mBtnCheckAll.setChecked(false);
        editInCart();
        inCartMap.clear();
        notifyCheckedChanged();
        getData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter = null;
    }

}
