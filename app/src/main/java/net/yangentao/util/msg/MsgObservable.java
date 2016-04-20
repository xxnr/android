package net.yangentao.util.msg;

import java.util.ArrayList;
import java.util.List;

import net.yangentao.util.MultiHashMap;
import net.yangentao.util.Util;

// 从java.util.Observable拷贝过来的, 修改了一下
/**
 * <p>
 * 观察者模式, 对属性的观察
 * </p>
 * 
 * @author yangentao
 * 
 */
public class MsgObservable {
	private MultiHashMap<String, MsgListener> listeners = new MultiHashMap<>(
			12);
	private ArrayList<MsgListener> globalListeners = new ArrayList<>();

	/**
	 * 如果没有指定消息ID, 则会添加一个全局的监听器, 能监听所有消息.
	 * 
	 * @param listener
	 *            非空
	 * @param msgs
	 *            要监听的消息ID列表
	 */
	synchronized public void addListener(MsgListener listener, String... msgs) {
		if (listener == null) {
			throw new IllegalArgumentException("listener can not be null!");
		}
		if (globalListeners.contains(listener)) {
			return;
		}
		if (msgs.length == 0) {
			globalListeners.add(listener);
		} else {
			for (String msg : msgs) {
				listeners.put(msg, listener);
			}
		}
	}

	public synchronized void remove(String msg, MsgListener listener) {
		listeners.remove(msg, listener);
	}

	public synchronized void remove(MsgListener listener) {
		listeners.removeValue(listener);
		globalListeners.remove(listener);
	}

	public synchronized void clear() {
		listeners.clear();
		globalListeners.clear();
	}

	/**
	 * 激发一个广播,通知所有监听者,
	 * 
	 * @param sender
	 *            谁激发的, 可以是null, 由监听者和被监听者协商
	 * @param msg
	 *            消息ID, 标识事件
	 * @param args
	 *            自定义的参数
	 */
	public void fire(Object sender, String msg, Object... args) {
		ArrayList<MsgListener> tofire = new ArrayList<>(
				globalListeners.size() + 8);
		synchronized (this) {
			tofire.addAll(globalListeners);
			List<MsgListener> ls = listeners.get(msg);
			if (Util.notEmpty(ls)) {
				tofire.addAll(ls);
			}
		}
		for (MsgListener observer : tofire) {
			try {
				observer.onMsg(sender, msg, args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
