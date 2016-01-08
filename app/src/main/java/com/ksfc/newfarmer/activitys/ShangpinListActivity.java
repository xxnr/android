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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.db.dao.ShoppingDao;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.ApiType.RequestMethod;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.BrandsShaixuan;
import com.ksfc.newfarmer.protocol.beans.BrandsShaixuan.Datas;
import com.ksfc.newfarmer.protocol.beans.GetGoodsData;
import com.ksfc.newfarmer.protocol.beans.addtoCart;
import com.ksfc.newfarmer.protocol.beans.GetGoodsData.SingleGood;
import com.ksfc.newfarmer.utils.ImageLoaderUtils;
import com.ksfc.newfarmer.utils.PullToRefreshUtils;
import com.ksfc.newfarmer.utils.ScreenUtil;
import com.ksfc.newfarmer.widget.GridViewWithHeaderAndFooter;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ShangpinListActivity extends BaseActivity implements OnItemClickListener, PullToRefreshBase.OnRefreshListener2, AbsListView.OnScrollListener {

    private List<SingleGood> nycList = new ArrayList<SingleGood>();
    private PullToRefreshListView listView;

    private ShoppingDao dao;
    private GoodsListAdapter goodsListAdapter;

    private String goods_flag;

    private ImageView return_top;

    private LinearLayout goods_shanxuan_lin;

    // 声明PopupWindow对象的引用
    private PopupWindow popupWindow;
    //分割线
    private TextView goods_bar_separatrix;

    private GridViewWithHeaderAndFooter banrds_gv;

    private GridViewWithHeaderAndFooter price_gv;

    private List<Datas> brands;

    private int page = 1;
    private int rowCount = 20;
    private String classId;
    private String brandName;
    private String modelName;
    private String sort;
    private String reservePrice;

    private boolean price_flag = false;

    private bransAdapter brans_adapter;
    private priceAdapter adapter_price;
    private TextView zonghe_text;
    private TextView jiage_text;
    private ImageView jiage_image;

    private int pass_flag_brands;
    private int pass_flag_price;
    private TextView shaixuan_text;
    private ImageView shaixuan_image;
    private RelativeLayout goods_none_view_rel;

    @Override
    public int getLayout() {
        // TODO Auto-generated method stub
        return R.layout.information_newfarmer_layout;

    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        // 根据intent判断加载化肥还是汽车
        goods_flag = getIntent().getStringExtra("goods");
        if (goods_flag.equals("huafei")) {
            setTitle("化肥");
            classId = "531680A5";
        } else if (goods_flag.equals("qiche")) {
            setTitle("汽车");
            classId = "6C7D8F66";
        }
        //未登录时，加入购物车
        dao = new ShoppingDao(ShangpinListActivity.this);
        initView();
        getData();
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
        // 筛选框
        goods_shanxuan_lin = (LinearLayout) findViewById(R.id.goods_shanxuan_lin);
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
        showProgressDialog();
        RequestParams params = new RequestParams();
        params.put("page", page);
        params.put("rowCount", rowCount);
        params.put("classId", classId);
        params.put("brandName", brandName);
        params.put("modelName", modelName);
        params.put("sort", sort);
        params.put("reservePrice", reservePrice);
        if (isLogin()) {
            params.put("locationUserId", Store.User.queryMe().userid);
        } else {
            params.put("locationUserId", "");
        }
        execApi(ApiType.GET_HUAFEI, params);

    }

    //获得筛选类型
    private void getBrandsData() {

        if (goods_flag.equals("huafei")) {
            RequestParams params = new RequestParams();
            execApi(ApiType.GET_ATTRIBUTENAME.setMethod(RequestMethod.GET).setOpt(
                    "/api/v2.0/products/brands" + "?category=" + "化肥"), params);
        } else {
            RequestParams params = new RequestParams();
            execApi(ApiType.GET_ATTRIBUTENAME.setMethod(RequestMethod.GET).setOpt(
                    "/api/v2.0/products/models" + "?category=" + "汽车"), params);
        }


    }

    @Override
    public void OnViewClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.return_top:
                listView.getRefreshableView().setSelection(0);
                break;
            case R.id.goods_shaixuan_rel:
                if (popupWindow!=null&&popupWindow.isShowing()){
                    popupWindow.dismiss();
                }else {
                    getPopupWindow();
                    getBrandsData();
                    popupWindow.showAsDropDown(goods_bar_separatrix);
                }
                break;
            case R.id.goods_zonghe_rel:
                jiage_image.setVisibility(View.GONE);
                zonghe_text.setTextColor(Color.parseColor("#ff4e00"));
                jiage_text.setTextColor(Color.BLACK);
                sort = null;
                getData();
                break;
            case R.id.goods_jiage_rel:
                zonghe_text.setTextColor(Color.BLACK);
                jiage_image.setVisibility(View.VISIBLE);
                jiage_text.setTextColor(Color.parseColor("#ff4e00"));
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

        // // 点击其他地方消失
        // popupWindow_view.setOnTouchListener(new OnTouchListener() {
        //
        // @SuppressLint("ClickableViewAccessibility")
        // @Override
        // public boolean onTouch(View v, MotionEvent event) {
        //
        // if (popupWindow != null && popupWindow.isShowing()) {
        // popupWindow.dismiss();
        // popupWindow = null;
        // }
        // return false;
        // }
        // });
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
                            popupWindow = null;
                        }
                    }
                });
        // 重置筛选框
        popupWindow_view.findViewById(R.id.shuaixuan_icon_reset)
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        reset();
                    }
                });

    }

    // 得到筛选框中筛选的结果
    private void getShuaixuan_value() {
        int pri_position = 0;
        //化肥还是汽车
        if (goods_flag.equals("huafei")) {
            for (int i = 0, j = banrds_gv.getCount(); i < j; i++) {
                View child = banrds_gv.getChildAt(i);
                RadioButton rdoBtn = (RadioButton) child
                        .findViewById(R.id.shuaixuan_item_button);
                if (rdoBtn.isChecked() && i != 0) {
                    brandName = rdoBtn.getText().toString();
                    pass_flag_brands = i;
                } else if (i == 0) {
                    brandName = null;
                    pass_flag_brands = 0;
                }
            }

            for (int i = 0, j = price_gv.getCount(); i < j; i++) {
                View child = price_gv.getChildAt(i);
                RadioButton rdoBtn = (RadioButton) child
                        .findViewById(R.id.shuaixuan_item_button);
                if (rdoBtn.isChecked()) {
                    pri_position = i;
                    pass_flag_price = i;
                }
            }

            switch (pri_position) {
                case 0:
                    reservePrice = null;
                    break;
                case 1:
                    reservePrice = "0,1000";
                    break;
                case 2:
                    reservePrice = "1000,2000";
                    break;
                case 3:
                    reservePrice = "2000,3000";
                    break;
                case 4:
                    reservePrice = "3000,1000000";
                    break;
                default:
                    break;
            }

        } else if (goods_flag.equals("qiche")) {
            brandName = "江淮";
            for (int i = 0, j = banrds_gv.getCount(); i < j; i++) {
                View child = banrds_gv.getChildAt(i);
                RadioButton rdoBtn = (RadioButton) child
                        .findViewById(R.id.shuaixuan_item_button);
                if (rdoBtn.isChecked() && i != 0) {
                    modelName = rdoBtn.getText().toString();
                    pass_flag_brands = i;
                } else if (i == 0) {
                    modelName = null;
                    pass_flag_brands = 0;
                }
            }

            for (int i = 0, j = price_gv.getCount(); i < j; i++) {
                View child = price_gv.getChildAt(i);
                RadioButton rdoBtn = (RadioButton) child
                        .findViewById(R.id.shuaixuan_item_button);
                if (rdoBtn.isChecked()) {
                    pri_position = i;
                    pass_flag_price = i;
                }
            }

            switch (pri_position) {
                case 0:
                    reservePrice = null;
                    break;

                case 1:
                    reservePrice = "0,50000";
                    break;
                case 2:
                    reservePrice = "50000,60000";
                    break;
                case 3:
                    reservePrice = "60000,70000";
                    break;
                case 4:
                    reservePrice = "70000,1000000";
                    break;
                default:
                    break;
            }
        }

        if (pass_flag_brands == 0 && pass_flag_price == 0) {
            shaixuan_text.setTextColor(Color.BLACK);
            shaixuan_image.setImageResource(R.drawable.shaixuan_gary);
        } else {
            shaixuan_text.setTextColor(Color.parseColor("#ff4e00"));
            shaixuan_image.setImageResource(R.drawable.shaixuan_orange);
        }

    }

    // 重置筛选框
    private void reset() {

        brans_adapter.states.clear();
        adapter_price.states.clear();

        pass_flag_brands = 0;
        pass_flag_price = 0;

        brans_adapter.states.put("0", true);
        adapter_price.states.put("0", true);

        brans_adapter.notifyDataSetChanged();
        adapter_price.notifyDataSetChanged();
        brandName = null;
        modelName = null;
        reservePrice = null;
    }

    // 传送上次筛选的结果给筛选框
    private void passValue() {

        brans_adapter.states.clear();
        adapter_price.states.clear();

        brans_adapter.states.put(pass_flag_brands + "", true);
        adapter_price.states.put(pass_flag_price + "", true);

        brans_adapter.notifyDataSetChanged();
        adapter_price.notifyDataSetChanged();

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
                        showToast("没有数据了");
                        page--;
                    }
                    goodsListAdapter.notifyDataSetChanged();
                }
            }
        } else if (req.getApi() == ApiType.ADDTOCART) {
            addtoCart data = (addtoCart) req.getData();
            if ("1000".equals(data.getStatus())) {
                showToast("添加购物车成功");
            }
        } else if (req.getApi() == ApiType.GET_ATTRIBUTENAME) {
            BrandsShaixuan data = (BrandsShaixuan) req.getData();
            if (data.getStatus().equals("1000")) {
                brands = data.datas;
                Datas datas = new Datas();
                datas.name = "全部";
                brands.add(0, datas);
                brans_adapter = new bransAdapter(brands);
                banrds_gv.setAdapter(brans_adapter);

                List<String> prices = new ArrayList<String>();
                if (goods_flag.equals("huafei")) {

                    prices.add("全部");
                    prices.add("0-1000元");
                    prices.add("1000-2000元");
                    prices.add("2000-3000元");
                    prices.add("3000元以上");

                } else if (goods_flag.equals("qiche")) {
                    prices.add("全部");
                    prices.add("0-5万");
                    prices.add("5万-6万");
                    prices.add("6万-7万");
                    prices.add("7万以上");
                }

                adapter_price = new priceAdapter(prices);
                price_gv.setAdapter(adapter_price);
                passValue();

            }

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

            holder.title_tv.setText(nycList.get(position).goodsName);
            String price = nycList.get(position).unitPrice;
            if (nycList.get(position).presale) {
                holder.price_tv.setText("即将上线");
                holder.price_tv.setTextColor(Color.GRAY);
                holder.goodsCar_iv.setVisibility(View.GONE);
            } else {
                holder.price_tv.setText("¥" + price);
                holder.goodsCar_iv.setVisibility(View.VISIBLE);
                holder.price_tv.setTextColor(Color.parseColor("#ff4e00"));
            }
            // 加入购物车
            holder.goodsCar_iv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (isLogin()) {
                        // app/shopCart/addToCart
                        // locationUserId:操作人ID
                        // goodsId:商品ID
                        // userId:用户ID
                        // count：件数
                        // showProgressDialog("正在添加购物车");
                        RequestParams params = new RequestParams();
                        params.put("locationUserId",
                                Store.User.queryMe().userid);
                        params.put("userId", Store.User.queryMe().userid);
                        params.put("goodsId", nycList.get(position).goodsId);
                        params.put("count", 1);
                        params.put("update_by_add", "true");
                        execApi(ApiType.ADDTOCART, params);
                    } else {
                        // 查询数据库 为空 就插入 不为空 就更新
                        Map<String, String> shopping = dao.getShopping(nycList
                                .get(position).goodsId);
                        if (shopping.isEmpty()) {
                            nycList.get(position).orderNum = 1;
                            // 新插入
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("pid", nycList.get(position).goodsId);
                            map.put("title", nycList.get(position).goodsName);
                            map.put("imageurl", nycList.get(position).imgUrl);
                            map.put("numbers", nycList.get(position).orderNum
                                    + "");
                            map.put("pricenow", nycList.get(position).unitPrice);
                            map.put("type", nycList.get(position).brandName);
                            map.put("priceold",
                                    nycList.get(position).originalPrice);
                            map.put("totalscore",
                                    nycList.get(position).allowScore);
                            map.put("usecore", nycList.get(position).allowScore);
                            // map.put("praises", "88");
                            dao.saveShopping(map);
                        } else {
                            // 先从数据库获取对应id的个数 然后相加本地的个数
                            Map<String, String> shop = dao.getShopping(nycList
                                    .get(position).goodsId);
                            String string = shop.get("numbers");
                            int num = Integer.valueOf(string) + 1;
                            // 更新数据库商品对应的id
                            dao.updateShopping(nycList.get(position).goodsId,
                                    "" + num);
                        }
                        showToast("添加购物车成功");
                    }

                }
            });

            return convertView;
        }

        class ViewHolder {
            private ImageView image_iv, goodsCar_iv;
            private TextView title_tv, price_tv;

            ViewHolder(View view) {
                image_iv = (ImageView) view.findViewById(R.id.goods_image);
                title_tv = (TextView) view.findViewById(R.id.goods_title);
                price_tv = (TextView) view.findViewById(R.id.goods_price);
                goodsCar_iv = (ImageView) view.findViewById(R.id.goods_car);

            }

        }

    }
    //popWindow中的品牌
    class bransAdapter extends BaseAdapter {
        private List<Datas> list;
        // 用于记录每个RadioButton的状态，并保证只可选一个
        HashMap<String, Boolean> states = new HashMap<String, Boolean>();

        public bransAdapter(List<Datas> list) {
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

                            // 重置，确保最多只有一项被选中
                            for (String key : states.keySet()) {
                                states.put(key, false);
                            }
                            states.put(String.valueOf(position),
                                    holder.brands_name_tv.isChecked());
                            bransAdapter.this.notifyDataSetChanged();
                        }
                    });

            boolean res ;
            res = !(states.get(String.valueOf(position)) == null
                    || !states.get(String.valueOf(position)));

            holder.brands_name_tv.setChecked(res);

            return convertView;
        }

        class ViewHolder {
            private RadioButton brands_name_tv;

            ViewHolder(View view) {
                brands_name_tv = (RadioButton) view
                        .findViewById(R.id.shuaixuan_item_button);
            }

        }

    }
    //popWindow中的价格
    class priceAdapter extends BaseAdapter {
        private List<String> list;
        // 用于记录每个RadioButton的状态，并保证只可选一个
        HashMap<String, Boolean> states = new HashMap<String, Boolean>();

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
            //再次点击进入后
            res = !(states.get(String.valueOf(position)) == null
                    || !states.get(String.valueOf(position)));

            holder.brands_name_tv.setChecked(res);

            return convertView;
        }

        class ViewHolder {
            private RadioButton brands_name_tv;

            ViewHolder(View view) {
                brands_name_tv = (RadioButton) view
                        .findViewById(R.id.shuaixuan_item_button);
            }

        }

    }

    /**
     * 下拉和上拉刷新
     */

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

}
