package net.yangentao.util;

import android.util.Log;
import net.yangentao.util.XLog.XPrinter;

public class LogcatPrinter implements XPrinter {

	@Override
	public void println(int priority, String tag, String msg) {
		Log.println(priority, tag, "" + msg);
	}

}