package com.ksfc.newfarmer.activitys;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.adapter.CommonAdapter;
import com.ksfc.newfarmer.adapter.CommonViewHolder;
import com.ksfc.newfarmer.common.CommonFunction;
import com.ksfc.newfarmer.common.LoadMoreOnsrcollListener;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.RemoteApi;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.IntegralGetResult;
import com.ksfc.newfarmer.protocol.beans.PointLogsResult;
import com.ksfc.newfarmer.protocol.beans.PointResult;
import com.ksfc.newfarmer.utils.DateFormatUtils;
import com.ksfc.newfarmer.utils.PullToRefreshUtils;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.LoadingFooter;
import com.squareup.picasso.Picasso;

import net.yangentao.util.msg.MsgCenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by CAI on 2016/6/17.
 */
public class MyIntegralActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener {

    @BindView(R.id.unSwipeListView)
    PullToRefreshListView unSwipeListView;
    @BindView(R.id.content_empty_ll)
    LinearLayout content_empty_ll;

    private int page = 1;
    private PointAdapter adapter;


    private LoadingFooter loadingFooter;
    private LoadMoreOnsrcollListener moreOnsrcollListener = new LoadMoreOnsrcollListener() {
        @Override
        public void loadMore() {
            //加载更多
            if (loadingFooter.getState() == LoadingFooter.State.Idle) {
                loadingFooter.setState(LoadingFooter.State.Loading);
                page++;
                RemoteApi.getPointsLogs(MyIntegralActivity.this, page);
            }
        }
    };
    private ViewHolder holder;
    private int score;


    @Override
    public int getLayout() {
        return R.layout.my_integral_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setTitle("我的积分");

        View headView = getLayoutInflater().inflate(R.layout.head_my_integral_layout, null);
        holder = new ViewHolder(headView);

        unSwipeListView.setOnScrollListener(moreOnsrcollListener);
        loadingFooter = new LoadingFooter(this, unSwipeListView.getRefreshableView());

        unSwipeListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        unSwipeListView.setOnRefreshListener(this);
        unSwipeListView.getRefreshableView().addHeaderView(headView);
        unSwipeListView.setAdapter(null);

        holder.signButtonTv.setOnClickListener(this);
        holder.contentLl.setVisibility(View.GONE);
        content_empty_ll.setVisibility(View.GONE);

        showProgressDialog();
        RemoteApi.getIntegral(this);
        RemoteApi.getPointsLogs(this, page);


    }


    /**
     * 签到
     */
    private void sign() {
        showProgressDialog("请稍后...");
        RequestParams params = new RequestParams();
        if (isLogin()) {
            params.put("userId", Store.User.queryMe().userid);
        }
        execApi(ApiType.SIGN_IN_POINT, params);
    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.sign_button_tv:
                sign();
                break;
        }
    }

    @Override
    public void onResponsed(Request req) {
        unSwipeListView.onRefreshComplete();
        if (req.getApi() == ApiType.GET_INTEGRAL) {
            IntegralGetResult data = (IntegralGetResult) req.getData();
            if (data.datas != null) {
                //积分
                score = data.datas.score;
                holder.myIntegralCountTv.setText(score + "");
                //签到msg
                if (data.datas.sign != null) {
                    int consecutiveTimes = data.datas.sign.consecutiveTimes;
                    //签到时间大于今天
                    boolean isSign = data.datas.sign.signed == 1;
                    // 签到后不能再次签到
                    holder.signButtonTv.setEnabled(!isSign);
                    holder.signButtonTv.setText(isSign ? "已签到" : "签到");
                    setSignMsg(holder.signDescriptionTv, isSign, consecutiveTimes);
                    //加载图片
                    if (StringUtil.checkStr(data.datas.sign.large_imgUrl)) {
                        Picasso.with(MyIntegralActivity.this).load(MsgID.IP + data.datas.sign.large_imgUrl).into(holder.signStateImgIv);
                    }
                } else {
                    setSignMsg(holder.signDescriptionTv, false, 0);
                }
            }
        } else if (req.getApi() == ApiType.GET_POINTS_LOGS) {

            PointLogsResult reqData = (PointLogsResult) req.getData();
            if (reqData.datas != null) {
                List<PointLogsResult.DatasBean.PointslogsBean> list = reqData.datas.pointslogs;
                if (list != null && !list.isEmpty()) {
                    loadingFooter.setSize(page, list.size());
                    holder.contentLl.setVisibility(View.VISIBLE);
                    if (page == 1) {
                        if (adapter == null) {
                            adapter = new PointAdapter(MyIntegralActivity.this, list);
                            unSwipeListView.setAdapter(adapter);
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
                    loadingFooter.setSize(page, 0);
                    if (page == 1) {
                        //积分为0时的布局
                        if (score == 0 && holder.signButtonTv.isEnabled()) {
                            content_empty_ll.setVisibility(View.VISIBLE);
                        } else {
                            content_empty_ll.setVisibility(View.GONE);
                        }
                        if (adapter != null) {
                            adapter.clear();
                            holder.contentLl.setVisibility(View.GONE);
                        }
                    } else {
                        page--;
                    }

                }

            }

        } else if (ApiType.SIGN_IN_POINT == req.getApi()) {
            MsgCenter.fireNull(MsgID.IS_Signed);
            //签到成功后，重新请求数据
            PointResult reqData = (PointResult) req.getData();
            //展示签到成功的页面
            CommonFunction.showSuccess(this, reqData);
            RemoteApi.getIntegral(this);
            page = 1;
            RemoteApi.getPointsLogs(this, page);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        PullToRefreshUtils.setFreshClose(refreshView);
        page = 1;
        RemoteApi.getPointsLogs(this, page);
    }


    class PointAdapter extends CommonAdapter<PointLogsResult.DatasBean.PointslogsBean> {

        public PointAdapter(Context context, List<PointLogsResult.DatasBean.PointslogsBean> data) {
            super(context, data, R.layout.item_point_logs_layout);
        }

        @Override
        public void convert(CommonViewHolder holder, PointLogsResult.DatasBean.PointslogsBean pointslogsBean) {
            if (pointslogsBean != null) {
                if (pointslogsBean.event != null) {
                    holder.setText(R.id.point_log_title_tv, pointslogsBean.event.name);
                }
                TextView point_log_point_tv = holder.getView(R.id.point_log_point_tv);

                if (pointslogsBean.points >= 0) {
                    point_log_point_tv.setTextColor(getResources().getColor(R.color.orange));
                    holder.setText(R.id.point_log_point_tv, "+" + pointslogsBean.points);
                } else {
                    point_log_point_tv.setTextColor(getResources().getColor(R.color.deep_gray));
                    holder.setText(R.id.point_log_point_tv, String.valueOf(pointslogsBean.points));
                }

                TextView point_log_text_tv = holder.getView(R.id.point_log_text_tv);
                if (StringUtil.checkStr(pointslogsBean.description)) {
                    point_log_text_tv.setVisibility(View.VISIBLE);
                    holder.setText(R.id.point_log_text_tv, pointslogsBean.description);
                } else {
                    point_log_text_tv.setVisibility(View.GONE);
                }

                if (StringUtil.checkStr(pointslogsBean.date)) {
                    holder.setText(R.id.point_log_time_tv, DateFormatUtils.convertTime(pointslogsBean.date));
                } else {
                    holder.setText(R.id.point_log_time_tv, "");
                }

            }
        }
    }

    /**
     * 展示签到成功message
     *
     * @param textView
     * @param isSign
     * @param times
     */
    public void setSignMsg(TextView textView, boolean isSign, int times) {
        if (isSign) {
            String str = "已连续签到" + times + "天" + "，明天再来呦";
            SpannableStringBuilder style = new SpannableStringBuilder(str);
            style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange)), str.indexOf(String.valueOf(times)), str.indexOf(String.valueOf(times)) + String.valueOf(times).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(style);//将其添加到tv中
        } else {
            if (times > 0) {
                String str = "已连续签到" + times + "天" + "，继续签到吧";
                SpannableStringBuilder style = new SpannableStringBuilder(str);
                style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange)), str.indexOf(String.valueOf(times)), str.indexOf(String.valueOf(times)) + String.valueOf(times).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(style);
            } else {
                textView.setText("连续签到获取更多积分");
            }
        }
    }


    static class ViewHolder {
        @BindView(R.id.my_integral_count_tv)
        TextView myIntegralCountTv;
        @BindView(R.id.sign_button_tv)
        TextView signButtonTv;
        @BindView(R.id.sign_state_img_iv)
        ImageView signStateImgIv;
        @BindView(R.id.sign_description_tv)
        TextView signDescriptionTv;
        @BindView(R.id.content_ll)
        LinearLayout contentLl;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
