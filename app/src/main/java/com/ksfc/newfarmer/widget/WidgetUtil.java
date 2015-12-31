package com.ksfc.newfarmer.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.yangentao.util.XLog;

/**
 * 控件相关的工具类
 *
 * @author Bruce.Wang
 */
public class WidgetUtil {

    final static String TAG = "WidgetUtil";

    /**
     * 解决Listview在scrollview中嵌套问题
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            XLog.wTag(TAG, "adapter have not seted of this listview.");
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem == null) {
                continue;
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static void setListViewHeightBasedOnChildren(PullToRefreshListView listView) {
        ListAdapter listAdapter = listView.getRefreshableView().getAdapter();
        if (listAdapter == null) {
            XLog.wTag(TAG, "adapter have not seted of this listview.");
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem == null) {
                continue;
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getRefreshableView().getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 解决Listview在scrollview中嵌套问题
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(GridView gridView) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            XLog.wTag(TAG, "adapter have not seted of this listview.");
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, gridView);
            if (listItem == null) {
                continue;
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight
                + (gridView.getHeight() * (listAdapter.getCount() - 1));
        gridView.setLayoutParams(params);
    }

}
