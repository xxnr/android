package com.ksfc.newfarmer.activitys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.adapter.CommonAdapter;
import com.ksfc.newfarmer.adapter.CommonViewHolder;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.ApiType.RequestMethod;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.ClassIDResult;
import com.ksfc.newfarmer.protocol.beans.GetGoodsData;
import com.ksfc.newfarmer.protocol.beans.GetGoodsData.SingleGood;
import com.ksfc.newfarmer.protocol.beans.HomeImageResult;
import com.ksfc.newfarmer.protocol.beans.HomeImageResult.Rows;
import com.ksfc.newfarmer.protocol.beans.HomeImageResult.UserRollImage;
import com.ksfc.newfarmer.protocol.beans.PointResult;
import com.ksfc.newfarmer.utils.PullToRefreshUtils;
import com.ksfc.newfarmer.utils.SPUtils;
import com.ksfc.newfarmer.utils.ScreenUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.Utils;
import com.ksfc.newfarmer.widget.CarouselDiagramViewPager;
import com.ksfc.newfarmer.widget.UnSwipeGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.update.UmengUpdateAgent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;


public class HomepageActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener {
    private LinearLayout ll_banner_container;
    private UnSwipeGridView huafei_gv, qiche_gv;
    private CarouselDiagramViewPager carouselDiagramViewPager;
    private int page1 = 1;
    private int page2 = 1;
    List<SingleGood> hfList = new ArrayList<>();
    List<SingleGood> qcList = new ArrayList<>();
    private HuafeiAdapter hfAdapter;
    private RelativeLayout go_huafei;
    private RelativeLayout go_car;
    private CarAdapter qcAdapter;
    private RelativeLayout car_bar_layout;

    private PullToRefreshScrollView scrollView;

    @Override
    public int getLayout() {
        return R.layout.home_layout_new;
    }

    @SuppressWarnings("static-access")
    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);//在主页判断是否更新版本

        setTitle("新新农人");
        hideLeft();
        qiandao();
        initView();
        // 获取首页轮播图
        RequestParams params1 = new RequestParams();
        execApi(ApiType.GETHOMEPIC, params1);
        showProgressDialog();
        getNyc();
        getData();
        getClassId();
    }

    private void getClassId() {
        RequestParams params = new RequestParams();
        execApi(ApiType.GET_CLASSID.setMethod(RequestMethod.GET), params);
    }

    private void qiandao() {
        setRightImage(R.drawable.qiandao);
        showRightImage();
        getRightImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initQiandao();
            }
        });
        getRightImageView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    setRightImage(R.drawable.qiandao);
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setRightImage(R.drawable.qiandao_press);

                }
                return false;
            }
        });


    }

    /**
     *
     */
    private void getData() {
        RequestParams params = new RequestParams();
        Map<String, Object> map = new HashMap<>();
        map.put("page", page1);
        map.put("rowCount", 6);
        String huafeiId = (String) SPUtils.get(HomepageActivity.this, "HuafeiId", "531680A5");
        map.put("classId", huafeiId);
        Gson gson = new Gson();
        params.put("JSON", gson.toJson(map));
        execApi(ApiType.GET_HUAFEI.setMethod(RequestMethod.POSTJSON), params);
    }

    /**
     *
     */
    private void getNyc() {
        // TODO Auto-generated method stub
        RequestParams params = new RequestParams();
        Map<String, Object> map = new HashMap<>();
        map.put("page", page2);
        map.put("rowCount", 6);
        String carID = (String) SPUtils.get(HomepageActivity.this, "CarID", "6C7D8F66");
        map.put("classId", carID);
        Gson gson = new Gson();
        params.put("JSON", gson.toJson(map));
        execApi(ApiType.GET_NYC.setMethod(RequestMethod.POSTJSON), params);
    }

    private void initView() {

        ll_banner_container= (LinearLayout) findViewById(R.id.ll_banner_container);
        go_huafei = (RelativeLayout) findViewById(R.id.huafei_zhuanchang);
        go_car = (RelativeLayout) findViewById(R.id.car_zhuanchang);

        scrollView = (PullToRefreshScrollView) findViewById(R.id.pull_ll_srcoll);
        scrollView.setOnRefreshListener(this);
        //设置刷新的文字
        ILoadingLayout startLabels = scrollView
                .getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在载入...");// 刷新时
        startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示


        // 设置点击跳转监听
        go_huafei.setOnClickListener(this);
        go_car.setOnClickListener(this);
        findViewById(R.id.head_bar_gengduo).setOnClickListener(
                this);
        findViewById(R.id.head_bottom_bar_qianjin)
                .setOnClickListener(this);
        car_bar_layout = (RelativeLayout) findViewById(R.id.car_bar_layout);

        setViewClick(R.id.car_bar_gengduo);
        setViewClick(R.id.car_bottom_bar_qianjin);
        initGv();
    }


    private void initQiandao() {
        if (isLogin()) {
            showProgressDialog("请稍后...");
            RequestParams params = new RequestParams();
            if (isLogin()) {
                params.put("userId", Store.User.queryMe().userid);
            }
            execApi(ApiType.SIGN_IN_POINT, params);
        } else {
            showToast("您还未登录哦，请登录后签到");
        }
    }

    private void initGv() {
        // 获取数据 填充化肥和农用车的gv
        huafei_gv = (UnSwipeGridView) findViewById(R.id.huafei_gv);
        qiche_gv = (UnSwipeGridView) findViewById(R.id.qiche_gv);
        huafei_gv.setFocusable(false);
        qiche_gv.setFocusable(false);
//        huafei_gv.setColumnWidth(Utils.dip2px(this, 158));
//        qiche_gv.setColumnWidth(Utils.dip2px(this, 158));
    }

    @Override
    public void OnViewClick(View v) {

        switch (v.getId()) {
            case R.id.car_zhuanchang:
            case R.id.head_bar_gengduo:     //本为化肥
            case R.id.head_bottom_bar_qianjin://本为化肥
                Intent intent1 = new Intent(HomepageActivity.this,
                        GoodsListActivity.class);
                intent1.putExtra("goods", "qiche");
                startActivity(intent1);
                break;
            case R.id.huafei_zhuanchang:
            case R.id.car_bar_gengduo://本为汽车
            case R.id.car_bottom_bar_qianjin://本为汽车
                Intent intent = new Intent(HomepageActivity.this,
                        GoodsListActivity.class);
                intent.putExtra("goods", "huafei");
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        scrollView.onRefreshComplete();
        if (req.getApi() == ApiType.GETHOMEPIC) {
            HomeImageResult res = (HomeImageResult) req.getData();
            UserRollImage rolls = res.datas;
            List<Rows> rowList = rolls.rows;
            //自定义的可滚动的viewpager
            carouselDiagramViewPager = new CarouselDiagramViewPager(this,
                    rowList);
            ll_banner_container.removeAllViews();
            ll_banner_container.addView(carouselDiagramViewPager.getView());

        } else if (req.getApi() == ApiType.GET_HUAFEI) {
            scrollView.getRefreshableView().fullScroll(ScrollView.FOCUS_UP);
            GetGoodsData goodsData = (GetGoodsData) req.getData();
            List<SingleGood> huafeiList = goodsData.datas.rows;
            if (huafeiList != null && huafeiList.size() > 0) {
                if (page1 == 1) {
                    hfList.clear();
                    hfList.addAll(huafeiList);
                    if (hfAdapter == null) {
                        hfAdapter = new HuafeiAdapter(this, hfList);
                        qiche_gv.setAdapter(hfAdapter);
                    } else {
                        hfAdapter.notifyDataSetChanged();
                    }
                } else {
                    hfList.addAll(huafeiList);
                    hfAdapter.notifyDataSetChanged();
                }
            } else {
                showToast("没有数据了");
            }
            disMissDialog();
        } else if (req.getApi() == ApiType.GET_NYC) {
            scrollView.getRefreshableView().fullScroll(ScrollView.FOCUS_UP);
            GetGoodsData goodsData = (GetGoodsData) req.getData();
            List<SingleGood> carList = goodsData.datas.rows;
            if (carList != null && carList.size() > 0) {
                car_bar_layout.setVisibility(View.VISIBLE);
                if (page2 == 1) {
                    qcList.clear();
                    qcList.addAll(carList);
                    if (qcAdapter == null) {
                        qcAdapter = new CarAdapter(this, qcList);
                        huafei_gv.setAdapter(qcAdapter);

                    } else {
                        qcAdapter.notifyDataSetChanged();
                    }
                } else {
                    qcList.addAll(carList);
                    qcAdapter.notifyDataSetChanged();
                }
            } else {
                showToast("没有数据了");

            }
            disMissDialog();

        } else if (ApiType.SIGN_IN_POINT == req.getApi()) {
            PointResult data = (PointResult) req.getData();
            String status = data.getStatus();
            if (status.equals("1000")) {
                Intent intent = new Intent(HomepageActivity.this,
                        QiandaoActivity.class);
                startActivity(intent);
            } else if (status.equals("1010")) {
                showToast("您今日已签到成功，明天再来呦");
            }

        } else if (ApiType.GET_CLASSID == req.getApi()) {
            ClassIDResult data = (ClassIDResult) req.getData();
            if (data.getStatus().equals("1000")) {
                for (int i = 0; i < data.categories.size(); i++) {
                    if (data.categories.get(i).name.equals("化肥")) {
                        //保存到本地class的Id
                        SPUtils.put(HomepageActivity.this, "HuafeiId",
                                data.categories.get(i).id);

                    } else if (data.categories.get(i).name.equals("汽车")) {
                        SPUtils.put(HomepageActivity.this, "CarID",
                                data.categories.get(i).id);
                    }
                }

            } else {
                //保存到本地class的Id
                SPUtils.put(HomepageActivity.this, "HuafeiId",
                        "531680A5");
                SPUtils.put(HomepageActivity.this, "CarID",
                        "6C7D8F66");
            }

        }

    }


    class HuafeiAdapter extends CommonAdapter<SingleGood> {


        public HuafeiAdapter(Context context, List<SingleGood> data) {
            super(context, data, R.layout.home_gv_item);
        }

        @Override
        public void convert(CommonViewHolder holder, final SingleGood singleGood) {
            if (singleGood != null) {
                //商品图
                ImageLoader.getInstance().displayImage(MsgID.IP + singleGood.imgUrl,
                        ((ImageView) holder.getView(R.id.huafei_img)));
                //商品名
                if (StringUtil.checkStr(singleGood.goodsName)) {
                    holder.setText(R.id.huafei_name_tv, singleGood.goodsName);
                }
                //商品是否预售
                if (singleGood.presale) {
                    TextView huafei_xianjia = (TextView) holder.getView(R.id.huafei_xianjia);
                    huafei_xianjia.setTextColor(Color.GRAY);
                    huafei_xianjia.setText("即将上线");
                } else {
                    //商品价格
                    TextView huafei_xianjia = (TextView) holder.getView(R.id.huafei_xianjia);
                    huafei_xianjia.setTextColor(getResources().getColor(R.color.orange_goods_price));
                    if (StringUtil.checkStr(singleGood.unitPrice)) {
                        huafei_xianjia
                                .setText("¥" + singleGood.unitPrice);
                    }

                }
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 带商品的id过去
                        Intent intent = new Intent(HomepageActivity.this,
                                GoodsDetailActivity.class);
                        intent.putExtra("goodId", singleGood.goodsId);
                        startActivity(intent);
                    }
                });

            }

        }
    }


    class CarAdapter extends CommonAdapter<SingleGood> {

        public CarAdapter(Context context, List<SingleGood> data) {
            super(context, data, R.layout.home_gv_item);
        }

        @Override
        public void convert(CommonViewHolder holder, final SingleGood singleGood) {
            if (singleGood != null) {
                //商品图
                ImageView carImage = (ImageView) holder.getView(R.id.huafei_img);
                carImage.setScaleType(ScaleType.CENTER_CROP);
                ImageLoader.getInstance().displayImage(MsgID.IP + singleGood.imgUrl, carImage);
                //商品名
                if (StringUtil.checkStr(singleGood.goodsName)) {
                    holder.setText(R.id.huafei_name_tv, singleGood.goodsName);
                }
                //商品是否预售
                if (singleGood.presale) {
                    TextView huafei_xianjia = (TextView) holder.getView(R.id.huafei_xianjia);
                    huafei_xianjia.setTextColor(Color.GRAY);
                    huafei_xianjia.setText("即将上线");
                } else {
                    //商品价格
                    TextView huafei_xianjia = (TextView) holder.getView(R.id.huafei_xianjia);
                    huafei_xianjia.setTextColor(getResources().getColor(R.color.orange_goods_price));
                    if (StringUtil.checkStr(singleGood.unitPrice)) {
                        huafei_xianjia
                                .setText("¥" + singleGood.unitPrice);
                    }

                }
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 带商品的id过去
                        Intent intent = new Intent(HomepageActivity.this,
                                GoodsDetailActivity.class);
                        intent.putExtra("goodId", singleGood.goodsId);
                        startActivity(intent);
                    }
                });

            }

        }
    }


    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        PullToRefreshUtils.setFreshClose(refreshView);
        getNyc();
        getData();
        // 获取首页轮播图
        RequestParams params1 = new RequestParams();
        execApi(ApiType.GETHOMEPIC, params1);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!(qcAdapter != null
                && qcAdapter.getCount() > 0
                && hfAdapter != null
                && huafei_gv.getCount() > 0)) {
            showProgressDialog();
            getNyc();
            getData();
        }

    }
}
