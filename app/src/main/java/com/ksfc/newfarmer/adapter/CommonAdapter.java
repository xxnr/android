package com.ksfc.newfarmer.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ksfc.newfarmer.Push.App;

/**
 * @param <T>
 * @author Stephen Huang
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    protected Context mContext;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    protected int layoutId;

    public CommonAdapter(Context context, List<T> data, int layoutId) {
        if (context == null) {
            context = App.getApp().getApplicationContext();
        }
        this.mContext = context;
        try {
            mInflater = LayoutInflater.from(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mDatas = data;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        if (mDatas != null) {
            return mDatas.size() > 0 ? mDatas.size() : 0;
        }
        return 0;
    }

    @Override
    public T getItem(int position) {
        if (mDatas != null) {
            return mDatas.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addAll(List<T> data) {
        if (mDatas != null) {
            mDatas.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        if (mDatas != null) {
            mDatas.clear();
            notifyDataSetChanged();
        }
    }

    public List<T> getData() {
        return mDatas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder holder = CommonViewHolder.get(mContext, convertView, parent, layoutId, position);
        convert(holder, mDatas.get(position));
        return holder.getConvertView();
    }

    public abstract void convert(CommonViewHolder holder, T t);

}