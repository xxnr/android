package com.ksfc.newfarmer.activitys;

import java.io.UnsupportedEncodingException;
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
import com.ksfc.newfarmer.utils.PullToRefreshUtils;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.ScreenUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.Utils;
import com.ksfc.newfarmer.widget.CarouselDiagramViewPager;
import com.ksfc.newfarmer.widget.UnSwipeGridView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.common.message.Log;
import com.umeng.update.UmengUpdateAgent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.apache.http.entity.StringEntity;


public class HomepageActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener {
    private LinearLayout ll_banner_container;
    private PullToRefreshScrollView scrollView;
    private CarouselDiagramViewPager carouselDiagramViewPager;
    private LinearLayout view_container;
    private int itemWitch;
    private String huaFeiClassId = "531680A5";
    private String carClassId = "6C7D8F66";

    private List<HomepageViewBean> ViewBeanList = new ArrayList<>();

    @Override
    public int getLayout() {
        return R.layout.home_layout_new;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);//在主页判断是否更新版本

        setTitle("新新农人");
        hideLeft();
        qiandao();
        initView();
        showProgressDialog();
        getClassId();
        getBanner();
    }

    private void getBanner() {
        // 获取首页轮播图
        RequestParams params1 = new RequestParams();
        execApi(ApiType.GETHOMEPIC, params1);
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
    private void getData(final HomepageViewBean homepageViewBean) {
        if (homepageViewBean != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("page", 1);
            map.put("rowCount", 6);
            map.put("classId", homepageViewBean.classId);
            final Gson gson = new Gson();
            String json = gson.toJson(map);

            com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
            try {
                params.setBodyEntity(new StringEntity(json, "UTF-8"));
                params.setContentType("application/json");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            HttpUtils http = new HttpUtils();
            http.send(HttpRequest.HttpMethod.POST, ApiType.GET_HUAFEI.getOpt(), params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    if (responseInfo != null && StringUtil.checkStr(responseInfo.result)) {
                        RndLog.i(TAG, responseInfo.result);
                        GetGoodsData getGoodsData = gson.fromJson(responseInfo.result, GetGoodsData.class);
                        if (getGoodsData != null && getGoodsData.getStatus().equals("1000")) {
                            if (getGoodsData.datas.rows != null && !getGoodsData.datas.rows.isEmpty()) {
                                HuafeiAdapter adapter = new HuafeiAdapter(HomepageActivity.this, getGoodsData.datas.rows);
                                if (homepageViewBean.unSwipeGridView != null) {
                                    homepageViewBean.unSwipeGridView.setAdapter(adapter);
                                }
                                if (homepageViewBean.viewGroup != null) {
                                    homepageViewBean.viewGroup.setVisibility(View.VISIBLE);
                                }
                            } else {
                                if (homepageViewBean.unSwipeGridView != null) {
                                    homepageViewBean.unSwipeGridView.setAdapter(null);
                                }
                                if (homepageViewBean.viewGroup != null) {
                                    homepageViewBean.viewGroup.setVisibility(View.GONE);
                                }

                            }
                        }
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    e.printStackTrace();
                }
            });


        }


    }


    private void initView() {
        view_container = (LinearLayout) findViewById(R.id.view_container);
        ll_banner_container = (LinearLayout) findViewById(R.id.ll_banner_container);
        scrollView = (PullToRefreshScrollView) findViewById(R.id.pull_ll_srcoll);
        scrollView.setOnRefreshListener(this);
        //设置刷新的文字
        ILoadingLayout startLabels = scrollView
                .getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在载入...");// 刷新时
        startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

        ScreenUtil.setHeight(this, ll_banner_container, 175);
        // 设置点击跳转监听
        setViewClick(R.id.huafei_zhuanchang);
        setViewClick(R.id.car_zhuanchang);

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

    @Override
    public void OnViewClick(View v) {

        switch (v.getId()) {
            case R.id.huafei_zhuanchang:
                Intent intent = new Intent(HomepageActivity.this,
                        GoodsListActivity.class);
                intent.putExtra("className", "化肥");
                intent.putExtra("classId", huaFeiClassId);
                startActivity(intent);
                break;
            case R.id.car_zhuanchang:
                Intent intent1 = new Intent(HomepageActivity.this,
                        GoodsListActivity.class);
                intent1.putExtra("className", "汽车");
                intent1.putExtra("classId", carClassId);
                startActivity(intent1);
                break;
            default:
                break;
        }

    }

    class HomepageViewBean {
        public String classId;
        public UnSwipeGridView unSwipeGridView;
        public ViewGroup viewGroup;

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

        } else if (ApiType.SIGN_IN_POINT == req.getApi()) {
            if (req.getData().getStatus().equals("1000")) {
                startActivity(QiandaoActivity.class);
            }
        } else if (ApiType.GET_CLASSID == req.getApi()) {
            ClassIDResult data = (ClassIDResult) req.getData();
            if (data.getStatus().equals("1000") && data.categories != null) {
                if (ViewBeanList.isEmpty()) {
                    for (int i = 0; i < data.categories.size(); i++) {
                        HomepageViewBean homepageViewBean = new HomepageViewBean();
                        final ClassIDResult.CategoriesEntity entity = data.categories.get(i);
                        if (entity != null) {
                            //设置 title_bar
                            ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.homepage_view_more_bar, null);
                            viewGroup.setVisibility(View.GONE);
                            ViewHolder holder = new ViewHolder(viewGroup);
                            if (i % 2 == 1) {
                                holder.view_bar_more_bar.setBackgroundColor(getResources().getColor(R.color.orange_goods_price));
                            } else {
                                holder.view_bar_more_bar.setBackgroundColor(getResources().getColor(R.color.green));
                            }
                            if (entity.name.equals("化肥")) {
                                huaFeiClassId = entity.id;//设置化肥的id
                                holder.view_bar_more_bar.setBackgroundColor(getResources().getColor(R.color.green));
                            }
                            if (entity.name.equals("汽车")) {
                                carClassId = entity.id;//设置汽车的id
                                holder.view_bar_more_bar.setBackgroundColor(getResources().getColor(R.color.orange_goods_price));
                            }
                            if (StringUtil.checkStr(entity.name)) {
                                holder.view_bar_more_title.setText(entity.name + "精选");
                            }
                            holder.view_bar_more_qianjin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (StringUtil.checkStr(entity.id)) {
                                        Intent intent = new Intent(HomepageActivity.this,
                                                GoodsListActivity.class);
                                        intent.putExtra("className", entity.name);
                                        intent.putExtra("classId", entity.id);
                                        startActivity(intent);
                                    }
                                }
                            });
                            holder.view_bar_more.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (StringUtil.checkStr(entity.id)) {
                                        Intent intent = new Intent(HomepageActivity.this,
                                                GoodsListActivity.class);
                                        intent.putExtra("className", entity.name);
                                        intent.putExtra("classId", entity.id);
                                        startActivity(intent);
                                    }
                                }
                            });

                            //设置GridView
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            lp.setMargins(32, 0, 32, 0);
                            UnSwipeGridView unSwipeGridView = new UnSwipeGridView(this);
                            unSwipeGridView.setFocusable(false);
                            unSwipeGridView.setHorizontalSpacing(Utils.dip2px(this, 12));
                            unSwipeGridView.setVerticalSpacing(Utils.dip2px(this, 15));
                            unSwipeGridView.setNumColumns(2);
                            unSwipeGridView.setGravity(Gravity.CENTER);
                            unSwipeGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
                            WindowManager windowManager = getWindowManager();
                            Display display = windowManager.getDefaultDisplay();
                            int wh = display.getWidth();
                            itemWitch = (wh - (Utils.dip2px(this, 12 + 16 * 2))) / 2;
                            unSwipeGridView.setColumnWidth(Utils.dip2px(this, itemWitch));
                            unSwipeGridView.setLayoutParams(lp);

                            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            LinearLayout linearLayout = new LinearLayout(getApplicationContext());
                            linearLayout.setLayoutParams(lp2);
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            linearLayout.addView(unSwipeGridView);


                            if (entity.name.equals("汽车")) {
                                view_container.addView(viewGroup, 0);
                                view_container.addView(linearLayout, 1);
                            } else {
                                view_container.addView(viewGroup);
                                view_container.addView(linearLayout);
                            }

                            homepageViewBean.classId = entity.id;
                            homepageViewBean.viewGroup = viewGroup;
                            homepageViewBean.unSwipeGridView = unSwipeGridView;
                            ViewBeanList.add(homepageViewBean);
                            getData(homepageViewBean);
                        }
                    }
                } else {

                    if (data.categories.size() <= ViewBeanList.size()) {
                        for (int i = 0; i < data.categories.size(); i++) {
                            ClassIDResult.CategoriesEntity entity = data.categories.get(i);
                            HomepageViewBean homepageViewBean = ViewBeanList.get(i);
                            if (entity != null && homepageViewBean != null) {
                                homepageViewBean.classId = entity.id;
                                ViewHolder holder = new ViewHolder(homepageViewBean.viewGroup);
                                holder.view_bar_more_title.setText(entity.name + "精选");
                            }
                        }

                        int count = ViewBeanList.size();
                        for (int i = 0; i < count; i++) {
                            if (i >= data.categories.size()) {
                                try {
                                    HomepageViewBean homepageViewBean = ViewBeanList.get(i);
                                    if (homepageViewBean != null) {
                                        homepageViewBean.unSwipeGridView.setVisibility(View.GONE);
                                        homepageViewBean.viewGroup.setVisibility(View.GONE);
                                        homepageViewBean.unSwipeGridView.setAdapter(null);
                                    }
                                    ViewBeanList.remove(i);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    } else if (data.categories.size() > ViewBeanList.size()) {

                        for (int i = 0; i < data.categories.size(); i++) {
                            if (i >= (ViewBeanList.size() - 1)) {
                                HomepageViewBean homepageViewBean = new HomepageViewBean();
                                final ClassIDResult.CategoriesEntity entity = data.categories.get(i);
                                if (entity != null) {
                                    //设置 title_bar
                                    ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.homepage_view_more_bar, null);
                                    viewGroup.setVisibility(View.GONE);
                                    ViewHolder holder = new ViewHolder(viewGroup);
                                    if (i % 2 == 1) {
                                        holder.view_bar_more_bar.setBackgroundColor(getResources().getColor(R.color.orange_goods_price));
                                    } else {
                                        holder.view_bar_more_bar.setBackgroundColor(getResources().getColor(R.color.green));
                                    }
                                    if (entity.name.equals("化肥")) {
                                        huaFeiClassId = entity.id;//设置化肥的id
                                        holder.view_bar_more_bar.setBackgroundColor(getResources().getColor(R.color.green));
                                    }
                                    if (entity.name.equals("汽车")) {
                                        carClassId = entity.id;//设置汽车的id
                                        holder.view_bar_more_bar.setBackgroundColor(getResources().getColor(R.color.orange_goods_price));
                                    }
                                    if (StringUtil.checkStr(entity.name)) {
                                        holder.view_bar_more_title.setText(entity.name + "精选");
                                    }
                                    holder.view_bar_more_qianjin.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (StringUtil.checkStr(entity.id)) {
                                                Intent intent = new Intent(HomepageActivity.this,
                                                        GoodsListActivity.class);
                                                intent.putExtra("className", entity.name);
                                                intent.putExtra("classId", entity.id);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                                    holder.view_bar_more.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (StringUtil.checkStr(entity.id)) {
                                                Intent intent = new Intent(HomepageActivity.this,
                                                        GoodsListActivity.class);
                                                intent.putExtra("className", entity.name);
                                                intent.putExtra("classId", entity.id);
                                                startActivity(intent);
                                            }
                                        }
                                    });

                                    //设置GridView
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                    lp.setMargins(32, 0, 32, 0);
                                    UnSwipeGridView unSwipeGridView = new UnSwipeGridView(this);
                                    unSwipeGridView.setFocusable(false);
                                    unSwipeGridView.setHorizontalSpacing(Utils.dip2px(this, 12));
                                    unSwipeGridView.setVerticalSpacing(Utils.dip2px(this, 15));
                                    unSwipeGridView.setNumColumns(2);
                                    unSwipeGridView.setGravity(Gravity.CENTER);
                                    unSwipeGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
                                    WindowManager windowManager = getWindowManager();
                                    Display display = windowManager.getDefaultDisplay();
                                    int wh = display.getWidth();
                                    itemWitch = (wh - (Utils.dip2px(this, 12 + 16 * 2))) / 2;
                                    unSwipeGridView.setColumnWidth(Utils.dip2px(this, itemWitch));
                                    unSwipeGridView.setLayoutParams(lp);

                                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                    LinearLayout linearLayout = new LinearLayout(getApplicationContext());
                                    linearLayout.setLayoutParams(lp2);
                                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                                    linearLayout.addView(unSwipeGridView);


                                    if (entity.name.equals("汽车")) {
                                        view_container.addView(viewGroup, 0);
                                        view_container.addView(linearLayout, 1);
                                    } else {
                                        view_container.addView(viewGroup);
                                        view_container.addView(linearLayout);
                                    }
                                    homepageViewBean.classId = entity.id;
                                    homepageViewBean.viewGroup = viewGroup;
                                    homepageViewBean.unSwipeGridView = unSwipeGridView;
                                    ViewBeanList.add(homepageViewBean);
                                }
                            }
                        }
                    }
                    for (int i = 0; i < ViewBeanList.size(); i++) {
                        getData(ViewBeanList.get(i));
                    }
                }
            }

        }

    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        PullToRefreshUtils.setFreshClose(refreshView);
        getBanner();
        getClassId();
    }


    class ViewHolder {
        private TextView view_bar_more_bar, view_bar_more_title, view_bar_more;
        private ImageView view_bar_more_qianjin;

        ViewHolder(View view) {
            view_bar_more_bar = (TextView) view.findViewById(R.id.view_bar_more_bar);
            view_bar_more_title = (TextView) view.findViewById(R.id.view_bar_more_title);
            view_bar_more = (TextView) view.findViewById(R.id.view_bar_more);
            view_bar_more_qianjin = (ImageView) view.findViewById(R.id.view_bar_more_qianjin);
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
                ImageView imageView = (ImageView) holder.getView(R.id.huafei_img);

                // 商品图的外边
                if (itemWitch != 0) {
                    RelativeLayout relativeLayout = holder.getView(R.id.huafei_img_rel);
                    ViewGroup.LayoutParams layoutParams = relativeLayout.getLayoutParams();
                    layoutParams.height = itemWitch;
                    layoutParams.width = itemWitch;
                    relativeLayout.setLayoutParams(layoutParams);
                }

                if (itemWitch > Utils.dip2px(HomepageActivity.this, 2)) {
                    ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                    layoutParams.height = itemWitch - Utils.dip2px(HomepageActivity.this, 2);
                    layoutParams.width = itemWitch - Utils.dip2px(HomepageActivity.this, 2);
                    imageView.setLayoutParams(layoutParams);
                    ImageLoader.getInstance().displayImage(MsgID.IP + singleGood.imgUrl, imageView);
                }
                ImageLoader.getInstance().displayImage(MsgID.IP + singleGood.imgUrl, imageView);

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
}
