package net.yangentao.util;

import java.util.List;

import android.widget.BaseAdapter;

public abstract class SimpleBaseAdapter<T> extends BaseAdapter {
	private List<T> items;

	/**
	 * 会自动调用notifyDataSetChanged(), 因此,必须在主线程调用
	 * 
	 * @param items
	 */
	public void setItems(List<T> items) {
		this.items = items;
		notifyDataSetChanged();
	}

	/**
	 * 直接返回当前的列表引用, 对返回值的增删改会影响到Adapter, 如果有增删改的需求, 应该在主线程执行,
	 * 并且调用notifyDataSetChanged();
	 * 
	 * @return
	 */
	public List<T> getItems() {
		return items;
	}

	@Override
	public int getCount() {
		if (items == null) {
			return 0;
		}
		return items.size();
	}

	@Override
	public T getItem(int position) {
		if (items == null) {
			return null;
		}
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
