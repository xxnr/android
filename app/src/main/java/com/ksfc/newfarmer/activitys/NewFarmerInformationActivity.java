package com.ksfc.newfarmer.activitys;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.common.CommonAdapter;
import com.ksfc.newfarmer.common.CommonViewHolder;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.beans.InformationResult;
import com.ksfc.newfarmer.beans.InformationResult.DatasEntity.ItemsEntity;
import com.ksfc.newfarmer.utils.DateFormatUtils;
import com.ksfc.newfarmer.utils.ExpandViewTouch;
import com.ksfc.newfarmer.common.PullToRefreshHelper;
import com.ksfc.newfarmer.utils.ScreenUtil;

import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.Utils;
import com.ksfc.newfarmer.widget.LoadingFooter;
import com.squareup.picasso.Picasso;

public class NewFarmerInformationActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener {

    private PullToRefreshListView listView;
    private InformationAdapter adapter;
    private int page = 1;
    private ImageView return_top;

    private LoadingFooter mLoadingFooter;


    @Override
    public int getLayout() {
        return R.layout.activity_information;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {

        setTitle("新农资讯");
        hideLeft();

        listView = (PullToRefreshListView) findViewById(R.id.information_listView);
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.setOnRefreshListener(this);
        listView.setOnScrollListener(mOnScrollListener);

        mLoadingFooter = new LoadingFooter(this, listView.getRefreshableView());

        //设置刷新的文字
        PullToRefreshHelper.setFreshText(listView);
        return_top = (ImageView) findViewById(R.id.return_top);
        //扩大点击区域
        ExpandViewTouch.expandViewTouchDelegate(return_top, 100, 100, 100, 100);
        setViewClick(R.id.return_top);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ItemsEntity entity = adapter.getItem(position);
                if (entity!=null){
                    Intent intent = ArticleActivity.getCallingIntent(NewFarmerInformationActivity.this, entity);
                    startActivity(intent);
                }
            }
        });

    }

    private void getData() {
        RequestParams params = new RequestParams();
        params.put("max", 20);
        params.put("page", page);
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
        if (req.getApi() == ApiType.GET_INFORMATION) {
            if (req.getData().getStatus().equals("1000")) {

                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                InformationResult data = (InformationResult) req.getData();
                if (data.datas != null) {
                    List<ItemsEntity> list = data.datas.items;
                    if (list != null && !list.isEmpty()) {
                        mLoadingFooter.setSize(page, list.size());
                        if (page == 1) {
                            if (adapter == null) {
                                adapter = new InformationAdapter(this, list);
                                listView.setAdapter(adapter);
                            } else {
                                adapter.clear();
                                adapter.addAll(list);
                            }
                        } else {
                            if (adapter != null) {
                                adapter.addAll(list);
                            }
                        }
                    } else {
                        mLoadingFooter.setSize(page, 0);

                        if (page == 1) {
                            if (adapter != null) {
                                adapter.clear();
                            }
                        } else {
                            page--;
                        }

                    }

                }

            }
        }
    }


    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        PullToRefreshHelper.setFreshClose(refreshView);
        page = 1;
        getData();
    }


    class InformationAdapter extends CommonAdapter<ItemsEntity> {

        public InformationAdapter(Context context, List<ItemsEntity> data) {
            super(context, data, R.layout.item_information);
        }

        @Override
        public void convert(CommonViewHolder holder, ItemsEntity itemsEntity) {
            if (itemsEntity != null) {
                //图片
                if (StringUtil.checkStr(itemsEntity.image)){
                    Picasso.with(NewFarmerInformationActivity.this)
                            .load(itemsEntity.image)
                            .placeholder(R.drawable.zhanweitu_wide)
                            .config(Bitmap.Config.RGB_565)
                            .error(R.drawable.error)
                            .resize(Utils.dip2px(NewFarmerInformationActivity.this,100),Utils.dip2px(NewFarmerInformationActivity.this,75))
                            .into((ImageView) holder.getView(R.id.information_image));
                }

                holder.setText(R.id.information_title, itemsEntity.title);
                //格式化时间
                String time = DateFormatUtils.convertTime(itemsEntity.datecreated);
                holder.setText(R.id.information_time, time);

            }
        }

    }


    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

            switch (scrollState) {
                // 当不滚动时
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 是当屏幕停止滚动时
                    // 判断滚动到底部
                    if (listView.getRefreshableView().getLastVisiblePosition() ==
                            (listView.getRefreshableView().getCount() - 1)) {
                        return_top.setVisibility(View.VISIBLE);

                        //加载更多
                        if (mLoadingFooter.getState() == LoadingFooter.State.Idle) {
                            mLoadingFooter.setState(LoadingFooter.State.Loading);
                            page++;
                            getData();
                        }
                    }
                    // 判断滚动到顶部
                    if (listView.getRefreshableView().getFirstVisiblePosition() == 0) {
                        return_top.setVisibility(View.GONE);
                    }
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            // 当开始滑动且ListView底部的Y轴点超出屏幕最大范围时，显示或隐藏顶部按钮
            if (getScrollY() >= ScreenUtil
                    .getScreenHeight(NewFarmerInformationActivity.this)) {
                return_top.setVisibility(View.VISIBLE);
            }
        }


    };


    @Override
    protected void onResume() {
        super.onResume();
        if (!(adapter != null && adapter.getCount() > 0)) {
            showProgressDialog();
            page = 1;
            getData();
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

}
