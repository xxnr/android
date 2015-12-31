package com.ksfc.newfarmer.widget;

import com.ksfc.newfarmer.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 设置的条目，可用于显示键值
 * 
 * @author Bruce.Wang
 * 
 */
@SuppressLint("NewApi")
public class SettingItemView extends LinearLayout {

	private boolean showLeftImage;
	private boolean showRightImage;
	private boolean showValue;
	private String key;
	private String value;
	private int leftRes;
	private ImageView iv_left;
	private ImageView iv_more;
	private TextView tv_key;
	private TextView tv_value;

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttrs(context, attrs);
		initViews(context);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttrs(context, attrs);
		initViews(context);
	}

	public SettingItemView(Context context) {
		super(context);
	}

	/**
	 * 初始化自定义属性
	 * 
	 * @param context
	 * @param attrs
	 */
	private void initAttrs(Context context, AttributeSet attrs) {
		// 加载自定义属性
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.setting_item_style);
		showLeftImage = mTypedArray.getBoolean(
				R.styleable.setting_item_style_showleftimage, false);
		showRightImage = mTypedArray.getBoolean(
				R.styleable.setting_item_style_showrightimage, false);
		showValue = mTypedArray.getBoolean(
				R.styleable.setting_item_style_showvalue, false);
		leftRes = mTypedArray.getResourceId(
				R.styleable.setting_item_style_leftimage,
				R.drawable.ic_launcher);
		key = mTypedArray.getString(R.styleable.setting_item_style_key);
		if (key == null) {
			key = "";
		}
		value = mTypedArray.getString(R.styleable.setting_item_style_value);
		if (value == null) {
			value = "";
		}
		mTypedArray.recycle();
	}

	/**
	 * 初始化显示
	 * 
	 * @param context
	 */
	private void initViews(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_setting_item, this);
		iv_left = (ImageView) findViewById(R.id.iv_left);
		iv_more = (ImageView) findViewById(R.id.iv_more);
		tv_key = (TextView) findViewById(R.id.tv_key);
		tv_value = (TextView) findViewById(R.id.tv_value);

		iv_left.setVisibility(showLeftImage ? View.VISIBLE : View.GONE);
		iv_more.setVisibility(showRightImage ? View.VISIBLE : View.GONE);
		tv_value.setVisibility(showValue ? View.VISIBLE : View.GONE);
		iv_left.setImageResource(leftRes);
		tv_key.setText(key);
		tv_value.setText(value);
	}

	/**
	 * 设置键名称
	 * 
	 * @param key
	 */
	public void setKey(String key) {
		if (key != null) {
			this.key = key;
			tv_key.setText(key);
		}
	}

	/**
	 * 设置值名称
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		if (value != null) {
			this.value = value;
			tv_value.setText(value);
		}
	}

	/**
	 * 显示左图片
	 */
	public void showLeftImage() {
		showLeftImage = true;
		setViewVisible(iv_left, View.VISIBLE);
	}

	/**
	 * 隐藏左图片
	 */
	public void hideImage() {
		showLeftImage = false;
		setViewVisible(iv_left, View.GONE);
	}

	/**
	 * 显示右图片
	 */
	public void showRightImage() {
		showRightImage = true;
		setViewVisible(iv_more, View.VISIBLE);
	}

	/**
	 * 隐藏右图片
	 */
	public void hideRightImage() {
		showRightImage = false;
		setViewVisible(iv_more, View.GONE);
	}

	/**
	 * 显示值控件
	 */
	public void showValue() {
		showRightImage = true;
		setViewVisible(tv_value, View.VISIBLE);
	}

	/**
	 * 隐藏值控件
	 */
	public void hideValue() {
		showRightImage = false;
		setViewVisible(tv_value, View.GONE);
	}

	/**
	 * 设置控件显示
	 * 
	 * @param view
	 * @param visible
	 */
	private void setViewVisible(View view, int visible) {
		view.setVisibility(visible);
	}

}
