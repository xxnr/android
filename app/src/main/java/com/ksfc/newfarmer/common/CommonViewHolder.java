package com.ksfc.newfarmer.common;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ksfc.newfarmer.utils.StringUtil;

/**
 * @author Stephen Huang
 */
public class CommonViewHolder {

    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;

    public CommonViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mPosition = position;
        this.mViews = new SparseArray<>();
        this.mConvertView = LayoutInflater.from(context).inflate(layoutId, null);
        mConvertView.setTag(this);
    }

    public static CommonViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int postion) {
        if (convertView == null) {
            return new CommonViewHolder(context, parent, layoutId, postion);
        } else {
            CommonViewHolder holder = (CommonViewHolder) convertView.getTag();
            holder.mPosition = postion;
            return holder;
        }
    }

    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 返回下标
     */
    public int getPosition() {
        return mPosition;
    }

    public View getConvertView() {
        return mConvertView;
    }

    public CommonViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(checkStr(text) ? text : "");
        return this;
    }

    // 判断字符串的合法性
    public boolean checkStr(String str) {
        if (null == str) {
            return false;
        }
        if ("".equals(str.trim())) {
            return false;
        }
        if ("null".equals(str)) {
            return false;
        }
        return true;
    }
}