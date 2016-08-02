package com.ksfc.newfarmer.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ksfc.newfarmer.EventBaseFragment;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.BigImageActivity;
import com.ksfc.newfarmer.event.BigImageEvent;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.beans.GetGoodsDetail;
import com.ksfc.newfarmer.utils.ActivityAnimationUtils;
import com.ksfc.newfarmer.utils.ScreenUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.CirclePageIndicator;
import com.squareup.picasso.Picasso;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by CAI on 2016/7/15.
 */
public class GoodsDetailTopFragment extends EventBaseFragment implements ViewPager.OnPageChangeListener {
    private static final String ARG_PARAM1 = "param1";

    private GetGoodsDetail.GoodsDetail detail;
    private TextView current_count;
    private ViewPager viewPager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            detail = (GetGoodsDetail.GoodsDetail) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    public static GoodsDetailTopFragment newInstance(GetGoodsDetail.GoodsDetail detail) {
        GoodsDetailTopFragment fragment = new GoodsDetailTopFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, detail);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public View InItView() {

        View view = inflater.inflate(R.layout.goods_detail_top, null);
        viewPager = (ViewPager) view.findViewById(R.id.goods_detail_top_viewpager);
        View goods_detail_top_viewpager_rel = view.findViewById(R.id.goods_detail_top_viewpager_rel);
        ScreenUtil.setHeight(activity, goods_detail_top_viewpager_rel, 360);

        TextView good_xianjia = (TextView) view.findViewById(R.id.product_price);//xianjia:价格
        CirclePageIndicator indicator = (CirclePageIndicator) view.findViewById(R.id.circlePageIndicator);//Viewpager的指示器Indicator
        current_count = (TextView) view.findViewById(R.id.current_count);//当前Viewpager/item
        TextView good_name = (TextView) view.findViewById(R.id.good_name); //商品名
        TextView product_dingjing = (TextView) view.findViewById(R.id.product_dingjin_price);//定金
        RelativeLayout product_description_rel = (RelativeLayout) view.findViewById(R.id.product_description_rel);//描述rel
        TextView product_description = (TextView) view.findViewById(R.id.product_description);//描述
        TextView product_dingjing_name = (TextView) view.findViewById(R.id.product_dingjin_name);//"定金"
        RelativeLayout detail_detail = (RelativeLayout) view.findViewById(R.id.goods_detail_detail);//继续上滑，可以加载更多
        LinearLayout market_price_ll = (LinearLayout) view.findViewById(R.id.market_price_ll);//市场价 lin
        TextView market_price_tv = (TextView) view.findViewById(R.id.market_price_tv);//市场价
        market_price_tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//市场价的中划线
        TextView product_newFarmer_price = (TextView) view.findViewById(R.id.product_newFarmer_price);//新农价
        RelativeLayout circlePageIndicator_rel = (RelativeLayout) view.findViewById(R.id.circlePageIndicator_rel);//商品多个主图时Indicator的背景
        if (StringUtil.checkStr(detail.name)) {
            good_name.setText(detail.name);
        }
        if (StringUtil.checkStr(detail.description)) {
            product_description.setText(detail.description);
        } else {
            product_description_rel.setVisibility(View.GONE);
        }
        if (detail.pictures != null) {
            MyPagerAdapter myPagerAdapter = new MyPagerAdapter(detail.pictures);
            viewPager.setAdapter(myPagerAdapter);
            if (detail.pictures.size() > 1) {
                viewPager.addOnPageChangeListener(this);
                current_count.setText("1/" + detail.pictures.size());
                indicator.setViewPager(viewPager);
            } else {
                viewPager.addOnPageChangeListener(null);
                circlePageIndicator_rel.setVisibility(View.GONE);
                current_count.setVisibility(View.GONE);
                indicator.setVisibility(View.GONE);
            }
        }
        //根据有无商品详情的url,隐藏继续滑动
        if (!TextUtils.isEmpty(detail.app_body_url)) {
            detail_detail.setVisibility(View.VISIBLE);
        } else {
            detail_detail.setVisibility(View.INVISIBLE);
        }
        //是否有订金
        if (!detail.deposit.equals("0")) {
            product_dingjing_name.setVisibility(View.VISIBLE);
            product_dingjing.setText("¥" + detail.deposit);
        }
        //下架的商品也不展示订金
        if (!detail.online) {
            product_dingjing.setVisibility(View.GONE);
            product_dingjing_name.setVisibility(View.GONE);
        }
        //是否预售
        if (detail.presale) {
            product_dingjing.setVisibility(View.GONE);
            product_dingjing_name.setVisibility(View.GONE);
            product_newFarmer_price.setTextColor(Color.GRAY);
            product_newFarmer_price.setText("即将上线");
        } else {
            //新农价
            if (detail.SKUPrice != null && detail.online) {
                if (StringUtil.checkStr(detail.SKUPrice.min) && StringUtil.checkStr(detail.SKUPrice.max)) {
                    if (!detail.SKUPrice.min.equals(detail.SKUPrice.max)) {
                        good_xianjia.setText("¥" + detail.SKUPrice.min + "-" + detail.SKUPrice.max);
                    } else {
                        good_xianjia.setText("¥" + detail.SKUPrice.min);
                    }
                }
            } else {
                if (detail.referencePrice != null) {
                    if (!detail.referencePrice.min.equals(detail.referencePrice.max)) {
                        good_xianjia.setText("¥" + detail.referencePrice.min + "-" + detail.referencePrice.max);
                    } else {
                        good_xianjia.setText("¥" + detail.referencePrice.min);
                    }
                }
            }
        }

        //市场价
        if (detail.SKUMarketPrice != null
                && StringUtil.checkStr(detail.SKUMarketPrice.min)
                && StringUtil.checkStr(detail.SKUMarketPrice.max)
                && !detail.SKUMarketPrice.min.equals("0")
                && !detail.SKUMarketPrice.max.equals("0")) {
            market_price_ll.setVisibility(View.VISIBLE);
            if (!detail.SKUMarketPrice.min.equals(detail.SKUMarketPrice.max)) {
                market_price_tv.setText("¥" + detail.SKUMarketPrice.min + "-" + detail.SKUMarketPrice.max);
            } else {
                market_price_tv.setText("¥" + detail.SKUMarketPrice.min);
            }
        } else {
            market_price_ll.setVisibility(View.GONE);
        }
        return view;
    }

    /**
     * 监听在大图页选中的是哪个item
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateItem(BigImageEvent event) {
        if (viewPager != null) {
            viewPager.setCurrentItem(event.position);
        }
    }

    class MyPagerAdapter extends PagerAdapter {

        private List<GetGoodsDetail.GoodsDetail.Pictures> pictures;


        public MyPagerAdapter(List<GetGoodsDetail.GoodsDetail.Pictures> pictures) {
            this.pictures = pictures;
        }

        @Override
        public int getCount() {
            return pictures.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            View view = inflater.inflate(R.layout.viewpager_pic, null);
            final ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            if (StringUtil.checkStr(pictures.get(position).imgUrl)){
                Picasso.with(activity)
                        .load(MsgID.IP+pictures.get(position).imgUrl)
                        .placeholder(R.drawable.zhanweitu)
                        .error(R.drawable.error)
                        .config(Bitmap.Config.RGB_565)
                        .into(imageView);
            }else {
               imageView.setImageResource(R.drawable.error);
            }
            container.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(BigImageActivity.DETAIL, detail);
                    bundle.putInt(BigImageActivity.POSITION, position);
                    Intent intent = new Intent(activity, BigImageActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    ActivityAnimationUtils.setActivityAnimation(activity, 0, 0);

                }
            });
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (current_count != null && detail.pictures != null) {
            StringBuilder builder = new StringBuilder();
            builder.append(position + 1).append("/").append(detail.pictures.size());
            current_count.setText(builder);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void onResponsed(Request req) {

    }
}
