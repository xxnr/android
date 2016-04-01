package com.ksfc.newfarmer.activitys;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.adapter.CommonAdapter;
import com.ksfc.newfarmer.adapter.CommonViewHolder;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.InformationResult;
import com.ksfc.newfarmer.protocol.beans.InformationResult.DatasEntity.ItemsEntity;
import com.ksfc.newfarmer.utils.DateFormatUtils;
import com.ksfc.newfarmer.utils.ExpandViewTouch;
import com.ksfc.newfarmer.utils.PullToRefreshUtils;
import com.ksfc.newfarmer.utils.ScreenUtil;

import com.nostra13.universalimageloader.core.ImageLoader;

public class NewFarmerInfomation extends BaseActivity implements PullToRefreshBase.OnRefreshListener2, AbsListView.OnScrollListener {

    private PullToRefreshListView listView;
    private List<ItemsEntity> items;
    private InformationAdapter adapter;
    private int page = 1;
    private ImageView return_top;

    @Override
    public int getLayout() {
        return R.layout.information_newfarmer_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        listView = (PullToRefreshListView) findViewById(R.id.information_listView);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        listView.setOnScrollListener(this);


        //设置刷新的文字
        PullToRefreshUtils.setFreshText(listView);
        return_top = (ImageView) findViewById(R.id.return_top);
        //扩大点击区域
        ExpandViewTouch.expandViewTouchDelegate(return_top, 100, 100, 100, 100);
        setViewClick(R.id.return_top);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ItemsEntity entity = (ItemsEntity) adapter.getItem(position);
                Intent intent = new Intent(NewFarmerInfomation.this,
                        ArticleActivity.class);
                if (!TextUtils.isEmpty(entity.url)) {
                    intent.putExtra("articleUrl", entity.url);
                    intent.putExtra("shareUrl", entity.shareurl);
                    intent.putExtra("urlImage", entity.image);
                    intent.putExtra("urlTitle", entity.title);
                    intent.putExtra("newsAbstract", entity.newsabstract);
                    startActivity(intent);
                }

            }
        });
        setTitle("新农资讯");
        hideLeft();
        items = new ArrayList<>();
        showProgressDialog();
        getData();
    }

    private void getData() {

        RequestParams params = new RequestParams();
        params.put("max", "20");
        params.put("page", String.valueOf(page));
        execApi(ApiType.GET_INFORMATION.setMethod(ApiType.RequestMethod.GET), params);

    }

    @Override
    public void OnViewClick(View v) {
        if (v.getId() == R.id.return_top) {
            listView.getRefreshableView().setSelection(0);
        }
    }

    @Override
    public void onResponsed(Request req) {
        listView.onRefreshComplete();
        disMissDialog();
        if (req.getApi() == ApiType.GET_INFORMATION) {
            if (req.getData().getStatus().equals("1000")) {
                InformationResult data = (InformationResult) req.getData();
                if (data.datas!= null
                        && data.datas.items != null
                        && data.datas.items.size() > 0) {
                    List<ItemsEntity> list = data.datas.items;
                    items.addAll(list);
                    if (adapter == null) {
                        adapter = new InformationAdapter(this, items);
                        listView.setAdapter(adapter);
                    }
                    adapter.notifyDataSetChanged();

                } else {
                    showToast("没有更多资讯");
                    if (page != 1) {
                        page--;
                    }
                }
            }

        }
    }

    //上拉，下拉刷新
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        PullToRefreshUtils.setFreshClose(refreshView);
        items.clear();
        page = 1;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        PullToRefreshUtils.setFreshClose(refreshView);
        page++;
        getData();
    }

    //监听listView滚动是否出现return_top
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            // 当不滚动时
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 是当屏幕停止滚动时
                // 判断滚动到底部
                if (listView.getRefreshableView().getLastVisiblePosition() ==
                        (listView.getRefreshableView().getCount() - 1)) {
                    return_top.setVisibility(View.VISIBLE);
                }
                // 判断滚动到顶部
                if (listView.getRefreshableView().getFirstVisiblePosition() == 0) {
                    return_top.setVisibility(View.GONE);
                }
                break;
        }


    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        // 当开始滑动且ListView底部的Y轴点超出屏幕最大范围时，显示或隐藏顶部按钮
        if (getScrollY() >= ScreenUtil
                .getScreenHeight(NewFarmerInfomation.this)) {
            return_top.setVisibility(View.VISIBLE);
        }
    }

    //获得lisView的滚动高度

    public int getScrollY() {
        View c = listView.getRefreshableView().getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = listView.getRefreshableView().getFirstVisiblePosition();
        int top = c.getTop();
        return -top + firstVisiblePosition * c.getHeight();
    }

    class InformationAdapter extends CommonAdapter<ItemsEntity> {


        public InformationAdapter(Context context, List<ItemsEntity> data) {
            super(context, data, R.layout.item_information_layout);
        }

        @Override
        public void convert(CommonViewHolder holder, ItemsEntity itemsEntity) {
            if (itemsEntity != null) {


                if (!TextUtils.isEmpty(itemsEntity.image)) {
                    ImageLoader.getInstance().displayImage(itemsEntity.image,
                            ((ImageView) holder.getView(R.id.information_image)));
                }
                holder.setText(R.id.information_title, itemsEntity.title);
                //格式化时间
                String time = DateFormatUtils.convertTime(itemsEntity.datecreated);
                holder.setText(R.id.information_time, time);

            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!(adapter != null && adapter.getCount() > 0)) {
            showProgressDialog();
            items.clear();
            page = 1;
            getData();
        }
    }
}
