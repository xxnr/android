package com.ksfc.newfarmer.activitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.adapter.CommonAdapter;
import com.ksfc.newfarmer.adapter.CommonViewHolder;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.LoginResult;
import com.ksfc.newfarmer.protocol.beans.RSCAddressListResult;
import com.ksfc.newfarmer.protocol.beans.RSCStateInfoResult;
import com.ksfc.newfarmer.utils.PopWindowUtils;
import com.ksfc.newfarmer.utils.PullToRefreshUtils;
import com.ksfc.newfarmer.utils.StringUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HePeng on 2016/3/9.
 */
public class SelectDeliveriesStateActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2 {

    private TextView state_province_text;
    private ImageView state_province_img;
    private TextView state_city_text;
    private ImageView state_city_img;
    private TextView state_county_text;
    private ImageView state_county_img;
    private View state_province_bar;
    private View state_city_bar;
    private View state_county_bar;

    private PullToRefreshListView select_state_listView;

    // 声明PopupWindow对象的引用
    private PopupWindow popupWindow;
    //分割线
    private TextView shaixuan_bar_separatrix;

    private DeliveryAddressProvinceAdapter provinceAdapter;
    private DeliveryAddressCityAdapter cityAdapter;
    private DeliveryAddressCountyAdapter countyAdapter;

    private final static int provinceTag = 1;
    private final static int cityTag = 2;
    private final static int countyTag = 3;

    private String provinceId;
    private String cityId;
    private String countyId;

    private ListView popListView;
    private LinearLayout state_county_rel;//根据需要可以隐藏
    private TextView save_userInfo;
    private RelativeLayout pop_bg;
    private int page = 1;

    private Map<String, Boolean> checkedStateMap = new HashMap<>();
    private DeliveryAdapter deliveryAdapter;

    private final static int ResultState = 0x12;


    @Override
    public int getLayout() {
        return R.layout.select_deliveries_state_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("选择自提网点");
        initView();
        setViewClick(R.id.state_province_rel);
        setViewClick(R.id.state_city_rel);
        setViewClick(R.id.state_county_rel);
        setViewClick(R.id.save_userInfo);


        getProvinceState();
        //默认选中河南
        state_province_text.setText("河南");
        provinceId = "5649bd6c8eba3c20360afa0a";
        showProgressDialog();
        getCityState(provinceId);
        getStateList(provinceId, null, null);
        //默认县级不可选
        state_county_rel.setEnabled(false);
        state_county_img.setBackgroundResource(R.drawable.bottom_arrow_gray);
        state_county_text.setTextColor(getResources().getColor(R.color.gray));

        //默认保存不可点
        save_userInfo.setEnabled(false);
        //默认背景不展示
        pop_bg.setVisibility(View.GONE);
    }


    private void initView() {
        state_province_text = (TextView) findViewById(R.id.state_province_text);
        state_province_img = (ImageView) findViewById(R.id.state_province_img);
        state_city_text = (TextView) findViewById(R.id.state_city_text);
        state_city_img = (ImageView) findViewById(R.id.state_city_img);
        state_county_text = (TextView) findViewById(R.id.state_county_text);
        state_county_img = (ImageView) findViewById(R.id.state_county_img);
        state_province_bar = (View) findViewById(R.id.state_province_bar);
        state_city_bar = (View) findViewById(R.id.state_city_bar);
        state_county_bar = (View) findViewById(R.id.state_county_bar);
        shaixuan_bar_separatrix = (TextView) findViewById(R.id.shaixuan_bar_separatrix);
        select_state_listView = (PullToRefreshListView) findViewById(R.id.select_state_listView);
        state_county_rel = (LinearLayout) findViewById(R.id.state_county_rel);
        save_userInfo = (TextView) findViewById(R.id.save_userInfo);
        pop_bg = ((RelativeLayout) findViewById(R.id.pop_bg));

        select_state_listView.setMode(PullToRefreshBase.Mode.BOTH);
        select_state_listView.setOnRefreshListener(this);
        //设置刷新的文字
        PullToRefreshUtils.setFreshText(select_state_listView);
    }


    @Override
    public void OnViewClick(View v) {

        switch (v.getId()) {
            case R.id.state_province_rel:

                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    showPopupWindow(provinceTag);
                }
                state_province_img.setBackgroundResource(R.drawable.top_arrow);
                state_province_text.setTextColor(getResources().getColor(R.color.orange_goods_price));
                state_province_bar.setVisibility(View.VISIBLE);
                break;
            case R.id.state_city_rel:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    showPopupWindow(cityTag);
                }

                state_city_img.setBackgroundResource(R.drawable.top_arrow);
                state_city_text.setTextColor(getResources().getColor(R.color.orange_goods_price));
                state_city_bar.setVisibility(View.VISIBLE);
                break;
            case R.id.state_county_rel:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    showPopupWindow(countyTag);
                }
                state_county_img.setBackgroundResource(R.drawable.top_arrow);
                state_county_text.setTextColor(getResources().getColor(R.color.orange_goods_price));
                state_county_bar.setVisibility(View.VISIBLE);
                break;

            case R.id.save_userInfo:
                String RSCId = null;
                RSCStateInfoResult.RSCsEntity rsCsEntity = null;
                //寻找选中ID
                for (String key : checkedStateMap.keySet()) {
                    if (checkedStateMap.get(key)) {
                        RSCId = key;
                    }
                }
                //寻找选中对象
                if (deliveryAdapter != null) {
                    List<RSCStateInfoResult.RSCsEntity> data = deliveryAdapter.getData();
                    if (data != null) {
                        for (int i = 0; i < data.size(); i++) {
                            if (data.get(i)._id.equals(RSCId)) {
                                rsCsEntity = data.get(i);
                            }
                        }

                    }

                }
                //保存用户
                Intent intent = new Intent();
                intent.putExtra("rsCsEntity", (Serializable) rsCsEntity);
                setResult(ResultState, intent);
                finish();
                break;
        }
    }

    public void resetView() {
        state_province_img.setBackgroundResource(R.drawable.bottom_arrow);
        state_city_img.setBackgroundResource(R.drawable.bottom_arrow);
        state_province_text.setTextColor(getResources().getColor(R.color.main_index_gary));
        state_city_text.setTextColor(getResources().getColor(R.color.main_index_gary));
        state_province_bar.setVisibility(View.INVISIBLE);
        state_city_bar.setVisibility(View.INVISIBLE);
        state_county_bar.setVisibility(View.INVISIBLE);

        if (state_county_rel.isEnabled()) {
            state_county_img.setBackgroundResource(R.drawable.bottom_arrow);
            state_county_text.setTextColor(getResources().getColor(R.color.main_index_gary));
        } else {
            state_county_img.setBackgroundResource(R.drawable.bottom_arrow_gray);
            state_county_text.setTextColor(getResources().getColor(R.color.gray));
        }
    }


    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (req.getApi() == ApiType.GET_RSC_ADDRESS_PROVINCE) {
            if (req.getData().getStatus().equals("1000")) {

                RSCAddressListResult data = (RSCAddressListResult) req.getData();
                List<RSCAddressListResult.ProvinceListEntity> provinceList = data.provinceList;
                provinceAdapter = new DeliveryAddressProvinceAdapter(this, provinceList);
            }
        } else if (req.getApi() == ApiType.GET_RSC_ADDRESS_CITY) {
            if (req.getData().getStatus().equals("1000")) {
                RSCAddressListResult data = (RSCAddressListResult) req.getData();
                List<RSCAddressListResult.CityListEntity> cityList = data.cityList;
                //全部地区
                RSCAddressListResult.CityListEntity ListEntity = new RSCAddressListResult.CityListEntity();
                ListEntity._id = null;
                ListEntity.name = "全部地区";
                cityList.add(0, ListEntity);
                cityAdapter = new DeliveryAddressCityAdapter(this, cityList);
            }

        } else if (req.getApi() == ApiType.GET_RSC_ADDRESS_COUNTY) {
            if (req.getData().getStatus().equals("1000")) {
                RSCAddressListResult data = (RSCAddressListResult) req.getData();
                List<RSCAddressListResult.CountyListEntity> countyList = data.countyList;
                //全部地区
                RSCAddressListResult.CountyListEntity ListEntity = new RSCAddressListResult.CountyListEntity();
                ListEntity._id = null;
                ListEntity.name = "全部地区";
                countyList.add(0, ListEntity);
                countyAdapter = new DeliveryAddressCountyAdapter(this, countyList);
            }
        } else if (req.getApi() == ApiType.GET_RSC_STATE_INFO) {
            select_state_listView.onRefreshComplete();
            if (req.getData().getStatus().equals("1000")) {
                RSCStateInfoResult data = (RSCStateInfoResult) req.getData();


                if (data.RSCs != null && !data.RSCs.isEmpty()) {


                    if (page == 1) {
                        boolean is_clear = true;
                        for (RSCStateInfoResult.RSCsEntity key : data.RSCs) {
                            if (key != null) {
                                if (checkedStateMap.containsKey(key._id) && checkedStateMap.get(key._id)) {
                                    is_clear = false;
                                }
                            }
                        }
                        if (is_clear) {
                            save_userInfo.setEnabled(false);
                            checkedStateMap.clear();
                        }

                        deliveryAdapter = new DeliveryAdapter(this, data.RSCs);
                        select_state_listView.setAdapter(deliveryAdapter);
                    } else {
                        if (deliveryAdapter != null) {
                            deliveryAdapter.addAll(data.RSCs);
                        }
                    }
                } else {
                    if (page == 1) {
                        if (deliveryAdapter!=null){
                            deliveryAdapter.clear();
                        }
                        showToast("该地区没有网点");
                    } else {
                        showToast("没有更多网点");
                        page--;
                    }

                }


            }
        }

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        PullToRefreshUtils.setFreshClose(refreshView);
        page = 1;
        getStateList(provinceId, cityId, countyId);

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        PullToRefreshUtils.setFreshClose(refreshView);
        page++;
        getStateList(provinceId, cityId, countyId);
    }

    //网点适配器
    class DeliveryAdapter extends CommonAdapter<RSCStateInfoResult.RSCsEntity> {


        public DeliveryAdapter(Context context, List<RSCStateInfoResult.RSCsEntity> data) {
            super(context, data, R.layout.item_delivery_state_layout);
        }

        @Override
        public void convert(CommonViewHolder holder, final RSCStateInfoResult.RSCsEntity rsCsEntity) {
            //初始化一切文本
            if (rsCsEntity != null && rsCsEntity.RSCInfo != null) {
                //网点名
                if (StringUtil.checkStr(rsCsEntity.RSCInfo.companyName)){
                    holder.setText(R.id.title_delivery_state_item, rsCsEntity.RSCInfo.companyName);
                }else {
                    holder.setText(R.id.title_delivery_state_item, "");
                }
                //电话
                if (StringUtil.checkStr(rsCsEntity.RSCInfo.phone)) {
                    holder.setText(R.id.phone_delivery_state_item, "电话：" + rsCsEntity.RSCInfo.phone);
                }else {
                    holder.setText(R.id.phone_delivery_state_item, "电话：");
                }
                //网点所在地址
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
                builder.append("地址：").append(address);
                if (companyAddress != null && StringUtil.checkStr(companyAddress.details)) {
                    builder.append(companyAddress.details);
                }
                holder.setText(R.id.address_delivery_state_item, builder.toString());
                final CheckBox checkBox = (CheckBox) holder.getView(R.id.btn_delivery_state_item);


                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 重置，确保最多只有一项被选中
                        for (String key : checkedStateMap.keySet()) {
                            checkedStateMap.put(key, false);
                        }
                        checkedStateMap.put(rsCsEntity._id,
                                true);
                        save_userInfo.setEnabled(true);
                        notifyDataSetChanged();
                    }
                });
                Boolean res = checkedStateMap.get(rsCsEntity._id);
                if (res != null && res) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                    checkedStateMap.put(rsCsEntity._id,
                            false);
                }
            }

        }
    }

    //popWindow中的地址省适配器
    class DeliveryAddressProvinceAdapter extends CommonAdapter<RSCAddressListResult.ProvinceListEntity> {


        public DeliveryAddressProvinceAdapter(Context context, List<RSCAddressListResult.ProvinceListEntity> data) {
            super(context, data, R.layout.item_delivery_address_layout);
        }

        @Override
        public void convert(CommonViewHolder holder, final RSCAddressListResult.ProvinceListEntity provinceListEntity) {
            if (provinceListEntity != null) {
                TextView textView = (TextView) holder.getView(R.id.item_delivery_address_text);
                textView.setText(provinceListEntity.name);

                if (provinceListEntity.name.equals(state_province_text.getText().toString())) {
                    textView.setTextColor(getResources().getColor(R.color.orange_goods_price));
                } else {
                    textView.setTextColor(getResources().getColor(R.color.black_goods_titile));
                }
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        provinceId = provinceListEntity._id;
                        //执行api请求河南所有的市和所有的网点
                        getCityState(provinceId);
                        //执行api请求网点
                        page = 1;
                        showProgressDialog();
                        //选省 重置市县
                        cityId = null;
                        countyId = null;
                        state_county_text.setText("全部地区");
                        state_city_text.setText("全部地区");
                        getStateList(provinceId, cityId, countyId);
                        if (popupWindow != null && popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        state_province_text.setText(provinceListEntity.name);

                    }
                });
            }


        }
    }

    //popWindow中的地址市适配器
    class DeliveryAddressCityAdapter extends CommonAdapter<RSCAddressListResult.CityListEntity> {

        public DeliveryAddressCityAdapter(Context context, List<RSCAddressListResult.CityListEntity> data) {
            super(context, data, R.layout.item_delivery_address_layout);
        }

        @Override
        public void convert(CommonViewHolder holder, final RSCAddressListResult.CityListEntity cityListEntity) {
            if (cityListEntity != null) {
                TextView textView = (TextView) holder.getView(R.id.item_delivery_address_text);
                textView.setText(cityListEntity.name);

                if (cityListEntity.name.equals(state_city_text.getText().toString())) {
                    textView.setTextColor(getResources().getColor(R.color.orange_goods_price));
                } else {
                    textView.setTextColor(getResources().getColor(R.color.black_goods_titile));
                }
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cityId = cityListEntity._id;
                        //执行api请求河南所有的县
                        if (cityId == null) {
                            countyId = null;
                            state_county_text.setText("全部地区");
                            if (countyAdapter != null) {
                                countyAdapter.clear();
                            }
                            state_county_rel.setEnabled(false);
                            state_county_img.setBackgroundResource(R.drawable.bottom_arrow_gray);
                            state_county_text.setTextColor(getResources().getColor(R.color.gray));


                        } else {
                            state_county_rel.setEnabled(true);
                            state_county_img.setBackgroundResource(R.drawable.bottom_arrow);
                            state_county_text.setTextColor(getResources().getColor(R.color.black_goods_titile));
                            getCountyState(provinceId, cityId);
                        }
                        //执行api请求网点
                        page = 1;
                        showProgressDialog();
                        //选市重置县
                        countyId = null;
                        state_county_text.setText("全部地区");
                        getStateList(provinceId, cityId, countyId);
                        if (popupWindow != null && popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        state_city_text.setText(cityListEntity.name);

                    }
                });
            }


        }
    }


    //popWindow中的地址县适配器
    class DeliveryAddressCountyAdapter extends CommonAdapter<RSCAddressListResult.CountyListEntity> {

        public DeliveryAddressCountyAdapter(Context context, List<RSCAddressListResult.CountyListEntity> data) {
            super(context, data, R.layout.item_delivery_address_layout);
        }

        @Override
        public void convert(CommonViewHolder holder, final RSCAddressListResult.CountyListEntity countyListEntity) {
            if (countyListEntity != null) {
                TextView textView = (TextView) holder.getView(R.id.item_delivery_address_text);
                textView.setText(countyListEntity.name);

                if (countyListEntity.name.equals(state_county_text.getText().toString())) {
                    textView.setTextColor(getResources().getColor(R.color.orange_goods_price));
                } else {
                    textView.setTextColor(getResources().getColor(R.color.black_goods_titile));
                }
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countyId = countyListEntity._id;
                        //执行api请求网点
                        page = 1;
                        showProgressDialog();
                        getStateList(provinceId, cityId, countyId);
                        if (popupWindow != null && popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        state_county_text.setText(countyListEntity.name);

                    }
                });
            }


        }
    }


    /***
     * 获取PopupWindow实例
     */
    private void showPopupWindow(int tag) {
        resetView();
        if (null != popupWindow) {
            popupWindow.dismiss();
        }
        initPopuptWindow(tag);
        popupWindow.showAsDropDown(shaixuan_bar_separatrix);
        PopWindowUtils.setBackgroundBlack(pop_bg, 0);

    }

    /**
     * 创建PopupWindow
     */
    private void initPopuptWindow(int tag) {
        // TODO Auto-generated method stub
        View popupWindow_view = getLayoutInflater().inflate(
                R.layout.popwindow_select_delivery_state_layout, null, false);
        popListView = (ListView) popupWindow_view.findViewById(R.id.popWindow_ListView);
        popupWindow = new PopupWindow(popupWindow_view,
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
        // 设置动画效果
        popupWindow.setAnimationStyle(R.style.AnimTop2);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(true);

        switch (tag) {
            case provinceTag:
                if (provinceAdapter != null) {
                    popListView.setAdapter(provinceAdapter);
                }
                break;
            case cityTag:
                if (cityAdapter != null) {
                    popListView.setAdapter(cityAdapter);
                }
                break;
            case countyTag:
                if (countyAdapter != null) {
                    popListView.setAdapter(countyAdapter);
                }
                break;
        }
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                resetView();
                PopWindowUtils.setBackgroundBlack(pop_bg, 1);
            }
        });
    }


    //获取网点所在地区 省
    private void getProvinceState() {
        showProgressDialog();
        Bundle bundle = getIntent().getExtras();
        int flags = 0;
        if (bundle!=null){
            flags = bundle.getInt("flags");
        }

        if (flags == 1) {
            String product_id = bundle.getString("product_id");
            RequestParams params = new RequestParams();
            if (StringUtil.checkStr(product_id)) {
                params.put("products", product_id);
            }
            LoginResult.UserInfo userInfo = Store.User.queryMe();
            if (userInfo != null) {
                params.put("userId", userInfo.userid);
            }
            execApi(ApiType.GET_RSC_ADDRESS_PROVINCE.setMethod(ApiType.RequestMethod.GET), params);
        } else if (flags == 2) {
            RequestParams params = new RequestParams();
            LoginResult.UserInfo userInfo = Store.User.queryMe();
            if (userInfo != null) {
                params.put("token", userInfo.token);
            }
            List<String> productIds = (List<String>) bundle.getSerializable("productIds");
            StringBuilder builder = new StringBuilder();
            if (productIds != null && productIds.size() > 0) {
                for (int j = 0; j < productIds.size(); j++) {
                    String _id = (String) productIds.get(j);
                    if (StringUtil.checkStr(_id)) {
                        builder.append(_id).append(",");
                    }
                }
                String subUrl = builder.substring(0, builder.length() - 1);
                if (StringUtil.checkStr(subUrl)) {
                    params.put("products", subUrl);
                }
            }
            execApi(ApiType.GET_RSC_ADDRESS_PROVINCE.setMethod(ApiType.RequestMethod.GET), params);
        }
    }


    //获取网点所在地区 市
    private void getCityState(String provinceId) {
        showProgressDialog();
        Bundle bundle = getIntent().getExtras();
        int flags = 0;
        if (bundle!=null){
            flags = bundle.getInt("flags");
        }
        if (flags == 1) {
            String product_id = bundle.getString("product_id");
            RequestParams params = new RequestParams();
            if (StringUtil.checkStr(product_id)) {
                params.put("products", product_id);
            }
            if (StringUtil.checkStr(provinceId)) {
                params.put("province", provinceId);
            }
            LoginResult.UserInfo userInfo = Store.User.queryMe();
            if (userInfo != null) {
                params.put("userId", userInfo.userid);
            }
            execApi(ApiType.GET_RSC_ADDRESS_CITY.setMethod(ApiType.RequestMethod.GET), params);
        } else if (flags == 2) {
            RequestParams params = new RequestParams();
            LoginResult.UserInfo userInfo = Store.User.queryMe();
            if (userInfo != null) {
                params.put("userId", userInfo.userid);
            }
            List<String> productIds = (List<String>) bundle.getSerializable("productIds");
            StringBuilder builder = new StringBuilder();
            if (productIds != null && productIds.size() > 0) {
                for (int j = 0; j < productIds.size(); j++) {
                    String _id = (String) productIds.get(j);
                    if (StringUtil.checkStr(_id)) {
                        builder.append(_id).append(",");
                    }
                }
                String subUrl = builder.substring(0, builder.length() - 1);
                if (StringUtil.checkStr(subUrl)) {
                    params.put("products", subUrl);
                }
                if (StringUtil.checkStr(provinceId)) {
                    params.put("province", provinceId);
                }
            }
            execApi(ApiType.GET_RSC_ADDRESS_CITY.setMethod(ApiType.RequestMethod.GET), params);
        }
    }

    //获取网点所在地区 县
    private void getCountyState(String provinceId, String cityId) {
        showProgressDialog();
        Bundle bundle = getIntent().getExtras();
        int flags = 0;
        if (bundle!=null){
            flags = bundle.getInt("flags");
        }
        if (flags == 1) {
            RequestParams params = new RequestParams();
            String product_id = bundle.getString("product_id");
            if (StringUtil.checkStr(product_id)) {
                params.put("products", product_id);
            }
            if (StringUtil.checkStr(provinceId)) {
                params.put("province", provinceId);
            }
            if (StringUtil.checkStr(cityId)) {
                params.put("city", cityId);
            }

            LoginResult.UserInfo userInfo = Store.User.queryMe();
            if (userInfo != null) {
                params.put("userId", userInfo.userid);
            }
            execApi(ApiType.GET_RSC_ADDRESS_COUNTY.setMethod(ApiType.RequestMethod.GET), params);
        } else if (flags == 2) {
            RequestParams params = new RequestParams();
            LoginResult.UserInfo userInfo = Store.User.queryMe();
            if (userInfo != null) {
                params.put("userId", userInfo.userid);
            }
            List<String> productIds = (List<String>) bundle.getSerializable("productIds");
            StringBuilder builder = new StringBuilder();
            if (productIds != null && productIds.size() > 0) {
                for (int j = 0; j < productIds.size(); j++) {
                    String _id = (String) productIds.get(j);
                    if (StringUtil.checkStr(_id)) {
                        builder.append(_id).append(",");
                    }
                }
                String subUrl = builder.substring(0, builder.length() - 1);
                if (StringUtil.checkStr(subUrl)) {
                    params.put("products", subUrl);
                }
                if (StringUtil.checkStr(provinceId)) {
                    params.put("province", provinceId);
                }
                if (StringUtil.checkStr(cityId)) {
                    params.put("city", cityId);
                }
            }
            execApi(ApiType.GET_RSC_ADDRESS_COUNTY.setMethod(ApiType.RequestMethod.GET), params);
        }
    }


    //获取网点
    private void getStateList(String provinceId, String cityId, String countyId) {
        Bundle bundle = getIntent().getExtras();
        int flags = 0;
        if (bundle!=null){
            flags = bundle.getInt("flags");
        }

        if (flags == 1) {
            String product_id = bundle.getString("product_id");
            RequestParams params = new RequestParams();
            if (StringUtil.checkStr(product_id)) {
                params.put("products", product_id);
            }
            if (StringUtil.checkStr(provinceId)) {
                params.put("province", provinceId);
            }
            if (StringUtil.checkStr(cityId)) {
                params.put("city", cityId);
            }
            if (StringUtil.checkStr(countyId)) {
                params.put("county", countyId);
            }
            params.put("page", page);
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
            List<String> productIds = (List<String>) bundle.getSerializable("productIds");
            StringBuilder builder = new StringBuilder();
            if (productIds != null && productIds.size() > 0) {
                for (int j = 0; j < productIds.size(); j++) {
                    String _id = (String) productIds.get(j);
                    if (StringUtil.checkStr(_id)) {
                        builder.append(_id).append(",");
                    }
                }
                String subUrl = builder.substring(0, builder.length() - 1);
                if (StringUtil.checkStr(subUrl)) {
                    params.put("products", subUrl);
                }
                if (StringUtil.checkStr(provinceId)) {
                    params.put("province", provinceId);
                }
                if (StringUtil.checkStr(cityId)) {
                    params.put("city", cityId);
                }
                if (StringUtil.checkStr(countyId)) {
                    params.put("county", countyId);
                }
                params.put("page", page);
            }
            execApi(ApiType.GET_RSC_STATE_INFO.setMethod(ApiType.RequestMethod.GET), params);
        }
    }


}
