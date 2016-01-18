package com.ksfc.newfarmer.activitys;

import java.util.ArrayList;
import java.util.List;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.Push.App;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.db.dao.ShoppingDao;
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
import com.ksfc.newfarmer.utils.ImageLoaderUtils;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.CarouselDiagramViewPager;
import com.ksfc.newfarmer.widget.CirclePageIndicator;
import com.ksfc.newfarmer.widget.GridViewWithHeaderAndFooter;
import com.ksfc.newfarmer.widget.PullToRefreshView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.umeng.update.UmengUpdateAgent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.yangentao.util.msg.MsgCenter;
import net.yangentao.util.msg.MsgListener;

public class HomepageActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener {
    private LinearLayout headerlayout, ll_banner_container;
    private LayoutInflater inflat;
    private GridViewWithHeaderAndFooter huafei_gv, qiche_gv;
    private CarouselDiagramViewPager carouselDiagramViewPager;
    private int page1 = 1;
    private int page2 = 1;
    List<SingleGood> hfList = new ArrayList<SingleGood>();
    List<SingleGood> qcList = new ArrayList<SingleGood>();
    private huafeiAdapter hfAdapter;
    private RelativeLayout go_huafei;
    private RelativeLayout go_car;
    private qicheAdapter qcAdapter;
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
        inflat = getLayoutInflater().from(this);
        hideLeft();
        qiandao();
        initView();
        // 获取首页轮播图
        RequestParams params1 = new RequestParams();
        execApi(ApiType.GETHOMEPIC, params1);
        // getClassId();
        getData();
        getNyc();
    }

    private void getClassId() {
        RequestParams params = new RequestParams();
        execApi(ApiType.GET_CLASSID.setMethod(RequestMethod.GET), params);
    }

    private void qiandao() {
        setRightImage(R.drawable.qiandao);
        showRightImage();
        getRightImageView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    setRightImage(R.drawable.qiandao);
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setRightImage(R.drawable.qiandao_press);
                    initQiandao();
                }
                return true;
            }
        });


    }

    /**
     *
     */
    private void getData() {
        showProgressDialog();
        RequestParams params = new RequestParams();
        params.put("page", page1);
        params.put("rowCount", 6);
        params.put("classId", "531680A5");

        if (isLogin()) {
            params.put("locationUserId", Store.User.queryMe().userid);
        } else {
            params.put("locationUserId", "");
        }
        execApi(ApiType.GET_HUAFEI, params);
    }

    /**
     *
     */
    private void getNyc() {
        // TODO Auto-generated method stub
        showProgressDialog();
        RequestParams params = new RequestParams();
        params.put("page", page2);
        params.put("rowCount", 6);
        params.put("classId", "6C7D8F66");
        if (isLogin()) {
            params.put("locationUserId", Store.User.queryMe().userid);
        } else {
            params.put("locationUserId", "");
        }
        execApi(ApiType.GET_NYC, params);
    }

    private void initView() {
        headerlayout = (LinearLayout) inflat
                .inflate(R.layout.homeheader1, null);
        ll_banner_container = (LinearLayout) headerlayout
                .findViewById(R.id.ll_banner_container);
        go_huafei = (RelativeLayout) headerlayout
                .findViewById(R.id.huafei_zhuanchang);
        go_car = (RelativeLayout) headerlayout
                .findViewById(R.id.car_zhuanchang);

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
        headerlayout.findViewById(R.id.head_bar_gengduo).setOnClickListener(
                this);
        headerlayout.findViewById(R.id.head_bottom_bar_qianjin)
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
            params.put("userId", Store.User.queryMe().userid);
            execApi(ApiType.SIGN_IN_POINT, params);
        } else {
            showToast("您还未登录哦，请登录后签到");
        }
    }

    private void initGv() {
        // 获取数据 填充化肥和农用车的gv
        huafei_gv = (GridViewWithHeaderAndFooter) findViewById(R.id.huafei_gv);
        huafei_gv.addHeaderView(headerlayout);
        qiche_gv = (GridViewWithHeaderAndFooter) findViewById(R.id.qiche_gv);

    }

    @Override
    public void OnViewClick(View v) {

        switch (v.getId()) {
            case R.id.huafei_zhuanchang:
            case R.id.head_bar_gengduo:
            case R.id.head_bottom_bar_qianjin:
                Intent intent = new Intent(HomepageActivity.this,
                        ShangpinListActivity.class);
                intent.putExtra("goods", "huafei");
                startActivity(intent);

                break;
            case R.id.car_zhuanchang:
            case R.id.car_bar_gengduo:
            case R.id.car_bottom_bar_qianjin:
                Intent intent1 = new Intent(HomepageActivity.this,
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

            GetGoodsData goodsData = (GetGoodsData) req.getData();
            List<SingleGood> huafeiList = goodsData.datas.rows;
            if (huafeiList.size() > 0) {
                if (page1 == 1) {
                    hfList.clear();
                    hfList.addAll(huafeiList);
                    hfAdapter = new huafeiAdapter();
                    huafei_gv.setAdapter(hfAdapter);
                } else {
                    hfList.addAll(huafeiList);
                    hfAdapter.notifyDataSetChanged();
                }
            } else {
                showToast("没有数据了");
            }
            disMissDialog();
        } else if (req.getApi() == ApiType.GET_NYC) {

            GetGoodsData goodsData = (GetGoodsData) req.getData();
            List<SingleGood> nongyongcheList = goodsData.datas.rows;
            if (nongyongcheList.size() > 0) {
                car_bar_layout.setVisibility(View.VISIBLE);

                if (page2 == 1) {
                    qcList.clear();
                    qcList.addAll(nongyongcheList);
                    qcAdapter = new qicheAdapter();
                    qiche_gv.setAdapter(qcAdapter);
                } else {
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
            @SuppressWarnings("unchecked")
            List<ClassIDResult> data = (List<ClassIDResult>) req.getData();
        }

    }


    public class huafeiAdapter extends BaseAdapter {
        float jiefen;

        @Override
        public int getCount() {
            return hfList.size() > 0 ? hfList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            View view = LayoutInflater.from(HomepageActivity.this).inflate(
                    R.layout.home_gv_item, parent, false);
            TextView huafei_name_tv = (TextView) view
                    .findViewById(R.id.huafei_name_tv);
            TextView huafei_xianjia = (TextView) view
                    .findViewById(R.id.huafei_xianjia);
            ImageView huafei_img = (ImageView) view
                    .findViewById(R.id.huafei_img);

            ImageLoader.getInstance().displayImage(MsgID.IP + hfList.get(position).imgUrl,huafei_img);
            huafei_name_tv.setText(hfList.get(position).goodsName);
            if (hfList.get(position).presale) {
                huafei_xianjia.setTextColor(Color.GRAY);
                huafei_xianjia
                        .setText("即将上线");
            } else {
                huafei_xianjia.setTextColor(Color.parseColor("#ff4e00"));
                huafei_xianjia
                        .setText("¥" + hfList.get(position).originalPrice);
            }
            huafei_gv.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    // 带商品的id过去
                    Intent intent = new Intent(HomepageActivity.this,
                            ShangpinDetailActivity.class);
                    intent.putExtra("goodId", hfList.get(arg2).goodsId);
                    intent.putExtra("type", hfList.get(arg2).brandName);
                    startActivity(intent);
                }
            });
            return view;

        }

    }

    public class qicheAdapter extends BaseAdapter {
        float jiefen;

        @Override
        public int getCount() {
            return qcList.size() > 0 ? qcList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            View view = LayoutInflater.from(HomepageActivity.this).inflate(
                    R.layout.home_gv_item, parent, false);
            TextView huafei_name_tv = (TextView) view
                    .findViewById(R.id.huafei_name_tv);
            TextView huafei_xianjia = (TextView) view
                    .findViewById(R.id.huafei_xianjia);
            ImageView huafei_img = (ImageView) view
                    .findViewById(R.id.huafei_img);
            huafei_img.setScaleType(ScaleType.CENTER_CROP);
            ImageLoader.getInstance().displayImage(MsgID.IP + qcList.get(position).imgUrl, huafei_img);
            huafei_name_tv.setText(qcList.get(position).goodsName);
            huafei_xianjia
                    .setText("¥" + qcList.get(position).originalPrice);
            qiche_gv.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    // 带商品的id过去
                    Intent intent = new Intent(HomepageActivity.this,
                            ShangpinDetailActivity.class);
                    intent.putExtra("goodId", qcList.get(arg2).goodsId);
                    startActivity(intent);
                }
            });
            return view;

        }

    }


    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        getData();
        getNyc();
        // 获取首页轮播图
        RequestParams params1 = new RequestParams();
        execApi(ApiType.GETHOMEPIC, params1);
    }
}
