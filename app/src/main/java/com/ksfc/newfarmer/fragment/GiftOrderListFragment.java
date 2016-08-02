package com.ksfc.newfarmer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ksfc.newfarmer.EventBaseFragment;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.common.PicassoHelper;
import com.ksfc.newfarmer.common.LoadMoreScrollListener;
import com.ksfc.newfarmer.event.GiftListReFresh;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.remoteapi.RemoteApi;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.beans.GiftOrderListResult;
import com.ksfc.newfarmer.utils.DateFormatUtils;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.AnimatedExpandableListView;
import com.ksfc.newfarmer.widget.LoadingFooter;
import com.ksfc.newfarmer.widget.PtrHeaderView;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by CAI on 2016/6/22.
 */
public class GiftOrderListFragment extends EventBaseFragment {
    @BindView(R.id.gift_order_listView)
    AnimatedExpandableListView listView;
    @BindView(R.id.rotate_header_list_view_frame)
    PtrClassicFrameLayout frameLayout;
    @BindView(R.id.exchange_record_empty_iv)
    ImageView exchange_record_empty_iv;
    private int type;
    private int page = 1;

    private LoadingFooter loadingFooter;

    private LoadMoreScrollListener moreOnsrcollListener = new LoadMoreScrollListener() {
        @Override
        public void loadMore() {
            //加载更多
            if (loadingFooter.getState() == LoadingFooter.State.Idle) {
                loadingFooter.setState(LoadingFooter.State.Loading);
                page++;
                RemoteApi.getGiftOrderList(GiftOrderListFragment.this, type, page);
            }
        }
    };
    private GiftOrderListAdapter adapter;
    private int position;

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public View InItView() {
        View rootView = inflater.inflate(R.layout.fragment_gift_order_list, null);
        ButterKnife.bind(this, rootView);
        listView.setGroupIndicator(null);
        listView.setOnScrollListener(moreOnsrcollListener);
        loadingFooter = new LoadingFooter(activity, listView);
        PtrHeaderView header = new PtrHeaderView(activity);
           /* 设置刷新头部view */
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
                page = 1;
                RemoteApi.getGiftOrderList(GiftOrderListFragment.this, type, page);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });


        Bundle arguments = getArguments();
        if (arguments != null) {
            position = arguments.getInt("position");
            if (position == 0) {
                type = 1;
            } else {
                type = 2;
            }
        }

        if (type == 2) {//已完成的订单不展开
            listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    return true;
                }
            });
        } else {
            listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                    if (parent.isGroupExpanded(groupPosition)) {
                        //展开被选的group
                        listView.collapseGroupWithAnimation(groupPosition);
                        //设置被选中的group置于顶端
                        listView.setSelectedGroup(groupPosition);
                    } else {
                        //展开被选的group
                        listView.expandGroupWithAnimation(groupPosition);
                        //设置被选中的group置于顶端
                        listView.setSelectedGroup(groupPosition);
                    }

                    return true;
                }
            });

            // 这里是控制只有一个group展开的效果
            listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int groupPosition) {
                    for (int i = 0; i < adapter.getGroupCount(); i++) {
                        if (groupPosition != i) {
                            listView.collapseGroup(i);
                        }
                    }
                }
            });
        }
        RemoteApi.getGiftOrderList(this, type, page);
        return rootView;
    }

    /**
     * 礼品订单刷新
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void giftRefreshEvent(GiftListReFresh event){
        if (event.position == position) {
            showProgressDialog();
            page = 1;
            RemoteApi.getGiftOrderList(GiftOrderListFragment.this, type, page);
        }
    }




    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_GIFT_ORDER_LIST) {
            if (frameLayout != null) {
                frameLayout.refreshComplete();
            }
            GiftOrderListResult reqData = (GiftOrderListResult) req.getData();
            if (reqData.datas != null) {
                List<GiftOrderListResult.DatasBean.GiftordersBean> list = reqData.datas.giftorders;
                if (list != null && !list.isEmpty()) {
                    exchange_record_empty_iv.setVisibility(View.GONE);
                    loadingFooter.setSize(page, list.size());
                    if (page == 1) {
                        if (adapter == null) {
                            adapter = new GiftOrderListAdapter(list);
                            listView.setAdapter(adapter);
                        } else {
                            adapter.clear();
                            adapter.addAll(list);
                        }
                        listView.setSelection(0);
                    } else {
                        if (adapter != null) {
                            adapter.addAll(list);
                        }
                    }
                } else {
                    loadingFooter.setSize(page, 0);
                    if (page == 1) {
                        if (adapter != null) {
                            adapter.clear();
                        }
                        exchange_record_empty_iv.setVisibility(View.VISIBLE);
                    } else {
                        page--;
                    }
                }
            }
        }
    }


    class GiftOrderListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
        private List<GiftOrderListResult.DatasBean.GiftordersBean> giftorders;


        public GiftOrderListAdapter(List<GiftOrderListResult.DatasBean.GiftordersBean> giftorders) {
            this.giftorders = giftorders;
        }

        public void addAll(List<GiftOrderListResult.DatasBean.GiftordersBean> giftorders) {
            this.giftorders.addAll(giftorders);
            notifyDataSetChanged();
        }

        public void clear() {
            this.giftorders.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public int getGroupCount() {
            return giftorders.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return giftorders.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return giftorders.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return groupPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GiftOrderListResult.DatasBean.GiftordersBean giftordersBean = giftorders.get(groupPosition);
            if (convertView == null) {
                convertView = LayoutInflater.from(activity)
                        .inflate(R.layout.item_gift_order_list, null);
                convertView.setTag(new GroupViewHolder(convertView));
            }

            GroupViewHolder holder = (GroupViewHolder) convertView.getTag();

            if (groupPosition == 0) {
                holder.div_view.setVisibility(View.GONE);
            } else {
                holder.div_view.setVisibility(View.VISIBLE);
            }
            if (type == 2) {
                holder.item_gift_order_Indicator.setVisibility(View.INVISIBLE);
            }
            if (isExpanded) {
                holder.item_gift_order_Indicator.setImageResource(R.drawable.arrow_top_light_gary);
            } else {
                holder.item_gift_order_Indicator.setImageResource(R.drawable.arrow_bottom_light_gary);
            }

            if (giftordersBean != null) {

                holder.giftOrderTime.setText(StringUtil.checkStr(giftordersBean.dateCreated)
                        ? DateFormatUtils.convertTime(giftordersBean.dateCreated) : "");
                if (giftordersBean.orderStatus != null) {
                    holder.giftOrderDeliveryState.setText(StringUtil.checkStr(giftordersBean.orderStatus.value)
                            ? giftordersBean.orderStatus.value : "");
                }
                if (giftordersBean.gift != null) {
                    PicassoHelper.setImageRes(GiftOrderListFragment.this,giftordersBean.gift.thumbnail,holder.giftOrderImgIv);

                    holder.giftOrderNameIv.setText(StringUtil.checkStr(giftordersBean.gift.name)
                            ? giftordersBean.gift.name : "");
                    holder.giftOrderPriceIv.setText(StringUtil.checkStr(String.valueOf(giftordersBean.gift.points))
                            ? String.valueOf(giftordersBean.gift.points) : "");
                }
            }
            return convertView;
        }

        @Override
        public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(activity)
                        .inflate(R.layout.item_gift_order_list_child, null);
                convertView.setTag(new ChildViewHolder(convertView));
            }
            ChildViewHolder holder = (ChildViewHolder) convertView.getTag();


            GiftOrderListResult.DatasBean.GiftordersBean giftordersBean = giftorders.get(groupPosition);
            if (giftordersBean != null) {
                if (giftordersBean.deliveryType == 1) {
                    holder.item_gift_detail_ll.setVisibility(View.VISIBLE);
                    holder.item_gift_up_grade_ll.setVisibility(View.GONE);
                    //设置RSCInfo
                    holder.giftOrderDeliveryCode.setText(StringUtil.checkStr(giftordersBean.deliveryCode) ? giftordersBean.deliveryCode : "");
                    if (giftordersBean.RSCInfo != null) {
                        holder.selectStateName.setText(StringUtil.checkStr(giftordersBean.RSCInfo.companyName) ? giftordersBean.RSCInfo.companyName : "");
                        holder.selectStatePhone.setText(StringUtil.checkStr(giftordersBean.RSCInfo.RSCPhone) ? giftordersBean.RSCInfo.RSCPhone : "");
                        holder.selectStateAddress.setText(StringUtil.checkStr(giftordersBean.RSCInfo.RSCAddress) ? giftordersBean.RSCInfo.RSCAddress : "");
                    }
                    holder.selectStatePersonInfo.setText(giftordersBean.consigneeName + " " + giftordersBean.consigneePhone);

                } else {
                    holder.item_gift_detail_ll.setVisibility(View.GONE);
                    holder.item_gift_up_grade_ll.setVisibility(View.VISIBLE);
                }

            }
            return convertView;
        }

    }


    static class GroupViewHolder {
        @BindView(R.id.gift_order_time)
        TextView giftOrderTime;
        @BindView(R.id.gift_order_delivery_state)
        TextView giftOrderDeliveryState;
        @BindView(R.id.gift_order_img_iv)
        ImageView giftOrderImgIv;
        @BindView(R.id.gift_order_name_iv)
        TextView giftOrderNameIv;
        @BindView(R.id.gift_order_price_iv)
        TextView giftOrderPriceIv;
        @BindView(R.id.div_view)
        View div_view;
        @BindView(R.id.item_gift_order_Indicator)
        ImageView item_gift_order_Indicator;


        GroupViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ChildViewHolder {
        @BindView(R.id.gift_order_delivery_code)
        TextView giftOrderDeliveryCode;
        @BindView(R.id.select_state_name)
        TextView selectStateName;
        @BindView(R.id.select_state_address)
        TextView selectStateAddress;
        @BindView(R.id.select_state_phone)
        TextView selectStatePhone;
        @BindView(R.id.select_state_person_info)
        TextView selectStatePersonInfo;
        @BindView(R.id.item_gift_up_grade_ll)
        LinearLayout item_gift_up_grade_ll;
        @BindView(R.id.item_gift_detail_ll)
        LinearLayout item_gift_detail_ll;

        ChildViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
