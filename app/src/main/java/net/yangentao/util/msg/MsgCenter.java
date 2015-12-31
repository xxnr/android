package net.yangentao.util.msg;

public class MsgCenter {
	private static MsgObservable stub = new MsgObservable();

	/**
	 * 如果没有指定消息ID, 则会添加一个全局的监听器, 能监听所有消息.
	 * 
	 * @param listener
	 *            非空
	 * @param msgs
	 *            要监听的消息ID列表
	 */
	public static void addListener(MsgListener listener, String... msgs) {
		stub.addListener(listener, msgs);
	}

	public static void remove(String msg, MsgListener listener) {
		stub.remove(msg, listener);
	}

	public static void remove(MsgListener listener) {
		stub.remove(listener);
	}

	public static void clear() {
		stub.clear();
	}

	public static void fireSender(Object sender, String msg, Object... args) {
		stub.fire(sender, msg, args);
	}

	public static void fireNull(String msg, Object... args) {
		stub.fire(null, msg, args);
	}
}
