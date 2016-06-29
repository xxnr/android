package com.ksfc.newfarmer.fragment;

import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseFragment;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.http.beans.GiftListResult;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.yangentao.util.msg.MsgCenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by CAI on 2016/6/14.
 */
public class IntegralTallGuideFragment extends BaseFragment {
    private Bundle arguments;
    private int page;
    private Unbinder unbinder;

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.guide_next_button:
                if (page == 1) {
                    MsgCenter.fireNull(MsgID.Integral_Guide_Change, 2);
                } else if (page == 2) {
                    MsgCenter.fireNull(MsgID.Integral_Guide_Change, 3);
                }
                break;
            case R.id.guide_finish_button:
                activity.finish();
                break;
        }
    }

    @Override
    public View InItView() {
        arguments = getArguments();
        if (arguments != null) {
            page = arguments.getInt("page", 1);
        }
        View view = null;
        switch (page) {
            case 1:
                view = inflater.inflate(R.layout.fragment_integral_guide1, null);
                view.findViewById(R.id.guide_next_button).setOnClickListener(this);
                TextView integral_count_tv = (TextView) view.findViewById(R.id.integral_count_tv);
                if (arguments != null) {
                    String integral = arguments.getString("integral");
                    if (StringUtil.checkStr(integral)) {
                        integral_count_tv.setText(integral);
                    }
                }
                break;
            case 2:
                view = inflater.inflate(R.layout.fragment_integral_guide2, null);
                //设置content内容
                GiftListResult.DatasBean.GiftsBean gift = (GiftListResult.DatasBean.GiftsBean) arguments.getSerializable("gift");
                if (gift != null) {
                    Guide2ViewHolder viewHolder = new Guide2ViewHolder(view);
                    viewHolder.guideNextButton.setOnClickListener(this);
                    //设置content位置
                    WindowManager windowManager = activity.getWindowManager();
                    Display display = windowManager.getDefaultDisplay();
                    int wh = display.getWidth();
                    int itemWitch = (wh - (Utils.dip2px(activity, 12 + 16 * 2))) / 2;
                    if (itemWitch != 0) {
                        ViewGroup.LayoutParams layoutParams = viewHolder.giftContentRel.getLayoutParams();
                        layoutParams.height = itemWitch;
                        layoutParams.width = itemWitch;
                        viewHolder.giftContentRel.setLayoutParams(layoutParams);
                    }

                    ImageLoader.getInstance().displayImage(MsgID.IP + gift.largeUrl, viewHolder.giftImg);
                    viewHolder.giftNameTv.setText(gift.name);
                    viewHolder.giftPriceTv.setText(gift.points + "");

                    if (gift.soldout) {
                        viewHolder.soldOutTv.setVisibility(View.VISIBLE);
                        viewHolder.giftNameTv.setTextColor(getResources().getColor(R.color.deep_gray));
                        viewHolder.giftPriceTv.setTextColor(getResources().getColor(R.color.deep_gray));
                        viewHolder.giftPriceIconIv.setBackgroundResource(R.drawable.integral_sold_out_icon);
                    } else {
                        viewHolder.giftNameTv.setTextColor(getResources().getColor(R.color.black_goods_titile));
                        viewHolder.giftPriceTv.setTextColor(getResources().getColor(R.color.orange_goods_price));
                        viewHolder.giftPriceIconIv.setBackgroundResource(R.drawable.gift_integral_icon);
                        viewHolder.soldOutTv.setVisibility(View.INVISIBLE);
                    }
                }
                break;
            case 3:
                view = inflater.inflate(R.layout.fragment_integral_guide3, null);
                view.findViewById(R.id.guide_finish_button).setOnClickListener(this);
                break;
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResponsed(Request req) {

    }

    class Guide2ViewHolder {
        @BindView(R.id.gift_img)
        ImageView giftImg;
        @BindView(R.id.gift_name_tv)
        TextView giftNameTv;
        @BindView(R.id.gift_price_icon_iv)
        ImageView giftPriceIconIv;
        @BindView(R.id.gift_price_tv)
        TextView giftPriceTv;
        @BindView(R.id.sold_out_tv)
        TextView soldOutTv;
        @BindView(R.id.gift_content_rel)
        LinearLayout giftContentRel;
        @BindView(R.id.guide_next_button)
        ImageView guideNextButton;
        @BindView(R.id.integral_guide1_rel)
        RelativeLayout integralGuide1Rel;
        @BindView(R.id.gift_content_ll)
        LinearLayout gift_content_ll;

        Guide2ViewHolder(View view) {
            unbinder = ButterKnife.bind(this, view);
        }
    }
}
