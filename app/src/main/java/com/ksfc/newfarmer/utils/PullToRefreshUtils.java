package com.ksfc.newfarmer.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.yangentao.util.app.App;

/**
 * Created by HePeng on 2015/12/18.
 */
public class PullToRefreshUtils {

    public static void setFreshText(PullToRefreshListView listView) {
        //设置刷新的文字
        ILoadingLayout startLabels = listView
                .getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在载入...");// 刷新时
        startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = listView.getLoadingLayoutProxy(
                false, true);
        endLabels.setPullLabel("上拉刷新...");// 刚下拉时，显示的提示
        endLabels.setRefreshingLabel("正在载入...");// 刷新时
        endLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示
    }


    public static void setFreshClose(final PullToRefreshBase View) {
        //设置刷新的文字
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (View.isRefreshing()) {
                    View.onRefreshComplete();
                }
            }
        }, 12 * 1000);
    }

}
