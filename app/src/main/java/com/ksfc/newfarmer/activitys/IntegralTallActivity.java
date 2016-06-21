/**
 *
 */
package com.ksfc.newfarmer.activitys;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
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
import com.ksfc.newfarmer.adapter.CommonAdapter;
import com.ksfc.newfarmer.adapter.CommonViewHolder;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.RemoteApi;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.beans.GiftCategoriesResult;
import com.ksfc.newfarmer.protocol.beans.GiftListResult;
import com.ksfc.newfarmer.protocol.beans.IntegralGetResult;
import com.ksfc.newfarmer.utils.ActivityAnimationUtils;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.Utils;
import com.ksfc.newfarmer.widget.UnSwipeGridView;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.yangentao.util.PreferenceUtil;
import net.yangentao.util.msg.MsgCenter;
import net.yangentao.util.msg.MsgListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by CAI on 2016/6/16.
 */
public class IntegralTallActivity extends BaseActivity  {

    @BindView(R.id.titleview)
    RelativeLayout titleview;
    @BindView(R.id.integral_count_tv)
    TextView integral_tv;
    @BindView(R.id.view_container)
    LinearLayout view_container;
    @BindView(R.id.view_none_container)
    LinearLayout view_none_container;
    @BindView(R.id.head_unLogin_tall_layout)
    LinearLayout headUnLoginTallLayout;
    @BindView(R.id.head_unLogin_tall_layout_float)
    LinearLayout headUnLoginTallLayoutFloat;
    @BindView(R.id.tall_srcollView)
    ScrollView scrollView;


    //GridView里宽度
    private int itemWitch;
    //缓存view
    private List<IntegralBean> ViewBeanList = new ArrayList<>();
    //用户积分
    private int score = 0;


    @Override
    public int getLayout() {
        return R.layout.integral_tall_layout;
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
                RemoteApi.getIntegral(IntegralTallActivity.this);
                headUnLoginTallLayout.setVisibility(View.GONE);
                headUnLoginTallLayoutFloat.setVisibility(View.GONE);
            }
        }, MsgID.ISLOGIN);
    }


    private void initView() {
        ViewGroup.LayoutParams layoutParams = titleview.getLayoutParams();
        layoutParams.height = Utils.dip2px(IntegralTallActivity.this, 40);
        titleview.setLayoutParams(layoutParams);
        view_none_container.setVisibility(View.GONE);

        headUnLoginTallLayout.setVisibility(View.GONE);
        headUnLoginTallLayoutFloat.setVisibility(View.GONE);

        setViewClick(R.id.integral_count_ll);
        setViewClick(R.id.changing_record_ll);
        setViewClick(R.id.integral_rules_ll);
        setViewClick(R.id.head_unLogin_tall_layout_float);


    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.integral_count_ll:
                if (isLogin()) {
                    IntentUtil.activityForward(IntegralTallActivity.this, MyIntegralActivity.class, null, false);
                } else {
                    startActivity(LoginActivity.class);
                }
                break;
            case R.id.changing_record_ll:
                if (isLogin()) {
                    IntentUtil.activityForward(IntegralTallActivity.this, MyIntegralActivity.class, null, false);
                } else {
                    startActivity(LoginActivity.class);
                }
                break;
            case R.id.head_unLogin_tall_layout_float:
                if (!isLogin()) {
                    startActivity(LoginActivity.class);
                } else {
                    headUnLoginTallLayoutFloat.setVisibility(View.GONE);
                    headUnLoginTallLayout.setVisibility(View.GONE);
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
        if (req.getApi() == ApiType.GET_INTEGRAL) {
            IntegralGetResult data = (IntegralGetResult) req.getData();
            if (data.datas != null) {
                score = data.datas.score;
                integral_tv.setText(score + "");
            }
        } else if (req.getApi() == ApiType.GET_GIFT_CATEGORIES) {
            GiftCategoriesResult categoriesResult = (GiftCategoriesResult) req.getData();
            if (categoriesResult.categories != null) {
                for (int i = 0; i < categoriesResult.categories.size(); i++) {
                    GiftCategoriesResult.CategoriesBean categoriesBean = categoriesResult.categories.get(i);
                    if (categoriesBean != null) {
                        //设置 title_bar
                        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.gift_view_more_bar, null);
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
            }
        } else if (ApiType.GET_GIFT_LIST == req.getApi()) {
            GiftListResult data = (GiftListResult) req.getData();
            if (data.datas != null && data.datas.gifts != null) {
                List<GiftListResult.DatasBean.GiftsBean> gifts = data.datas.gifts;
                if (!gifts.isEmpty()) {
                    GiftListResult.DatasBean.GiftsBean giftsBean = gifts.get(0);
                    int count = 0;
                    for (int i = 0; i < ViewBeanList.size(); i++) {
                        IntegralBean integralBean = ViewBeanList.get(i);
                        if (integralBean != null && giftsBean.category != null
                                && integralBean._id.equals(giftsBean.category._id)) {
                            GiftAdapter adapter = new GiftAdapter(IntegralTallActivity.this, gifts);
                            integralBean.unSwipeGridView.setAdapter(adapter);
                            integralBean.viewGroup.setVisibility(View.VISIBLE);
                            count += integralBean.unSwipeGridView.getCount();
                            if (i == ViewBeanList.size() - 1) {
                                try {
                                    //加载完成数据后展示引导页
                                    GiftListResult.DatasBean.GiftsBean giftsBean1 = (GiftListResult.DatasBean.GiftsBean) ViewBeanList.get(0).unSwipeGridView.getAdapter().getItem(0);
                                    showGuide(score, giftsBean1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //是否显示没有商品的布局
                                if (count == 0) {
                                    view_none_container.setVisibility(View.VISIBLE);
                                } else {
                                    view_none_container.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                }
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
            super(context, data, R.layout.gift_gv_item);
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
                ImageLoader.getInstance().displayImage(MsgID.IP + gift.largeUrl, imageView);

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
                        IntentUtil.activityForward(IntegralTallActivity.this, IntegralGiftDetailActivity.class, bundle, false);
                    }
                });
            }
        }

    }

    //展示浮层
    private void showGuide(int integral, GiftListResult.DatasBean.GiftsBean gift) {
        //第一次进入我的展示积分引导页
        PreferenceUtil pu = new PreferenceUtil(IntegralTallActivity.this, "config");
        boolean firstInIntegral = pu.getBool("firstInIntegral", true);
        if (firstInIntegral) {
            Bundle integralData = new Bundle();
            integralData.putString("activity", getClass().getSimpleName());
            integralData.putString("integral", String.valueOf(integral));
            integralData.putSerializable("gift", gift);
            IntentUtil.activityForward(this, FloatingLayerActivity.class, integralData, false);
            ActivityAnimationUtils.setActivityAnimation(this, R.anim.animation_none, R.anim.animation_none);
        }
        pu.putBool("firstInIntegral", false);
    }


}
