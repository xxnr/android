package net.yangentao.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Util {

	public static int length(Collection<?> c) {
		return c == null ? 0 : c.size();
	}

	public static int length(String s) {
		return s == null ? 0 : s.length();
	}

	@SuppressWarnings("rawtypes")
	public static int length(Map m) {
		return m == null ? 0 : m.size();
	}

	public static void failWhen(boolean condition, String info) {
		if (condition) {
			fail(info);
		}
	}

	public static void failIf(boolean condition, String info) {
		if (condition) {
			fail(info);
		}
	}

	public static void fail(String info) {
		throw new IllegalStateException("" + info);
	}

	public static void setViewWidth(View v, int width) {
		ViewGroup.LayoutParams lp = v.getLayoutParams();
		lp.width = width;
		v.setLayoutParams(lp);
	}

	public static void setViewSize(View v, int width, int height) {
		ViewGroup.LayoutParams lp = v.getLayoutParams();
		lp.width = width;
		lp.height = height;
		v.setLayoutParams(lp);
	}

	public static void fillTextView(View parentView, int textViewId, String text) {
		TextView textView = (TextView) parentView.findViewById(textViewId);
		textView.setText(text);
	}

	/**
	 * for example : yyyy-MM-dd HH:mm:ss
	 * 
	 * @param pattern
	 * @return
	 */
	public static String getDateString(String pattern) {
		SimpleDateFormat ff = new SimpleDateFormat(pattern);
		return ff.format(new Date());
	}

	/**
	 * 会关闭输入输出流
	 */
	public static boolean copy(InputStream from, OutputStream to) {
		try {
			byte[] buffer = new byte[4096];
			int n = -1;
			while ((n = from.read(buffer)) != -1) {
				to.write(buffer, 0, n);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeStream(from);
			closeStream(to);
		}
		return false;
	}

	public static boolean copy(File from, File to) {
		try {
			FileInputStream fin = new FileInputStream(from);
			FileOutputStream fos = new FileOutputStream(to);
			return copy(fin, fos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void close(Cursor c) {
		if (c != null) {
			c.close();
		}
	}

	public static boolean isHttp(String url) {
		if (isEmpty(url) || url.length() < 10) {
			return false;
		}
		return url.startsWith("http://");
	}

	public static void closeStream(InputStream is) {
		try {
			if (is != null) {
				is.close();
				is = null;
			}
		} catch (Exception e) {
			// XLog.e(e);
		}
	}

	public static void closeStream(OutputStream os) {
		try {
			if (os != null) {
				os.close();
				os = null;
			}
		} catch (Exception e) {
			// XLog.e(e);
		}
	}

	public static boolean saveStreamToFile(InputStream is, File file) {
		if (is == null || file == null) {
			XLog.i("saveStreamToFile()..........");
			return false;
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) >= 0) {
				fos.write(buffer, 0, len);
			}
			fos.flush();
			fos.close();
			fos = null;
			return true;
		} catch (Exception e) {
			XLog.e(e);
		}
		return false;
	}

	public static byte[] saveStreamToBytes(InputStream is) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) >= 0) {
				os.write(buffer, 0, len);
			}
			return os.toByteArray();
		} catch (Exception e) {
			XLog.e(e);
		}
		return null;
	}

	public static String saveStreamToString(InputStream is, String encoding) {
		try {
			byte[] data = saveStreamToBytes(is);
			if (data != null) {
				return new String(data, encoding);
			}
		} catch (Exception e) {
			XLog.e(e);
		}
		return null;
	}

	private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String bytes2HexString(byte[] data) {
		StringBuilder sb = new StringBuilder(32);
		for (byte b : data) {
			char low = DIGITS[b & 0x0F];
			char high = DIGITS[(b & 0xF0) >>> 4];
			sb.append(high);
			sb.append(low);
		}
		return sb.toString();
	}

	public static String md5(String val) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(val.getBytes());
			byte[] m = md5.digest();// 加密
			return bytes2HexString(m);
		} catch (Exception e) {
			XLog.e(e);
		}
		return null;
	}

	public static boolean inSetN(int target, int... values) {
		for (int v : values) {
			if (target == v) {
				return true;
			}
		}
		return false;
	}

	public static boolean inSet(Object target, Object... values) {
		for (Object o : values) {
			if (equal(target, o)) {
				return true;
			}
		}
		return false;
	}

	public static boolean equal(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		if (o1 == null) {
			return false;
		}
		return o1.equals(o2);
	}

	public static boolean notEmpty(String s) {
		return !isEmpty(s);
	}

	public static boolean notEmpty(Collection<?> c) {
		return !isEmpty(c);
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean empty(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean isEmpty(Collection<?> list) {
		return list == null || list.isEmpty();
	}

	public static boolean empty(Collection<?> list) {
		return list == null || list.isEmpty();
	}

	/**
	 * val 属于区间[from, to]
	 * 
	 * @Description:
	 * @param val
	 * @param from
	 * @param to
	 * @return
	 * @see:
	 * @since:
	 * @author: yangentao
	 * @date:2012-7-18
	 */

	public static boolean inRange11(int val, int from, int to) {
		return val >= from && val <= to;
	}

	/**
	 * val 属于区间[from, to)
	 * 
	 * @Description:
	 * @param val
	 * @param from
	 * @param to
	 * @return
	 * @see:
	 * @since:
	 * @author: yangentao
	 * @date:2012-7-18
	 */

	public static boolean inRange10(int val, int from, int to) {
		return val >= from && val < to;
	}

	/**
	 * 将a-z的小写字符转换成A-Z的大写字符, 如果给定的字符不在[a,z]区间内, 则返回原字符
	 * 
	 * @Description:
	 * @param ch
	 * @return
	 * @see:
	 * @since:
	 * @author: yangentao
	 * @date:2012-7-18
	 */

	public static char toUpper(char ch) {
		if (inRange11(ch, 'a', 'z')) {
			return (char) (ch - 'a' + 'A');
		}
		return ch;
	}

	/**
	 * 图片处理，防止图片过大导致内存溢出
	 * 
	 * @param imageFile
	 *            图片路径
	 * @param maxWidth
	 *            最大宽
	 * @param maxHeight
	 *            最大高
	 * @return
	 */
	public static Bitmap loadBitmapFromFile(File imageFile, int maxWidth,
			int maxHeight) {
		// int newSize = 7 * 1024 * 1024 ; //设置最小堆内存大小为7MB
		// VMRuntime.getRuntime().setMinimumHeapSize(newSize);
		if (null == imageFile || !imageFile.exists()) {
			return null;
		}
		BitmapFactory.Options opts = null;
		try {
			if (maxWidth > 0 && maxHeight > 0) {
				opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(new FileInputStream(imageFile),
						null, opts);
				double scaleW = opts.outWidth * 1.0 / maxWidth;
				double scaleH = opts.outHeight * 1.0 / maxHeight;
				double scale = Math.max(scaleW, scaleH);
				opts.inJustDecodeBounds = false;
				opts.inSampleSize = (int) Math.floor(scale + 0.5);
				opts.inPreferredConfig = Bitmap.Config.RGB_565;
				opts.inInputShareable = true;
				opts.inPurgeable = true;
			}
			return BitmapFactory.decodeStream(new FileInputStream(imageFile),
					null, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean saveBitmap(Bitmap bmp, File saveTo) {
		try {
			FileOutputStream fos = new FileOutputStream(saveTo);
			return saveBitmap(bmp, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean saveBitmap(Bitmap bmp, OutputStream saveTo) {

		try {
			boolean success = bmp.compress(CompressFormat.PNG, 100, saveTo);
			saveTo.close();
			return success;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

}
