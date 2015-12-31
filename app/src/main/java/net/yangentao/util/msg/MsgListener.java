package net.yangentao.util.msg;

public interface MsgListener {
	/**
	 * 观察者的回调接口方法. sender是否为null取决于被观察者激发一个消息时的设置,
	 * 通常可以根据sender==this来判断是否是自己激发的事件
	 * 
	 * @param sender
	 * @param messageID
	 * @param datas
	 */
	public void onMsg(Object sender, String msg, Object... args);// (this, 123,
																	// a,
																	// b, c, d)

}
