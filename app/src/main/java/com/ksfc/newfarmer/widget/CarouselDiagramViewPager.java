package com.ksfc.newfarmer.widget;

import java.util.ArrayList;
import java.util.List;

import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.beans.HomeImageResult.Rows;
import com.ksfc.newfarmer.utils.BaseTools;
import com.ksfc.newfarmer.utils.ImageLoaderUtils;
import com.ksfc.newfarmer.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 自定义的可滚动的viewpager
 */
@SuppressLint({ "HandlerLeak", "ViewConstructor" })
public class CarouselDiagramViewPager extends ViewPager {

	private Context context;
	// 可滚动viewpager的图片标题
	// private TextView top_news_title;
	// 标题，图片，点的列表容器
	// private List<String> titleList;
	private List<Rows> carouselDiagramList;
	private List<View> dotList;
	// 运行任务对象
	private RunnableTask runnableTask;
	// 轮播图的数据适配器
	private MyPagerAdapter adapter;
	// 轮播图的当前图片位置
	private int currentPosition;
	// 手指按在轮播图上时的位置
	private int downX;
	private int downY;

	// 返回的视图中的控件
	private View layout_roll_view;
	private LinearLayout ll_viewpager_container;
	private LinearLayout ll_dots;

	// viewpager中一个页面(条目)被点击的监听器
	private OnRollViewPagerItemClickListener itemClickListener;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 设置当前页图片，currentPosition数值的更新在run()方法中
			CarouselDiagramViewPager.this.setCurrentItem(currentPosition);
			// 保持一直去滚动
			startRoll();
		};
	};

	public CarouselDiagramViewPager(Context context,
			List<Rows> carourseDiagramList) {
		super(context);
		// 初始化数据
		this.context = context;
		this.carouselDiagramList = carourseDiagramList;
		initData();
	}

	public CarouselDiagramViewPager(Context context,
			List<Rows> carourseDiagramList,
			OnRollViewPagerItemClickListener itemClickListener) {
		super(context);
		// 初始化数据
		this.context = context;
		this.carouselDiagramList = carourseDiagramList;
		// 初始化图片点击监听对象
		this.itemClickListener = itemClickListener;
		initData();
	}

	private void initData() {
		// 初始化可运行任务对象
		runnableTask = new RunnableTask();
		// 设置图片变化的监听事件
		this.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// 如果传入的imageUrlList为空或者大小为0，则不再进行后续操作
				if (carouselDiagramList == null
						|| carouselDiagramList.size() == 0) {
					return;
				}
				// 当使用手指滑动viewpager时，停留在哪个位置就将currentPosition设置为哪个位置，这样松开手后就可以以当前位置开始滚动了。
				currentPosition = position;
				// 根据当前图片更新标题和标志
				// top_news_title.setText(titleList.get(position));
				// 将positon的值从21亿中间的某个值转化为0-imageUrlList.size()之间的某个值，这样才能找到对应的资源
				position = position % carouselDiagramList.size();
				for (int i = 0; i < carouselDiagramList.size(); i++) {
					if (position == i) {
						CarouselDiagramViewPager.this.dotList.get(i)
								.setBackgroundResource(
										R.drawable.dot_focus_green);
					} else {
						CarouselDiagramViewPager.this.dotList.get(i)
								.setBackgroundResource(R.drawable.dot_unfocus_trans);
					}
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

	}

	// public void initTitle(List<String> titleList, TextView top_news_title) {
	// this.titleList = titleList;
	// this.top_news_title = top_news_title;
	// //设置第一次进入页面时的图片标题信息
	// if(titleList != null && titleList.size() > 0 && top_news_title != null){
	// top_news_title.setText(titleList.get(0));
	// }
	// }

	public void initDot(LinearLayout ll_dots) {
		// 清除布局中所有视图对象
		ll_dots.removeAllViews();
		dotList = new ArrayList<View>();
		// 将所有的点加入到布局文件对象中
		for (int i = 0; i < carouselDiagramList.size(); i++) {
			View view = new View(context);
			if (i == 0) {
				view.setBackgroundResource(R.drawable.dot_focus_green);
			} else {
				view.setBackgroundResource(R.drawable.dot_unfocus_trans);
			}
			// 设置dot的大小和间距
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					Utils.dip2px(context, 8), Utils.dip2px(context, 8));
			// 最后一个点右侧不填充4个像素
			if (i == (carouselDiagramList.size() - 1)) {
				params.setMargins(Utils.dip2px(context, 4), 0, 0, 0);
			} else {
				params.setMargins(Utils.dip2px(context, 4), 0,
						Utils.dip2px(context, 4), 0);
			}
			view.setLayoutParams(params);
			// 将dot加入线性布局对象中
			ll_dots.addView(view);
			// 将点对象加入到容器中，当图片改变时，可以通过点的列表改变点的指示状态
			dotList.add(view);
		}
	}

	public void startRoll() {
		if (adapter == null) {
			adapter = new MyPagerAdapter();
			this.setAdapter(adapter);
		} else {
			// 重新设置数据适配器，getCount--->instantiateItem
			// adapter.notifyDataSetChanged();
		}
		// 调用任务run方法，更新当前页面索引，向handler发送消息，设置当前当前图片，进行轮播图片
		handler.postDelayed(runnableTask, 3000);
	}

	public void stopRoll() {
		// 移除当前handler中所有维护的任务
		handler.removeCallbacksAndMessages(null);
	}

	class RunnableTask implements Runnable {

		@Override
		public void run() {
			// 将当前图片索引位置值转化为下一页的索引值
			currentPosition++;
			// 向handler发送一条空信息
			handler.obtainMessage().sendToTarget();
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 请求外层view不要拦截该viewpager的滑动事件
			this.getParent().requestDisallowInterceptTouchEvent(true);
			// 记录当前按下的点坐标
			downX = (int) ev.getX();
			downY = (int) ev.getY();
			stopRoll();
			break;
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) ev.getX();
			int moveY = (int) ev.getY();
			if (Math.abs(moveX - downX) > Math.abs(moveY - downY)) {
				// 水平方向位移大于竖直方向位移，请求父view不要拦截该viewpager的左右滑动操作
				this.getParent().requestDisallowInterceptTouchEvent(true);
			} else {
				// 竖直方向位移大于水平方向位移，请求父view拦截该viewpager的左右滑动操作
				this.getParent().requestDisallowInterceptTouchEvent(false);
			}
			break;
		case MotionEvent.ACTION_UP:
			startRoll();
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	// 从界面中移出去后调用的方法
	@Override
	protected void onDetachedFromWindow() {
		// 移除当前handler中所有维护的任务
		handler.removeCallbacksAndMessages(null);
		super.onDetachedFromWindow();
	}

	// 轮播图的数据适配器
	class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return carouselDiagramList.size() * 20000;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// 如果传入的imageUrlList为空或者大小为0，则不再进行后续操作
			if (carouselDiagramList == null || carouselDiagramList.size() == 0) {
				return null;
			}
			// 将positon的值从21亿中间的某个值转化为0-imageUrlList.size()之间的某个值，这样才能找到对应的资源
			final int newPosition = position % carouselDiagramList.size();
			// 拿到当前轮播图对象
			final Rows carouselDiagram = carouselDiagramList.get(newPosition);
			// 将轮播图的单个图的布局文件转化为view对象
			View view = View.inflate(context, R.layout.item_poster, null);
			// 绑定控件
			ImageView imageView = (ImageView) view.findViewById(R.id.iv);
			// 使用BitmapUtil通过图片url加载出图片，设置进imageView中
			ImageLoader.getInstance().displayImage(MsgID.IP + carouselDiagram.imgUrl, imageView,
					ImageLoaderUtils.buildImageOptionsBanner(context));
			// 将该图片加入viewpager中
			container.addView(view);
			// 设置该图片的点击监听事件
			view.setOnTouchListener(new OnTouchListener() {

				private int downX;
				private long downTime;

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						// 当鼠标点在图片上时，取消handler所有任务，即停止轮播图的自动滚动
						stopRoll();
						downX = (int) event.getX();
						downTime = System.currentTimeMillis();
						break;
					case MotionEvent.ACTION_UP:
						// 当鼠标离开图片时，判断是触摸操作还是点击事件
						int upX = (int) event.getX();
						long upTime = System.currentTimeMillis();
						// 如果按下和离开在一个位置，切间隔小于500ms,即为点击事件
						if (downX == upX && upTime - downTime < 500) {
							// 确定为点击事件，要跳转到详情页面
							if (itemClickListener != null) {
								itemClickListener.click(carouselDiagram);
							}
						}
						break;
					case MotionEvent.ACTION_CANCEL:
						// viewpager和内部嵌套的view的事件交互规则
						// 1，按下事件作用在内部view上
						// 2,当滑动稍许距离，加速度未达到一定值时，对应滑动事件作用在view身上
						// 3，当滑动达到一定距离，并且加速度达到一定值，内部的view触发cancel事件(而不会触发view的up事件)，然后将滑动的事件返还给父控件(ViewPager)
						// startRoll();
						break;
					}
					// 返回true表示当前控件响应该事件，不在进行事件分发
					return true;
				}
			});
			return view;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

	// 得到轮播图的视图对象
	public View getView() {
		// 如果传入的imageUrlList为空或者大小为0，则返回一个空的view
		if (carouselDiagramList == null || carouselDiagramList.size() == 0) {
			return new View(context);
		}
		// 将轮播图的布局文件转化为View对象
		layout_roll_view = View.inflate(context,
				R.layout.layout_roll_viewpager, null);
		ll_viewpager_container = (LinearLayout) layout_roll_view
				.findViewById(R.id.ll_viewpager_container);
		ll_dots = (LinearLayout) layout_roll_view.findViewById(R.id.ll_dots);
		// 初始化可滚动的viewpager的相关信息(图片，标题，标志dot)
		initDot(ll_dots);

		// 清除顶部布局视图，将轮播图片添加到头部布局文件中
		ll_viewpager_container.removeAllViews();
		ll_viewpager_container.addView(this);

		// 开始滚动轮播图
		startRoll();
		// 让页面从21亿之间的值开始，可以进行左右循环滑动，不过要确保第一次开始的数是imageList.size()的倍数，只有把取模后的值减去即可
		// currentPosition = Integer.MAX_VALUE/2 - (Integer.MAX_VALUE/2 %
		// carouselDiagramList.size());
		currentPosition = carouselDiagramList.size() * 10000;
		this.setCurrentItem(currentPosition);

		return layout_roll_view;
	}

	// 根据轮播图所占屏幕的权重得到对应的视图对象
	public View getViewByWeight(int weight, int totalWeight) {
		// 如果传入的imageUrlList为空或者大小为0，则返回一个空的view
		if (carouselDiagramList == null || carouselDiagramList.size() == 0) {
			return new View(context);
		}
		// 将轮播图的布局文件转化为View对象
		layout_roll_view = View.inflate(context,
				R.layout.layout_roll_viewpager, null);
		ll_viewpager_container = (LinearLayout) layout_roll_view
				.findViewById(R.id.ll_viewpager_container);

		// 根据权重设置轮播图的高度
		int screenHeight = BaseTools.getWindowsHeight((Activity) context);
		android.widget.RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) ll_viewpager_container
				.getLayoutParams();
		layoutParams.height = screenHeight * weight / totalWeight;
		ll_viewpager_container.setLayoutParams(layoutParams);

		ll_dots = (LinearLayout) layout_roll_view.findViewById(R.id.ll_dots);
		// 初始化可滚动的viewpager的相关信息(图片，标题，标志dot)
		initDot(ll_dots);

		// 清除顶部布局视图，将轮播图片添加到头部布局文件中
		ll_viewpager_container.removeAllViews();
		ll_viewpager_container.addView(this);

		// 开始滚动轮播图
		startRoll();
		// 让页面从21亿之间的值开始，可以进行左右循环滑动，不过要确保第一次开始的数是carouselDiagramList.size()的倍数，只要把取模后的值减去即可
		// currentPosition = Integer.MAX_VALUE/2 - (Integer.MAX_VALUE/2 %
		// carouselDiagramList.size());
		currentPosition = carouselDiagramList.size() * 10000;
		this.setCurrentItem(currentPosition);

		return layout_roll_view;
	}

	// 之所以定义一个接口，就是为了让使用该RollViewPager类的类，在new对象时，必须实现该接口并重写内部的click方法
	public interface OnRollViewPagerItemClickListener {
		// 必须实现接口中方法，对应就是外层业务逻辑
		void click(Rows carouselDiagram);
	}

}
