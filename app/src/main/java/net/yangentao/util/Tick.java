package net.yangentao.util;

public class Tick {
	private static final String TAG = "tick";

	public long start = 0;
	public long end = 0;

	public Tick() {
		begin();
	}

	/**
	 * 开始计时
	 * 
	 * @Description:
	 * @see:
	 * @since:
	 * @author: yangentao
	 * @date:2012-7-6
	 */

	public void begin() {
		start = System.currentTimeMillis();
	}

	/**
	 * 停止计时
	 * 
	 * @Description:
	 * @see:
	 * @since:
	 * @author: yangentao
	 * @date:2012-7-5
	 */

	public void end() {
		end = System.currentTimeMillis();
	}

	/**
	 * 结束计时
	 * 
	 * @Description:
	 * @param prefix
	 *            打印信息
	 * @return
	 * @see:
	 * @since:
	 * @author: yangentao
	 * @date:2012-7-6
	 */
	public void end(String prefix) {
		end(0, prefix);
	}

	/**
	 * @param warnLevel
	 *            计时大于这个值(单位毫秒)时, 用警告输出
	 * @param prefix
	 */
	public void end(long warnLevel, String prefix) {
		end();
		long tick = getTick();
		if (null == prefix) {
			prefix = "";
		}
		if (warnLevel > 0 && tick > warnLevel) {
			XLog.wTag(TAG, "[TimeTick]", prefix, ":", tick, "(expect<",
					warnLevel, ")");
		} else {
			XLog.dTag(TAG, "[TimeTick]", prefix, ":", tick);
		}
	}

	/**
	 * 返回开始和结束的时间差
	 * 
	 * @Description:
	 * @return
	 * @see:
	 * @since:
	 * @author: yangentao
	 * @date:2012-7-6
	 */

	public long getTick() {
		return end - start;
	}

	/**
	 * 开始计时
	 * 
	 * @Description:
	 * @return 计时器
	 * @see:
	 * @since:
	 * @author: yangentao
	 * @date:2012-7-6
	 */
	public static Tick go() {
		Tick t = new Tick();
		return t;
	}
}
