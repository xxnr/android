package com.ksfc.newfarmer.activitys;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.ksfc.newfarmer.App;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.beans.CampaignListResult;
import com.ksfc.newfarmer.common.CommonAdapter;
import com.ksfc.newfarmer.common.CommonViewHolder;
import com.ksfc.newfarmer.common.CommonFunction;
import com.ksfc.newfarmer.common.CompleteReceiver;
import com.ksfc.newfarmer.common.PicassoHelper;
import com.ksfc.newfarmer.event.CampaignListEvent;
import com.ksfc.newfarmer.event.IsLoginEvent;
import com.ksfc.newfarmer.event.MainTabSelectEvent;
import com.ksfc.newfarmer.event.SignEvent;
import com.ksfc.newfarmer.event.TokenErrorEvent;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.remoteapi.RemoteApi;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.beans.AppUpgrade;
import com.ksfc.newfarmer.beans.ClassIDResult;
import com.ksfc.newfarmer.beans.GetGoodsData;
import com.ksfc.newfarmer.beans.GetGoodsData.SingleGood;
import com.ksfc.newfarmer.beans.HomeImageResult;
import com.ksfc.newfarmer.beans.HomeImageResult.Rows;
import com.ksfc.newfarmer.beans.HomeImageResult.UserRollImage;
import com.ksfc.newfarmer.beans.IntegralGetResult;
import com.ksfc.newfarmer.beans.PointResult;
import com.ksfc.newfarmer.protocol.RxApi.RxService;
import com.ksfc.newfarmer.utils.ActivityAnimationUtils;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.common.PullToRefreshHelper;
import com.ksfc.newfarmer.utils.ScreenUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.Utils;
import com.ksfc.newfarmer.widget.CarouselDiagramViewPager;
import com.ksfc.newfarmer.widget.UnSwipeGridView;
import com.ksfc.newfarmer.widget.dialog.CustomDialog;
import com.ksfc.newfarmer.widget.dialog.CustomDialogUpdate;
import com.trello.rxlifecycle.ActivityEvent;

import net.yangentao.util.PreferenceUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class HomepageActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener {
    @BindView(R.id.ll_banner_container)
    LinearLayout ll_banner_container;
    @BindView(R.id.view_container)
    LinearLayout view_container;
    @BindView(R.id.pull_ll_srcoll)
    PullToRefreshScrollView scrollView;
    @BindView(R.id.title_back_img)
    ImageView title_back_img;
    private CarouselDiagramViewPager carouselDiagramViewPager;


    private int itemWitch;  //gv 中item的宽度
    private String huaFeiClassId = "531680A5";
    private String carClassId = "6C7D8F66";

    private List<HomepageViewBean> ViewBeanList = new ArrayList<>();

    private CompleteReceiver completeReceiver;//监听下载
    private Animation shakeAnimation;//签到图标抖动的icon
    private boolean isSigned = false;
    private List<CampaignListResult.CampaignsBean> campaigns;

    private boolean isTokenError=false;

    @Override
    public int getLayout() {
        return R.layout.activity_home_page;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setTitle("新新农人");
        initView();
        //在主页判断版本是否需要升级 并注册监听下载完成之后的广播
        completeReceiver = new CompleteReceiver();
        registerReceiver(completeReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        //app 是否需要升级
        RemoteApi.appIsNeedUpdate(this);
        //商品和banner列表
        showProgressDialog();
        RemoteApi.getBanner(this);
        RemoteApi.getClassId(this);
        if (isLogin()) {
            RemoteApi.getIntegral(this);
        }

        //获取活动列表
        execApi(ApiType.GET_CAMPAIGNS.setMethod(ApiType.RequestMethod.GET), null);
    }

    /**
     * 监听登录事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void isLoginEvent(IsLoginEvent event) {
        RemoteApi.getIntegral(HomepageActivity.this);
    }

    /**
     * 签到通知
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void isSignEvent(SignEvent event) {
        isSigned = true;
    }


    /**
     * token错误通知
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void isSignEvent(TokenErrorEvent event) {
        isTokenError = true;
    }

    //获得首页列表数据
    private void getData(final HomepageViewBean homepageViewBean) {
        if (homepageViewBean != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("page", 1);
            map.put("rowCount", 6);
            map.put("classId", homepageViewBean.classId);
            RxService.createApi()
                    .GET_GOODS(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(this.<GetGoodsData>bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(new Action1<GetGoodsData>() {
                        @Override
                        public void call(GetGoodsData getGoodsData) {
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
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });
        }
    }


    private void initView() {
        showRightImage();
        setRightImage(R.drawable.sign_icon);
        getRightImageView().setOnClickListener(this);
        title_back_img.setBackgroundResource(R.drawable.acitity_icon);
        setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showCampaign();
            }
        });
        //设置下拉刷新
        scrollView.setOnRefreshListener(this);
        //设置刷新的文字
        ILoadingLayout startLabels = scrollView
                .getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在载入...");// 刷新时
        startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示
        //设置banner为的高度
        ScreenUtil.setHeight(this, ll_banner_container, 175);
        setViewClick(R.id.huafei_zhuanchang);
        setViewClick(R.id.car_zhuanchang);
        //初始化抖动动画
        shakeAnimation = AnimationUtils.loadAnimation(HomepageActivity.this, R.anim.shake_animation);


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
            case R.id.title_right_img:
                if (isLogin()) {
                    RemoteApi.sign(this);
                } else {
                    startActivity(LoginActivity.class);
                }
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
        scrollView.onRefreshComplete();
        if (req.getApi() == ApiType.GET_INTEGRAL) {
            IntegralGetResult data = (IntegralGetResult) req.getData();
            if (data.datas != null && data.datas.sign != null && data.datas.sign.signed == 1) {
                isSigned = true;
            } else {
                isSigned = false;
                getRightImageView().startAnimation(shakeAnimation);
            }
        } else if (req.getApi() == ApiType.GETHOMEPIC) {
            HomeImageResult res = (HomeImageResult) req.getData();
            UserRollImage rolls = res.datas;
            List<Rows> rowList = rolls.rows;
            //自定义的可滚动的viewpager
            carouselDiagramViewPager = new CarouselDiagramViewPager(this,
                    rowList);
            ll_banner_container.removeAllViews();
            ll_banner_container.addView(carouselDiagramViewPager.getView());

        } else if (ApiType.SIGN_IN_POINT == req.getApi()) {
            isSigned = true;
            PointResult reqData = (PointResult) req.getData();
            //展示签到成功的页面
            CommonFunction.showSuccess(MainActivity.getInstance(), reqData);
        } else if (ApiType.APP_UP_GRADE == req.getApi()) {
            final AppUpgrade reqData = (AppUpgrade) req.getData();
            if (StringUtil.checkStr(reqData.android_update_url)) {
                CustomDialogUpdate.Builder builder = new CustomDialogUpdate.Builder(
                        HomepageActivity.this);
                builder.setMessage(reqData.getMessage())
                        .setTitle("V" + reqData.version)
                        .setPositiveButton("立即下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utils.loadApk(HomepageActivity.this, reqData.android_update_url, "Download");
                                showToast("正在下载请稍后...");
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                CustomDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        } else if (ApiType.GET_CLASSID == req.getApi()) {
            ClassIDResult data = (ClassIDResult) req.getData();
            if (data.getStatus().equals("1000") && data.categories != null) {
                if (ViewBeanList.isEmpty() || ViewBeanList.size() != data.categories.size()) {
                    try {
                        ViewBeanList.clear();
                        view_container.removeAllViews();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < data.categories.size(); i++) {
                        HomepageViewBean homepageViewBean = new HomepageViewBean();
                        final ClassIDResult.CategoriesEntity entity = data.categories.get(i);
                        if (entity != null) {
                            //设置 title_bar
                            ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.home_page_view_more_bar, null);
                            viewGroup.setVisibility(View.GONE);
                            ViewHolder holder = new ViewHolder(viewGroup);
                            if (i % 2 == 1) {
                                holder.view_bar_more_bar.setBackgroundColor(getResources().getColor(R.color.orange_goods_price));
                            } else {
                                holder.view_bar_more_bar.setBackgroundColor(getResources().getColor(R.color.green));
                            }
                            if (entity.name.equals("化肥")) {
                                huaFeiClassId = entity.id;//设置化肥的id
                                PreferenceUtil pu = new PreferenceUtil(this, App.SPNAME);
                                pu.putString("huafei", huaFeiClassId);

                                holder.view_bar_more_bar.setBackgroundColor(getResources().getColor(R.color.green));
                            }
                            if (entity.name.equals("汽车")) {
                                carClassId = entity.id;//设置汽车的id
                                PreferenceUtil pu = new PreferenceUtil(this, App.SPNAME);
                                pu.putString("qiche", carClassId);

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
                    for (int i = 0; i < data.categories.size(); i++) {
                        ClassIDResult.CategoriesEntity entity = data.categories.get(i);
                        HomepageViewBean homepageViewBean = ViewBeanList.get(i);

                        if (entity != null && homepageViewBean != null) {
                            homepageViewBean.classId = entity.id;
                            ViewHolder holder = new ViewHolder(homepageViewBean.viewGroup);
                            holder.view_bar_more_title.setText(entity.name + "精选");
                            if (entity.name.equals("化肥")) {
                                huaFeiClassId = entity.id;//设置化肥的id
                                holder.view_bar_more_bar.setBackgroundColor(getResources().getColor(R.color.green));
                            }
                            if (entity.name.equals("汽车")) {
                                carClassId = entity.id;//设置汽车的id
                                holder.view_bar_more_bar.setBackgroundColor(getResources().getColor(R.color.orange_goods_price));
                            }
                            getData(homepageViewBean);
                        }
                    }

                }
            }
        } else if (ApiType.GET_CAMPAIGNS == req.getApi()) {
            if (req.getData().getStatus().equals("1000")) {
                CampaignListResult reqData = (CampaignListResult) req.getData();
                campaigns = reqData.campaigns;
                //是否看过的活动
                boolean isSee = false;
                PreferenceUtil pu = new PreferenceUtil(HomepageActivity.this, App.CAMPAIGN);
                if (campaigns != null && !campaigns.isEmpty()) {
                    CampaignListResult.CampaignsBean campaignsBean = campaigns.get(0);
                    isSee = pu.getBool(campaignsBean._id, false);
                }
                if (!isSee&&!isTokenError) {
                    showCampaign();
                }
            }
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        PullToRefreshHelper.setFreshClose(refreshView);
        RemoteApi.getBanner(this);
        RemoteApi.getClassId(this);
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
            super(context, data, R.layout.item_home_gv);
        }

        @Override
        public void convert(CommonViewHolder holder, final SingleGood singleGood) {
            if (singleGood != null) {
                //商品图
                ImageView imageView = holder.getView(R.id.huafei_img);
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
                }
                PicassoHelper.setImageRes(HomepageActivity.this, singleGood.imgUrl, imageView);
                //商品名
                if (StringUtil.checkStr(singleGood.goodsName)) {
                    holder.setText(R.id.huafei_name_tv, singleGood.goodsName);
                }
                //商品是否预售
                if (singleGood.presale) {
                    TextView huafei_nowPrice = holder.getView(R.id.huafei_xianjia);
                    huafei_nowPrice.setTextColor(Color.GRAY);
                    huafei_nowPrice.setText("即将上线");
                } else {
                    //商品价格
                    TextView huafei_xianjia = holder.getView(R.id.huafei_xianjia);
                    huafei_xianjia.setTextColor(getResources().getColor(R.color.orange_goods_price));
                    huafei_xianjia.setText(StringUtil.checkStr(singleGood.unitPrice) ? "¥" + singleGood.unitPrice : "");
                }
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 带商品的id过去
                        Intent intent = new Intent(HomepageActivity.this, GoodsDetailActivity.class);
                        intent.putExtra("goodsId", singleGood.goodsId);
                        startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLogin()) {
            if (!isSigned && shakeAnimation != null) {
                getRightImageView().startAnimation(shakeAnimation);
            }
        } else {
            if (shakeAnimation != null) {
                getRightImageView().startAnimation(shakeAnimation);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除对下载完成事件的监听
        unregisterReceiver(completeReceiver);
        EventBus.getDefault().unregister(this);
    }

    private void showCampaign() {
        if (campaigns != null) {
            EventBus.getDefault().postSticky(new CampaignListEvent(campaigns));
        }
        Bundle bundle = new Bundle();
        bundle.putString(FloatingLayerActivity.KEY, FloatingLayerActivity.ACTIVITY_LIST);
        IntentUtil.activityForward(MainActivity.getInstance(), FloatingLayerActivity.class, bundle, false);
        ActivityAnimationUtils.setActivityAnimation(MainActivity.getInstance(), R.anim.animation_none, R.anim.animation_none);
        EventBus.getDefault().post(new MainTabSelectEvent(MainActivity.Tab.INDEX));
    }


}
