package net.yangentao.util;

/**
 * 倒计时读秒, 只能在主线程调用, 读秒并不是一个精确的值, 只是一个大概值.<br/>
 * 倒计时 回调范围[max, 0], 如果回调时current==0,说明是最后一次回调<br/>
 * 
 * @author yangentao
 * 
 */
public class CountDownSeconds {
	public interface CountDownListener {
		public void onCount(int current);
	}

	private CountDownListener listener;
	private int current = -1;

	/**
	 * 倒计时, [max, 0]. 比如, 30秒倒计时: countDown(30, 1000, callback); => 30, 29,
	 * 28....3,2,1,0
	 * 
	 * @param max
	 *            需要>0
	 */
	public void start(int max, CountDownListener listener) {
		this.listener = listener;
		current = max;
		if (current > 0 && listener != null) {
			TaskUtil.foreDelay(run, 0);// 立即回调一次, 然后再延时1000
		}
	}

	public void cancel() {
		current = -1;
	}

	private Runnable run = new Runnable() {

		@Override
		public void run() {
			if (current >= 0) {
				listener.onCount(current);
			}
			--current;
			if (current >= 0) {
				TaskUtil.foreDelay(run, 1000);
			}
		}
	};

}
