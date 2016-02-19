package com.ksfc.newfarmer.activitys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.custom.vg.list.CustomAdapter;
import com.custom.vg.list.CustomListView;
import com.google.gson.Gson;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MainActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.RndApplication;
import com.ksfc.newfarmer.adapter.GoodsDetailAdapter;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.db.dao.ShoppingDao;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.GetGoodsDetail;
import com.ksfc.newfarmer.protocol.beans.RemainGoodsAttr;
import com.ksfc.newfarmer.protocol.beans.addtoCart;
import com.ksfc.newfarmer.utils.ExpandViewTouch;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.dialog.CustomDialog;
import com.ksfc.newfarmer.widget.VerticalViewPager;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ShangpinDetailActivity extends BaseActivity implements ViewTreeObserver.OnGlobalLayoutListener {
    private ShoppingDao dao;
    private String goodId;
    private GetGoodsDetail.GoodsDetail detail;
    private TextView jinqingqidai_bar;
    private LinearLayout shangpin_detail_bottom_bar;
    private int fenshu;
    private VerticalViewPager viewPager;
    private boolean toast_flag = true;//true的时候提示 添加购物车成功  false 为不提示
    private PopupWindow popupWindow;
    private LinearLayout shangpin_detail_bg;
    private ImageView pop_image;
    private TextView pop_price;
    private TextView pop_title;
    private CustomListView pop_gv1, pop_gv2, pop_gv3, pop_gv4, pop_gv5;
    private TextView pop_text1, pop_text2, pop_text3, pop_text4, pop_text5;
    private ArrayList<AttributesAdapter> adapters;
    private ArrayList<TextView> pop_texts;
    private boolean pop_flag = false;//如果从点击购物车 或者立即购买进入popWindow pop_flag_为true ,否则false;
    private LinearLayout pop_bottom_bar;
    private LinearLayout pop_buy_now;
    private LinearLayout pop_add_to_shopcart;
    private LinearLayout pop_sure;
    private boolean pop_action = false;//如果从点击购物车 或者立即购买进入popWindow sure键有不同的action; true为加入购物车false为立即购买
    private EditText pop_discount_geshu;
    private TextView pop_jingqingqidai_bar;
    private LinearLayout pop_discount_lin;
    private String SKUId;
    private TextView product_price;//商品价格
    private TextView product_attribute;//商品已选择的属性
    private AdditionsAdapter additionsAdapter;


    @Override
    public int getLayout() {
        return R.layout.shangpin_detail_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        RndApplication.tempDestroyActivityList.add(ShangpinDetailActivity.this);
        goodId = getIntent().getStringExtra("goodId");
        setTitle("商品详情");
        initView();
        dao = new ShoppingDao(ShangpinDetailActivity.this);
        getData();
    }

    private void getData() {
        showProgressDialog();
        RequestParams params = new RequestParams();
        params.put("productId", goodId);
        execApi(ApiType.GET_GOOD_DETAIL, params);
    }

    private void initView() {

        viewPager = (VerticalViewPager) findViewById(R.id.viewPager_vertical);
        //预售时显示
        jinqingqidai_bar = ((TextView) findViewById(R.id.jingqingqidai_bar));
        shangpin_detail_bottom_bar = ((LinearLayout) findViewById(R.id.shangpin_detail_bottom_bar));
        //popWindow弹出的时候用于遮挡背景
        shangpin_detail_bg = ((LinearLayout) findViewById(R.id.shangpin_detail_bg));

        setRightImage(R.drawable.goods_shopping_cart_icon);
        setRightViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShangpinDetailActivity.this, MainActivity.class);
                intent.putExtra("id", 3);
                startActivity(intent);
            }
        });
        showRightImage();
    }

    @Override
    public void OnViewClick(View v) {

        switch (v.getId()) {
            //状态分别进行判断
            case R.id.pop_discount_jian:
                if (!TextUtils.isEmpty(pop_discount_geshu.getText().toString().trim())) {
                    fenshu = Integer
                            .valueOf(pop_discount_geshu.getText().toString().trim());
                } else {
                    //空的时候初始化为1
                    pop_discount_geshu.setText("1");
                    fenshu = 1;
                }
                if (fenshu <= 1) {
                    showToast("商品数量不能再减少了");
                    return;
                } else {
                    fenshu--;
                    pop_discount_geshu.setText(fenshu + "");
                }
                break;
            case R.id.pop_discount_jia:
                if (!TextUtils.isEmpty(pop_discount_geshu.getText().toString().trim())) {
                    fenshu = Integer
                            .valueOf(pop_discount_geshu.getText().toString().trim());
                } else {
                    //空的时候初始化为1
                    pop_discount_geshu.setText("1");
                    fenshu = 1;
                }
                fenshu++;
                pop_discount_geshu.setText(fenshu + "");
                break;
            case R.id.add_to_shopcart:
                pop_flag = true;
                pop_action = true;
                showPopUp(v);
                break;
            case R.id.buy_now:
                pop_flag = true;
                pop_action = false;
                showPopUp(v);
                break;
            case R.id.product_attribute_rel:
                //弹出popWindow筛选规格
                pop_flag = false;
                showPopUp(v);
                break;
            case R.id.pop_add_to_shopcart:
                if (!TextUtils.isEmpty(pop_discount_geshu.getText().toString().trim())) {
                    fenshu = Integer
                            .valueOf(pop_discount_geshu.getText().toString().trim());
                } else {
                    showToast("请输入正确的商品数量");
                    return;
                }
                toast_flag = true;
                addToCar();
                break;
            case R.id.pop_buy_now:
                if (!TextUtils.isEmpty(pop_discount_geshu.getText().toString().trim())) {
                    fenshu = Integer
                            .valueOf(pop_discount_geshu.getText().toString().trim());
                } else {
                    showToast("请输入正确的商品数量");
                    return;
                }
                if (isLogin()) {
                    toast_flag = false;//是否显示toast
                    addToCar();

                } else {
                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            ShangpinDetailActivity.this);
                    builder.setMessage("您还没有登录,是否登录？")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(ShangpinDetailActivity.this,
                                            LoginActivity.class);
                                    intent.putExtra("id", 0);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    CustomDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
                break;
            case R.id.pop_sure:
                //触发一次点击事件
                if (pop_action) {
                    pop_add_to_shopcart.performClick();
                } else {
                    pop_buy_now.performClick();
                }
                break;
            case R.id.pop_close:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
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
        // 获取自定义布局文件activity_popupWindow.xml的视图
        View popupWindow_view = getLayoutInflater().inflate(
                R.layout.pop_layout_goods_detail, null, false);
        //初始化popWindow中的组件
        ImageView pop_close = (ImageView) popupWindow_view.findViewById(R.id.pop_close);
        //扩大点击区域
        ExpandViewTouch.expandViewTouchDelegate(pop_close, 100, 100, 100, 100);
        pop_close.setOnClickListener(this);
        pop_image = ((ImageView) popupWindow_view.findViewById(R.id.pop_image));
        pop_price = ((TextView) popupWindow_view.findViewById(R.id.pop_price));
        pop_title = ((TextView) popupWindow_view.findViewById(R.id.pop_title));

        pop_gv1 = (CustomListView) popupWindow_view.findViewById(R.id.pop_gv_1);
        pop_gv2 = (CustomListView) popupWindow_view.findViewById(R.id.pop_gv_2);
        pop_gv3 = (CustomListView) popupWindow_view.findViewById(R.id.pop_gv_3);
        pop_gv4 = (CustomListView) popupWindow_view.findViewById(R.id.pop_gv_4);
        pop_gv5 = (CustomListView) popupWindow_view.findViewById(R.id.pop_gv_5);

        pop_gv1.setDividerHeight(20);
        pop_gv1.setDividerWidth(20);
        pop_gv2.setDividerHeight(20);
        pop_gv2.setDividerWidth(20);
        pop_gv3.setDividerHeight(20);
        pop_gv3.setDividerWidth(20);
        pop_gv4.setDividerHeight(20);
        pop_gv4.setDividerWidth(20);
        pop_gv5.setDividerHeight(20);
        pop_gv5.setDividerWidth(20);

        pop_text1 = (TextView) popupWindow_view.findViewById(R.id.pop_text1);
        pop_text2 = (TextView) popupWindow_view.findViewById(R.id.pop_text2);
        pop_text3 = (TextView) popupWindow_view.findViewById(R.id.pop_text3);
        pop_text4 = (TextView) popupWindow_view.findViewById(R.id.pop_text4);
        //附加选项
        pop_text5 = (TextView) popupWindow_view.findViewById(R.id.pop_text5);

        pop_jingqingqidai_bar = (TextView) popupWindow_view.findViewById(R.id.pop_jingqingqidai_bar);
        pop_bottom_bar = (LinearLayout) popupWindow_view.findViewById(R.id.pop_detail_bottom_bar);
        pop_buy_now = (LinearLayout) popupWindow_view.findViewById(R.id.pop_buy_now);
        pop_add_to_shopcart = (LinearLayout) popupWindow_view.findViewById(R.id.pop_add_to_shopcart);
        pop_sure = (LinearLayout) popupWindow_view.findViewById(R.id.pop_sure);
        pop_discount_lin = (LinearLayout) popupWindow_view.findViewById(R.id.pop_discount_lin);


        ImageView pop_discount_jian = (ImageView) popupWindow_view.findViewById(R.id.pop_discount_jian);
        ImageView pop_discount_jia = (ImageView) popupWindow_view.findViewById(R.id.pop_discount_jia);
        pop_discount_geshu = (EditText) popupWindow_view.findViewById(R.id.pop_discount_geshu);
        pop_discount_geshu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("0")) {
                    // 光标移到最后
                    pop_discount_geshu.setText("1");
                    Editable eText = pop_discount_geshu.getText();
                    Selection.setSelection(eText, eText.length());
                }
            }
        });


        pop_buy_now.setOnClickListener(this);
        pop_add_to_shopcart.setOnClickListener(this);
        pop_sure.setOnClickListener(this);
        pop_discount_jian.setOnClickListener(this);
        pop_discount_jia.setOnClickListener(this);

        //初始化popWindow上的一些属性
        initPopData();
        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popupWindow = new PopupWindow(popupWindow_view,
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);

        // 设置动画效果
        popupWindow.setAnimationStyle(R.style.popWindow_anim_style);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundBlack(shangpin_detail_bg, 1);
            }
        });

    }

    //初始化popWindow上的一些属性
    private void initPopData() {
        if (detail.pictures != null && !detail.pictures.isEmpty()) {
            if (StringUtil.checkStr(detail.pictures.get(0).thumbnail)) {
                ImageLoader.getInstance().displayImage(MsgID.IP + detail.pictures.get(0).thumbnail, pop_image);
            }
        }
        //名称
        if (StringUtil.checkStr(detail.name)) {
            pop_title.setText(detail.name);
        }

        //是否预售
        if (detail.presale) {
            pop_bottom_bar.setVisibility(View.GONE);
            pop_jingqingqidai_bar.setVisibility(View.VISIBLE);
            pop_discount_lin.setVisibility(View.GONE);
            pop_price.setText("即将上线");
            pop_price.setTextColor(Color.GRAY);

        } else {
            //价格
            if (detail.SKUPrice != null) {
                if (StringUtil.checkStr(detail.SKUPrice.min) && StringUtil.checkStr(detail.SKUPrice.max)) {
                    if (!detail.SKUPrice.min.equals(detail.SKUPrice.max)) {
                        pop_price.setText("¥" + detail.SKUPrice.min + "-" + detail.SKUPrice.max);
                    } else {
                        pop_price.setText("¥" + detail.SKUPrice.min);
                    }
                }
            }
        }
        if (detail.SKUAdditions != null && !detail.SKUAdditions.isEmpty()) {
            pop_text5.setVisibility(View.GONE);
            additionsAdapter = new AdditionsAdapter(detail.SKUAdditions);
            pop_gv5.setVisibility(View.GONE);
            pop_gv5.setAdapter(additionsAdapter);
        }

        //设置规格属性
        if (detail.SKUAttributes != null && !detail.SKUAttributes.isEmpty()) {
            adapters = new ArrayList<>();
            pop_texts = new ArrayList<>();
            switch (detail.SKUAttributes.size()) {
                case 4:
                    pop_text4.setVisibility(View.VISIBLE);
                    pop_gv4.setVisibility(View.VISIBLE);
                    pop_text4.setText(detail.SKUAttributes.get(3).name);
                    AttributesAdapter adapter1 = new AttributesAdapter(detail.SKUAttributes.get(3).values);
                    pop_gv4.setAdapter(adapter1);
                    adapters.add(adapter1);
                    pop_texts.add(pop_text4);
                case 3:
                    pop_text3.setVisibility(View.VISIBLE);
                    pop_gv3.setVisibility(View.VISIBLE);
                    pop_text3.setText(detail.SKUAttributes.get(2).name);
                    AttributesAdapter adapter2 = new AttributesAdapter(detail.SKUAttributes.get(2).values);
                    pop_gv3.setAdapter(adapter2);
                    pop_texts.add(pop_text3);
                    adapters.add(adapter2);
                case 2:
                    pop_text2.setVisibility(View.VISIBLE);
                    pop_gv2.setVisibility(View.VISIBLE);
                    pop_text2.setText(detail.SKUAttributes.get(1).name);
                    AttributesAdapter adapter3 = new AttributesAdapter(detail.SKUAttributes.get(1).values);
                    pop_gv2.setAdapter(adapter3);
                    pop_texts.add(pop_text2);
                    adapters.add(adapter3);
                case 1:
                    pop_text1.setVisibility(View.VISIBLE);
                    pop_gv1.setVisibility(View.VISIBLE);
                    pop_text1.setText(detail.SKUAttributes.get(0).name);
                    AttributesAdapter adapter4 = new AttributesAdapter(detail.SKUAttributes.get(0).values);
                    pop_gv1.setAdapter(adapter4);
                    pop_texts.add(pop_text1);
                    adapters.add(adapter4);
                    break;
            }
        }
        //如果单项为1时，默认选中
        try {
            for (int i = 0; i < adapters.size(); i++) {
                if (adapters.get(i).getCount() == 1) {
                    adapters.get(i).states.put(detail.SKUAttributes.get(adapters.size() - (i + 1)).values.get(0), true);
                }
            }
            getRemainAttr();
        } catch (Exception e) {
            RndLog.v(TAG, "adapter_null");
        }
    }

    /**
     * 规格适配器
     */
    class AttributesAdapter extends CustomAdapter {
        private List<String> list;
        public HashMap<String, Boolean> states = new HashMap<>();


        public AttributesAdapter(List<String> values) {
            this.list = values;
            initMap();
        }

        public void initMap() {
            //把所有的规格加入到集合中
            for (String key : list) {
                states.put(key, false);
            }
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_popwindow_gv_attr, parent, false);
                convertView.setTag(new ViewHolder(convertView));
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag();
            if (StringUtil.checkStr(list.get(position))) {
                holder.checkBox.setText(list.get(position));
            }
            //如果选中，集合中的值为true 否则为false
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 重置，确保最多只有一项被选中
                    for (String key : states.keySet()) {
                        states.put(key, false);
                    }
                    states.put(list.get(position), holder.checkBox.isChecked());
                    getRemainAttr();
                    //正在纠结点完刷新还是数据下来才刷新
                    //AttributesAdapter.this.notifyDataSetChanged();
                }

            });

            //根据map刷新适配器的选中状态
            boolean res = false;
            if (states.get(list.get(position)) != null) {
                ColorStateList csl = getResources().getColorStateList(R.color.tab_selector_invite_textcolor);
                holder.checkBox.setTextColor(csl);
                holder.checkBox.setEnabled(true);
                res = states.get(list.get(position));
            } else {
                holder.checkBox.setTextColor(Color.parseColor("#d0d0d0"));
                holder.checkBox.setEnabled(false);
            }
            holder.checkBox.setChecked(res);
            return convertView;
        }


        class ViewHolder {
            private CheckBox checkBox;

            public ViewHolder(View convertView) {
                this.checkBox = (CheckBox) convertView.findViewById(R.id.attr_item_checkBox);
            }
        }
    }

    /**
     * 附加选项1
     */
    class AdditionsAdapter extends CustomAdapter {
        public List<GetGoodsDetail.GoodsDetail.SKUAdditions> list;
        private HashMap<String, Boolean> states = new HashMap<>();

        public AdditionsAdapter(List<GetGoodsDetail.GoodsDetail.SKUAdditions> values) {
            this.list = values;
            initMap();
        }

        public void initMap() {
            //把所有的规格加入到集合中
            for (GetGoodsDetail.GoodsDetail.SKUAdditions key : list) {
                states.put(key._id, false);
            }
        }


        public void addAll(Collection<? extends GetGoodsDetail.GoodsDetail.SKUAdditions> values) {
            list.clear();
            list.addAll(values);
            AdditionsAdapter.this.notifyDataSetChanged();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_popwindow_gv_attr, parent, false);
                convertView.setTag(new ViewHolder(convertView));
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag();
            if (StringUtil.checkStr(list.get(position).name) && StringUtil.checkStr(list.get(position).price)) {
                holder.checkBox.setText(list.get(position).name + "( +" + list.get(position).price + ")");
            }


            //如果选中，集合中的值为true 否则为false
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    states.put(list.get(position)._id, holder.checkBox.isChecked());
                    AdditionsAdapter.this.notifyDataSetChanged();
                    getRemainAttr();
                }
            });

            //根据map刷新适配器的选中状态
            boolean res = false;
            if (states.get(list.get(position)._id) != null) {
                res = states.get(list.get(position)._id);
            }
            holder.checkBox.setChecked(res);
            return convertView;
        }


        class ViewHolder {
            private CheckBox checkBox;

            public ViewHolder(View convertView) {
                this.checkBox = (CheckBox) convertView.findViewById(R.id.attr_item_checkBox);
            }
        }
    }

    //得到规格属性
    public void getRemainAttr() {
        List<Map<String, String>> list = new ArrayList<>();

        for (int i = 0; i < adapters.size(); i++) {
            String name = pop_texts.get(i).getText().toString();
            String value = "";
            for (String key : adapters.get(i).states.keySet()) {
                if (adapters.get(i).states.get(key)) {
                    value = key;
                }
            }
            if (StringUtil.checkStr(value)) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", name);
                map.put("value", value);
                list.add(map);
            }
            //初始化其他未选中的列表
            adapters.get(i).states.clear();
            adapters.get(i).initMap();
            adapters.get(i).states.put(value, true);
        }


        //请求数据
        Gson gson = new Gson();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("product", detail._id);
        map1.put("attributes", list);
        String jsonStr = gson.toJson(map1);
        RequestParams params = new RequestParams();
        params.put("JSON", jsonStr);
        execApi(ApiType.GET_GOOD_ATTR.setMethod(ApiType.RequestMethod.POSTJSON), params);
        showProgressDialog();

    }


    //显示popWindow
    private void showPopUp(View parent) {
        if (null != popupWindow) {
            popupWindow.dismiss();
        } else {
            initPopuptWindow();
        }
        //如果从点击购物车 或者立即购买进入popWindow pop_flag_为true ,否则false;
        if (pop_flag) {
            pop_buy_now.setVisibility(View.GONE);
            pop_add_to_shopcart.setVisibility(View.GONE);
            pop_sure.setVisibility(View.VISIBLE);
        } else {
            pop_buy_now.setVisibility(View.VISIBLE);
            pop_add_to_shopcart.setVisibility(View.VISIBLE);
            pop_sure.setVisibility(View.GONE);
        }


        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //软件盘监听
        View toSoft_ll = pop_discount_lin;
        toSoft_ll.getViewTreeObserver().addOnGlobalLayoutListener(this);

        //设置背景及展示
        setBackgroundBlack(shangpin_detail_bg, 0);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }


    /**
     * 加入购物车
     */
    private void addToCar() {
        if (!TextUtils.isEmpty(pop_discount_geshu.getText().toString().trim()) && !pop_discount_geshu.getText().toString().trim().equals("0")) {
            fenshu = Integer
                    .valueOf(pop_discount_geshu.getText().toString().trim());
        } else {
            fenshu = 1;
        }
        if (fenshu <= 0) {
            showToast("请先添加商品！");
            return;
        }
        if (isLogin()) {
            // app/shopCart/addToCart
            // locationUserId:操作人ID
            // goodsId:商品ID
            // userId:用户ID
            // count：件数
            if (StringUtil.checkStr(SKUId)) {
                RequestParams params = new RequestParams();
                Gson gson = new Gson();
                Map<String, Object> map = new HashMap<>();
                if (isLogin()){
                    map.put("token", Store.User.queryMe().token);
                }
                map.put("quantity", fenshu);
                map.put("SKUId", SKUId);
                map.put("update_by_add", "true");
                if (additionsAdapter != null
                        && additionsAdapter.states != null
                        && additionsAdapter.states.containsValue(true)) {
                    List<String> list = new ArrayList<>();
                    for (String key : additionsAdapter.states.keySet()) {
                        if (additionsAdapter.states.get(key)) {
                            list.add(key);
                        }
                    }
                    map.put("additions", list);
                } else {
                    map.put("additions", new ArrayList<>());
                }
                String json = gson.toJson(map);
                params.put("JSON", json);
                execApi(ApiType.ADDTOCART.setMethod(ApiType.RequestMethod.POSTJSON), params);
                //立即购买
                if (!toast_flag) {
                    Intent intent = new Intent(ShangpinDetailActivity.this, OrderDetailActivity.class);
                    intent.putExtra("SKUId", SKUId);
                    intent.putExtra("count", fenshu + "");
                    //附加选项发到提交订单页  立即购买
                    if (detail.SKUAdditions != null && !detail.SKUAdditions.isEmpty()) {
                        if (additionsAdapter != null
                                && additionsAdapter.states != null
                                && additionsAdapter.states.containsValue(true)) {
                            List<String> list = new ArrayList<>();
                            List<GetGoodsDetail.GoodsDetail.SKUAdditions> skuAdditions = new ArrayList<>();
                            for (String key : additionsAdapter.states.keySet()) {
                                if (additionsAdapter.states.get(key)) {
                                    list.add(key);
                                }
                            }
                            for (GetGoodsDetail.GoodsDetail.SKUAdditions key : detail.SKUAdditions) {
                                if (list.contains(key._id)) {
                                    skuAdditions.add(key);
                                }
                            }
                            intent.putExtra("additions", (Serializable) skuAdditions);
                        }
                    }

                    intent.addFlags(1);
                    startActivity(intent);
                }
            } else {
                showToast("请选择商品信息");
            }
        } else {
            // 查询数据库 为空 就插入 不为空 就更新
            if (StringUtil.checkStr(SKUId)) {
                Map<String, String> shopping = dao.getShopping(SKUId);
                if (shopping.isEmpty()) {
                    // 新插入
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("pid", detail.id);
                    map.put("title", detail.name);
                    map.put("imageurl", detail.imgUrl);
                    map.put("numbers", pop_discount_geshu.getText().toString()
                            .trim()
                            + "");
                    //附加选项存在本地数据库
                    if (detail.SKUAdditions != null && !detail.SKUAdditions.isEmpty()) {
                        if (additionsAdapter != null
                                && additionsAdapter.states != null
                                && additionsAdapter.states.containsValue(true)) {
                            List<String> list = new ArrayList<>();
                            List<GetGoodsDetail.GoodsDetail.SKUAdditions> skuAdditions = new ArrayList<>();
                            for (String key : additionsAdapter.states.keySet()) {
                                if (additionsAdapter.states.get(key)) {
                                    list.add(key);
                                }
                            }
                            for (GetGoodsDetail.GoodsDetail.SKUAdditions key : detail.SKUAdditions) {
                                if (list.contains(key._id)) {
                                    skuAdditions.add(key);
                                }
                            }
                            Gson gson = new Gson();
                            String additions = gson.toJson(skuAdditions);
                            map.put("additions", additions);
                        }
                    }
                    map.put("SKUId", SKUId);
                    map.put("stars", "");
                    map.put("pricenow", detail.price);
                    dao.saveShopping(map);
                } else {
                    // 先从数据库获取对应id的个数 然后相加本地的个数
                    Map<String, String> shop = dao.getShopping(SKUId);
                    String numbers = shop.get("numbers");
                    // 更新数据库商品对应的id
                    int sumNum = Integer.valueOf(numbers)
                            + Integer.valueOf(pop_discount_geshu.getText()
                            .toString().trim());
                    dao.updateShopping(SKUId, sumNum + "");

                    //附加选项存在本地数据库
                    if (detail.SKUAdditions != null && !detail.SKUAdditions.isEmpty()) {
                        if (additionsAdapter != null
                                && additionsAdapter.states != null
                                && additionsAdapter.states.containsValue(true)) {
                            List<String> list = new ArrayList<>();
                            List<GetGoodsDetail.GoodsDetail.SKUAdditions> skuAdditions = new ArrayList<>();
                            for (String key : additionsAdapter.states.keySet()) {
                                if (additionsAdapter.states.get(key)) {
                                    list.add(key);
                                }
                            }
                            for (GetGoodsDetail.GoodsDetail.SKUAdditions key : detail.SKUAdditions) {
                                if (list.contains(key._id)) {
                                    skuAdditions.add(key);
                                }
                            }
                            Gson gson = new Gson();
                            String additions = gson.toJson(skuAdditions);
                            dao.updateShoppingAdditions(SKUId, additions);
                        } else {
                            dao.updateShoppingAdditions(SKUId, "[]");
                        }
                    }


                }
                //添加购物车成功后关闭popupWindow
                showToast("添加购物车成功");
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            } else {
                showToast("请选择商品信息");
            }
        }
    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (req.getApi() == ApiType.GET_GOOD_DETAIL) {
            GetGoodsDetail data = (GetGoodsDetail) req.getData();
            detail = data.datas;
            //如果存在bodyUrl加载更多详情
            if (!TextUtils.isEmpty(detail.app_body_url)) {
                GoodsDetailAdapter adapter = new GoodsDetailAdapter(getSupportFragmentManager(), 2, detail);
                viewPager.setAdapter(adapter);
                viewPager.setOffscreenPageLimit(0);
            } else {
                GoodsDetailAdapter adapter = new GoodsDetailAdapter(getSupportFragmentManager(), 1, detail);
                viewPager.setAdapter(adapter);
            }
            //商品是否预售
            if (detail.presale || !detail.online) {
                jinqingqidai_bar.setVisibility(View.VISIBLE);
                shangpin_detail_bottom_bar.setVisibility(View.GONE);
                if (detail.presale) {
                    jinqingqidai_bar.setText("敬请期待");
                } else {
                    jinqingqidai_bar.setText("商品已下架");
                }

            } else {
                jinqingqidai_bar.setVisibility(View.GONE);
                shangpin_detail_bottom_bar.setVisibility(View.VISIBLE);
            }


            setViewClick(R.id.add_to_shopcart);
            setViewClick(R.id.discount_jian);
            setViewClick(R.id.discount_jia);
            setViewClick(R.id.buy_now);


            //获得其子布局中的fragment中的组件
            Fragment fragment = getSupportFragmentManager().getFragments().get(0);
            if (fragment != null) {
                View view = fragment.getView().findViewById(R.id.product_attribute_rel);
                product_price = (TextView) fragment.getView().findViewById(R.id.product_price);
                product_attribute = (TextView) fragment.getView().findViewById(R.id.product_attribute);
                //如果是下架的商品 不展示sku
                if (view != null) {
                    if (detail.online) {
                        view.setOnClickListener(this);
                    } else {
                        view.setVisibility(View.GONE);
                    }
                }
            }


        } else if (req.getApi() == ApiType.ADDTOCART) {
            addtoCart data = (addtoCart) req.getData();
            if ("1000".equals(data.getStatus())) {
                if (toast_flag) {
                    //添加购物车成功后关闭popupWindow
                    showToast("添加购物车成功");
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                }
            }


            /**
             * 获得SKU
             */
        } else if (req.getApi() == ApiType.GET_GOOD_ATTR) {
            RemainGoodsAttr remainGoodsAttr = (RemainGoodsAttr) req.getData();
            if ("1000".equals(remainGoodsAttr.getStatus())) {
                //更新popWindow中和商品详情中的的价格区间
                if (StringUtil.checkStr(remainGoodsAttr.data.price.min) && StringUtil.checkStr(remainGoodsAttr.data.price.max)) {
                    if (!detail.presale) {

                        if (remainGoodsAttr.data.price.min.equals(remainGoodsAttr.data.price.max)) {
                            //加入规格的价钱
                            if (additionsAdapter != null) {
                                float price = 0;
                                List<String> list1 = new ArrayList<>();
                                for (Map.Entry<String, Boolean> entry : additionsAdapter.states.entrySet()) {
                                    if (entry.getValue()) {
                                        list1.add(entry.getKey());
                                    }
                                }
                                for (int i = 0; i < additionsAdapter.list.size(); i++) {
                                    if (list1.contains(additionsAdapter.list.get(i)._id)) {
                                        try {
                                            price += Double.parseDouble(additionsAdapter.list.get(i).price);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                try {
                                    double price_total = Double.parseDouble(remainGoodsAttr.data.price.min) + price;
                                    pop_price.setText("¥" + reduceDouble(price_total));
                                    product_price.setText("¥" + reduceDouble(price_total));
                                } catch (Exception e) {
                                    pop_price.setText("¥" + remainGoodsAttr.data.price.min);
                                    product_price.setText("¥" + remainGoodsAttr.data.price.min);
                                }

                            } else {
                                pop_price.setText("¥" + remainGoodsAttr.data.price.min);
                                product_price.setText("¥" + remainGoodsAttr.data.price.min);
                            }


                        } else {
                            //加入规格的价钱
                            if (additionsAdapter != null) {
                                float price = 0;
                                List<String> list1 = new ArrayList<>();
                                for (Map.Entry<String, Boolean> entry : additionsAdapter.states.entrySet()) {
                                    if (entry.getValue()) {
                                        list1.add(entry.getKey());
                                    }
                                }
                                for (int i = 0; i < additionsAdapter.list.size(); i++) {
                                    if (list1.contains(additionsAdapter.list.get(i)._id)) {
                                        try {
                                            price += Double.parseDouble(additionsAdapter.list.get(i).price);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                try {
                                    double price_min = Double.parseDouble(remainGoodsAttr.data.price.min) + price;
                                    double price_max = Double.parseDouble(remainGoodsAttr.data.price.max) + price;
                                    pop_price.setText("¥" + reduceDouble(price_min) + "-" + reduceDouble(price_max));
                                    product_price.setText("¥" + reduceDouble(price_min) + "-" + reduceDouble(price_max));
                                } catch (Exception e) {
                                    pop_price.setText
                                            ("¥" + remainGoodsAttr.data.price.min + "-" + remainGoodsAttr.data.price.max);
                                    product_price.setText
                                            ("¥" + remainGoodsAttr.data.price.min + "-" + remainGoodsAttr.data.price.max);
                                }
                            } else {
                                pop_price.setText
                                        ("¥" + remainGoodsAttr.data.price.min + "-" + remainGoodsAttr.data.price.max);
                                product_price.setText
                                        ("¥" + remainGoodsAttr.data.price.min + "-" + remainGoodsAttr.data.price.max);
                            }
                        }
                    }
                }
                //已选的SKU
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("已选择");
                for (int i = adapters.size() - 1; i >= 0; i--) {
                    for (Map.Entry<String, Boolean> entry : adapters.get(i).states.entrySet()) {
                        if (entry.getValue()) {
                            if (!entry.getKey().equals("")) {
                                stringBuilder.append("\"" + entry.getKey() + "\"");
                            }
                        }
                    }
                    adapters.get(i).notifyDataSetChanged();
                }
                if (stringBuilder.toString().equals("已选择")) {
                    product_attribute.setText("请选择商品属性");
                } else {
                    product_attribute.setText(stringBuilder.toString());
                }
                if (remainGoodsAttr.data.SKU != null) {
                    SKUId = remainGoodsAttr.data.SKU._id;
                    if (additionsAdapter != null && additionsAdapter.getCount() > 0) {
                        pop_text5.setVisibility(View.VISIBLE);
                        pop_gv5.setVisibility(View.VISIBLE);
                    }
                } else {
                    pop_text5.setVisibility(View.GONE);
                    pop_gv5.setVisibility(View.GONE);
                    SKUId = null;
                }
                //popWindow中的sku
                if (remainGoodsAttr.data.attributes != null) {
                    List<String> list = new ArrayList<>();
                    //list 中还有所有剩余的规格
                    for (int i = 0; i < remainGoodsAttr.data.attributes.size(); i++) {
                        for (int j = 0; j < remainGoodsAttr.data.attributes.get(i).values.size(); j++) {
                            list.add(remainGoodsAttr.data.attributes.get(i).values.get(j));
                        }
                    }
                    //待选sku
                    for (int i = 0; i < adapters.size(); i++) {
                        Iterator<Map.Entry<String, Boolean>> it = adapters.get(i).states.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<String, Boolean> entry = it.next();
                            if (!list.contains(entry.getKey()) && !entry.getValue()) {
                                it.remove();
                            }
                            adapters.get(i).notifyDataSetChanged();
                        }
                    }
                }
                //popWindow中的附加选项
                if (remainGoodsAttr.data.additions != null && !remainGoodsAttr.data.additions.isEmpty()) {
                    List<GetGoodsDetail.GoodsDetail.SKUAdditions> list = new ArrayList<>();
                    List<String> stringArrayList = new ArrayList<>();
                    for (int i = 0; i < remainGoodsAttr.data.additions.size(); i++) {
                        GetGoodsDetail.GoodsDetail.SKUAdditions additions = new GetGoodsDetail.GoodsDetail.SKUAdditions();
                        additions.name = remainGoodsAttr.data.additions.get(i).name;
                        additions._id = remainGoodsAttr.data.additions.get(i).ref;
                        additions.price = remainGoodsAttr.data.additions.get(i).price;
                        list.add(additions);
                        stringArrayList.add(additions._id);
                    }


                    Iterator<Map.Entry<String, Boolean>> it = additionsAdapter.states.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, Boolean> entry = it.next();
                        if (!stringArrayList.contains(entry.getKey())) {
                            it.remove();
                        }
                        additionsAdapter.notifyDataSetChanged();
                    }
                    if (additionsAdapter != null) {
                        additionsAdapter.addAll(list);
                    }
                }
            }
        }
    }

    //监听软键盘收起时，如果输入框为“”，变为1
    @Override
    public void onGlobalLayout() {
        if (pop_discount_geshu.getText().toString().trim().equals("")) {
            pop_discount_geshu.setText("1");
            Editable eText = pop_discount_geshu.getText();
            Selection.setSelection(eText, eText.length());
        }
    }

    /**
     * popupWindow时控制背景变暗 0变暗 1变亮
     */
    public void setBackgroundBlack(View view, int what) {
        switch (what) {
            case 0:
                AlphaAnimation animation =new AlphaAnimation(0.0f,1.0f);
                animation.setDuration(150);
                view.setAnimation(animation);
                view.setVisibility(View.VISIBLE);
                break;
            case 1:
                view.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 去掉double类型尾部的0
     */
    public String reduceDouble(double price) {
        int i = (int) price;
        if (i == price) {
            return i + "";
        } else {
            return StringUtil.toTwoString(price + "");
        }

    }
}
