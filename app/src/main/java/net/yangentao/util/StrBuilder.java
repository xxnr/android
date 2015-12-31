package net.yangentao.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 字符串构造, 类似StringBuilder <br/>
 * 
 * @important 这个类的目的不是为性能, 而是为易用, 尽管内部使用StringBuffer做代理; 对性能要求高时建议使用StringBuffer
 * @author yangentao
 * 
 */
public class StrBuilder {
	private StringBuffer sb;

	public static String build(Object... args) {
		StrBuilder builder = new StrBuilder();
		builder.append(args);
		return builder.toString();
	}

	public StrBuilder() {
		sb = new StringBuffer(32);
	}

	public StrBuilder(int capcity) {
		sb = new StringBuffer(capcity);
	}

	public int length() {
		return sb.length();
	}

	public boolean isEmpty() {
		return length() == 0;
	}

	public StrBuilder append(Object... args) {
		if (args.length > 0) {
			sb.ensureCapacity(sb.length() + args.length * 8 + 8);
			for (Object obj : args) {
				sb.append(getString(obj));
			}
		}
		return this;
	}

	private static String getString(Object obj) {
		if (obj == null) {
			return "null";
		}
		// else
		if (obj instanceof Throwable) {
			Throwable tr = (Throwable) obj;
			StringWriter sw = new StringWriter(128);
			PrintWriter pw = new PrintWriter(sw);
			tr.printStackTrace(pw);
			return sw.toString();
		}
		// else
		if (obj instanceof String) {
			return (String) obj;
		}
		// else
		return obj.toString();
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}
