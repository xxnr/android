package com.ksfc.newfarmer.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ksfc.newfarmer.R;

/**
 * 底部加载更多   用法 1:调用构造初始化  2每次传入page 和size 3：到底部 判断 state =idle 刷新 改loading
 */
public class LoadingFooter {

    protected View mLoadingFooter;

    protected TextView mLoadingText;
    protected  TextView line_lift;
    protected  TextView line_right;
    protected State mState = State.Deal;

    private ProgressBar mProgress;
    private ListView listView;


    public enum State {
        Idle, TheEnd, Loading, Deal
    }

    public LoadingFooter(Context context, ListView listView) {
        mLoadingFooter = LayoutInflater.from(context).inflate(R.layout.foot_load_more, null);
        mLoadingFooter.setOnClickListener(null);
        mProgress = (ProgressBar) mLoadingFooter.findViewById(R.id.progressBar);
        mLoadingText = (TextView) mLoadingFooter.findViewById(R.id.foot_load_text);
        line_right = (TextView) mLoadingFooter.findViewById(R.id.line_right);
        line_lift = (TextView) mLoadingFooter.findViewById(R.id.line_lift);
        this.listView = listView;
        mState=State.Deal;
    }




    public View getView() {
        return mLoadingFooter;
    }

    public State getState() {
        return mState;
    }

    public void setText(String msg) {
        mLoadingText.setVisibility(View.VISIBLE);
        mLoadingText.setText(msg);
    }

    private void hideProgress() {
        addFooter();
        mLoadingText.setText("已到最后");
        mProgress.setVisibility(View.GONE);
        line_right.setVisibility(View.VISIBLE);
        line_lift.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        addFooter();
        mLoadingText.setText("正在载入");
        line_right.setVisibility(View.INVISIBLE);
        line_lift.setVisibility(View.INVISIBLE);
        mProgress.setVisibility(View.VISIBLE);
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getState()==State.Loading){
                    setState(State.Idle);
                }
            }
        },300);
    }

    private void showIdle(){
        addFooter();
        mLoadingText.setText("正在载入");
        line_right.setVisibility(View.INVISIBLE);
        line_lift.setVisibility(View.INVISIBLE);
        mProgress.setVisibility(View.VISIBLE);
    }

    private void removeFooter() {
        try {
            if (listView != null) {
                listView.removeFooterView(mLoadingFooter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addFooter() {
        try {
            if (listView != null && listView.getFooterViewsCount() == 0) {
                listView.addFooterView(mLoadingFooter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSize(int page, int size) {
        if (size == 20) {
            setState(State.Idle);
        } else if (size < 10) {
            if (page == 1) {
                setState(State.Deal);
            } else {
                setState(State.TheEnd);
            }
        } else {
            setState(State.TheEnd);
        }
    }


    public void setState(State status) {
        mState = status;
        switch (status) {
            case Loading:
                showLoading();
                break;
            case TheEnd:
                hideProgress();
                break;
            case Idle:
                showIdle();
                break;
            case Deal:
                removeFooter();
                break;
            default:
                mLoadingFooter.setVisibility(View.GONE);
                break;
        }
    }
}
