package com.ksfc.newfarmer.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import net.yangentao.util.XLog;
import net.yangentao.util.app.App;

public class IoUtils {

	private static String imageCacheDir = "images";

	/**
	 * 得到图片的缓存目录
	 * 
	 * @return
	 */
	@SuppressLint("NewApi")
	public static File getImageCacheDir() {
		File imageDir = null;
		String appName = "foodie";
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			File dir = Environment.getExternalStorageDirectory();
			String path = dir.getAbsolutePath() + File.separator + appName
					+ File.separator + imageCacheDir;
			imageDir = new File(path);
			if (!imageDir.exists()) {
				imageDir.mkdirs();
			}
		} else {
			imageDir = App.getApp().getCacheDir();
		}
		return imageDir;
	}

	/**
	 * 将bitmap保存到指定路径
	 * 
	 * @param map
	 * @param path
	 * @return
	 */
	public static void saveBitmap(Bitmap map, String path) {

		FileOutputStream out = null;
		try {

			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			CompressFormat format = getFormat(path);
			if (null != format) {
				out = new FileOutputStream(file);
				if (map.compress(format, 100, out)) {
					out.flush();
					out.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (map != null && !map.isRecycled()) {
			map.recycle();
			map = null;
		}
	}

	private static CompressFormat getFormat(String path) {

		String type = getType(path);
		CompressFormat format = null;
		if (!TextUtils.isEmpty(type)) {
			if (type.equalsIgnoreCase("png")) {
				format = CompressFormat.PNG;
			} else if (type.equalsIgnoreCase("jpg")
					|| type.equalsIgnoreCase("jpe")
					|| type.equalsIgnoreCase("jpeg")) {
				format = CompressFormat.JPEG;
			}
		}

		return format;
	}

	private static String getType(String path) {

		String type;
		type = "";
		try {
			int pos = path.lastIndexOf(".");
			if (pos != -1) {
				type = path.substring(pos + 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return type;
	}

	// decodefile 并进行内存溢出的判断
	public static Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f));

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 100;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = 1;
			return BitmapFactory.decodeStream(new FileInputStream(f));
		} catch (Exception e) {
			e.printStackTrace();
			XLog.e("获取文件出错.");
		}
		return null;
	}

	/**
	 * 将图片压缩到500k以下
	 * 
	 * @param path
	 */
	public static Bitmap compressImageFifty(Bitmap image) {
		return null;
	}

	public static void compressImageAndSave(Bitmap image, String path) {

		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 500) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			options -= 10;// 每次都减少10
			if (options == 0) {
				break;
			}
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
		}
		// ByteArrayInputStream isBm = new
		// ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		// Bitmap bitmap = BitmapFactory.decodeStream(isBm, null,
		// null);//把ByteArrayInputStream数据生成图片
		try {
			FileOutputStream out = new FileOutputStream(file);
			baos.writeTo(out);
			out.flush();
			out.close();
			// if (bitmap.compress(Bitmap.CompressFormat.JPEG, options+10, out))
			// {
			// out.flush();
			// out.close();
			// }
			// if (bitmap != null && !bitmap.isRecycled()) {
			// bitmap.recycle();
			// bitmap = null;
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
		// return bitmap;
	}

}
