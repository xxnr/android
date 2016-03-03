package com.ksfc.newfarmer.activitys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.RndApplication;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.ApiType.RequestMethod;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.ResponseResult;
import com.ksfc.newfarmer.protocol.beans.AttrSelectResult;
import com.ksfc.newfarmer.protocol.beans.BrandsResult;
import com.ksfc.newfarmer.protocol.beans.GetGoodsData;
import com.ksfc.newfarmer.protocol.beans.GetGoodsData.SingleGood;
import com.ksfc.newfarmer.utils.ExpandViewTouch;
import com.ksfc.newfarmer.utils.ImageLoaderUtils;
import com.ksfc.newfarmer.utils.PullToRefreshUtils;
import com.ksfc.newfarmer.utils.SPUtils;
import com.ksfc.newfarmer.utils.ScreenUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.GridViewWithHeaderAndFooter;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ShangpinListActivity extends BaseActivity implements OnItemClickListener, PullToRefreshBase.OnRefreshListener2, AbsListView.OnScrollListener {

    private List<SingleGood> nycList = new ArrayList<>();
    private PullToRefreshListView listView;
    private GoodsListAdapter goodsListAdapter;
    private String goods_flag;

    private ImageView return_top;

    // 声明PopupWindow对象的引用
    private PopupWindow popupWindow;
    //分割线
    private TextView goods_bar_separatrix;

    private int page = 1;
    private String classId;
    private StringBuilder brand;
    private String sort;
    private String reservePrice;

    private boolean price_flag = false;//价格正反序

    private priceAdapter adapter_price;
    private TextView zonghe_text;
    private TextView jiage_text;
    private ImageView jiage_image;

    private TextView shaixuan_text;
    private ImageView shaixuan_image;
    private RelativeLayout goods_none_view_rel;//没有商品时的视图

    private GridViewWithHeaderAndFooter banrds_gv;
    private GridViewWithHeaderAndFooter price_gv;

    private StringBuilder brandBuilder;//品牌的value
    private TextView popwindow_text1, popwindow_text2, popwindow_text3, popwindow_text4, popwindow_text5;//动态的标题
    private GridViewWithHeaderAndFooter pop_gv1, pop_gv2, pop_gv3, pop_gv4, pop_gv5;//动态的内容
    private ShangpinListActivity.bransAdapter bransAdapter;
    private HashMap<String, AttrAdapter> attrAdapterMap = new HashMap<>();
    private List<Map<String, Object>> attributesList;

    private List<String> lastBrands = new ArrayList<>(); //点击确定后保存上一次的品牌数据
    private List<String> levelAttr = new ArrayList<>(); //点击确定后保存车型级别数据
    private List<String> lastAttr = new ArrayList<>(); //点击确定后保存上一次的attr数据
    private int lastPrice = -1; //点击确定后保存上一次价格
    private boolean isReset = false;
    private List<String> commonAttr = new ArrayList<>();


    @Override
    public int getLayout() {
        // TODO Auto-generated method stub
        return R.layout.information_newfarmer_layout;

    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        // 根据intent判断加载化肥还是汽车
        RndApplication.tempDestroyActivityList.add(ShangpinListActivity.this);
        goods_flag = getIntent().getStringExtra("goods");
        if (goods_flag.equals("huafei")) {
            setTitle("化肥");
            classId = (String) SPUtils.get(this, "HuafeiId", "531680A5");
        } else if (goods_flag.equals("qiche")) {
            setTitle("汽车");
            classId = (String) SPUtils.get(this, "CarID", "6C7D8F66");
        }
        initView();
        showProgressDialog();
        getData();
        getCommonAttr();
    }

    public void initView() {
        listView = (PullToRefreshListView) findViewById(R.id.information_listView);
        listView.setOnRefreshListener(this);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        //监听滚动 控制return top 按钮
        listView.setOnScrollListener(this);
        //设置刷新的文字
        PullToRefreshUtils.setFreshText(listView);
        //没有商品时的layout
        goods_none_view_rel = (RelativeLayout) findViewById(R.id.goods_none_view_rel);
        // 去顶部
        return_top = (ImageView) findViewById(R.id.return_top);
        //扩大点击区域
        ExpandViewTouch.expandViewTouchDelegate(return_top, 100, 100, 100, 100);
        // 筛选框
        LinearLayout goods_shanxuan_lin = (LinearLayout) findViewById(R.id.goods_shanxuan_lin);
        goods_shanxuan_lin.setVisibility(View.VISIBLE);
        // 分割线
        goods_bar_separatrix = (TextView) findViewById(R.id.goods_bar_separatrix);
        listView.setOnItemClickListener(this);
        setViewClick(R.id.return_top);
        setViewClick(R.id.goods_zonghe_rel);
        setViewClick(R.id.goods_jiage_rel);
        setViewClick(R.id.goods_shaixuan_rel);

        zonghe_text = (TextView) findViewById(R.id.goods_zonghe_rel_text);
        jiage_text = (TextView) findViewById(R.id.goods_jiage_rel_text);
        jiage_image = (ImageView) findViewById(R.id.goods_jiage_rel_image);
        shaixuan_text = (TextView) findViewById(R.id.goods_shaixuan_rel_text);
        shaixuan_image = (ImageView) findViewById(R.id.goods_shaixuan_rel_image);
    }

    private void getData() {
        RequestParams params = new RequestParams();
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();

        map.put("page", page);
        map.put("rowCount", "20");
        map.put("classId", classId);
        map.put("sort", sort);
        map.put("reservePrice", reservePrice);
        if (brand != null) {
            map.put("brand", brand.toString());
        }

        if (attributesList != null && !attributesList.isEmpty()) {
            map.put("attributes", attributesList);
        }

        params.put("JSON", gson.toJson(map));
        execApi(ApiType.GET_HUAFEI.setMethod(RequestMethod.POSTJSON), params);
    }


    //获得筛选类型
    private void getBrandsList() {
        RequestParams params = new RequestParams();
        execApi(ApiType.GET_BRANDS_LIST.setMethod(RequestMethod.GET).setOpt(
                "/api/v2.1/brands" + "?category=" + classId), params);
    }


    //获得筛选value
    private void getAttrsData() {
        brandBuilder = new StringBuilder();
        List<String> list = new ArrayList<>();//存放选中的brands
        if (bransAdapter != null && !bransAdapter.states.isEmpty()) {
            for (Map.Entry<String, Boolean> entry : bransAdapter.states.entrySet()) {
                if (entry.getValue()) {
                    list.add(entry.getKey());
                }
            }
        }
        for (String key : list) {
            brandBuilder.append(key).append(",");
        }
        String brandString;
        brandBuilder.append("0");
        brandString = brandBuilder.toString();
        RequestParams params = new RequestParams();
        execApi(ApiType.GET_GOODS_ATTR.setMethod(RequestMethod.GET).setOpt(
                "/api/v2.1/products/attributes" + "?brand=" + brandString + "&category=" + classId), params);
    }

    @Override
    public void OnViewClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.return_top:
                listView.getRefreshableView().setSelection(0);
                break;
            case R.id.goods_zonghe_rel:
                jiage_image.setVisibility(View.GONE);
                zonghe_text.setTextColor(getResources().getColor(R.color.orange_goods_price));
                jiage_text.setTextColor(Color.BLACK);
                sort = null;
                getData();
                break;
            case R.id.goods_jiage_rel:
                zonghe_text.setTextColor(Color.BLACK);
                jiage_image.setVisibility(View.VISIBLE);
                jiage_text.setTextColor(getResources().getColor(R.color.orange_goods_price));
                page = 1;
                if (price_flag) {
                    sort = "price-desc";
                    jiage_image.setImageResource(R.drawable.price_order_down);
                    price_flag = false;
                } else {
                    sort = "price-asc";
                    jiage_image.setImageResource(R.drawable.price_order_up);
                    price_flag = true;
                }
                getData();
                break;
            case R.id.goods_shaixuan_rel:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    isReset = false;
                    getPopupWindow();
                    getBrandsList();
                    popupWindow.showAsDropDown(goods_bar_separatrix);
                }
                break;

            default:
                break;
        }

    }

    /**
     * 创建PopupWindow
     */
    private void initPopuptWindow() {
        // TODO Auto-generated method stub
        // 获取自定义布局文件activity_popupwindow_left.xml的视图
        View popupWindow_view = getLayoutInflater().inflate(
                R.layout.goods_popwindow_layout, null, false);
        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popupWindow = new PopupWindow(popupWindow_view,
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
        // 设置动画效果
        popupWindow.setAnimationStyle(R.style.AnimTop2);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(true);
        // 初始化popWindow中的两个gridView
        banrds_gv = (GridViewWithHeaderAndFooter) popupWindow_view
                .findViewById(R.id.brand_gv);
        price_gv = (GridViewWithHeaderAndFooter) popupWindow_view
                .findViewById(R.id.price_gv);

        popwindow_text1 = (TextView) popupWindow_view.findViewById(R.id.pop_text1);
        popwindow_text2 = (TextView) popupWindow_view.findViewById(R.id.pop_text2);
        popwindow_text3 = (TextView) popupWindow_view.findViewById(R.id.pop_text3);
        popwindow_text4 = (TextView) popupWindow_view.findViewById(R.id.pop_text4);
        popwindow_text5 = (TextView) popupWindow_view.findViewById(R.id.pop_text5);

        pop_gv1 = (GridViewWithHeaderAndFooter) popupWindow_view
                .findViewById(R.id.pop_gv1);
        pop_gv2 = (GridViewWithHeaderAndFooter) popupWindow_view
                .findViewById(R.id.pop_gv2);
        pop_gv3 = (GridViewWithHeaderAndFooter) popupWindow_view
                .findViewById(R.id.pop_gv3);
        pop_gv4 = (GridViewWithHeaderAndFooter) popupWindow_view
                .findViewById(R.id.pop_gv4);
        pop_gv5 = (GridViewWithHeaderAndFooter) popupWindow_view
                .findViewById(R.id.pop_gv5);


        // 确定筛选框
        popupWindow_view.findViewById(R.id.shuaixuan_icon_sure)
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        nycList.clear();
                        page = 1;
                        getShuaixuan_value();
                        getData();
                        if (popupWindow != null && popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                    }
                });
        // 重置筛选框
        popupWindow_view.findViewById(R.id.shuaixuan_icon_reset)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isReset = true;
                        reset();
                    }
                });

    }

    /***
     * 获取PopupWindow实例
     */
    private void getPopupWindow() {
        if (null != popupWindow) {
            popupWindow.dismiss();
        } else {
            initPopuptWindow();
        }
    }

    // 得到筛选框中筛选的结果
    private void getShuaixuan_value() {
        lastBrands.clear();
        lastPrice = -1;
        lastAttr.clear();

        int pri_position = -1;
        //得到品牌
        brand = new StringBuilder();
        List<String> list = new ArrayList<>();
        if (bransAdapter != null && !bransAdapter.states.isEmpty()) {
            for (Map.Entry<String, Boolean> entry : bransAdapter.states.entrySet()) {
                if (entry.getValue()) {
                    list.add(entry.getKey());
                }
            }
        }
        //保存 以便下次筛选时使用
        lastBrands.addAll(list);

        for (String key : list) {
            brand.append(key).append(",");
        }

        if (brand.toString().equals("")) {
            brand = null;
        } else {
            brand.deleteCharAt(brand.toString().length() - 1);
        }
        //价钱
        for (int i = 0, j = price_gv.getCount(); i < j; i++) {
            View child = price_gv.getChildAt(i);
            CheckBox rdoBtn = (CheckBox) child
                    .findViewById(R.id.shuaixuan_item_button);
            if (rdoBtn.isChecked()) {
                pri_position = i;
            }
        }

        //保存上次价格 以便下次筛选时使用
        lastPrice = pri_position;
        if (goods_flag.equals("huafei")) {
            switch (pri_position) {
                case 0:
                    reservePrice = "0,1000";
                    break;
                case 1:
                    reservePrice = "1000,2000";
                    break;
                case 2:
                    reservePrice = "2000,3000";
                    break;
                case 3:
                    reservePrice = "3000,1000000";
                    break;
                default:
                    reservePrice = null;
                    break;
            }

        } else if (goods_flag.equals("qiche")) {
            switch (pri_position) {
                case 0:
                    reservePrice = "0,50000";
                    break;
                case 1:
                    reservePrice = "50000,60000";
                    break;
                case 2:
                    reservePrice = "60000,70000";
                    break;
                case 3:
                    reservePrice = "70000,1000000";
                    break;
                default:
                    reservePrice = null;
                    break;
            }
        }
        //获得选中的属性
        if (attrAdapterMap != null && !attrAdapterMap.isEmpty()) {
            attributesList = new ArrayList<>();
            for (Map.Entry<String, AttrAdapter> entry : attrAdapterMap.entrySet()) {
                Map<String, Object> map = new HashMap<>();
                Map<String, Object> mapKey_Value = new HashMap<>();
                List<String> listString = new ArrayList<>();
                for (Map.Entry<String, Boolean> entry1 : entry.getValue().states.entrySet()) {
                    if (entry1.getValue()) {
                        listString.add(entry1.getKey());
                    }
                }

                if (!listString.isEmpty()) {
                    //保存上一次的数据以便于筛选
                    lastAttr.addAll(listString);
                    map.put("$in", listString);
                    mapKey_Value.put("name", entry.getKey());
                    mapKey_Value.put("value", map);
                }
                if (!mapKey_Value.isEmpty()) {
                    attributesList.add(mapKey_Value);
                }
            }
        }

        if (brand == null && (attributesList == null || attributesList.isEmpty()) && pri_position == -1) {
            shaixuan_text.setTextColor(Color.BLACK);
            shaixuan_image.setImageResource(R.drawable.shaixuan_gary);
        } else {
            shaixuan_text.setTextColor(getResources().getColor(R.color.orange_goods_price));
            shaixuan_image.setImageResource(R.drawable.shaixuan_orange);
        }
    }

    // 重置筛选框
    private void reset() {
        //删除map
        if (attrAdapterMap != null && !attrAdapterMap.isEmpty()) {
            for (Map.Entry<String, AttrAdapter> entry : attrAdapterMap.entrySet()) {
                entry.getValue().states.clear();
                entry.getValue().notifyDataSetChanged();
            }
        }
        levelAttr.clear();
        adapter_price.states.clear();
        adapter_price.notifyDataSetChanged();
        bransAdapter.states.clear();
        bransAdapter.notifyDataSetChanged();
        brand = null;
        brandBuilder = null;
        reservePrice = null;
        getAttrsData();
    }

    //获取公共属性的title  如车型级别 品类

    private void getCommonAttr() {
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, ApiType.GET_GOODS_ATTR.getOpt() + "?brand=" + "0" + "&category=" + classId, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        commonAttr.add("车型级别");
                        commonAttr.add("品类");
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        Gson gson = new Gson();
                        AttrSelectResult attrSelectResult = gson.fromJson(arg0.result, AttrSelectResult.class);
                        if (attrSelectResult.getStatus().equals("1000")) {
                            if (!attrSelectResult.attributes.isEmpty()) {
                                for (int i = 0; i < attrSelectResult.attributes.size(); i++) {
                                    if (attrSelectResult.attributes.get(i)._id != null) {
                                        if (StringUtil.checkStr(attrSelectResult.attributes.get(i)._id.name)) {
                                            commonAttr.add(attrSelectResult.attributes.get(i)._id.name);
                                        }
                                    }
                                }
                            }
                        }

                    }

                });

    }


    @Override
    public void onResponsed(Request req) {
        listView.onRefreshComplete();
        disMissDialog();
        if (req.getApi() == ApiType.GET_NYC
                || req.getApi() == ApiType.GET_HUAFEI) {
            GetGoodsData goodsData = (GetGoodsData) req.getData();
            if ("1000".equals(goodsData.getStatus())) {
                List<SingleGood> list = goodsData.datas.rows;
                if (list.size() > 0) {
                    goods_none_view_rel.setVisibility(View.GONE);
                    if (page == 1) {
                        if (goodsListAdapter == null) {
                            goodsListAdapter = new GoodsListAdapter();
                            listView.setAdapter(goodsListAdapter);
                            goodsListAdapter.addAll(list);
                        } else {
                            goodsListAdapter.clear();
                            goodsListAdapter.addAll(list);
                        }
                    } else {
                        goodsListAdapter.addAll(list);
                    }
                } else {
                    if (page == 1) {
                        goods_none_view_rel.setVisibility(View.VISIBLE);
                        return_top.setVisibility(View.GONE);
                    } else {
                        showToast("没有更多商品");
                        page--;
                    }
                    if (goodsListAdapter!=null){
                        goodsListAdapter.notifyDataSetChanged();
                    }
                }
            }
            //商品品牌
        } else if (req.getApi() == ApiType.GET_BRANDS_LIST) {
            BrandsResult data = (BrandsResult) req.getData();
            if (data.getStatus().equals("1000")) {
                if (data.brands != null && !data.brands.isEmpty()) {
                    bransAdapter = new bransAdapter(data.brands);
                    banrds_gv.setAdapter(bransAdapter);
                }
            }
            //价格筛选表
            List<String> prices = new ArrayList<>();
            if (goods_flag.equals("huafei")) {
                prices.add("0-1000元");
                prices.add("1000-2000元");
                prices.add("2000-3000元");
                prices.add("3000元以上");

            } else if (goods_flag.equals("qiche")) {
                prices.add("0-5万");
                prices.add("5万-6万");
                prices.add("6万-7万");
                prices.add("7万以上");
            }

            adapter_price = new priceAdapter(prices);
            price_gv.setAdapter(adapter_price);
            //初始化 保存的价格
            adapter_price.states.put(lastPrice + "", true);
            adapter_price.notifyDataSetChanged();

            //初始化 保存的品牌
            for (String key : lastBrands) {
                bransAdapter.states.put(key, true);
            }
            bransAdapter.notifyDataSetChanged();
            getAttrsData();

        } else if (req.getApi() == ApiType.GET_GOODS_ATTR) {
            AttrSelectResult data = (AttrSelectResult) req.getData();
            if (data.getStatus().equals("1000")) {
                if (data.attributes != null && !data.attributes.isEmpty()) {
                    initAttrAdapter();
                    switch (data.attributes.size()) {
                        case 5:
                            popwindow_text5.setVisibility(View.VISIBLE);
                            pop_gv5.setVisibility(View.VISIBLE);
                            AttrAdapter attrAdapter5 = new AttrAdapter(data.attributes.get(4).values, data.attributes.get(4)._id.name);
                            popwindow_text5.setText(data.attributes.get(4)._id.name);
                            pop_gv5.setAdapter(attrAdapter5);
                            attrAdapterMap.put(data.attributes.get(4)._id.name, attrAdapter5);
                        case 4:
                            popwindow_text4.setVisibility(View.VISIBLE);
                            pop_gv4.setVisibility(View.VISIBLE);
                            AttrAdapter attrAdapter4 = new AttrAdapter(data.attributes.get(3).values, data.attributes.get(3)._id.name);
                            popwindow_text4.setText(data.attributes.get(3)._id.name);
                            pop_gv4.setAdapter(attrAdapter4);
                            attrAdapterMap.put(data.attributes.get(3)._id.name, attrAdapter4);
                        case 3:
                            popwindow_text3.setVisibility(View.VISIBLE);
                            pop_gv3.setVisibility(View.VISIBLE);
                            AttrAdapter attrAdapter3 = new AttrAdapter(data.attributes.get(2).values, data.attributes.get(2)._id.name);
                            popwindow_text3.setText(data.attributes.get(2)._id.name);
                            pop_gv3.setAdapter(attrAdapter3);
                            attrAdapterMap.put(data.attributes.get(2)._id.name, attrAdapter3);
                            break;
                        case 2:
                            popwindow_text2.setVisibility(View.VISIBLE);
                            pop_gv2.setVisibility(View.VISIBLE);
                            AttrAdapter attrAdapter2 = new AttrAdapter(data.attributes.get(1).values, data.attributes.get(1)._id.name);
                            popwindow_text2.setText(data.attributes.get(1)._id.name);
                            pop_gv2.setAdapter(attrAdapter2);
                            attrAdapterMap.put(data.attributes.get(1)._id.name, attrAdapter2);
                        case 1:
                            popwindow_text1.setVisibility(View.VISIBLE);
                            pop_gv1.setVisibility(View.VISIBLE);
                            AttrAdapter attrAdapter1 = new AttrAdapter(data.attributes.get(0).values, data.attributes.get(0)._id.name);
                            popwindow_text1.setText(data.attributes.get(0)._id.name);
                            pop_gv1.setAdapter(attrAdapter1);
                            attrAdapterMap.put(data.attributes.get(0)._id.name, attrAdapter1);
                            break;
                    }
                }
                //初始化 保存的attr
                for (Map.Entry<String, AttrAdapter> entry : attrAdapterMap.entrySet()) {
                    for (Map.Entry<String, Boolean> entry1 : entry.getValue().states.entrySet()) {
                        //非公共属性
                        if (lastAttr.contains(entry1.getKey())) {
                            if (!isReset) {
                                entry1.setValue(true);
                            }
                        }
                        //共有属性
                        if (levelAttr.contains(entry1.getKey())) {
                            entry1.setValue(true);
                        }
                    }
                    entry.getValue().notifyDataSetChanged();
                }
            }
        }
    }


    //如果取消了品牌 特有属性消失
    private void initAttrAdapter() {
        popwindow_text1.setVisibility(View.GONE);
        popwindow_text2.setVisibility(View.GONE);
        popwindow_text3.setVisibility(View.GONE);
        popwindow_text4.setVisibility(View.GONE);
        popwindow_text5.setVisibility(View.GONE);

        pop_gv1.setVisibility(View.GONE);
        pop_gv2.setVisibility(View.GONE);
        pop_gv3.setVisibility(View.GONE);
        pop_gv4.setVisibility(View.GONE);
        pop_gv5.setVisibility(View.GONE);
    }

    //popWindow中的品牌
    class bransAdapter extends BaseAdapter {
        private List<BrandsResult.BrandsEntity> list;
        // 用于记录每个RadioButton的状态，并保证只可选一个
        HashMap<String, Boolean> states = new HashMap<String, Boolean>();

        public bransAdapter(List<BrandsResult.BrandsEntity> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub

            if (convertView == null) {
                convertView = LayoutInflater.from(ShangpinListActivity.this)
                        .inflate(R.layout.item_popwindow_gv, null);
                convertView.setTag(new ViewHolder(convertView));
            }

            final ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.brands_name_tv.setText(list.get(position).name);

            // 当RadioButton被选中时，将其状态记录进States中，并更新其他RadioButton的状态使它们不被选中
            holder.brands_name_tv
                    .setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            isReset = true;
                            states.put(list.get(position)._id, holder.brands_name_tv.isChecked());
                            getAttrsData();
                        }
                    });
            boolean res;


            if (states.get(list.get(position)._id) != null && states.get(list.get(position)._id)) {
                res = true;
            } else {
                res = false;
                states.put(list.get(position)._id, false);
            }
            holder.brands_name_tv.setChecked(res);
            return convertView;
        }

        class ViewHolder {
            private CheckBox brands_name_tv;

            ViewHolder(View view) {
                brands_name_tv = (CheckBox) view
                        .findViewById(R.id.shuaixuan_item_button);
            }

        }

    }

    //popWindow中的属性
    class AttrAdapter extends BaseAdapter {
        private List<String> list;
        private String tag;
        // 用于记录每个RadioButton的状态，并保证只可选一个
        HashMap<String, Boolean> states = new HashMap<String, Boolean>();

        public AttrAdapter(List<String> list, String tag) {
            this.list = list;
            this.tag = tag;

            for (String key : list) {
                states.put(key, false);
            }

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub

            if (convertView == null) {
                convertView = LayoutInflater.from(ShangpinListActivity.this)
                        .inflate(R.layout.item_popwindow_gv, null);
                convertView.setTag(new ViewHolder(convertView));
            }

            final ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.brands_name_tv.setText(list.get(position));
            holder.brands_name_tv
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            states.put(list.get(position),
                                    holder.brands_name_tv.isChecked());
                            if (commonAttr.contains(tag)) {
                                if (holder.brands_name_tv.isChecked()) {
                                    levelAttr.add(list.get(position));
                                } else {
                                    levelAttr.remove(list.get(position));
                                }
                            }
                        }
                    });

            boolean res;

            if (states.get(list.get(position)) != null && states.get(list.get(position))) {
                res = true;
            } else {
                res = false;
                states.put(list.get(position), false);
            }
            holder.brands_name_tv.setChecked(res);
            return convertView;
        }

        class ViewHolder {
            private CheckBox brands_name_tv;

            ViewHolder(View view) {
                brands_name_tv = (CheckBox) view
                        .findViewById(R.id.shuaixuan_item_button);
            }

        }

    }

    //popWindow中的价格
    class priceAdapter extends BaseAdapter {
        private List<String> list;
        // 用于记录每个RadioButton的状态，并保证只可选一个
        HashMap<String, Boolean> states = new HashMap<>();

        public priceAdapter(List<String> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub

            if (convertView == null) {
                convertView = LayoutInflater.from(ShangpinListActivity.this)
                        .inflate(R.layout.item_popwindow_gv, null);
                convertView.setTag(new ViewHolder(convertView));
            }

            final ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.brands_name_tv.setText(list.get(position));

            // 当RadioButton被选中时，将其状态记录进States中，并更新其他RadioButton的状态使它们不被选中
            holder.brands_name_tv
                    .setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {

                            // 重置，确保最多只有一项被选中
                            for (String key : states.keySet()) {
                                states.put(key, false);
                            }
                            states.put(String.valueOf(position),
                                    holder.brands_name_tv.isChecked());
                            priceAdapter.this.notifyDataSetChanged();
                        }
                    });

            boolean res;

            if (states.get(String.valueOf(position)) != null && states.get(String.valueOf(position))) {
                res = true;
            } else {
                res = false;
                states.put(String.valueOf(position), false);
            }
            holder.brands_name_tv.setChecked(res);
            return convertView;
        }

        class ViewHolder {
            private CheckBox brands_name_tv;

            ViewHolder(View view) {
                brands_name_tv = (CheckBox) view
                        .findViewById(R.id.shuaixuan_item_button);
            }

        }

    }

    class GoodsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return nycList.size() > 0 ? nycList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return nycList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public void clear() {
            nycList.clear();
            notifyDataSetChanged();
        }

        public void addAll(Collection<? extends SingleGood> collection) {
            nycList.addAll(collection);
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ShangpinListActivity.this)
                        .inflate(R.layout.item_shangpin_list_layout, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (!TextUtils.isEmpty(nycList.get(position).imgUrl)) {
                ImageLoader.getInstance().displayImage((MsgID.IP + nycList.get(position).imgUrl), holder.image_iv,
                        ImageLoaderUtils.buildImageOptions(getApplicationContext()));
            }
            if (StringUtil.checkStr(nycList.get(position).goodsName)) {
                holder.title_tv.setText(nycList.get(position).goodsName);
            }
            String price = nycList.get(position).unitPrice;
            if (nycList.get(position).presale) {
                holder.price_tv.setText("即将上线");
                holder.price_tv.setTextColor(Color.GRAY);
            } else {
                if (StringUtil.checkStr(price)) {
                    holder.price_tv.setText("¥" + price);
                }
                holder.price_tv.setTextColor(Color.parseColor("#ff4e00"));
            }
            return convertView;
        }

        class ViewHolder {
            private ImageView image_iv;
            private TextView title_tv, price_tv;

            ViewHolder(View view) {
                image_iv = (ImageView) view.findViewById(R.id.goods_image);
                title_tv = (TextView) view.findViewById(R.id.goods_title);
                price_tv = (TextView) view.findViewById(R.id.goods_price);

            }

        }

    }


    /**
     * 下拉和上拉刷新
     */

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        PullToRefreshUtils.setFreshClose(refreshView);
        page = 1;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        PullToRefreshUtils.setFreshClose(refreshView);
        page++;
        getData();
    }


    // 点击item跳转到详情界面
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        SingleGood good = (SingleGood) goodsListAdapter.getItem(position);
        Intent intent = new Intent(ShangpinListActivity.this,
                ShangpinDetailActivity.class);
        if (!TextUtils.isEmpty(good.goodsId)) {
            intent.putExtra("goodId", good.goodsId);
            startActivity(intent);
        }

    }


    //监听listView滚动是否出现return_top
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            // 当不滚动时
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 是当屏幕停止滚动时
                // 判断滚动到底部
                if (listView.getRefreshableView().getLastVisiblePosition() ==
                        (listView.getRefreshableView().getCount() - 1)) {
                    return_top.setVisibility(View.VISIBLE);
                }
                // 判断滚动到顶部
                if (listView.getRefreshableView().getFirstVisiblePosition() == 0) {
                    return_top.setVisibility(View.GONE);
                }
                break;
        }
    }

    /**
     * 显示或隐藏顶部按钮
     *
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // 当开始滑动且ListView底部的Y轴点超出屏幕最大范围时，显示或隐藏顶部按钮
        if (getScrollY() >= ScreenUtil
                .getScreenHeight(ShangpinListActivity.this)) {
            return_top.setVisibility(View.VISIBLE);
        }
    }

    //获得lisView的滚动高度
    public int getScrollY() {
        View c = listView.getRefreshableView().getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = listView.getRefreshableView().getFirstVisiblePosition();
        int top = c.getTop();
        return -top + firstVisiblePosition * c.getHeight();
    }
}
