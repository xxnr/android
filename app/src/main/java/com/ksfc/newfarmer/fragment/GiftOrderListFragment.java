package com.ksfc.newfarmer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.adapter.CommonAdapter;
import com.ksfc.newfarmer.adapter.CommonViewHolder;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.RemoteApi;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.beans.GiftOrderListResult;
import com.ksfc.newfarmer.utils.DateFormatUtils;
import com.ksfc.newfarmer.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by CAI on 2016/6/22.
 */
public class GiftOrderListFragment extends BaseFragment {
    @BindView(R.id.gift_order_listView)
    ListView listView;
    private Unbinder unbinder;

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public View InItView() {
        View rootView = inflater.inflate(R.layout.gift_order_list_fragment, null);
        unbinder = ButterKnife.bind(this, rootView);
        Bundle arguments = getArguments();
        if (arguments != null) {
            int position = arguments.getInt("position");
            if (position == 0) {
                RemoteApi.getGiftOrderList(this, 1, 1);
            } else {
                RemoteApi.getGiftOrderList(this, 2, 1);
            }
        }
        return rootView;
    }


    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_GIFT_ORDER_LIST) {
            GiftOrderListResult reqData = (GiftOrderListResult) req.getData();
            if (reqData.datas != null) {
                List<GiftOrderListResult.DatasBean.GiftordersBean> giftorders = reqData.datas.giftorders;
                if (giftorders != null) {
                    GiftOrderListAdapter adapter = new GiftOrderListAdapter(activity, giftorders);
                    listView.setAdapter(adapter);
                }
            }
        }
    }

    class GiftOrderListAdapter extends CommonAdapter<GiftOrderListResult.DatasBean.GiftordersBean> {

        public GiftOrderListAdapter(Context context, List<GiftOrderListResult.DatasBean.GiftordersBean> data) {
            super(context, data, R.layout.item_gift_order_list_layout);
        }

        @Override
        public void convert(CommonViewHolder holder, GiftOrderListResult.DatasBean.GiftordersBean giftordersBean) {


            holder.setText(R.id.gift_order_time, StringUtil.checkStr(giftordersBean.dateCreated)
                    ? DateFormatUtils.convertTime(giftordersBean.dateCreated) : "");
            if (giftordersBean.orderStatus!=null){
                holder.setText(R.id.gift_order_delivery_state, StringUtil.checkStr(giftordersBean.orderStatus.value)
                        ?giftordersBean.orderStatus.value : "");
            }

            if (giftordersBean.gift!=null){
                ImageView gift_iv = holder.getView(R.id.gift_order_img_iv);
                if (StringUtil.checkStr(giftordersBean.gift.thumbnail)) {
                    ImageLoader.getInstance().displayImage(MsgID.IP + giftordersBean.gift.thumbnail, gift_iv);
                }

                holder.setText(R.id.gift_order_name_iv, StringUtil.checkStr(giftordersBean.gift.name)
                        ? giftordersBean.gift.name : "");

                holder.setText(R.id.gift_order_price_iv, StringUtil.checkStr(String.valueOf(giftordersBean.gift.points))
                        ? String.valueOf(giftordersBean.gift.points) : "");
            }

        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
