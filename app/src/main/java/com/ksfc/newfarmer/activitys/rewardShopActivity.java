/**
 *
 */
package com.ksfc.newfarmer.activitys;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.common.CommonAdapter;
import com.ksfc.newfarmer.common.CommonViewHolder;
import com.ksfc.newfarmer.common.GlideUtils;
import com.ksfc.newfarmer.http.ApiType;
import com.ksfc.newfarmer.http.RemoteApi;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.http.beans.GiftCategoriesResult;
import com.ksfc.newfarmer.http.beans.GiftListResult;
import com.ksfc.newfarmer.http.beans.IntegralGetResult;
import com.ksfc.newfarmer.utils.ActivityAnimationUtils;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.ScreenUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.Utils;
import com.ksfc.newfarmer.widget.ObservableScrollView;
import com.ksfc.newfarmer.widget.PtrHeaderView;
import com.ksfc.newfarmer.widget.UnSwipeGridView;

import net.yangentao.util.PreferenceUtil;
import net.yangentao.util.msg.MsgCenter;
import net.yangentao.util.msg.MsgListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by CAI on 2016/6/16.
 */
public class RewardShopActivity extends BaseActivity {

    @BindView(R.id.titleview)
    RelativeLayout titleview;
    @BindView(R.id.integral_count_tv)
    TextView integral_tv;
    @BindView(R.id.view_container)
    LinearLayout view_container;
    @BindView(R.id.view_none_container)
    LinearLayout view_none_container;
    @BindView(R.id.head_unLogin_tall_layout)
    View headUnLoginTallLayout;
    @BindView(R.id.head_unLogin_tall_layout_float)
    LinearLayout headUnLoginTallLayoutFloat;
    @BindView(R.id.rotate_header_list_view_frame)
    PtrClassicFrameLayout frameLayout;
    @BindView(R.id.tall_srcollView)
    ObservableScrollView scrollView;
    @BindView(R.id.return_top)
    ImageView return_top;


    //GridView里宽度
    private int itemWitch;
    //缓存view
    private List<IntegralBean> ViewBeanList = new ArrayList<>();
    //用户积分
    private int score = 0;
    private int count;
    private boolean isShow = false;
    private Handler handler = new Handler();

    @Override
    public int getLayout() {
        return R.layout.activity_integral_tall;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setTitle("积分商城");
        initView();
        if (isLogin()) {
            RemoteApi.getIntegral(this);
        } else {
            integral_tv.setText("0");
            headUnLoginTallLayout.setVisibility(View.VISIBLE);
            headUnLoginTallLayoutFloat.setVisibility(View.VISIBLE);
        }
        showProgressDialog();
        RemoteApi.getGiftCategories(this);
        //监听登录事件
        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                RemoteApi.getIntegral(RewardShopActivity.this);
                headUnLoginTallLayout.setVisibility(View.GONE);
                headUnLoginTallLayoutFloat.setVisibility(View.GONE);
            }
        }, MsgID.ISLOGIN);

        //签到通知
        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                RemoteApi.getIntegral(RewardShopActivity.this);
            }
        }, MsgID.IS_Signed);
    }


    private void initView() {
        ViewGroup.LayoutParams layoutParams = titleview.getLayoutParams();
        layoutParams.height = Utils.dip2px(RewardShopActivity.this, 40);
        titleview.setLayoutParams(layoutParams);
        view_none_container.setVisibility(View.GONE);

        headUnLoginTallLayout.setVisibility(View.GONE);
        headUnLoginTallLayoutFloat.setVisibility(View.GONE);

        setViewClick(R.id.integral_count_ll);
        setViewClick(R.id.changing_record_ll);
        setViewClick(R.id.integral_rules_ll);
        setViewClick(R.id.head_unLogin_tall_layout_float);
        setViewClick(R.id.return_top);

        final int screenHeight = ScreenUtil.getScreenHeight(RewardShopActivity.this);

        scrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
                if (y != 0) {
                    if (y > screenHeight) {
                        return_top.setVisibility(View.VISIBLE);
                    } else {
                        if (y > oldy) {
                            return_top.setVisibility(View.GONE);
                        } else {
                            return_top.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    return_top.setVisibility(View.GONE);
                }
            }
        });



            /* 设置刷新头部view */
        PtrHeaderView header = new PtrHeaderView(RewardShopActivity.this);
        frameLayout.setHeaderView(header);
        /* 设置回调 */
        frameLayout.addPtrUIHandler(header);
        frameLayout.setLastUpdateTimeRelateObject(this);
        frameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                frameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (frameLayout != null) {
                            frameLayout.refreshComplete();
                        }
                    }
                }, 2000);
                count = 0;
                RemoteApi.getGiftCategories(RewardShopActivity.this);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });


    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.integral_count_ll:
                if (isLogin()) {
                    IntentUtil.activityForward(RewardShopActivity.this, MyRewardActivity.class, null, false);
                } else {
                    startActivity(LoginActivity.class);
                }
                break;
            case R.id.changing_record_ll:
                if (isLogin()) {
                    IntentUtil.activityForward(RewardShopActivity.this, ExchangeRecordActivity.class, null, false);
                } else {
                    startActivity(LoginActivity.class);
                }
                break;
            case R.id.integral_rules_ll:
                startActivity(RewardRulesActivity.class);
                break;
            case R.id.head_unLogin_tall_layout_float:
                if (!isLogin()) {
                    startActivity(LoginActivity.class);
                } else {
                    headUnLoginTallLayoutFloat.setVisibility(View.GONE);
                    headUnLoginTallLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.return_top:
                if (scrollView != null) {
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
                break;
        }
    }


    class IntegralBean {
        public String _id;
        public UnSwipeGridView unSwipeGridView;
        public ViewGroup viewGroup;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResponsed(Request req) {
        if (frameLayout != null) {
            frameLayout.refreshComplete();
        }
        if (req.getApi() == ApiType.GET_INTEGRAL) {
            IntegralGetResult data = (IntegralGetResult) req.getData();
            if (data.datas != null) {
                score = data.datas.score;
                integral_tv.setText(score + "");
            }
        } else if (req.getApi() == ApiType.GET_GIFT_CATEGORIES) {
            GiftCategoriesResult categoriesResult = (GiftCategoriesResult) req.getData();
            if (categoriesResult.categories != null) {
                if (ViewBeanList.isEmpty() || categoriesResult.categories.size() != ViewBeanList.size()) {
                    try {
                        ViewBeanList.clear();
                        view_container.removeAllViews();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < categoriesResult.categories.size(); i++) {
                        GiftCategoriesResult.CategoriesBean categoriesBean = categoriesResult.categories.get(i);
                        if (categoriesBean != null) {
                            //设置 title_bar
                            ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.view_more_bar_gift, null);
                            ViewHolder holder = new ViewHolder(viewGroup);
                            if (i % 2 == 1) {
                                holder.view_bar_more_bar.setBackgroundColor(getResources().getColor(R.color.orange_goods_price));
                            } else {
                                holder.view_bar_more_bar.setBackgroundColor(getResources().getColor(R.color.green));
                            }
                            if (StringUtil.checkStr(categoriesBean.name)) {
                                holder.view_bar_more_title.setText(categoriesBean.name);
                            }
                            viewGroup.setVisibility(View.GONE);
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
                            view_container.addView(viewGroup);
                            view_container.addView(linearLayout);
                            //加入集合
                            IntegralBean integralBean = new IntegralBean();
                            integralBean._id = categoriesBean._id;
                            integralBean.unSwipeGridView = unSwipeGridView;
                            integralBean.viewGroup = viewGroup;
                            ViewBeanList.add(integralBean);
                            //请求gift列表
                            RemoteApi.getGiftList(this, categoriesBean._id);
                        }
                    }

                } else {
                    if (categoriesResult.categories.size() == ViewBeanList.size()) {
                        for (int i = 0; i < categoriesResult.categories.size(); i++) {
                            GiftCategoriesResult.CategoriesBean categoriesBean = categoriesResult.categories.get(i);
                            IntegralBean integralBean = ViewBeanList.get(i);
                            if (categoriesBean != null && integralBean != null) {
                                if (StringUtil.checkStr(categoriesBean.name)) {
                                    ViewHolder holder = new ViewHolder(integralBean.viewGroup);
                                    holder.view_bar_more_title.setText(categoriesBean.name);
                                }
                                //请求gift列表
                                RemoteApi.getGiftList(this, categoriesBean._id);
                            }
                        }
                    }
                }
            }


        } else if (ApiType.GET_GIFT_LIST == req.getApi()) {
            GiftListResult data = (GiftListResult) req.getData();
            if (data.datas != null && data.datas.gifts != null) {
                List<GiftListResult.DatasBean.GiftsBean> gifts = data.datas.gifts;
                if (!gifts.isEmpty()) {
                    GiftListResult.DatasBean.GiftsBean giftsBean = gifts.get(0);
                    for (int i = 0; i < ViewBeanList.size(); i++) {
                        IntegralBean integralBean = ViewBeanList.get(i);
                        if (integralBean != null && giftsBean.category != null && integralBean._id.equals(giftsBean.category._id)) {
                            GiftAdapter adapter = new GiftAdapter(RewardShopActivity.this, gifts);
                            integralBean.unSwipeGridView.setAdapter(adapter);
                            integralBean.viewGroup.setVisibility(View.VISIBLE);
                            count += integralBean.unSwipeGridView.getCount();
                            if (i == ViewBeanList.size() - 1) {
                                try {
                                    //加载完成数据后展示引导页
                                    final GiftListResult.DatasBean.GiftsBean giftsBean1 = (GiftListResult.DatasBean.GiftsBean) ViewBeanList.get(0).unSwipeGridView.getAdapter().getItem(0);
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (isShow) {
                                                showGuide(score, giftsBean1);
                                            }
                                        }
                                    }, 1000);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }

                }
            }

            //是否显示没有商品的布局
            if (count == 0) {
                view_none_container.setVisibility(View.VISIBLE);
            } else {
                view_none_container.setVisibility(View.GONE);
            }
        }

    }


    class ViewHolder {
        private TextView view_bar_more_bar, view_bar_more_title;

        ViewHolder(View view) {
            view_bar_more_bar = (TextView) view.findViewById(R.id.view_bar_more_bar);
            view_bar_more_title = (TextView) view.findViewById(R.id.view_bar_more_title);
        }
    }


    class GiftAdapter extends CommonAdapter<GiftListResult.DatasBean.GiftsBean> {

        public GiftAdapter(Context context, List<GiftListResult.DatasBean.GiftsBean> data) {
            super(context, data, R.layout.item_gift_gv);
        }

        @Override
        public void convert(CommonViewHolder holder, final GiftListResult.DatasBean.GiftsBean gift) {

            if (gift != null) {
                // 商品图的外边
                if (itemWitch != 0) {
                    LinearLayout relativeLayout = holder.getView(R.id.gift_content_rel);
                    ViewGroup.LayoutParams layoutParams = relativeLayout.getLayoutParams();
                    layoutParams.height = itemWitch;
                    layoutParams.width = itemWitch;
                    relativeLayout.setLayoutParams(layoutParams);
                }
                //商品图
                ImageView imageView = holder.getView(R.id.gift_img);
                GlideUtils.setImageRes(RewardShopActivity.this,gift.largeUrl,imageView);

                TextView gift_name_tv = holder.getView(R.id.gift_name_tv);
                TextView gift_price_tv = holder.getView(R.id.gift_price_tv);
                View gift_price_icon_iv = holder.getView(R.id.gift_price_icon_iv);

                gift_name_tv.setText(StringUtil.checkStr(gift.name) ? gift.name : "");
                gift_price_tv.setText(gift.points + "");

                if (gift.soldout) {
                    holder.getView(R.id.sold_out_tv).setVisibility(View.VISIBLE);
                    gift_name_tv.setTextColor(getResources().getColor(R.color.deep_gray));
                    gift_price_tv.setTextColor(getResources().getColor(R.color.deep_gray));
                    gift_price_icon_iv.setBackgroundResource(R.drawable.integral_sold_out_icon);
                } else {
                    gift_name_tv.setTextColor(getResources().getColor(R.color.black_goods_titile));
                    gift_price_tv.setTextColor(getResources().getColor(R.color.orange_goods_price));
                    gift_price_icon_iv.setBackgroundResource(R.drawable.gift_integral_icon);
                    holder.getView(R.id.sold_out_tv).setVisibility(View.INVISIBLE);
                }

                //去详情
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("id", gift.id);
                        bundle.putInt("score", score);
                        IntentUtil.activityForward(RewardShopActivity.this, RewardDetailActivity.class, bundle, false);
                    }
                });
            }
        }

    }

    //展示浮层
    private void showGuide(int integral, GiftListResult.DatasBean.GiftsBean gift) {
        //第一次进入我的展示积分引导页
        PreferenceUtil pu = new PreferenceUtil(RewardShopActivity.this, "config");
        boolean firstInIntegral = pu.getBool("firstInIntegral", true);
        if (firstInIntegral) {
            Bundle integralData = new Bundle();
            integralData.putString(FloatingLayerActivity.KEY, FloatingLayerActivity.REWARD_SHOP_GUIDE);
            integralData.putString("integral", String.valueOf(integral));
            integralData.putSerializable("gift", gift);
            IntentUtil.activityForward(this, FloatingLayerActivity.class, integralData, false);
            ActivityAnimationUtils.setActivityAnimation(this, R.anim.animation_none, R.anim.animation_none);
        }
        pu.putBool("firstInIntegral", false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isShow = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isShow = true;
    }
}
